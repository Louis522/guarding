package GAPDetector.json.inputDTO.dependencyModel;

import com.google.gson.annotations.SerializedName;
import lombok.Data;

@Data
public class AdditionalBin {
    @SerializedName("binNum")
    private Integer binNum;
    @SerializedName("binPath")
    private String binPath;
}
