package GAPDetector.entities;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class FileEntity extends AbstractEntity {

    public FileEntity(AbstractEntity abstractEntity) {
        super(abstractEntity.id, abstractEntity.qualifiedName, abstractEntity.file, abstractEntity.category,
                abstractEntity.modifier, abstractEntity.rawType, abstractEntity.isIntrusive,
                abstractEntity.isDecoupling, abstractEntity.ownership, abstractEntity.location, abstractEntity.parentId);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        FileEntity other = (FileEntity) o;
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

    public String toString() {
        return qualifiedName;
    }
}
