package GAPDetector.json.ArchitectureSmellCompareDTO;

import GAPDetector.json.outputDTO.smells.MH.MultipathHierarchyStructure;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class MultipathHierarchyCompareDTO {
    private Integer reduced_count_MultipathHierarchy = 0;
    private Integer added_count_MultipathHierarchy = 0;

    private List<MultipathHierarchyStructure> reduced_multipathHierarchyStructureList = new ArrayList<>();
    private List<MultipathHierarchyStructure> added_multipathHierarchyStructureList = new ArrayList<>();
    public String toString() {
        return "MultipathHierarchyCompareResult";
    }
}
