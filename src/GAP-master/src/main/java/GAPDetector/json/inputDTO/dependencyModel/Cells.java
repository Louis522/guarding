package GAPDetector.json.inputDTO.dependencyModel;

import com.google.gson.annotations.SerializedName;
import lombok.Data;

@Data
public class Cells {
    @SerializedName("src")
    private Integer src;
    @SerializedName("values")
    private Values values;
    @SerializedName("dest")
    private Integer dest;
}
