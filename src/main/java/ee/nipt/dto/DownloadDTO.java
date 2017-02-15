package ee.nipt.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DownloadDTO {

    private String sampleNumber;

    private Integer dataId;

    private Integer runId;

    private Boolean isPDf = false;
}