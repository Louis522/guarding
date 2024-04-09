package GAPDetector.json.outputDTO.relations;

import GAPDetector.json.inputDTO.dependencyModel.Location;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class EntityDependencyRelationDetail {
    private Integer fromEntityId;
    private String fromEntity;
    private String fromEntityType;
    private String fromEntityFile;
    private String fromEntityModifier;
    private Location fromEntityLocation;
    private Integer fromParentId;
    private String relationType;
    private Integer toEntityId;
    private String toEntity;
    private String toEntityType;
    private String toEntityFile;
    private String toEntityModifier;
    private Location toEntityLocation;
    private Integer toParentId;

    private Integer mode;
    private Integer intrusiveType;
    private Location depLocation;

    public EntityDependencyRelationDetail(String fromEntity, String fromEntityFile, String relationType, String toEntity, String toEntityFile, Location depLocation) {
        this.fromEntity = fromEntity;
        this.fromEntityFile = fromEntityFile;
        this.relationType = relationType;
        this.toEntity = toEntity;
        this.toEntityFile = toEntityFile;
        this.depLocation = depLocation;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        EntityDependencyRelationDetail other = (EntityDependencyRelationDetail) o;
        EqualsBuilder builder = new EqualsBuilder();
        builder.append(fromEntityId, other.fromEntityId)
                .append(relationType, other.relationType)
                .append(toEntityId, other.toEntityId);
        return builder.isEquals();
    }

    @Override
    public int hashCode() {
        HashCodeBuilder builder = new HashCodeBuilder(17, 37);
        builder.append(fromEntityId)
                .append(relationType)
                .append(toEntityId);
        return builder.toHashCode();
    }
}
