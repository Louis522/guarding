package GAPDetector.json.outputDTO.analyzers;

import GAPDetector.entities.EntityIdentifier;
import GAPDetector.json.outputDTO.smells.AwD.AbstractionWithoutDecouplingStructureDetail;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.HashMap;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class RebuildAbstractionWithoutDecouplingStructure {
    private Integer diffClientCount;
    private EntityIdentifier superType;
    private EntityIdentifier subType;
    private HashMap<EntityIdentifier, AbstractionWithoutDecouplingStructureDetail> clientClass2details = new HashMap<>();

    public RebuildAbstractionWithoutDecouplingStructure(EntityIdentifier superType, EntityIdentifier subType) {
        this.superType = superType;
        this.subType = subType;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        RebuildAbstractionWithoutDecouplingStructure other = (RebuildAbstractionWithoutDecouplingStructure) o;
        EqualsBuilder builder = new EqualsBuilder();
        builder.append(superType.getObject(), other.superType.getObject())
                .append(subType.getObject(), other.subType.getObject());
        return builder.isEquals();
    }

    @Override
    public int hashCode() {
        HashCodeBuilder builder = new HashCodeBuilder(17, 37);
        builder.append(superType.getObject())
                .append(subType.getObject());
        return builder.toHashCode();
    }
}
