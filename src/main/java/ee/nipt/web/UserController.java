package ee.nipt.web;

import ee.nipt.dto.PersonDTO;
import ee.nipt.dto.PersonsDTO;
import ee.nipt.dto.SettingsDTO;
import ee.nipt.exception.*;
import ee.nipt.feedback.FeedbackType;
import ee.nipt.service.UserService;
import ee.nipt.util.ControllerUtil;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.mail.MessagingException;
import javax.validation.Valid;
import java.security.Principal;
import java.util.Locale;

@Controller
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class UserController {
    private final UserService userService;
    private final MessageSource messageSource;


    @PreAuthorize("@userServiceImpl.canAccess(principal, T(ee.nipt.domain.Role).ADD_USERS)")
    @GetMapping("/new")
    public String getUserCreationPage(@ModelAttribute("person") PersonDTO personDTO, Model model, Principal principal) {
        model.addAttribute("creator", userService.getPersonDtoByPrincipal(principal));
        return "users";
    }

    @PreAuthorize("@userServiceImpl.canAccess(principal, T(ee.nipt.domain.Role).ADD_USERS)")
    @PostMapping("/new")
    public String postUser(@ModelAttribute("person") @Valid PersonDTO personDTO,
                           BindingResult bindingResult,
                           Model model,
                           Locale locale,
                           Principal principal) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("creator", userService.getPersonDtoByPrincipal(principal));
            ControllerUtil.setFeedback(model, FeedbackType.ERROR, "INVALID_INPUT", locale, messageSource);
        } else {
            try {
                userService.createAndNotifyUser(personDTO, principal);
                ControllerUtil.setFeedback(model, FeedbackType.SUCCESS, "E_MAIL_SUCCESS", locale, messageSource);
                ControllerUtil.setFeedback(model, FeedbackType.SUCCESS, "SUCCESS_SAVE", locale, messageSource);
            } catch (MessagingException e) {
                ControllerUtil.setFeedback(model, FeedbackType.ERROR, "E_MAIL_FAILED", locale, messageSource);
                ControllerUtil.setFeedback(model, FeedbackType.SUCCESS, "SUCCESS_SAVE", locale, messageSource);
            } catch (RoleViolationException e) {
                ControllerUtil.setFeedback(model, FeedbackType.ERROR, "FAILURE", locale, messageSource);
            } catch (DuplicateUserCreationException e) {
                ControllerUtil.setFeedback(model, FeedbackType.ERROR, "USER_ALREADY_EXISTS", locale, messageSource);
            }
            model.addAttribute("creator", userService.getPersonDtoByPrincipal(principal));

        }
        return "users";
    }

    @GetMapping("/manage")
    public String showUsersByPrincipalPage(Model model, Principal principal) {
        model.addAttribute("creator", userService.getPersonDtoByPrincipal(principal));
        model.addAttribute("persons", userService.getPersonsCreatedBy(principal));
        return "manage";
    }

    @PreAuthorize("@userServiceImpl.canAccess(principal, T(ee.nipt.domain.Role).ADD_USERS)")
    @PostMapping("/manage")
    public String updateSubordinateAccess(@Valid PersonsDTO personsDTO,
                                          BindingResult bindingResult,
                                          Model model,
                                          Locale locale,
                                          Principal principal) {
        if (bindingResult.hasErrors()) {
            ControllerUtil.setFeedback(model, FeedbackType.ERROR, "INVALID_INPUT", locale, messageSource);
        } else {
            try {
                userService.updateUsersRolesByPrincipal(personsDTO, principal, model, locale);
                ControllerUtil.setFeedback(model, FeedbackType.SUCCESS, "SUCCESS_SAVE", locale, messageSource);
            } catch (RoleViolationException | UnauthorizedUserManipulationException | RuntimeException e) {
                ControllerUtil.setFeedback(model, FeedbackType.ERROR, "FAILURE", locale, messageSource);
            }
        }

        model.addAttribute("creator", userService.getPersonDtoByPrincipal(principal));
        model.addAttribute("persons", userService.getPersonsCreatedBy(principal));
        return "manage";
    }

    @GetMapping("/settings")
    public String getSettingsPage(@ModelAttribute("settings") SettingsDTO settings, Model model, Principal principal) {
        PersonDTO person = userService.getPersonDtoByPrincipal(principal);
        model.addAttribute("person", person);
        return "settings";
    }

    @PostMapping("/settings")
    public String changePassword(@ModelAttribute("settings") SettingsDTO settings,
                                 Model model,
                                 Principal principal,
                                 Locale locale) {
        try {
            userService.updatePassword(principal, settings);
            ControllerUtil.setFeedback(model, FeedbackType.SUCCESS, "PASSWORD_CHANGED", locale, messageSource);
        } catch (WrongPasswordException e) {
            ControllerUtil.setFeedback(model, FeedbackType.ERROR, "WRONG_PASSWORD", locale, messageSource);
        } catch (PasswordNotMatchException e) {
            ControllerUtil.setFeedback(model, FeedbackType.ERROR, "PASSWORD_MUST_MATCH", locale, messageSource);
        }
        model.addAttribute("person", userService.getPersonDtoByPrincipal(principal));
        return "settings";
    }

    @PostMapping("/lang")
    public String changeLanguage(@ModelAttribute("settings") SettingsDTO settings,
                                 @RequestParam(value = "lang", defaultValue = "et") String lang,
                                 Principal principal) {
        userService.updateLang(principal, lang);
        return "redirect:settings?lang=" + lang;
    }
}