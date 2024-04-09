package GAPDetector.json.outputDTO.smells.CD;

import GAPDetector.entities.EntityIdentifier;
import GAPDetector.json.outputDTO.relations.SingleDependencyRelationDetailWithClassInfo;
import lombok.Data;
import org.apache.commons.lang3.mutable.MutableInt;

import java.util.HashMap;
import java.util.List;

@Data
public class   MutualPackageDependencyRelation {
    private EntityIdentifier sourcePackage;
    private EntityIdentifier targetPackage;
    private HashMap<String, MutableInt> dependencyRelationCount;
    private List<SingleDependencyRelationDetailWithClassInfo> entityDependencies;

}
