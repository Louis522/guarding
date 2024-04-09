package GAPDetector.json.util;

import GAPDetector.json.inputDTO.rule.Rule;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import java.io.File;
import java.io.IOException;

public class YAMLUtil {
    public static Rule resolveRuleFile(File file) {
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        try {
            return mapper.readValue(file, Rule.class);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

}
