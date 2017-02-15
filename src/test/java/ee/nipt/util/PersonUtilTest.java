package ee.nipt.util;

import ee.nipt.domain.CurrentUser;
import ee.nipt.domain.Person;
import ee.nipt.domain.Role;
import ee.nipt.dto.PersonDTO;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import static ee.nipt.util.TestUtil.nextString;

public class PersonUtilTest {

    private Person createRandomPerson() {
        return new Person(
                nextString(),
                nextString(),
                nextString(),
                nextString(),
                nextString(),
                nextString(),
                nextString(),
                Arrays.asList(Role.VIEW_REPORT, Role.CREATE_EXAMINATION, Role.ADD_USERS, Role.CREATE_REPORT)
        );
    }

    @Test
    public void convertPersonToDto() throws Exception {
        Person person = createRandomPerson();
        PersonDTO dto = PersonUtil.convert(person);

        Assert.assertEquals(person.getIdentificationCode(), dto.getIdentificationCode());
        Assert.assertEquals(person.getFirstName(), dto.getFirstName());
        Assert.assertEquals(person.getLastName(), dto.getLastName());
        Assert.assertEquals(person.getEmail(), dto.getEmail());
        Assert.assertEquals(new HashSet<>(person.getRoleList()), new HashSet<>(dto.getRoleList()));
    }

    @Test
    public void convertDtoToPerson() throws Exception {
        Person person = createRandomPerson();
        Person newPerson = PersonUtil.convert(PersonUtil.convert(person));

        Assert.assertEquals(person.getIdentificationCode(), newPerson.getIdentificationCode());
        Assert.assertEquals(person.getFirstName(), newPerson.getFirstName());
        Assert.assertEquals(person.getLastName(), newPerson.getLastName());
        Assert.assertEquals(person.getEmail(), newPerson.getEmail());
        Assert.assertEquals(new HashSet<>(person.getRoleList()), new HashSet<>(newPerson.getRoleList()));
    }

    @Test
    public void isSubset() throws Exception {
        List<Role> mainList = Arrays.asList(Role.CREATE_REPORT, Role.VIEW_REPORT, Role.ADD_USERS);
        List<Role> sub = Collections.singletonList(Role.VIEW_REPORT);
        List<Role> nonSub = Arrays.asList(Role.VIEW_REPORT, Role.CREATE_EXAMINATION);

        Assert.assertTrue(PersonUtil.isSubset(sub, mainList));
        Assert.assertTrue(PersonUtil.isSubset(sub, nonSub));
        Assert.assertFalse(PersonUtil.isSubset(mainList, sub));
        Assert.assertFalse(PersonUtil.isSubset(nonSub, mainList));
    }

    @Test
    public void nextPassword() throws Exception {
        Assert.assertNotEquals(PersonUtil.nextPassword(), PersonUtil.nextPassword());
    }

    @Test
    public void canAccess() throws Exception {
        List<Role> roles = Collections.singletonList(Role.VIEW_REPORT);
        String email = "some@hmail.com";
        Person person = SettingsUtil.assignPasswordForPerson(new Person(), PersonUtil.nextPassword());
        person.setEmail(email);
        person.setRoleList(roles);

        CurrentUser currentUser = new CurrentUser(person);

        Assert.assertTrue(PersonUtil.canAccess(currentUser, Role.VIEW_REPORT));
        Assert.assertFalse(PersonUtil.canAccess(currentUser, Role.CREATE_REPORT));
        Assert.assertFalse(PersonUtil.canAccess(null, Role.CREATE_REPORT));
    }

    @Test
    public void isCreator() throws Exception {
        Person creator = new Person();
        Person user = new Person();

        creator.setIdentificationCode("11111111111");
        user.setCreator(creator.getIdentificationCode());

        Assert.assertTrue(PersonUtil.isCreator(user, creator));
        Assert.assertFalse(PersonUtil.isCreator(creator, user));
        creator.setIdentificationCode("1");
        Assert.assertFalse(PersonUtil.isCreator(user, creator));
    }
}