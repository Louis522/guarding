package GAPDetector.json.inputDTO.dependencyModel;

import com.google.gson.annotations.SerializedName;
import lombok.Data;

@Data
public class Values {
    @SerializedName("loc")
    private Location location;
    @SerializedName("Cast")
    private Integer Cast;
    @SerializedName("Call")
    private Integer Call;
    @SerializedName("bindVar")
    private Integer bindVar;
    @SerializedName("Define")
    private Integer Define;
    @SerializedName("Contain")
    private Integer Contain;
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
    @SerializedName("Return")
    private Integer Return;
    @SerializedName("Parameter")
    private Integer Parameter;
    @SerializedName("Override")
    private Integer Override;
    @SerializedName("Inherit")
    private Integer Inherit;
    @SerializedName("Typed")
    private Integer Typed;
    @SerializedName("modifyAccessible")
    private Boolean modifyAccessible;
    @SerializedName("invoke")
    private Object invoke;
}
