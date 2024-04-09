package GAPDetector.json.ArchitectureSmellCompareDTO;

import GAPDetector.json.outputDTO.relations.EntityDependencyRelationDetail;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class SameAbstractionWithoutDecouplingStructureDetails {
    private List<EntityDependencyRelationDetail> sameClientClass2superType;
    private List<EntityDependencyRelationDetail> sameClientClass2subType;
}
