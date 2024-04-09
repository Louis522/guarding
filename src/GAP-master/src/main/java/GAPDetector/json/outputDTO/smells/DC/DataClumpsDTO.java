package GAPDetector.json.outputDTO.smells.DC;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class DataClumpsDTO {
    private Integer count;
    private List<DataClumpsStructure> instances;

    @Override
    public String toString() {
        return "DC";
    }
}
