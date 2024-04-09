package GAPDetector.json.inputDTO.dependencyModel;

import com.google.gson.annotations.SerializedName;
import lombok.Data;

@Data
public class Categories {
    @SerializedName("name")
    private String name;
}
