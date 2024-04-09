package GAPDetector.json.outputDTO.smells.MH;

import GAPDetector.entities.EntityIdentifier;
import GAPDetector.json.outputDTO.relations.EntityDependencyRelationDetail;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@Data
public class MultipathHierarchyStructure {
    private Integer id;
    private String mode;
    private EntityIdentifier start;
    private EntityIdentifier end;
    private List<List<EntityDependencyRelationDetail>> multipath;

    public MultipathHierarchyStructure(Integer id, EntityIdentifier start, EntityIdentifier end, List<List<EntityDependencyRelationDetail>> multipath) {
        this.id = id;
        this.start = start;
        this.end = end;
        this.multipath = multipath;
    }

    public MultipathHierarchyStructure(String mode, EntityIdentifier start, EntityIdentifier end, List<List<EntityDependencyRelationDetail>> multipath) {
        this.mode = mode;
        this.start = start;
        this.end = end;
        this.multipath = multipath;
    }
}
