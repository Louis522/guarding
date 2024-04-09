package GAPDetector.json.outputDTO.smells.CD;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.mutable.MutableInt;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Data
@NoArgsConstructor
public class PackageDependencyRelationCell {
    private String sourcePackage;
    private List<String> targetPackage = new ArrayList<>();
    private Integer outDegree = 0;
    private HashMap<String, MutableInt> dependencyRelationCountFromSrcToAllDest;
    private List<MutualPackageDependencyRelation> mutualPackageDependencyRelations = new ArrayList<>();

    public PackageDependencyRelationCell(String sourcePackage) {
        this.sourcePackage = sourcePackage;
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        PackageDependencyRelationCell other = (PackageDependencyRelationCell) o;
        EqualsBuilder builder = new EqualsBuilder();
        builder.append(sourcePackage, other.sourcePackage);
        return builder.isEquals();
    }
}
