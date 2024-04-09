package GAPDetector.json.outputDTO.analyzers;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class AWDAnalyzer {
    private Integer sameSuperAndSubCount;
    private HashSet<RebuildAbstractionWithoutDecouplingStructure> rebuildAbstractionWithoutDecouplingStructures = new HashSet<>();

    @Override
    public String toString() {
        return "AWDAnalyzer";
    }
}
