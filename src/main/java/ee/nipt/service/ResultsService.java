package ee.nipt.service;

import com.lowagie.text.DocumentException;
import ee.nipt.dto.AssessmentDTO;
import ee.nipt.dto.ChoiceDTO;
import ee.nipt.dto.DownloadDTO;
import ee.nipt.dto.ReportDTO;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.Principal;

public interface ResultsService {
    /**
     * Check if report is present in the database
     *
     * @param dto fields are used to query
     * @return <code>true</code> if present else <code>false</code>
     */
    boolean reportExists(ChoiceDTO dto);

    /**
     * Get {@link ReportDTO} by dto
     *
     * @param dto fields are used to query and fill {@link ReportDTO}
     * @return {@link ReportDTO} by dto
     */
    ReportDTO getReport(Principal principal, ChoiceDTO dto);

    /**
     * Get {@link ReportDTO} by dto
     *
     * @param dto fields are used to query and fill {@link ReportDTO}
     * @return {@link ReportDTO} by dto
     */
    ReportDTO getReport(Principal principal, DownloadDTO dto);

    /**
     * Update dto missing fields with data available from database by assigned dto fields
     *
     * @param dto to be updated
     * @return updated {@link ChoiceDTO}
     */
    ChoiceDTO assignValuesFromDatabase(ChoiceDTO dto);

    /**
     * Log report into the database
     * Create local copy of PDF
     * Save assessment into the database
     * Notify the person bound to this examination with e-mail
     *
     * @param assessor is the person who has created assessment
     * @param dto      is carrying assessment information to be inserted into the database
     */
    void saveAssessment(Principal assessor, AssessmentDTO dto) throws MessagingException, IOException, DocumentException;

    /**
     * Create and append PDF to {@link HttpServletResponse}
     *
     * @param response to where PDF is added
     * @param dto      from which PDF is created from
     */
    void appendPDF(HttpServletResponse response, ReportDTO dto) throws IOException, DocumentException;
}
