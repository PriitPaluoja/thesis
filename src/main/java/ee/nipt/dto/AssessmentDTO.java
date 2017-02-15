package ee.nipt.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AssessmentDTO {

    @NotNull
    Boolean isNormal;

    @NotNull
    String sampleNumber;

    @NotNull
    Integer dataId;

    @NotNull
    Integer runId;

    @NotNull
    @Size(max = 5000)
    String text;
}