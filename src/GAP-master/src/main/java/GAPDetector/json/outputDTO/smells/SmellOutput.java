package GAPDetector.json.outputDTO.smells;

import GAPDetector.json.outputDTO.smells.AwD.AbstractionWithoutDecouplingDTO;
import GAPDetector.json.outputDTO.smells.CD.CyclicDependencyDTO;
import GAPDetector.json.outputDTO.smells.CH.CyclicHierarchyDTO;
import GAPDetector.json.outputDTO.smells.FE.FeatureEnvyDTO;
import GAPDetector.json.outputDTO.smells.MH.MultipathHierarchyDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@NoArgsConstructor
@AllArgsConstructor
@Data
public class SmellOutput {
    private Integer countTotal;
    private AbstractionWithoutDecouplingDTO AbstractionWithoutDecoupling;
    private MultipathHierarchyDTO MultipathHierarchy;
    private CyclicHierarchyDTO CyclicHierarchy;
    private CyclicDependencyDTO CyclicDependency;
    private FeatureEnvyDTO FeatureEnvy;
}