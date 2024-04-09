package GAPDetector.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class Vertex {
    private Integer id;
    private String object;
    private String file;


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        Vertex other = (Vertex) o;
        EqualsBuilder builder = new EqualsBuilder();
        builder.append(id, other.id);
        return builder.isEquals();
    }

    @Override
    public int hashCode() {
        HashCodeBuilder builder = new HashCodeBuilder(17, 37);
        builder.append(id);
        return builder.toHashCode();
    }
}
