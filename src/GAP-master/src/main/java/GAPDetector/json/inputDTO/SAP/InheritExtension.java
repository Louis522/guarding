/**
 * Copyright 2022 ab173.com
 */
package GAPDetector.json.inputDTO.SAP;

import lombok.Data;

import java.util.List;

@Data
public class InheritExtension {

    private int count;
    private List<ReflectUseResInner> res;
}