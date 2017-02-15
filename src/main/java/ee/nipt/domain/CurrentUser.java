package ee.nipt.domain;

import org.springframework.security.core.authority.AuthorityUtils;

import java.util.List;

public class CurrentUser extends org.springframework.security.core.userdetails.User {
    private Person user;

    public CurrentUser(Person user) {
        super(
                user.getEmail(),
                user.getPswdHash(),
                AuthorityUtils.createAuthorityList(String.valueOf(user.getRoleList().stream().map(Enum::toString)))
        );
        this.user = user;
    }

    public String getId() {
        return user.getIdentificationCode();
    }

    public List<Role> getRole() {
        return user.getRoleList();
    }
}
