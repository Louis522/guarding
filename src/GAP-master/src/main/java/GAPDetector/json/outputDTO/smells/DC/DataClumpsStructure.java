package GAPDetector.json.outputDTO.smells.DC;

import GAPDetector.entities.EntityIdentifier;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.Set;

@AllArgsConstructor
@Data
public class DataClumpsStructure {
    private Integer id;
    private Set<String> sameParameters;
    private Integer sameParametersCount;
    private Set<EntityIdentifier> dataClumpsFunctionSet;
    private Integer dataClumpsFunctionSetCount;


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        DataClumpsStructure other = (DataClumpsStructure) o;
        EqualsBuilder builder = new EqualsBuilder();
        builder.append(sameParameters, other.sameParameters);
        return builder.isEquals();
    }

    @Override
    public int hashCode() {
        HashCodeBuilder builder = new HashCodeBuilder(17, 37);
        builder.append(sameParameters);
        return builder.toHashCode();
    }
}
