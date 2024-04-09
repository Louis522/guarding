package GAPDetector.json.inputDTO.SAP;


import lombok.Data;

import java.util.List;

@Data
public class InheritanceNative {

    private int count;
    private List<ReflectUseResInner> res;
}