package GAPDetector.json.outputDTO.smells.CH;

import GAPDetector.json.outputDTO.relations.EntityDependencyRelationDetail;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class CyclicHierarchyStructureDetail {

    private List<EntityDependencyRelationDetail> superType2subType;
    private List<EntityDependencyRelationDetail> subType2superType;

}
