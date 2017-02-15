package ee.nipt.component;

import ee.nipt.db.PersonRepository;
import ee.nipt.domain.Person;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.InteractiveAuthenticationSuccessEvent;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

@Component
@Slf4j
public class LoginListener implements ApplicationListener<InteractiveAuthenticationSuccessEvent> {
    private final PersonRepository personRepository;
    private final HttpServletResponse response;

    @Value("${locale.cookie.name}")
    private String cookieName;

    @Autowired
    public LoginListener(PersonRepository personRepository, HttpServletResponse response) {
        this.personRepository = personRepository;
        this.response = response;
    }

    @Override
    public void onApplicationEvent(InteractiveAuthenticationSuccessEvent event) {
        UserDetails userDetails = (UserDetails) event.getAuthentication().getPrincipal();
        Person person = personRepository.findOneByEmail(userDetails.getUsername()).get();

        String lang = person.getLanguage().toLowerCase();
        response.addCookie(new Cookie(cookieName, lang));
        log.info("{} logged in", person.getEmail());
    }
}