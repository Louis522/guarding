package GAPDetector.json.ArchitectureSmellCompareDTO;

import GAPDetector.json.outputDTO.smells.AwD.AbstractionWithoutDecouplingStructure;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class AbstractionWithoutDecouplingCompareDTO {
    private Integer reduced_count_AbstractionWithoutDecoupling;
    private Integer added_count_AbstractionWithoutDecoupling;
    private Integer diff_count_AbstractionWithoutDecoupling;
    private Integer diffAndSame_count_AbstractionWithoutDecoupling;
    private Integer same_count_AbstractionWithoutDecoupling;

    private List<AbstractionWithoutDecouplingStructure> reduced_AbstractionWithoutDecouplingStructures;
    private List<AbstractionWithoutDecouplingStructure> added_AbstractionWithoutDecouplingStructures;
    private List<DiffAbstractionWithoutDecouplingStructure> diff_AbstractionWithoutDecouplingStructures;
    private List<DiffAbstractionWithoutDecouplingStructure> diffAndSame_AbstractionWithoutDecouplingStructures;
    private List<DiffAbstractionWithoutDecouplingStructure> same_AbstractionWithoutDecouplingStructures;


    @Override
    public String toString() {
        return "AbstractionWithoutDecouplingCompareResult";
    }

}
