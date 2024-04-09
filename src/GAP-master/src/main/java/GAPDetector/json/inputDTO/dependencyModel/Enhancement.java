package GAPDetector.json.inputDTO.dependencyModel;

import lombok.Data;

@Data
public class Enhancement {
    private Boolean isGetter;
    private Boolean isRecursive;
    private Boolean isStatic;
    private Boolean isConstructor;
    private Boolean isOverride;
    private Boolean isSetter;
    private Boolean isPublic;
    private Boolean isDelegator;
    private Boolean isSynchronized;
    private Boolean isAbstract;
}

