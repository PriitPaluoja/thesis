package ee.nipt.util;

import ee.nipt.domain.Person;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class SettingsUtil {

    /**
     * @param person            whose password hash will be updated
     * @param plainTextPassword password that will be hashed and assigned to person
     * @return {@link Person} with updated password hash
     */
    public static Person assignPasswordForPerson(Person person, String plainTextPassword) {
        person.setPswdHash(new BCryptPasswordEncoder().encode((plainTextPassword)));
        return person;
    }

    /**
     * @param oldPasswordAsPlainText is the password that needs to be encoded
     * @param provided               is the hash that the previous argument is checked against
     * @return <code>true</code> if passwords match
     */
    public static boolean checkPasswordMatch(String oldPasswordAsPlainText, String provided) {
        return new BCryptPasswordEncoder().matches(oldPasswordAsPlainText, provided);
    }

    /**
     * Check that provided passwords as plain texts are equal
     */
    public static boolean passwordRepeatEquals(String password, String passwordRepeat) {
        return password.equals(passwordRepeat);
    }
}
