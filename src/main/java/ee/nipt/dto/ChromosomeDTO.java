package ee.nipt.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChromosomeDTO {

    private Integer chromosomeNumber;

    private Double score1;

    private Double score2;

    private Double score3;

    private Double score4;

    private String plot;
}