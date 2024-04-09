package GAPDetector.json.outputDTO.analyzers;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class GAPAnalyzer {
    private String project;
    private Integer Total;
    private Integer AWD;
    private Integer CD;
    private Integer CH;
    private Integer DC;
    private Integer FE;
    private Integer MH;
    private Integer SS;
}
