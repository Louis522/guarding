package GAPDetector.entities;

import GAPDetector.json.inputDTO.dependencyModel.Location;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

@AllArgsConstructor
@Data
public class EntityIdentifier {
    private String object;
    private String file;
    private String modifier;
    private String rawType;
    private Boolean isIntrusive;
    private Integer MCRank;
    private Location location;

    public EntityIdentifier(String object, Boolean isIntrusive, Location location) {
        this.object = object;
        this.isIntrusive = isIntrusive;
        this.location = location;
    }

    public EntityIdentifier(String object, String file, String modifier, Boolean isIntrusive, Location location) {
        this.object = object;
        this.file = file;
        this.modifier = modifier;
        this.isIntrusive = isIntrusive;
        this.location = location;
    }

    public EntityIdentifier(TypeEntity typeEntity, Boolean isIntrusive, Location location) {
        this.object = typeEntity.qualifiedName;
        this.file = typeEntity.file;
        this.isIntrusive = isIntrusive;
        this.location = location;
    }

    public EntityIdentifier(FuncImplEntity funcImplEntity, Boolean isIntrusive, Location location) {
        this.object = funcImplEntity.qualifiedName;
        this.file = funcImplEntity.file;
        this.modifier = funcImplEntity.modifier;
        this.isIntrusive = isIntrusive;
        this.location = location;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        EntityIdentifier other = (EntityIdentifier) o;
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
