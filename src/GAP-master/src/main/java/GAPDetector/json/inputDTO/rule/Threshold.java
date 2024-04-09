package GAPDetector.json.inputDTO.rule;

import lombok.Data;

@Data
public class Threshold {

    private String target;
    private Integer ATFD = 3;
    private Double LAA = 1.0 / 3;
    private Integer FDP = 3;
    private Integer CC = 5;
    private Integer CM = 6;
    private Integer FANOUT = 3;
    private Integer MIN_SAME_PARS = 3;
    private Integer MIN_SAME_PARS_FUNC = 4;

}
