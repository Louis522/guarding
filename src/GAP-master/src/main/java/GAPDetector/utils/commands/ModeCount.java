package GAPDetector.utils.commands;

import GAPDetector.json.outputDTO.relations.EntityDependencyRelationDetail;
import GAPDetector.json.outputDTO.smells.AwD.AbstractionWithoutDecouplingDTO;
import GAPDetector.json.outputDTO.smells.AwD.AbstractionWithoutDecouplingStructure;
import GAPDetector.json.outputDTO.smells.AwD.AbstractionWithoutDecouplingStructureDetail;
import GAPDetector.json.util.JSONUtil;
import org.apache.commons.lang3.mutable.MutableInt;
import picocli.CommandLine;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Callable;


@CommandLine.Command(name = "modeCount", mixinStandardHelpOptions = true, helpCommand = true,
        description = "Count Abstraction Without Decoupling result dependency relation modes.")
public class ModeCount implements Callable<Integer> {

    @CommandLine.Parameters(description = "AWD result file path")
    private String awdFilePath;

    @Override
    public Integer call() throws Exception { // your business logic goes here...
        resolveAWDMode(awdFilePath);
        return 0;
    }

    public static void resolveAWDMode(String input) throws FileNotFoundException {
        HashMap<Integer, MutableInt> clientClass2superTypeModeCounter = new HashMap<>();
        HashMap<Integer, MutableInt> clientClass2subTypeModeCounter = new HashMap<>();
        HashMap<Integer, Integer> totalModeCounter = new HashMap<>();

        AbstractionWithoutDecouplingDTO awdResult = JSONUtil.fromJson(new File(input), AbstractionWithoutDecouplingDTO.class);
        assert awdResult != null;
        List<AbstractionWithoutDecouplingStructure> abstractionWithoutDecouplingStructureList = awdResult.getInstances();
        for (AbstractionWithoutDecouplingStructure abstractionWithoutDecouplingStructure : abstractionWithoutDecouplingStructureList) {
            AbstractionWithoutDecouplingStructureDetail details = abstractionWithoutDecouplingStructure.getDetails();
            List<EntityDependencyRelationDetail> clientClass2superType = details.getClientClass2superType();
            List<EntityDependencyRelationDetail> clientClass2subType = details.getClientClass2subType();
            countMode(clientClass2superTypeModeCounter, clientClass2superType);
            countMode(clientClass2subTypeModeCounter, clientClass2subType);
        }

        totalModeCounter.put(0, clientClass2superTypeModeCounter.get(0).toInteger() + clientClass2subTypeModeCounter.get(0).toInteger());
        totalModeCounter.put(1, clientClass2superTypeModeCounter.get(1).toInteger() + clientClass2subTypeModeCounter.get(1).toInteger());
        totalModeCounter.put(2, clientClass2superTypeModeCounter.get(2).toInteger() + clientClass2subTypeModeCounter.get(2).toInteger());
        totalModeCounter.put(3, clientClass2superTypeModeCounter.get(3).toInteger() + clientClass2subTypeModeCounter.get(3).toInteger());

        System.out.println(clientClass2superTypeModeCounter);
        System.out.println(clientClass2subTypeModeCounter);
        System.out.println(totalModeCounter);

    }

    private static void countMode(HashMap<Integer, MutableInt> clientClass2superTypeModeCounter, List<EntityDependencyRelationDetail> clientClass2subType) {
        for (EntityDependencyRelationDetail entityDependencyRelationDetail : clientClass2subType) {
            Integer mode = entityDependencyRelationDetail.getMode();
            if (mode == null) {
                mode = 0;
            }
            MutableInt initValue = new MutableInt(1);
            MutableInt oldValue = clientClass2superTypeModeCounter.put(mode, initValue);
            if (oldValue != null) {
                initValue.setValue(oldValue.getValue() + 1);
            }
        }
    }

    public static void main(String... args) {
        int exitCode = new CommandLine(new Rebuild()).execute(args);
        System.exit(exitCode);
    }
}
