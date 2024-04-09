package GAPDetector.json.outputDTO.smells.AwD;

import GAPDetector.entities.EntityIdentifier;
import lombok.AllArgsConstructor;
import lombok.Data;


@AllArgsConstructor
@Data
public class AbstractionWithoutDecouplingStructure {
    private Integer id;
    private String mode;
    private EntityIdentifier superType;
    private EntityIdentifier clientClass;
    private EntityIdentifier subType;
    private AbstractionWithoutDecouplingStructureDetail details;

    public AbstractionWithoutDecouplingStructure(Integer id, EntityIdentifier superType, EntityIdentifier clientClass, EntityIdentifier subType, AbstractionWithoutDecouplingStructureDetail details) {
        this.id = id;
        this.superType = superType;
        this.clientClass = clientClass;
        this.subType = subType;
        this.details = details;
    }

    public AbstractionWithoutDecouplingStructure(String mode, EntityIdentifier superType, EntityIdentifier clientClass, EntityIdentifier subType, AbstractionWithoutDecouplingStructureDetail details) {
        this.mode = mode;
        this.superType = superType;
        this.clientClass = clientClass;
        this.subType = subType;
        this.details = details;
    }
}
