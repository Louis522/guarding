package GAPDetector.entities;

import GAPDetector.json.outputDTO.relations.EntityDependencyRelationDetail;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.HashSet;
import java.util.List;

public class TypeEntity extends AbstractEntity {

    @Getter
    @Setter
    private HashSet<String> entitySet = new HashSet<>();

    @Getter
    @Setter
    private List<List<EntityDependencyRelationDetail>> hierarchyChain = null;

    @Getter
    @Setter
    private EntityIdentifier entityIdentifier;


    @Getter
    @Setter
    private HashSet<EntityIdentifier> superClassIdentifier = new HashSet<>();


    @Getter
    @Setter
    private HashSet<FuncImplEntity> funcImplEntities = new HashSet<>();

    @Getter
    @Setter
    private HashSet<VarEntity> varEntities = new HashSet<>();


    @Getter
    @Setter
    private HashSet<TypeEntity> containTypeEntity = new HashSet<>();
    @Getter
    @Setter
    private AbstractEntity originEntity;

    public TypeEntity(AbstractEntity abstractEntity) {
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
        TypeEntity other = (TypeEntity) o;
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

