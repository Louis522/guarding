package GAPDetector.json.outputDTO.smells.AwD;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class AbstractionWithoutDecouplingDTO {
    private Integer count;
    private List<AbstractionWithoutDecouplingStructure> instances;
    @Override
    public String toString() {
        return "AWD";
    }
}
