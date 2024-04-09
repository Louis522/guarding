package GAPDetector.json.outputDTO.smells.FE;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@AllArgsConstructor
@Data
public class FeatureEnvyDTO {
    private Integer count;
    private List<FeatureEnvyStructure> instances;

    @Override
    public String toString() {
        return "FE";
    }
}
