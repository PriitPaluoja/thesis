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
public class DbData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer dataId;

    private String rawData;

    private String barcode;

    private Integer rawReads;

    private Integer snp;

    private Double pcrRedundancy;

    private Integer informativeSnp;

    private Double uniqueReadsPerSnp;

    private String lot;

    private LocalDate dnaSeparation;

    @OneToMany(
            cascade = CascadeType.ALL,
            fetch = FetchType.EAGER,
            orphanRemoval = true
    )
    @JoinColumn(name = "data_id")
    private Set<Run> runs = new HashSet<>();
}