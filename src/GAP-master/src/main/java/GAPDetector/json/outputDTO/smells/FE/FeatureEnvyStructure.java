package GAPDetector.json.outputDTO.smells.FE;

import GAPDetector.entities.EntityIdentifier;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.HashSet;

@AllArgsConstructor
@Data
public class FeatureEnvyStructure {
    private Integer id;
    private String file;
    private String object;
    private Boolean isIntrusive;

    private String originClass;
    private String targetClass;
    private Integer ATFD;
    private Double LAA;
    private Integer FDP;
    private HashSet<EntityIdentifier> funcForeignAttributesSet;

    public FeatureEnvyStructure(String file, String object, Boolean isIntrusive, String targetClass) {
        this.file = file;
        this.object = object;
        this.isIntrusive = isIntrusive;
        this.targetClass = targetClass;

    }

    public FeatureEnvyStructure(Integer id, String file, String object, Integer ATFD, Double LAA, Integer FDP, HashSet<EntityIdentifier> funcForeignAttributesSet) {
        this.id = id;
        this.file = file;
        this.object = object;
        this.ATFD = ATFD;
        this.LAA = LAA;
        this.FDP = FDP;
        this.funcForeignAttributesSet = funcForeignAttributesSet;

    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        FeatureEnvyStructure other = (FeatureEnvyStructure) o;
        EqualsBuilder builder = new EqualsBuilder();
        builder.append(object, other.object)
                .append(originClass, other.originClass)
                .append(targetClass, other.targetClass);
        return builder.isEquals();
    }

    @Override
    public int hashCode() {
        HashCodeBuilder builder = new HashCodeBuilder(17, 37);
        builder.append(object)
                .append(originClass)
                .append(targetClass);
        return builder.toHashCode();
    }
}
