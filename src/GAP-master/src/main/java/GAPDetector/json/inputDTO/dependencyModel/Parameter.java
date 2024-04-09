package GAPDetector.json.inputDTO.dependencyModel;

import com.google.gson.annotations.SerializedName;
import lombok.Data;

@Data
public class Parameter {
    @SerializedName("types")
    private String types;
    @SerializedName("names")
    private String names;
}
