package GAPDetector.json.inputDTO.facade;

import lombok.Data;

import java.util.List;

@Data
public class Res {
    private List<EVE> e2n;
    private List<EVE> n2e;
    private List<EVE> n2n;
    private List<EVE> e;
}
