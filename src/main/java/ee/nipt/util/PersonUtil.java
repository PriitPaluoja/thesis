package ee.nipt.util;

import ee.nipt.domain.CurrentUser;
import ee.nipt.domain.Person;
import ee.nipt.domain.Role;
import ee.nipt.dto.PersonDTO;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class PersonUtil {
    /**
     * Convert {@link Person} to {@link PersonDTO}
     * <p>
     * Some fields such as language are lost.
     *
     * @param person to be converted to {@link PersonDTO}
     * @return new {@link PersonDTO}
     */
    public static PersonDTO convert(Person person) {
        PersonDTO dto = new PersonDTO();
        dto.setIdentificationCode(person.getIdentificationCode());
        dto.setEmail(person.getEmail());
        dto.setLastName(person.getLastName());
        dto.setFirstName(person.getFirstName());
        dto.setCanAddUsers(person.getRoleList().contains(Role.ADD_USERS));
        dto.setCanCreateExamination(person.getRoleList().contains(Role.CREATE_EXAMINATION));
        dto.setCanCreateReport(person.getRoleList().contains(Role.CREATE_REPORT));
        dto.setCanViewReport(person.getRoleList().contains(Role.VIEW_REPORT));
        return dto;
    }

    /**
     * Convert {@link PersonDTO} to {@link Person}
     * <p>
     * Fields not present in the DTO are left uninitialized
     *
     * @param personDTO to be converted to {@link Person}
     * @return new {@link Person}
     */
    public static Person convert(PersonDTO personDTO) {
        Person person = new Person();
        person.setIdentificationCode(personDTO.getIdentificationCode());
        person.setEmail(personDTO.getEmail());
        person.setLastName(personDTO.getLastName());
        person.setFirstName(personDTO.getFirstName());
        person.setRoleList(new ArrayList<>(personDTO.getRoleList()));
        return person;
    }

    /**
     * Check if  A ⊆ B
     * <p>
     * Inputs will be converted to sets.
     *
     * @param roleListA that will be checked against other check
     * @param roleListB should be superset
     * @return <code>true</code> if A ⊆ B else <code>false</code>
     */
    public static boolean isSubset(List<Role> roleListA, List<Role> roleListB) {
        return isSubset(new HashSet<>(roleListA), new HashSet<>(roleListB));
    }

    /**
     * Generate random password
     *
     * @return new random password as {@link String}
     */
    public static String nextPassword() {
        SecureRandom random = new SecureRandom();
        return new BigInteger(130, random).toString(32);
    }

    /**
     * Check if {@link CurrentUser} can access the feature that requires role
     *
     * @param currentUser is the user under question
     * @param role        is the role that currentUser needs to access feature
     * @return <code>true</code> if user has role else <code>false</code>
     */
    public static boolean canAccess(CurrentUser currentUser, Role role) {
        return currentUser != null && (currentUser.getRole().contains(role));
    }

    /**
     * Check that creator has created user in the past
     */
    public static boolean isCreator(Person user, Person creator) {
        return user.getCreator() != null && user.getCreator().equals(creator.getIdentificationCode());
    }

    private static boolean isSubset(Set<Role> setA, Set<Role> setB) {
        return setB.containsAll(setA);
    }


}
