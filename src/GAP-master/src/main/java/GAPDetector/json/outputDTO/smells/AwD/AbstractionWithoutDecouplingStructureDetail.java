package GAPDetector.json.outputDTO.smells.AwD;

import GAPDetector.json.outputDTO.relations.EntityDependencyRelationDetail;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class AbstractionWithoutDecouplingStructureDetail {

    private List<EntityDependencyRelationDetail> clientClass2superType;
    private List<EntityDependencyRelationDetail> clientClass2subType;

}
