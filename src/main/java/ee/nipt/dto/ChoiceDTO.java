package ee.nipt.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChoiceDTO {

    @NotNull
    private String patientId;

    private List<String> ids;

    @NotNull
    private String selectedId;

    private String examinationId;

    private Integer dataId;

    private Integer runId;

    public ChoiceDTO(String patientId, List<String> ids) {
        this.patientId = patientId;
        this.ids = ids;
    }
}