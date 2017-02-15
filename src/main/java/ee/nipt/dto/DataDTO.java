package ee.nipt.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DataDTO {

    private Integer dataId;

    private String rawData;

    private String barcode;

    private Integer snp;

    private Integer informativeSnp;

    private Integer rawReads;

    private Double pcrRedundancy;

    private Double uniqueReadsPerSNP;

    private String lot;

    private LocalDate dnaSeparation;
}
