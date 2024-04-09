package GAPDetector.json.ArchitectureSmellCompareDTO;

import GAPDetector.json.outputDTO.smells.CH.CyclicHierarchyStructure;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class CyclicHierarchyCompareDTO {
    private Integer reduced_count_CyclicHierarchy = 0 ;
    private Integer added_count_CyclicHierarchy = 0 ;

    private List<CyclicHierarchyStructure> reduced_cyclicHierarchyStructureList = new ArrayList<>();
    private List<CyclicHierarchyStructure> added_cyclicHierarchyStructureList = new ArrayList<>();

    public String toString() {
        return "CyclicHierarchyCompareResult";
    }
}
