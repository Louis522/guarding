package GAPDetector.json.outputDTO.analyzers;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

@AllArgsConstructor
@Data
public class MHCounter {
    private String startModifier;
    private String endModifier;
    private Integer count;


    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        MHCounter other = (MHCounter) o;
        EqualsBuilder builder = new EqualsBuilder();
        builder.append(startModifier, other.startModifier)
                .append(endModifier, other.endModifier);
        return builder.isEquals();
    }

    @Override
    public int hashCode() {
        HashCodeBuilder builder = new HashCodeBuilder(17, 37);
        builder.append(startModifier)
                .append(endModifier);
        return builder.toHashCode();
    }


}
