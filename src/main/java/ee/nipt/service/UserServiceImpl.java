package ee.nipt.service;

import ee.nipt.db.PersonRepository;
import ee.nipt.domain.CurrentUser;
import ee.nipt.domain.Person;
import ee.nipt.domain.Role;
import ee.nipt.dto.PersonDTO;
import ee.nipt.dto.PersonsDTO;
import ee.nipt.dto.SettingsDTO;
import ee.nipt.exception.*;
import ee.nipt.util.PersonUtil;
import ee.nipt.util.SettingsUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import javax.mail.MessagingException;
import java.security.Principal;
import java.util.*;
import java.util.stream.Collectors;

import static ee.nipt.util.SettingsUtil.assignPasswordForPerson;

@Service
@AllArgsConstructor(onConstructor = @__(@Autowired))
@Slf4j
public class UserServiceImpl implements UserService, UserDetailsService {
    private final PersonRepository userRepository;
    private final MailService mailService;


    @Override
    public Optional<Person> getPersonByEmail(String email) {
        return userRepository.findOneByEmail(email);
    }

    @Override
    public PersonDTO getPersonDtoByPrincipal(Principal principal) {
        return PersonUtil.convert(getOneByEmail(principal.getName()));
    }

    @Override
    public void createAndNotifyUser(PersonDTO dto, Principal principal) throws MessagingException, RoleViolationException, DuplicateUserCreationException {
        Person creator = getOneByEmail(principal.getName());

        // Check that user under creation rights are subset of the creator.
        if (!PersonUtil.isSubset(dto.getRoleList(), creator.getRoleList())) {
            log.error("{} tried to set unauthorized access to user {}", creator.getEmail(), dto.getEmail());
            throw new RoleViolationException();
        }

        // Avoid duplication of users.
        if (userRepository.exists(dto.getIdentificationCode()) || getPersonByEmail(dto.getEmail()).isPresent()) {
            log.error("{} tried to duplicate user {} with name: {} {} and ID {}",
                    creator.getEmail(),
                    dto.getEmail(),
                    dto.getFirstName(),
                    dto.getLastName(),
                    dto.getIdentificationCode()
            );
            throw new DuplicateUserCreationException();
        }

        // Create new user
        String plainTextPswd = PersonUtil.nextPassword();
        Person newPerson = PersonUtil.convert(dto);
        newPerson.setLanguage("ET");
        newPerson.setCreator(creator.getIdentificationCode());
        newPerson = assignPasswordForPerson(newPerson, plainTextPswd);

        // Save
        userRepository.save(newPerson);
        log.info("{} created new user {} with ID", creator.getEmail(), dto.getEmail(), dto.getIdentificationCode());
        // Notify new user (and send him/her password)
        mailService.emailUserCreate(newPerson, plainTextPswd, creator);
    }

    @Override
    public PersonsDTO getPersonsCreatedBy(Principal creator) {
        String creatorID = getOneByEmail(creator.getName()).getIdentificationCode();
        return new PersonsDTO(
                userRepository.findManyByCreator(creatorID)
                        .stream()
                        .map(PersonUtil::convert)
                        .collect(Collectors.toList())
        );
    }

    @Override
    public void updateUsersRolesByPrincipal(PersonsDTO personsDTO, Principal creator, Model model, Locale locale) throws RoleViolationException, UnauthorizedUserManipulationException {
        Person changer = getOneByEmail(creator.getName());
        //Convert DTOs to Person's
        List<Person> people = personsDTO.getPersons().stream().map(PersonUtil::convert).collect(Collectors.toList());
        // In previous list only roles and id's are important, but later we need to load everything else for saving
        List<Person> toSave = new ArrayList<>();

        for (Person person : people) {
            // Check that creator assigns only allowed roles
            if (!PersonUtil.isSubset(person.getRoleList(), changer.getRoleList())) {
                log.error("{} tried to set unauthorized access to user {}", changer.getEmail(), person.getEmail());
                throw new RoleViolationException();
            }

            Person newPerson = userRepository.findOne(person.getIdentificationCode());
            // Check that creator is modifying users he/she has created
            if (!PersonUtil.isCreator(newPerson, changer)) {
                log.error("{} had no authorization to change user {} roles", changer.getEmail(), person.getEmail());
                throw new UnauthorizedUserManipulationException();
            }

            // Update user if there is anything to update
            if (!new HashSet<>(person.getRoleList()).equals(new HashSet<>(newPerson.getRoleList()))) {
                log.info("{} changed roles of user {} from {} to {}", changer.getEmail(),
                        newPerson.getEmail(),
                        newPerson.getRoleList().toString(),
                        person.getRoleList());

                newPerson.setRoleList(new ArrayList<>(person.getRoleList()));
                toSave.add(newPerson);
            }

        }
        // Save
        userRepository.save(toSave);
    }

    @Override
    public void updatePassword(Principal user, SettingsDTO dto) throws WrongPasswordException, PasswordNotMatchException {
        Person person = getOneByEmail(user.getName());
        // Previous password match checkup
        if (!SettingsUtil.checkPasswordMatch(dto.getOldPassword(), person.getPswdHash())) {
            log.info("{} tried to change password, but old password was wrong", user.getName());
            throw new WrongPasswordException();
        }

        // New password check-up
        if (!SettingsUtil.passwordRepeatEquals(dto.getNewPassword(), dto.getNewPasswordRepeat())) {
            log.info("{} tried to change password, but password repeat was not equal", user.getName());
            throw new PasswordNotMatchException();
        }
        //Save
        userRepository.save(SettingsUtil.assignPasswordForPerson(person, dto.getNewPassword()));
        log.info("{} changed password", user.getName());
    }

    @Override
    public void updateLang(Principal user, String lang) {
        Person person = getOneByEmail(user.getName());
        person.setLanguage(lang);
        userRepository.save(person);
    }

    @Override
    public boolean canAccess(CurrentUser currentUser, Role role) {
        return PersonUtil.canAccess(currentUser, role);
    }

    @Override
    public CurrentUser loadUserByUsername(String email) throws UsernameNotFoundException {
        Person user = getPersonByEmail(email)
                .orElseThrow(() -> {
                    log.warn("Somebody tried to login with invalid name {}", email);
                    return new UsernameNotFoundException(String.format("User with email=%s was not found", email));
                });
        return new CurrentUser(user);
    }

    /**
     * For internal use only. Retrieve {@link Person} from database by email
     */
    private Person getOneByEmail(String email) {
        return getPersonByEmail(email).get();
    }
}