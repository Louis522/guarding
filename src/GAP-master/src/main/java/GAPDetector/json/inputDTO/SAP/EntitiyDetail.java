package GAPDetector.json.inputDTO.SAP;

import lombok.Data;

@Data
public class EntitiyDetail {

    private Integer id;
    private Integer not_aosp;
    private Integer is_decoupling;
    private Integer old_aosp;
    private Integer isIntrusive;
    private String ownership;
    private String category;
    private String qualifiedName;
    private Integer called_times;
    private String name;
    private CommitsCount commits_count;
    private String File;
    private String packageName;
    private Location location;
    private String parameterTypes;
    private String parameterNames;
    private String rawType;
    private String modifiers;
    private IntrusiveModify intrusiveModify;
}