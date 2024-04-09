package GAPDetector.json.outputDTO.relations;

import GAPDetector.entities.EntityIdentifier;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class SingleDependencyRelationDetailWithClassInfo {
    private EntityIdentifier fromClass;
    private EntityIdentifier toClass;
    private EntityDependencyRelationDetail entityDependencyRelationDetail;


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        SingleDependencyRelationDetailWithClassInfo other = (SingleDependencyRelationDetailWithClassInfo) o;
        EqualsBuilder builder = new EqualsBuilder();
        builder.append(fromClass, other.fromClass)
                .append(toClass, other.toClass);
        return builder.isEquals();
    }

    @Override
    public int hashCode() {
        HashCodeBuilder builder = new HashCodeBuilder(17, 37);
        builder.append(fromClass)
                .append(toClass);
        return builder.toHashCode();
    }
}
