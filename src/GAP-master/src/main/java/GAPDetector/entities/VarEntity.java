package GAPDetector.entities;


import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.HashSet;

public class VarEntity extends AbstractEntity {
    @Getter
    private HashSet<FuncImplEntity> accessedFuncImpls = new HashSet<>();
    @Getter
    @Setter
    private AbstractEntity originEntity = null;

    @Getter
    @Setter
    private HashSet<String> entitySet = new HashSet<>();

    @Getter
    @Setter
    private String extendVarType;

    public VarEntity(AbstractEntity abstractEntity) {
        super(abstractEntity.id, abstractEntity.qualifiedName, abstractEntity.file, abstractEntity.category,
                abstractEntity.modifier, abstractEntity.rawType, abstractEntity.isIntrusive,
                abstractEntity.isDecoupling, abstractEntity.ownership, abstractEntity.location, abstractEntity.parentId);
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        VarEntity other = (VarEntity) o;
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
