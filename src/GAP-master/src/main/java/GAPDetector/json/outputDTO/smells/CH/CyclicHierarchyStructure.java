package GAPDetector.json.outputDTO.smells.CH;

import GAPDetector.entities.EntityIdentifier;
import GAPDetector.json.outputDTO.relations.EntityDependencyRelationDetail;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@AllArgsConstructor
@Data
public class CyclicHierarchyStructure {
    private Integer id;
    private String mode;
    private EntityIdentifier superType;
    private EntityIdentifier subType;
    private List<EntityDependencyRelationDetail> details;

    public CyclicHierarchyStructure(Integer id, EntityIdentifier superType, EntityIdentifier subType, List<EntityDependencyRelationDetail> details) {
        this.id = id;
        this.superType = superType;
        this.subType = subType;
        this.details = details;
    }

    public CyclicHierarchyStructure(String mode, EntityIdentifier superType, EntityIdentifier subType, List<EntityDependencyRelationDetail> details) {
        this.mode = mode;
        this.superType = superType;
        this.subType = subType;
        this.details = details;
    }
}
