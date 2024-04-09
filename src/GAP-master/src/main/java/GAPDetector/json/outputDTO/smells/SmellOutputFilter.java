package GAPDetector.json.outputDTO.smells;

import GAPDetector.json.outputDTO.relations.SingleDependencyRelationDetailWithClassInfo;
import GAPDetector.json.outputDTO.smells.AwD.AbstractionWithoutDecouplingStructure;
import GAPDetector.json.outputDTO.smells.CH.CyclicHierarchyStructure;
import GAPDetector.json.outputDTO.smells.MH.MultipathHierarchyStructure;
import GAPDetector.json.util.PathUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.List;


@NoArgsConstructor
@AllArgsConstructor
@Data
public class SmellOutputFilter {
    private String file;
    private Integer AbstractionWithoutDecouplingCount;
    private List<AbstractionWithoutDecouplingStructure> AbstractionWithoutDecoupling;
    private Integer CyclicDependencyCount;
    private HashMap<String, List<SingleDependencyRelationDetailWithClassInfo>> CyclicDependency;
    private Integer CyclicHierarchyCount;
    private List<CyclicHierarchyStructure> CyclicHierarchy;
    private Integer MultipathHierarchyCount;
    private List<MultipathHierarchyStructure> MultipathHierarchy;

    public SmellOutputFilter(String file) {
        this.file = file;
    }

    public String toString() {
        return PathUtil.getLastStrByPathDelimiter(file).split("\\.")[0];
    }
}