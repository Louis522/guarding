package GAPDetector.entities;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;


public class PackageEntity extends AbstractEntity {

    @Getter
    private List<FileEntity> files = new ArrayList<>();
    @Getter
    private Integer level;
    @Getter
    private HashSet<PackageEntity> childrenPackages = new HashSet<>();
    @Setter
    @Getter
    private PackageEntity fatherPackage;
    @Setter
    @Getter
    private boolean topPackage;

    public PackageEntity(AbstractEntity abstractEntity) {
        super(abstractEntity.id, abstractEntity.qualifiedName, abstractEntity.file, abstractEntity.category,
                abstractEntity.modifier, abstractEntity.rawType, abstractEntity.isIntrusive,
                abstractEntity.isDecoupling, abstractEntity.ownership, abstractEntity.location, abstractEntity.parentId);
        this.level = this.qualifiedName.split("\\.").length;
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        PackageEntity other = (PackageEntity) o;
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
