package ee.nipt.web;

import com.lowagie.text.DocumentException;
import ee.nipt.dto.*;
import ee.nipt.feedback.FeedbackType;
import ee.nipt.service.ResultsService;
import ee.nipt.util.ControllerUtil;
import ee.nipt.util.ResultsUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.security.Principal;
import java.util.Locale;

@Controller
@AllArgsConstructor(onConstructor = @__(@Autowired))
@Slf4j
public class ResultsController {
    private final ResultsService resultsService;
    private final MessageSource messageSource;


    @GetMapping("/assessment")
    @PreAuthorize("@userServiceImpl.canAccess(principal, T(ee.nipt.domain.Role).CREATE_REPORT)")
    public String getAssessmentPage(@ModelAttribute("examination") final ReportDTO report,
                                    @ModelAttribute("data") final DataDTO data,
                                    @ModelAttribute("run") final RunDTO run,
                                    Model model,
                                    Principal principal) {

        if (report.getSampleNumber() == null)
            return "redirect:choice";


        model.addAttribute("examination", report);
        model.addAttribute("data", data);
        model.addAttribute("run", run);
        model.addAttribute("assessment", ResultsUtil.createAssessmentWithIdFieldsFromReport(report));
        return "assessment";
    }

    @GetMapping("/report")
    @PreAuthorize("@userServiceImpl.canAccess(principal, T(ee.nipt.domain.Role).VIEW_REPORT)")
    public String getReportPage(@ModelAttribute("examination") final ReportDTO report,
                                @ModelAttribute("data") final DataDTO data,
                                @ModelAttribute("run") final RunDTO run,
                                Model model,
                                Principal principal) {

        if (report.getSampleNumber() == null)
            return "redirect:choice";

        DownloadDTO downloadDTO = new DownloadDTO();
        downloadDTO.setRunId(run.getRunId());
        downloadDTO.setDataId(data.getDataId());
        downloadDTO.setSampleNumber(report.getSampleNumber());


        model.addAttribute("download", downloadDTO);
        model.addAttribute("examination", report);
        model.addAttribute("data", data);
        model.addAttribute("run", run);
        return "report";
    }

    @GetMapping("/choice")
    public String getChoicePage(@ModelAttribute("choice") ChoiceDTO choice) {
        return "choice";
    }

    @PostMapping("/choice")
    public String postChoice(@Valid ChoiceDTO choice,
                             BindingResult bindingResult,
                             @RequestParam(value = "ac", defaultValue = "null") String action,
                             Model model,
                             RedirectAttributes redirectAttributes,
                             Principal principal,
                             Locale locale) {

        if (action.equals("null")) {
            choice = resultsService.assignValuesFromDatabase(choice);
            model.addAttribute("choice", choice);
            log.info("{} is querying information about {}", principal.getName(), choice.getPatientId());
        } else {
            model.addAttribute("choice", choice);
        }


        if (!bindingResult.hasErrors() && !action.equals("null")) {
            if (!resultsService.reportExists(choice) && action.equals("show")) {
                ControllerUtil.setFeedback(model, FeedbackType.ERROR, "DATA_NOT_PRESENT_YET", locale, messageSource);
                return "choice";
            }

            ReportDTO reportDTO = resultsService.getReport(principal, choice);
            redirectAttributes.addFlashAttribute("examination", reportDTO);
            redirectAttributes.addFlashAttribute("data", reportDTO.getData());
            redirectAttributes.addFlashAttribute("run", reportDTO.getRun());
            return "redirect:" + (action.equals("show") ? "report" : "assessment");
        }
        return "choice";
    }

    @PostMapping("/confirm")
    @PreAuthorize("@userServiceImpl.canAccess(principal, T(ee.nipt.domain.Role).CREATE_REPORT)")
    public String postAssessment(@Valid AssessmentDTO assessment,
                                 BindingResult bindingResult,
                                 Model model,
                                 Locale locale,
                                 Principal principal) {
        if (!bindingResult.hasErrors()) {
            try {
                resultsService.saveAssessment(principal, assessment);
                ControllerUtil.setFeedback(model, FeedbackType.SUCCESS, "SUCCESS_SAVE", locale, messageSource);
                ControllerUtil.setFeedback(model, FeedbackType.SUCCESS, "E_MAIL_SUCCESS", locale, messageSource);
            } catch (MessagingException e) {
                ControllerUtil.setFeedback(model, FeedbackType.SUCCESS, "SUCCESS_SAVE", locale, messageSource);
                ControllerUtil.setFeedback(model, FeedbackType.ERROR, "E_MAIL_FAILED", locale, messageSource);
            } catch (RuntimeException | DocumentException | IOException e) {
                ControllerUtil.setFeedback(model, FeedbackType.ERROR, "FAILURE", locale, messageSource);
                log.error("{} failed to save assessment of {}", principal.getName(), assessment.getSampleNumber());
                e.printStackTrace();
            }
            model.addAttribute("choice", new ChoiceDTO());
            return "choice";
        }
        return "error";
    }

    @PostMapping("/report")
    @PreAuthorize("@userServiceImpl.canAccess(principal, T(ee.nipt.domain.Role).VIEW_REPORT)")
    public void getPDF(@ModelAttribute("download") DownloadDTO dto,
                       Principal principal,
                       HttpServletResponse response) {
        response.setContentType("application/pdf");
        String reportName = dto.getSampleNumber();
        response.setHeader("Content-Disposition", "attachment; filename=\"" + reportName + ".pdf\"");
        try {
            resultsService.appendPDF(response, resultsService.getReport(principal, dto));
        } catch (IOException | DocumentException e) {
            log.error("PDF creation of {} failed!", dto.getSampleNumber());
        }
    }
}