package GAPDetector.json.inputDTO.rule;

import lombok.Data;

import java.util.List;

@Data
public class EdgeLevel {
    private List<String> all;
    private List<String> exclude;
    private List<String> include;
}