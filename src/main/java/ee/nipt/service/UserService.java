package ee.nipt.service;

import ee.nipt.domain.CurrentUser;
import ee.nipt.domain.Person;
import ee.nipt.domain.Role;
import ee.nipt.dto.PersonDTO;
import ee.nipt.dto.PersonsDTO;
import ee.nipt.dto.SettingsDTO;
import ee.nipt.exception.*;
import org.springframework.ui.Model;

import javax.mail.MessagingException;
import java.security.Principal;
import java.util.Locale;
import java.util.Optional;

public interface UserService {
    /**
     * Find and return corresponding {@link Optional<Person>} from the database by email
     *
     * @param email identifying user
     * @return {@link Optional<Person>} from the database
     */
    Optional<Person> getPersonByEmail(String email);

    /**
     * Find and return corresponding {@link PersonDTO} from the database by {@link Principal}
     *
     * @param principal identifying user
     * @return {@link PersonDTO} from the database
     */
    PersonDTO getPersonDtoByPrincipal(Principal principal);

    /**
     * Find and return all {@link PersonDTO} created by user
     *
     * @param creator as the user who has created users
     */
    PersonsDTO getPersonsCreatedBy(Principal creator);

    /**
     * Create new user, insert it into the database and send e-mail notification with password to new user.
     *
     * @param newPerson to be inserted into the database
     * @param creator   of the new person
     */
    void createAndNotifyUser(PersonDTO newPerson, Principal creator) throws MessagingException, RoleViolationException, DuplicateUserCreationException;

    /**
     * Update person roles
     *
     * @param toUpdate is the {@link PersonDTO} to modify
     * @param creator  is the user who has created to be updated users in the past
     * @param model    to add {@link java.util.List<ee.nipt.feedback.Feedback>} for front-end notifications
     * @param locale   used for translating text for feedback
     */
    void updateUsersRolesByPrincipal(PersonsDTO toUpdate, Principal creator, Model model, Locale locale) throws RoleViolationException, UnauthorizedUserManipulationException;

    /**
     * Update {@link Person} by {@link Principal} and {@link SettingsDTO}
     *
     * @param user whose password will be updated
     * @param dto  containing old password, new password and password repeat
     */
    void updatePassword(Principal user, SettingsDTO dto) throws WrongPasswordException, PasswordNotMatchException;

    /**
     * Update {@link Person} language by {@link Principal} and {@link String} language tag and save it to the database.
     *
     * @param user whose language will be updated
     * @param lang to be assigned for user
     */
    void updateLang(Principal user, String lang);

    /**
     * Check that current user has the role need for pre-authorization
     *
     * @param currentUser is the user whose role is checked
     * @param roleNeeded  is the role needed
     * @return <code>true</code> if user has the needed role else <code>false</code>
     */
    boolean canAccess(CurrentUser currentUser, Role roleNeeded);
}