package ee.nipt.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RunDTO {

    private Integer runId;

    private String testType;

    private LocalDate dateOfRun;

    private Double qScore;

    private String gender;

    private Double fetalFraction;

    private List<ChromosomeDTO> chromosomes = new ArrayList<>();
}
