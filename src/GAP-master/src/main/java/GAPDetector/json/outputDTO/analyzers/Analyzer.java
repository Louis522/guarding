package GAPDetector.json.outputDTO.analyzers;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class Analyzer {
    private String project;
    private Integer CDInSystem;
    private Integer CDWithReflectInSystem;
    private String CDWithReflectInSystemPercent;
    private Integer ReflectCount;
    private Integer ReflectInCD;
    private String ReflectInCDPercent;
    private Integer ReflectInCDRelatedToTEST;
    private String ReflectInCDRelatedToTESTPercent;
}
