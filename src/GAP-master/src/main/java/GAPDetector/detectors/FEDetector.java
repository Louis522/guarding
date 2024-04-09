package GAPDetector.detectors;

import GAPDetector.entities.FuncImplEntity;
import GAPDetector.json.outputDTO.smells.FE.FeatureEnvyStructure;

public class FEDetector extends GeneralDetector {
    public FEDetector() {
        super(storage, ownershipFlag, measureFlag);
    }

    public void workflow() {

//        int ATFD_Threshold = 3;
//        double LAA_Threshold = 1.0 / 3;
//        int FDP_Threshold = 3;

        int ATFD_Threshold = threshold.getATFD();
        double LAA_Threshold = threshold.getLAA();
        int FDP_Threshold = threshold.getFDP();
        for (FuncImplEntity funcImplEntity : storage.id_funcImplEntity.values()) {

            funcImplEntity.setATFD(funcImplEntity.getFuncForeignAttributesSet().toArray().length);

//            funcImplEntity.setLAA(1.0 - (double) funcImplEntity.getFuncForeignAttributesSet().toArray().length / (funcImplEntity.getParameters().toArray().length + funcImplEntity.getVariables().toArray().length));
            funcImplEntity.setLAA(1.0 - (double) funcImplEntity.getFuncForeignAttributesSet().toArray().length / funcImplEntity.getVariables().toArray().length);

            funcImplEntity.setFDP(funcImplEntity.getFuncForeignAttributesClassSet().toArray().length);

            if (funcImplEntity.getATFD() >= ATFD_Threshold && funcImplEntity.getLAA() <= LAA_Threshold && funcImplEntity.getFDP() >= FDP_Threshold) {
//                System.out.println("Test~FE");
                FeatureEnvyStructure featureEnvyStructure = new FeatureEnvyStructure(smellCounter, funcImplEntity.file, funcImplEntity.qualifiedName, funcImplEntity.getATFD(), funcImplEntity.getLAA(), funcImplEntity.getFDP(), funcImplEntity.getFuncForeignAttributesSet());
                featureEnvyStructureList.add(featureEnvyStructure);
                smellCounter += 1;
                if (ownershipFlag && funcImplEntity.isIntrusive) {
                    FeatureEnvyStructure featureEnvyStructureIsIntrusive = new FeatureEnvyStructure(smellCounter, funcImplEntity.file, funcImplEntity.qualifiedName, funcImplEntity.getATFD(), funcImplEntity.getLAA(), funcImplEntity.getFDP(), funcImplEntity.getFuncForeignAttributesSet());
                    featureEnvyStructureIsIntrusiveList.add(featureEnvyStructureIsIntrusive);
                    smellCounterIsIntrusive += 1;
                }
            }
        }

    }

}
