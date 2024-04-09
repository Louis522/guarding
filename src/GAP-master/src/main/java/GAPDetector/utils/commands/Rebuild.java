package GAPDetector.utils.commands;

import GAPDetector.entities.EntityIdentifier;
import GAPDetector.json.outputDTO.analyzers.AWDAnalyzer;
import GAPDetector.json.outputDTO.analyzers.RebuildAbstractionWithoutDecouplingStructure;
import GAPDetector.json.outputDTO.smells.AwD.AbstractionWithoutDecouplingDTO;
import GAPDetector.json.outputDTO.smells.AwD.AbstractionWithoutDecouplingStructure;
import GAPDetector.json.util.JSONUtil;
import picocli.CommandLine;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Callable;


@CommandLine.Command(name = "rebuild", mixinStandardHelpOptions = true, helpCommand = true,
        description = "Rebuild Abstraction Without Decoupling result by same superClass and subClass")
public class Rebuild implements Callable<Integer> {

    @CommandLine.Option(names = {"-a", "--awd"}, required = true, description = "AWD result file path")
    public String awdFilePath;

    @Override
    public Integer call() throws Exception { // your business logic goes here...

        AbstractionWithoutDecouplingDTO awdResult = JSONUtil.fromJson(new File(awdFilePath), AbstractionWithoutDecouplingDTO.class);
        HashMap<String, RebuildAbstractionWithoutDecouplingStructure> Id2RebuildAbstractionWithoutDecouplingStructureHashMap = new HashMap<>();
        assert awdResult != null;
        List<AbstractionWithoutDecouplingStructure> abstractionWithoutDecouplingStructureList = awdResult.getInstances();
        for (int i = 0; i < abstractionWithoutDecouplingStructureList.size(); i++) { // 时间复杂度O(N^2)
            AbstractionWithoutDecouplingStructure abstractionWithoutDecouplingStructure1 = abstractionWithoutDecouplingStructureList.get(i);
            for (int j = i + 1; j < abstractionWithoutDecouplingStructureList.size(); j++) {
                AbstractionWithoutDecouplingStructure abstractionWithoutDecouplingStructure2 = abstractionWithoutDecouplingStructureList.get(j);
                if (abstractionWithoutDecouplingStructure1 != abstractionWithoutDecouplingStructure2) {
                    EntityIdentifier superType1 = abstractionWithoutDecouplingStructure1.getSuperType();
                    EntityIdentifier subType1 = abstractionWithoutDecouplingStructure1.getSubType();
                    EntityIdentifier clientClass1 = abstractionWithoutDecouplingStructure1.getClientClass();
                    EntityIdentifier superType2 = abstractionWithoutDecouplingStructure2.getSuperType();
                    EntityIdentifier subType2 = abstractionWithoutDecouplingStructure2.getSubType();
                    EntityIdentifier clientClass2 = abstractionWithoutDecouplingStructure2.getClientClass();
                    if (superType1.equals(superType2) && subType1.equals(subType2) && !clientClass1.equals(clientClass2)) {
                        RebuildAbstractionWithoutDecouplingStructure rebuildAbstractionWithoutDecouplingStructure;
                        if (Id2RebuildAbstractionWithoutDecouplingStructureHashMap.get(superType1.getObject() + "+" + subType1.getObject()) != null) {
                            rebuildAbstractionWithoutDecouplingStructure = Id2RebuildAbstractionWithoutDecouplingStructureHashMap.get(superType1.getObject() + "+" + subType1.getObject());
                        } else {
                            rebuildAbstractionWithoutDecouplingStructure = new RebuildAbstractionWithoutDecouplingStructure(superType1, subType1);
                            Id2RebuildAbstractionWithoutDecouplingStructureHashMap.put(superType1.getObject() + "+" + subType1.getObject(), rebuildAbstractionWithoutDecouplingStructure);
                        }
                        rebuildAbstractionWithoutDecouplingStructure.getClientClass2details().put(clientClass1, abstractionWithoutDecouplingStructure1.getDetails());
                        rebuildAbstractionWithoutDecouplingStructure.getClientClass2details().put(clientClass2, abstractionWithoutDecouplingStructure2.getDetails());
                    }
                }
            }
        }

        for (RebuildAbstractionWithoutDecouplingStructure rebuildAbstractionWithoutDecouplingStructure : Id2RebuildAbstractionWithoutDecouplingStructureHashMap.values()) {
            rebuildAbstractionWithoutDecouplingStructure.setDiffClientCount(rebuildAbstractionWithoutDecouplingStructure.getClientClass2details().size());
        }

        AWDAnalyzer awdAnalyzer = new AWDAnalyzer();
        awdAnalyzer.getRebuildAbstractionWithoutDecouplingStructures().addAll(Id2RebuildAbstractionWithoutDecouplingStructureHashMap.values());
        awdAnalyzer.setSameSuperAndSubCount(Id2RebuildAbstractionWithoutDecouplingStructureHashMap.values().size());

        JSONUtil.toJson(awdAnalyzer, "./", "AWDBySameSuperAndSub");
        return 0;
    }

    public static void main(String... args) {
        int exitCode = new CommandLine(new Rebuild()).execute(args);
        System.exit(exitCode);
    }
}
