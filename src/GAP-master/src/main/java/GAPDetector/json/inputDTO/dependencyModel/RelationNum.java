package GAPDetector.json.inputDTO.dependencyModel;

import com.google.gson.annotations.SerializedName;
import lombok.Data;
@Data
public class RelationNum {
    @SerializedName("Cast")
    private Integer Cast;
    @SerializedName("Call")
    private Integer Call;
    @SerializedName("Import")
    private Integer Import;
    @SerializedName("Set")
    private Integer Set;
    @SerializedName("Call non-dynamic")
    private Integer CallNonDynamic;
    @SerializedName("Implement")
    private Integer Implement;
    @SerializedName("Modify")
    private Integer Modify;
    @SerializedName("Annotate")
    private Integer Annotate;
    @SerializedName("UseVar")
    private Integer UseVar;
    @SerializedName("Reflect")
    private Integer Reflect;
    @SerializedName("Parameter")
    private Integer Parameter;
    @SerializedName("Override")
    private Integer Override;
    @SerializedName("Inherit")
    private Integer Inherit;
}
