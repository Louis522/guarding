package GAPDetector.json.ArchitectureSmellCompareDTO;

import GAPDetector.json.outputDTO.smells.FE.FeatureEnvyStructure;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


@AllArgsConstructor
@NoArgsConstructor
@Data
public class FeatureEnvyCompareDTO {

    private Integer reduced_count_FeatureEnvy = 0;
    private Integer added_count_FeatureEnvy = 0;

    private List<FeatureEnvyStructure> reduced_featureEnvyStructureList;
    private List<FeatureEnvyStructure> added_featureEnvyStructureList;

    public String toString() {
        return "FeatureEnvyCompareResult";
    }
}
