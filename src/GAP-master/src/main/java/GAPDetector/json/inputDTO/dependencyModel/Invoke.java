package GAPDetector.json.inputDTO.dependencyModel;

import com.google.gson.annotations.SerializedName;
import lombok.Data;

@Data
public class Invoke {
    @SerializedName("endLine")
    private Integer endLine;
    @SerializedName("endColumn")
    private Integer endColumn;
    @SerializedName("startColumn")
    private Integer startColumn;
    @SerializedName("startLine")
    private Integer startLine;
}
