package GAPDetector.entities;


import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.HashSet;

public class FuncImplEntity extends AbstractEntity {
    @Getter
    @Setter
    private AbstractEntity originEntity = null;

    @Getter
    @Setter
    private HashSet<VarEntity> parameterVarEntity = new HashSet<>();

    @Getter
    @Setter
    private HashSet<String> entitySet = new HashSet<>();

    @Getter
    @Setter
    private String targetClass = null;

    @Getter
    @Setter
    private Boolean isOverride;
    @Getter
    @Setter
    private Boolean isSetter;
    @Getter
    @Setter
    private Boolean isGetter;
    @Getter
    @Setter
    private Boolean isDelegator;
    @Getter
    @Setter
    private Boolean isRecursive;
    @Getter
    @Setter
    private Boolean isPublic;
    @Getter
    @Setter
    private Boolean isStatic;
    @Getter
    @Setter
    private Boolean isAssign;
    @Getter
    @Setter
    private Boolean isSynchronized;
    @Getter
    @Setter
    private Boolean isConstructor;
    @Getter
    @Setter
    private Boolean isCallSuper;
    @Getter
    @Setter
    private Boolean methodIsAbstract;


    @Getter
    private HashSet<FuncImplEntity> callFuncImplEntities = new HashSet<>();

    @Getter
    private HashSet<VarEntity> useVarEntities = new HashSet<>();

    @Setter
    @Getter
    private String attributeOfGetterOrSetter;

    @Getter
    @Setter
    private HashSet<String> parameters = new HashSet<>();
    @Getter
    @Setter
    private HashSet<VarEntity> variables = new HashSet<>();


    // shotgun surgery
    @Getter
    @Setter
    private HashSet<FuncImplEntity> callSet = new HashSet<>();
    @Getter
    @Setter
    private HashSet<TypeEntity> callClassSet = new HashSet<>();
    @Getter
    @Setter
    private HashSet<FuncImplEntity> callBySet = new HashSet<>();
    @Getter
    @Setter
    private HashSet<TypeEntity> callByClassSet = new HashSet<>();
    @Getter
    @Setter
    private Integer CC;
    @Getter
    @Setter
    private Integer CM;
    @Getter
    @Setter
    private Integer FANOUT;


    @Setter
    @Getter
    private HashSet<EntityIdentifier> funcForeignAttributesSet = new HashSet<>();
    @Setter
    @Getter
    private HashSet<String> funcForeignAttributesClassSet = new HashSet<>();
    @Getter
    @Setter
    private Integer ATFD;
    @Getter
    @Setter
    private Double LAA;
    @Getter
    @Setter
    private Integer FDP;


    public FuncImplEntity(AbstractEntity abstractEntity) {
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
        FuncImplEntity other = (FuncImplEntity) o;
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
