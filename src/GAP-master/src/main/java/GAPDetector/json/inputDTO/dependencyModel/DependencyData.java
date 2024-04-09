package GAPDetector.json.inputDTO.dependencyModel;

import com.google.gson.annotations.SerializedName;
import lombok.Data;

import java.util.List;

@Data
public class DependencyData {
    @SerializedName("variables")
    private List<Variable> variables;
    @SerializedName("schemaVersion")
    private String schemaVersion;
    @SerializedName("cells")
    private List<Cells> cells;
    @SerializedName("entityNum")
    private EntityNum entityNum;
    @SerializedName("categories")
    private List<Categories> categories;
    @SerializedName("relationNum")
    private RelationNum relationNum;
}
