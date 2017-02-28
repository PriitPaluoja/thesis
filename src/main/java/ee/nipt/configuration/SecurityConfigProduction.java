package ee.nipt.configuration;


import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import java.lang.reflect.Method;

@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)
@Order(SecurityProperties.ACCESS_OVERRIDE_ORDER)
@AllArgsConstructor(onConstructor = @__(@Autowired))
@Profile("production")
@Slf4j
public class SecurityConfigProduction extends WebSecurityConfigurerAdapter {
    private UserDetailsService userDetailsService;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .antMatchers("/public/**").permitAll()
                .anyRequest().fullyAuthenticated()
                .and()
                .x509()
                .subjectPrincipalRegex("serialNumber=([1-9][0-9]+)")
                .and()
                .logout()
                // Forget SSL connection:
                .addLogoutHandler((request, response, authentication) -> {
                    response.setHeader("Connection", "close");
                    Object sslSessionMgr = request.getAttribute("javax.servlet.request.ssl_session_mgr");
                    if (sslSessionMgr != null) {
                        try {
                            Method invalidateSession =
                                    Class.forName("org.apache.tomcat.util.net.SSLSessionManager").getMethod("invalidateSession");
                            invalidateSession.setAccessible(true);
                            invalidateSession.invoke(sslSessionMgr);
                        } catch (Exception e) {
                            log.error("Unable to invalidate session!");
                        }
                    }
                })
                .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                .clearAuthentication(true)
                .deleteCookies("remember-me")
                .logoutSuccessUrl("/")
                .permitAll()
                .and()
                .rememberMe()
                .and().csrf();
    }

    @Override
    public void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService);
    }
}
