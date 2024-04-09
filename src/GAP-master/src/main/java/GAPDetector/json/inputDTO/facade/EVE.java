package GAPDetector.json.inputDTO.facade;

import GAPDetector.json.inputDTO.dependencyModel.Values;
import lombok.Data;

@Data
public class EVE {
    private Entity src;
    private Values values;
    private Entity dest;

}
