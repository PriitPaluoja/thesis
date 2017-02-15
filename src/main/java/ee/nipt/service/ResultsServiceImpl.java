package ee.nipt.service;

import com.lowagie.text.DocumentException;
import ee.nipt.db.ExaminationRepository;
import ee.nipt.db.PersonRepository;
import ee.nipt.domain.Examination;
import ee.nipt.domain.Person;
import ee.nipt.dto.*;
import ee.nipt.util.ResultsUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.xhtmlrenderer.layout.SharedContext;
import org.xhtmlrenderer.pdf.ITextRenderer;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLDecoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Principal;
import java.util.List;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Slf4j
public class ResultsServiceImpl implements ResultsService {
    private final ExaminationRepository examinationRepository;
    private final PersonRepository personRepository;
    private final MailService mailService;
    private final TemplateEngine templateEngine;


    @Override
    public boolean reportExists(ChoiceDTO dto) {
        dto = ResultsUtil.populateChoiceDtoIdFields(dto);
        return dto != null && examinationRepository.findOne(dto.getExaminationId()).getExpertAssessment() != null;
    }

    @Override
    public ReportDTO getReport(Principal principal, ChoiceDTO dto) {
        log.info("{} is viewing report of {} {} {}", principal.getName(), dto.getExaminationId(), dto.getDataId(), dto.getRunId());
        dto = ResultsUtil.populateChoiceDtoIdFields(dto);
        if (dto == null) return new ReportDTO();
        return ResultsUtil.createReportDto(
                examinationRepository.findOne(dto.getExaminationId()),
                dto.getDataId(),
                dto.getRunId()
        );
    }

    @Override
    public ReportDTO getReport(Principal principal, DownloadDTO dto) {
        log.info("{} is downloading report of {} {} {}", principal.getName(), dto.getSampleNumber(), dto.getDataId(), dto.getRunId());
        return ResultsUtil.createReportDto(
                examinationRepository.findOne(dto.getSampleNumber()),
                dto.getDataId(),
                dto.getRunId()
        );
    }

    @Override
    public ChoiceDTO assignValuesFromDatabase(ChoiceDTO dto) {
        List<Examination> examinations = examinationRepository.findAllByPatientId(dto.getPatientId());
        return ResultsUtil.createChoiceDTO(dto.getPatientId(), examinations);
    }

    @Override
    public void saveAssessment(Principal assessor, AssessmentDTO dto) throws MessagingException, IOException, DocumentException {
        Examination examination = examinationRepository.getOne(dto.getSampleNumber());
        Person expert = personRepository.findOneByEmail(assessor.getName()).get();

        examination = ResultsUtil.appendAssessment(examination, dto.getText(), dto.getIsNormal(), expert);
        // Finally save assessment
        examinationRepository.save(examination);
        log.info("{} saved assessment of {}", assessor.getName(), dto.getSampleNumber());

        try {
            Person doctor = personRepository.findOneByEmail(examination.getEmail()).get();
            mailService.emailNotifyResults(examination, assessor, doctor);
        } catch (RuntimeException e) {
            log.error("{} saved assessment, notifying of results {} failed!", assessor.getName(), dto.getSampleNumber());
        }
    }

    @Override
    public void appendPDF(HttpServletResponse response, ReportDTO dto) throws IOException, DocumentException {
        savePDF(dto, response.getOutputStream());
        response.flushBuffer();
    }


    private ByteArrayInputStream createPdfInputStream(ReportDTO reportDTO, DataDTO dataDTO, RunDTO runDTO) throws DocumentException, IOException {
        final Context ctx = new Context();

        ctx.setVariable("examination", reportDTO);
        ctx.setVariable("data", dataDTO);
        ctx.setVariable("run", runDTO);


        DownloadDTO downloadDTO = new DownloadDTO();
        downloadDTO.setIsPDf(true);
        ctx.setVariable("download", downloadDTO);

        final String htmlContent = templateEngine.process("report", ctx);

        ITextRenderer renderer = new ITextRenderer();

        SharedContext sharedContext = renderer.getSharedContext();
        sharedContext.setPrint(true);
        sharedContext.setInteractive(false);
        sharedContext.getTextRenderer().setSmoothingThreshold(0);

        renderer.setDocumentFromString(htmlContent);
        renderer.layout();

        ByteArrayOutputStream os = new ByteArrayOutputStream();
        renderer.createPDF(os);
        os.close();
        return new ByteArrayInputStream(os.toByteArray());
    }

    private void savePDF(ReportDTO dto, OutputStream outputStream) throws IOException, DocumentException {
        InputStream is = createPdfInputStream(dto, dto.getData(), dto.getRun());
        IOUtils.copy(is, outputStream);
        is.close();
        outputStream.close();
    }
}

