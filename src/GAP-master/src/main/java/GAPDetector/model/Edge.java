package GAPDetector.model;

import GAPDetector.json.outputDTO.relations.EntityDependencyRelationDetail;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.jgrapht.graph.DefaultEdge;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Edge extends DefaultEdge {
    private Integer sourceId;
    private Integer targetId;
    private String sourceType;
    private String targetType;
    private String source;
    private Boolean sourceIsIntrusive;
    private String sourceFile;

    private String target;
    private Boolean targetIsIntrusive;
    private String targetFile;
    private String label;
    private String sourcePackage;
    private String targetPackage;
    private EntityDependencyRelationDetail entityDependencyRelationDetail;
    private List<EntityDependencyRelationDetail> entityDependencyRelationClassDetails;
    private List<EntityDependencyRelationDetail> entityDependencyRelationPackageDetails;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        Edge other = (Edge) o;
        EqualsBuilder builder = new EqualsBuilder();
        builder.append(sourceId, other.sourceId)
                .append(targetId, other.targetId)
                .append(label, label);
        return builder.isEquals();
    }

    @Override
    public int hashCode() {
        HashCodeBuilder builder = new HashCodeBuilder(17, 37);
        builder.append(sourceId).append(targetId).append(label);
        return builder.toHashCode();
    }

    @Override
    public String toString() {
        return "(" + getSource() + " : " + getTarget() + " : " + label + ")";
    }
}