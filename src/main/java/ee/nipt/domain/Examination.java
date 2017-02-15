package ee.nipt.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Examination {

    private String patientId;

    @Id
    private String sampleNumber;

    private String creator;

    private String physicianId;

    private Integer pregnancyLength;

    private Boolean hasOneFetus;

    private Double bmi;

    private Boolean hasTumor;

    private Boolean hasIvf;

    private Boolean hasHsct;

    private String nationality = "EE";

    private String email;

    private String expertId;

    private LocalDate patientDateOfBirth;

    private LocalDate dateOfExamination;

    private Integer numberOfFetuses;

    private LocalDate dateOfSampling;

    private LocalDate sampleArrivalDate;

    private Boolean wasPrognosisTrue;

    private String expertAssessment;

    @Enumerated(EnumType.STRING)
    private AssessmentDecision decision;

    @OneToMany(
            cascade = CascadeType.ALL,
            fetch = FetchType.EAGER,
            orphanRemoval = true
    )
    @JoinColumn(name = "sample_number")
    private Set<DbData> datas = new HashSet<>();
}
