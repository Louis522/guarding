package GAPDetector.detectors;

import GAPDetector.entities.EntityIdentifier;
import GAPDetector.entities.FuncImplEntity;
import GAPDetector.json.outputDTO.smells.DC.DataClumpsStructure;

import java.util.*;

import static GAPDetector.utils.DistanceCalculator.intersection;

public class DCDetector extends GeneralDetector {
    public DCDetector() {
        super(storage, ownershipFlag, measureFlag);
    }

    public void workflow() {
        int MIN_SAME_PARS = threshold.getMIN_SAME_PARS();
        int MIN_SAME_PARS_FUNC = threshold.getMIN_SAME_PARS_FUNC();

        List<FuncImplEntity> tempFuncs = new ArrayList<>();
        for (FuncImplEntity funcImplEntity : storage.id_funcImplEntity.values()) { // 时间复杂度O(N)
            if (funcImplEntity.getParameters() != null && funcImplEntity.getParameters().size() >= 3) {
                tempFuncs.add(funcImplEntity);
            }
        }

        // 如果参数有非基本类型，是否可以排除？


        HashMap<Set<String>, Set<FuncImplEntity>> possibleDataClumpsPars = new HashMap<>();
        for (int i = 0; i < tempFuncs.size(); i++) { // 时间复杂度O(N^2)
            for (int j = i + 1; j < tempFuncs.size(); j++) {
                if (tempFuncs.get(i) != tempFuncs.get(j) && tempFuncs.get(i).getParameters() != null && tempFuncs.get(j).getParameters() != null) {
                    Set<String> samePars = intersection(tempFuncs.get(i).getParameters(), tempFuncs.get(j).getParameters());
                    if (samePars.size() >= MIN_SAME_PARS) {
                        possibleDataClumpsPars.computeIfAbsent(samePars, k -> new HashSet<>());
                        possibleDataClumpsPars.get(samePars).add(tempFuncs.get(i));
                        possibleDataClumpsPars.get(samePars).add(tempFuncs.get(j));
                    }
                }
            }
        }

        for (Map.Entry<Set<String>, Set<FuncImplEntity>> entry : possibleDataClumpsPars.entrySet()) {
            if (entry.getValue().size() >= MIN_SAME_PARS_FUNC) {
                Set<EntityIdentifier> dataClumpsFuncs = new HashSet<>();
                Set<EntityIdentifier> dataClumpsFuncsIsIntrusive = new HashSet<>();

                for (FuncImplEntity funcImplEntity : entry.getValue()) {
                    EntityIdentifier dataClumpsFunc = new EntityIdentifier(funcImplEntity.qualifiedName, funcImplEntity.file, funcImplEntity.modifier, funcImplEntity.isIntrusive, funcImplEntity.location);
                    dataClumpsFuncs.add(dataClumpsFunc);
                    if (ownershipFlag && funcImplEntity.isIntrusive) {
                        dataClumpsFuncsIsIntrusive.add(dataClumpsFunc);

                    }
                }
                DataClumpsStructure dataClumpsStructure = new DataClumpsStructure(smellCounter, entry.getKey(), entry.getKey().size(), dataClumpsFuncs, dataClumpsFuncs.size());
                dataClumpsStructureList.add(dataClumpsStructure);
                smellCounter += 1;

                if (ownershipFlag) {
                    DataClumpsStructure dataClumpsStructureIsIntrusive = new DataClumpsStructure(smellCounterIsIntrusive, entry.getKey(), entry.getKey().size(), dataClumpsFuncsIsIntrusive, dataClumpsFuncsIsIntrusive.size());
                    dataClumpsStructureIsIntrusiveList.add(dataClumpsStructureIsIntrusive);
                    smellCounterIsIntrusive += 1;
                    // TODO
                }
            }
        }
    }
}
