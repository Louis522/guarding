package GAPDetector.json.outputDTO.smells.CD;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
@NoArgsConstructor
@AllArgsConstructor
@Data
public class CyclicDependencyDTO {
    private Integer count;
    private List<CyclicDependencyStructure> instances;
    @Override
    public String toString() {
        return "CD";
    }
}
