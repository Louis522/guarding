package GAPDetector.json.outputDTO.smells.SS;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class ShotgunSurgeryDTO {
    private Integer count_ShotgunSurgery;
    private List<ShotgunSurgeryStructure> shotgunSurgeryStructureList;

    @Override
    public String toString() {
        return "SS";
    }
}
