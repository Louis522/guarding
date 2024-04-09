package GAPDetector.csv.util.dto;

import lombok.Data;

@Data
public class MeasureResultDTO {
    private Integer id;
    private String qualifiedName;
    private String type;
    private double score;
    private int rank;

}
