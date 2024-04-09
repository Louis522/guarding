package GAPDetector.json.ArchitectureSmellCompareDTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class ArchitectureSmellCompareDTO {
    private Integer reduced_smell_count = 0;
    private Integer added_smell_count = 0;

    private AbstractionWithoutDecouplingCompareDTO compare_AbstractionWithoutDecoupling;
    private CyclicDependencyCompareDTO compare_CyclicDependency;
    private CyclicHierarchyCompareDTO compare_CyclicHierarchy;
//    private FeatureEnvyCompareDTO compare_FeatureEnvy;
    private MultipathHierarchyCompareDTO compare_MultipathHierarchy;
}
