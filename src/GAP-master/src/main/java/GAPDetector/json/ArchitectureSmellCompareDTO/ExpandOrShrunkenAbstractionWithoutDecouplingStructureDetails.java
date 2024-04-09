package GAPDetector.json.ArchitectureSmellCompareDTO;

import GAPDetector.json.outputDTO.relations.EntityDependencyRelationDetail;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class ExpandOrShrunkenAbstractionWithoutDecouplingStructureDetails {
    private Integer count_expandedClientClass2superType = null;
    private List<EntityDependencyRelationDetail> expandedClientClass2superType= null;
    private Integer count_shrunkenClientClass2superType= null;
    private List<EntityDependencyRelationDetail> shrunkenClientClass2superType= null;
    private Integer count_expandedClientClass2subType = null;
    private List<EntityDependencyRelationDetail> expandedClientClass2subType = null;
    private Integer count_shrunkenClientClass2subType= null;
    private List<EntityDependencyRelationDetail> shrunkenClientClass2subType= null;
}
