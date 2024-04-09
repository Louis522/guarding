package GAPDetector.json.ArchitectureSmellCompareDTO;

import GAPDetector.json.outputDTO.smells.CD.CyclicDependencyStructure;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class CyclicDependencyCompareDTO {
    private Integer reduced_count_CyclicDependency = 0 ;
    private Integer added_count_CyclicDependency = 0 ;

    private List<CyclicDependencyStructure> reduced_cyclicDependencyStructureList = new ArrayList<>();
    private List<CyclicDependencyStructure> added_cyclicDependencyStructureList = new ArrayList<>();
    public String toString() {
        return "CyclicDependencyCompareResult";
    }

}
