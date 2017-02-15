package ee.nipt.web;

import ee.nipt.dto.ExaminationDTO;
import ee.nipt.exception.DuplicateSampleNumberException;
import ee.nipt.exception.EmailNotPresentExcpetion;
import ee.nipt.feedback.FeedbackType;
import ee.nipt.service.ExaminationService;
import ee.nipt.service.ExaminationServiceImpl;
import ee.nipt.service.UserService;
import ee.nipt.util.ControllerUtil;
import ee.nipt.util.ExaminationUtils;
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

import javax.validation.Valid;
import java.security.Principal;
import java.util.Locale;

@Controller
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class ExaminationController {
    private final ExaminationService examinationService;
    private final MessageSource messageSource;
    private final UserService userService;


    @GetMapping("/examination")
    @PreAuthorize("@userServiceImpl.canAccess(principal, T(ee.nipt.domain.Role).CREATE_EXAMINATION)")
    public String getExaminationPage(@ModelAttribute("examinationDTO") ExaminationDTO examinationDTO,
                                     Model model,
                                     Principal principal) {
        model.addAttribute("iso", ExaminationUtils.getCountryMap());
        model.addAttribute("examinator", userService.getPersonDtoByPrincipal(principal));
        return "examination";
    }

    @PostMapping("/examination")
    @PreAuthorize("@userServiceImpl.canAccess(principal, T(ee.nipt.domain.Role).CREATE_EXAMINATION)")
    public String postExamination(@ModelAttribute("examinationDTO") @Valid ExaminationDTO examination,
                                  BindingResult bindingResult,
                                  Model model,
                                  Principal principal,
                                  Locale locale) {

        model.addAttribute("iso", ExaminationUtils.getCountryMap());
        model.addAttribute("examinator", userService.getPersonDtoByPrincipal(principal));

        if (!bindingResult.hasErrors()) {
            try {
                examinationService.saveExamination(examination, principal);
                ControllerUtil.setFeedback(model, FeedbackType.SUCCESS, "SUCCESS_SAVE", locale, messageSource);
            } catch (EmailNotPresentExcpetion emailNotPresentExcpetion) {
                ControllerUtil.setFeedback(model, FeedbackType.ERROR, "E_MAIL_NOT_FOUND", locale, messageSource);
            } catch (DuplicateSampleNumberException e) {
                ControllerUtil.setFeedback(model, FeedbackType.ERROR, "SAMPLE_ALREADY_EXISTS", locale, messageSource);
            }
        } else {
            ControllerUtil.setFeedback(model, FeedbackType.ERROR, "INVALID_INPUT", locale, messageSource);
        }
        return "examination";
    }
}
