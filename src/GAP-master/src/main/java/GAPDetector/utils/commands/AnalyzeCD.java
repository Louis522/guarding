package GAPDetector.utils.commands;

import GAPDetector.json.outputDTO.smells.CD.CyclicDependencyDTO;
import GAPDetector.json.outputDTO.smells.CD.CyclicDependencyStructure;
import GAPDetector.json.util.JSONUtil;
import org.apache.commons.lang3.mutable.MutableInt;
import picocli.CommandLine;

import java.io.File;
import java.util.HashMap;
import java.util.concurrent.Callable;


@CommandLine.Command(name = "analyzeCD", mixinStandardHelpOptions = true, helpCommand = true,
        description = "Analyze Reflect dependency relation between in CD or not.")
public class AnalyzeCD implements Callable<Integer> {

    @CommandLine.Option(names = {"-c", "--cd"}, required = true, description = "cd result file path")
    public String cdFilePath;

    @Override
    public Integer call() throws Exception { // your business logic goes here...
        CyclicDependencyDTO cdResult = JSONUtil.fromJson(new File(cdFilePath), CyclicDependencyDTO.class);
        assert cdResult != null;
        Integer count = 0;

        for (CyclicDependencyStructure cyclicDependencyStructure : cdResult.getInstances()) {
            HashMap<String, MutableInt> dependencyRelationCountTotal = cyclicDependencyStructure.getDependencyRelationCountTotal();
            if (dependencyRelationCountTotal.get("Reflect") != null) {
                count++;
            }
        }
        System.out.println(count);
        System.out.println(cdResult.getCount());
        return 0;
    }

    public static void main(String... args) {
        int exitCode = new CommandLine(new AnalyzeCD()).execute(args);
        System.exit(exitCode);
    }
}
