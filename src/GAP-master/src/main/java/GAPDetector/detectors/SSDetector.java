package GAPDetector.detectors;

import GAPDetector.entities.EntityIdentifier;
import GAPDetector.entities.FuncImplEntity;
import GAPDetector.entities.TypeEntity;
import GAPDetector.json.outputDTO.smells.SS.ShotgunSurgeryStructure;

import java.util.HashSet;

public class SSDetector extends GeneralDetector {
    public SSDetector() {
        super(storage, ownershipFlag, measureFlag);
    }

    public void workflow() {

        int CC_Threshold = threshold.getCC();
        int CM_Threshold = threshold.getCM();
        int FANOUT_Threshold = threshold.getFANOUT();

        for (FuncImplEntity funcImplEntity : storage.id_funcImplEntity.values()) {
            funcImplEntity.setCC(funcImplEntity.getCallByClassSet().toArray().length);
            funcImplEntity.setCM(funcImplEntity.getCallBySet().toArray().length);
            funcImplEntity.setFANOUT(funcImplEntity.getCallClassSet().toArray().length);
            if (funcImplEntity.getCC() >= CC_Threshold && funcImplEntity.getCM() >= CM_Threshold && funcImplEntity.getFANOUT() >= FANOUT_Threshold) {
                HashSet<EntityIdentifier> callClassSet = new HashSet<>();
                HashSet<EntityIdentifier> callBySet = new HashSet<>();
                HashSet<EntityIdentifier> callByClassSet = new HashSet<>();

                for (FuncImplEntity funcImplEntity1 : funcImplEntity.getCallSet()) {
                    EntityIdentifier entityIdentifier = new EntityIdentifier(funcImplEntity1, funcImplEntity.isIntrusive, null);
                    callBySet.add(entityIdentifier);
                }

                for (TypeEntity typeEntity : funcImplEntity.getCallClassSet()) {
                    EntityIdentifier entityIdentifier = new EntityIdentifier(typeEntity, typeEntity.isIntrusive, null);
                    callClassSet.add(entityIdentifier);
                }

                for (TypeEntity typeEntity : funcImplEntity.getCallByClassSet()) {
                    EntityIdentifier entityIdentifier = new EntityIdentifier(typeEntity, typeEntity.isIntrusive, null);
                    callByClassSet.add(entityIdentifier);
                }

                ShotgunSurgeryStructure shotgunSurgeryStructure = new ShotgunSurgeryStructure(smellCounter, funcImplEntity.file, null, funcImplEntity.qualifiedName, funcImplEntity.isIntrusive, funcImplEntity.getCC(), funcImplEntity.getCM(), funcImplEntity.getFANOUT(), callClassSet, callBySet, callByClassSet);
                shotgunSurgeryStructureList.add(shotgunSurgeryStructure);
                smellCounter += 1;
                if (ownershipFlag && funcImplEntity.isIntrusive) {
                    shotgunSurgeryStructureIsIntrusiveList.add(shotgunSurgeryStructure);
                    smellCounterIsIntrusive += 1;
                }
            }
        }
    }
}
