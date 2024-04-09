package GAPDetector.utils.commands;

import GAPDetector.json.outputDTO.smells.AwD.AbstractionWithoutDecouplingDTO;
import GAPDetector.json.outputDTO.smells.CD.CyclicDependencyDTO;
import GAPDetector.json.outputDTO.smells.CD.CyclicDependencyStructure;
import GAPDetector.json.outputDTO.smells.SmellOutput;
import GAPDetector.json.util.JSONUtil;
import picocli.CommandLine;

import java.io.File;
import java.util.concurrent.Callable;


@CommandLine.Command(name = "classifier", mixinStandardHelpOptions = true, helpCommand = true,
        description = "Classify entities by ownership and package/file/class/method/variable.")
public class Classifier implements Callable<Integer> {
    @CommandLine.Option(names = {"-g", "--gapResult"}, required = true, description = "gapResult")
    public String gapResult;


    @Override
    public Integer call() throws Exception { // your business logic goes here...
        SmellOutput smellOutput = JSONUtil.fromJson(new File(gapResult), SmellOutput.class);
        CyclicDependencyDTO cdResult = smellOutput.getCyclicDependency();
        AbstractionWithoutDecouplingDTO abstractionWithoutDecouplingDTO = smellOutput.getAbstractionWithoutDecoupling();
        for (CyclicDependencyStructure cyclicDependencyStructure : cdResult.getInstances()){
            for (String cyclicDependencyModule : cyclicDependencyStructure.getModules()){

            }
        }


        return 0;
    }

    public static void main(String... args) {
        int exitCode = new CommandLine(new Classifier()).execute(args);
        System.exit(exitCode);
    }

}
