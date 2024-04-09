package GAPDetector.json.outputDTO.smells.MH;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class MultipathHierarchyDTO {
    private Integer count;
    private List<MultipathHierarchyStructure> instances;

    @Override
    public String toString() {
        return "MH";
    }
}
