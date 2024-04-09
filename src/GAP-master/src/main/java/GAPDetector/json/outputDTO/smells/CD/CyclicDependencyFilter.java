package GAPDetector.json.outputDTO.smells.CD;

import GAPDetector.json.outputDTO.relations.SingleDependencyRelationDetailWithClassInfo;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.HashMap;
import java.util.List;

@AllArgsConstructor
@Data
public class CyclicDependencyFilter {
    private HashMap<String, List<List<SingleDependencyRelationDetailWithClassInfo>>> packageDependencyRelationCells;
}
