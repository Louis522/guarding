
package GAPDetector.json.inputDTO.rule;

import lombok.Data;

@Data
public class DetectTarget {
    private Cycle cycle;
    private Multipath multipath;
    private FindVertex findVertex;
    private Distance distance;
    private Threshold threshold;

}