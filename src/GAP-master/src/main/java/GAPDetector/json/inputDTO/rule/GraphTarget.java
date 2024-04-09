
package GAPDetector.json.inputDTO.rule;

import lombok.Data;

import java.util.List;

@Data
public class GraphTarget {
    private List<String> edgeRelations;
    private EdgeLevel edgeLevel;
    private String vertexLevel;

}