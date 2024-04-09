package GAPDetector.json.outputDTO.relations;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.HashSet;

@AllArgsConstructor
@Data
@NoArgsConstructor
public class PackageDependencyRelationDetail {
    private Integer fromPackageId;
    private String fromPackage;
    private Integer toPackageId;
    private String toPackage;

    private HashSet<EntityDependencyRelationDetailWithClass> entityDependencyRelationDetailWithClasses;
    private HashSet<EntityDependencyRelationDetail> entityDependencyRelationDetails;

    public PackageDependencyRelationDetail(Integer fromPackageId, Integer toPackageId) {
        this.fromPackageId = fromPackageId;
        this.toPackageId = toPackageId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        PackageDependencyRelationDetail other = (PackageDependencyRelationDetail) o;
        EqualsBuilder builder = new EqualsBuilder();
        builder.append(fromPackageId, other.fromPackageId)
                .append(toPackageId, other.toPackageId);
        return builder.isEquals();
    }

    @Override
    public int hashCode() {
        HashCodeBuilder builder = new HashCodeBuilder(17, 37);
        builder.append(fromPackageId)
                .append(toPackageId);
        return builder.toHashCode();
    }
}
