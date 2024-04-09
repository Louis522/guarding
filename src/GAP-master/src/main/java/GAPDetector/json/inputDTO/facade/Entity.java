package GAPDetector.json.inputDTO.facade;

import GAPDetector.json.inputDTO.dependencyModel.Location;
import lombok.Data;

import java.util.List;

@Data
public class Entity {
    private Integer id;
    private Integer not_aosp;
    private Integer is_decoupling;
    private Integer old_aosp;
    private Integer isIntrusive;
    private String ownership;
    private Integer entity_mapping;
    private String category;
    private String qualifiedName;
    private Integer called_times;
    private String name;
    private CommitCount commits_count;
    private String File;
    private String packageName;
    private Location location;
    private String parameterTypes;
    private String parameterNames;
    private String rawType;
    private String modifiers;
    private String hidden;
    private List<RefactorSteps> refactor;
    private IntrusiveModify intrusiveModify;
}
