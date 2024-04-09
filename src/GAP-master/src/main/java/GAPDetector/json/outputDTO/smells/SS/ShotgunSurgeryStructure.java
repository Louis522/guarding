package GAPDetector.json.outputDTO.smells.SS;

import GAPDetector.entities.EntityIdentifier;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.HashSet;

@AllArgsConstructor
@Data
public class ShotgunSurgeryStructure {
    private Integer id;
    private String file;
    private Integer MCRank;

    private String object;
    private Boolean isIntrusive;
    private Integer CC;
    private Integer CM;
    private Integer FANOUT;
    private HashSet<EntityIdentifier> callClassSet;
    private HashSet<EntityIdentifier> callBySet;
    private HashSet<EntityIdentifier> callByClassSet;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        ShotgunSurgeryStructure other = (ShotgunSurgeryStructure) o;
        EqualsBuilder builder = new EqualsBuilder();
        builder.append(object, other.object)
                .append(file, other.file);
        return builder.isEquals();
    }

    @Override
    public int hashCode() {
        HashCodeBuilder builder = new HashCodeBuilder(17, 37);
        builder.append(object)
                .append(file);
        return builder.toHashCode();
    }
}
