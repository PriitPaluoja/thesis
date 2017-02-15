package ee.nipt.dto;


import ee.nipt.domain.AssessmentDecision;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReportDTO {

    private String patientId;

    private String sampleNumber;

    private String creator;

    private String physicianId;

    private Integer pregnancyLength;

    private Boolean hasOneFetus;

    private Double bmi;

    private Boolean hasTumor;

    private Boolean hasIvf;

    private Boolean hasHsct;

    private String nationality;

    private String email;

    private String expertId;

    private LocalDate patientDateOfBirth;

    private LocalDate dateOfExamination;

    private Integer numberOfFetuses;

    private LocalDate dateOfSampling;

    private LocalDate sampleArrivalDate;

    private Boolean wasPrognosisTrue;

    private String expertAssessment;

    private AssessmentDecision decision;

    private DataDTO data;

    private RunDTO run;
}