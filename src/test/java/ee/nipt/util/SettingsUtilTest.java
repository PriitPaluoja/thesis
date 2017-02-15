package ee.nipt.util;

import ee.nipt.domain.Person;
import org.junit.Assert;
import org.junit.Test;

public class SettingsUtilTest {
    @Test
    public void updatePersonPasswordHash() throws Exception {
        String password = "Some password...";
        Person person = new Person();
        SettingsUtil.assignPasswordForPerson(person, password);
        Assert.assertNotEquals(password, person.getPswdHash());
    }

    @Test
    public void checkPasswordMatch() throws Exception {
        String password = "Some password...";
        Person person = new Person();
        SettingsUtil.assignPasswordForPerson(person, password);
        Assert.assertTrue(SettingsUtil.checkPasswordMatch(password, person.getPswdHash()));
        Assert.assertFalse(SettingsUtil.checkPasswordMatch(password + "j", person.getPswdHash()));
    }

    @Test
    public void passwordRepeatEquals() throws Exception {
        Assert.assertTrue(SettingsUtil.passwordRepeatEquals("A", "A"));
        Assert.assertFalse(SettingsUtil.passwordRepeatEquals("A", ".A"));
    }
}