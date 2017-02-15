package ee.nipt.domain;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Chromosome {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer Id;

    private Integer chNumber;

    private Double score_1;

    private Double score_2;

    private Double score_3;

    private Double score_4;

    private String plot;
}