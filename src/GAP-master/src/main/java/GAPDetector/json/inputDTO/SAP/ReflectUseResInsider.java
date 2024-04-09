package GAPDetector.json.inputDTO.SAP;

import lombok.Data;

import java.util.List;

@Data
public class ReflectUseResInsider {
    private Metrics metrics;
    private List<InnerResValues> values;
}
