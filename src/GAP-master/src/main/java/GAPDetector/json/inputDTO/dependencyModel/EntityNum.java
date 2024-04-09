package GAPDetector.json.inputDTO.dependencyModel;

import com.google.gson.annotations.SerializedName;
import lombok.Data;

@Data
public class EntityNum {
//    @SerializedName("Enum")
    @SerializedName("Enum")
    private Integer EnumX;
    @SerializedName("Annotation Member")
    private Integer AnnotationMember;
    @SerializedName("variable")
    private Integer variableX;
    @SerializedName("Class")
    private Integer ClassX;
    @SerializedName("Package")
    private Integer PackageX;
    @SerializedName("Method")
    private Integer MethodX;
    @SerializedName("File")
    private Integer FileX;
    @SerializedName("Interface")
    private Integer InterfaceX;
    @SerializedName("Annotation")
    private Integer AnnotationX;
}
