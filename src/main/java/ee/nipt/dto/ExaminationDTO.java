package ee.nipt.dto;


import ee.nipt.constraint.NationalIdentificationNumber;
import ee.nipt.domain.AssessmentDecision;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Email;

import javax.validation.constraints.*;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExaminationDTO {

    @NotNull
    @NationalIdentificationNumber
    private String patientId;

    @NotNull
    private String sampleNumber;

    private String physicianId;

    @NotNull
    @Min(0)
    private Integer pregnancyLength;

    @NotNull
    @AssertTrue
    private Boolean hasOneFetus;

    @NotNull
    @Min(0)
    private Double bmi;

    @NotNull
    @AssertFalse
    private Boolean hasTumor;

    @NotNull
    @AssertFalse
    private Boolean hasIvf;

    @NotNull
    @AssertFalse
    private Boolean hasHsct;

    @NotNull
    @Size(min = 2, max = 2)
    private String nationality = "EE";

    @NotNull
    @Email
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
}