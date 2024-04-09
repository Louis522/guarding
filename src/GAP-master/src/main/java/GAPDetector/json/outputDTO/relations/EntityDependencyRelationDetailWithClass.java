package GAPDetector.json.outputDTO.relations;

import GAPDetector.entities.EntityIdentifier;
import GAPDetector.json.inputDTO.dependencyModel.Location;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.HashSet;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class EntityDependencyRelationDetailWithClass {
    private EntityIdentifier fromClass;
    private Integer fromClassId;

    private Location fromLocation;
    private EntityIdentifier toClass;
    private Integer toClassId;

    private Location toLocation;
    private List<EntityDependencyRelationDetail> entityDependencyRelationDetails;

    public EntityDependencyRelationDetailWithClass(Integer fromClassId, Integer toClassId){
        this.fromClassId = fromClassId;
        this.toClassId = toClassId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        EntityDependencyRelationDetailWithClass other = (EntityDependencyRelationDetailWithClass) o;
        EqualsBuilder builder = new EqualsBuilder();
        builder.append(fromClassId, other.fromClassId)
                .append(toClassId, other.toClassId);
        return builder.isEquals();
    }

    @Override
    public int hashCode() {
        HashCodeBuilder builder = new HashCodeBuilder(17, 37);
        builder.append(fromClassId)
                .append(toClassId);
        return builder.toHashCode();
    }
}
