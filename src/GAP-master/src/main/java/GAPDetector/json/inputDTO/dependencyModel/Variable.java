package GAPDetector.json.inputDTO.dependencyModel;

import com.google.gson.annotations.SerializedName;
import lombok.Data;

import java.util.List;

@Data
public class Variable {
    @SerializedName("external")
    private Boolean external;
    @SerializedName("rawType")
    private String rawType;
    @SerializedName("qualifiedName")
    private String qualifiedName;
    @SerializedName("parameter")
    private Parameter parameter;
    @SerializedName("name")
    private String name;
    @SerializedName("innerType")
    private List<Integer> innerType;
    @SerializedName("global")
    private Boolean global;
    @SerializedName("location")
    private Location location;
    @SerializedName("id")
    private Integer id;
    @SerializedName("category")
    private String category;
    @SerializedName("modifiers")
    private String modifiers;
    @SerializedName("File")
    private String file;
    @SerializedName("parentId")
    private Integer parentId;
    @SerializedName("additionalBin")
    private AdditionalBin additionalBin;
    @SerializedName("enhancement")
    private Enhancement enhancement;
}
