package GAPDetector.utils;

import GAPDetector.json.inputDTO.rule.DetectTarget;
import GAPDetector.json.inputDTO.rule.GraphTarget;
import GAPDetector.json.inputDTO.rule.Rule;
import lombok.Getter;

public class RuleSolver {
    @Getter
    private final DetectTarget detectTarget;
    @Getter
    private final GraphTarget graphTarget;


    public RuleSolver(Rule rule) {
        this.detectTarget = rule.getDetectTarget();
        this.graphTarget = rule.getGraphTarget();
    }
}
