package GAPDetector.entities;

import GAPDetector.json.inputDTO.dependencyModel.Location;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class AbstractEntity implements Serializable {

    private static final long serialVersionUID = -3009742709138514193L;
    public Integer id;
    public String qualifiedName;
    public String file;
    public String category;
    public String modifier;
    public String rawType;
    public Boolean isIntrusive;
    public Boolean isDecoupling;
    public String ownership;
    public Location location;
    public Integer parentId;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        AbstractEntity other = (AbstractEntity) o;
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