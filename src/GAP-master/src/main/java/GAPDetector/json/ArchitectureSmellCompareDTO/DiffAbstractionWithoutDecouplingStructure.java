package GAPDetector.json.ArchitectureSmellCompareDTO;

import GAPDetector.entities.EntityIdentifier;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class DiffAbstractionWithoutDecouplingStructure {
    private Integer preId;

    private Integer afterId;
    private EntityIdentifier superType;
    private EntityIdentifier clientClass;
    private EntityIdentifier subType;
    private ExpandOrShrunkenAbstractionWithoutDecouplingStructureDetails diffDetails = null;

    private SameAbstractionWithoutDecouplingStructureDetails sameDetails = null;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        DiffAbstractionWithoutDecouplingStructure other = (DiffAbstractionWithoutDecouplingStructure) o;
        EqualsBuilder builder = new EqualsBuilder();
        builder.append(superType.getObject(), other.superType.getObject())
                .append(clientClass.getObject(), other.clientClass.getObject())
                .append(subType.getObject(), other.subType.getObject());
        return builder.isEquals();
    }

    @Override
    public int hashCode() {
        HashCodeBuilder builder = new HashCodeBuilder(17, 37);
        builder.append(clientClass.getObject())
                .append(superType.getObject())
                .append(subType.getObject());
        return builder.toHashCode();
    }
}
