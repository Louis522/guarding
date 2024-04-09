package GAPDetector.json.outputDTO.smells.CD;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.apache.commons.lang3.mutable.MutableInt;

import java.util.HashMap;
import java.util.HashSet;

@AllArgsConstructor
@Data
public class CyclicDependencyStructure {
    private Integer id;

    private HashSet<String> modules;
    private Integer moduleCount;
    private HashMap<String, MutableInt> dependencyRelationCountTotal;
    private HashSet<PackageDependencyRelationCell> packageDependencyRelationCells;
    private HashSet<String> cyclicDependencyModulesIsIntrusive;
    private Integer cyclicDependencyModulesIsIntrusiveCount;

}
