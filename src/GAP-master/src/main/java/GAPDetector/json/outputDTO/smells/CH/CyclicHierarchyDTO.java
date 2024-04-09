package GAPDetector.json.outputDTO.smells.CH;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
@NoArgsConstructor
@AllArgsConstructor
@Data
public class CyclicHierarchyDTO {
    private Integer count;
    private List<CyclicHierarchyStructure> instances;
    @Override
    public String toString() {
        return "CH";
    }

}
