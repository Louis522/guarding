package GAPDetector.utils;


import GAPDetector.Storage;
import GAPDetector.entities.AbstractEntity;
import GAPDetector.entities.FuncImplEntity;
import GAPDetector.entities.TypeEntity;
import GAPDetector.entities.VarEntity;
import lombok.Getter;

import java.io.Serializable;
import java.util.*;


public class DistanceMatrix implements Serializable {
    private static final long serialVersionUID = -8640518415450784819L;

    private HashMap<String, Integer> entityIndexMap = new HashMap<>();
    private HashMap<String, Integer> typeIndexMap = new HashMap<>();
    private List<AbstractEntity> entityList = new ArrayList<>();

    @Getter
    private List<TypeEntity> typeEntityList = new ArrayList<>();
    @Getter
    private List<FuncImplEntity> funcImplEntityList = new ArrayList<>();
    //holds the entity set of each entity
    private HashMap<String, Set<String>> entityMap = new HashMap<>();
    //holds the entity set of each class
    private HashMap<String, Set<String>> typeMap = new HashMap<>();


    @Getter
    private HashMap<String, HashMap<String, Double>> allFuncImplJaccardDistanceMatrix;


    public DistanceMatrix(Storage storage) {
        typeEntityList.addAll(storage.id_typeEntity.values());
        funcImplEntityList.addAll(storage.id_funcImplEntity.values());
        generateDistances();
        allFuncImplJaccardDistanceMatrix = resolveAllFuncImplJaccardDistanceMatrix(funcImplEntityList);
    }

    public void generateDistances() {
        for (TypeEntity typeEntity : typeEntityList) {
            List<VarEntity> varEntities = new ArrayList<>(typeEntity.getVarEntities());
            for (VarEntity varEntity : varEntities) {
                entityList.add(varEntity);
                entityMap.put(varEntity.qualifiedName, varEntity.getEntitySet());
            }
            for (FuncImplEntity funcImplEntity : funcImplEntityList) {
                entityList.add(funcImplEntity);
                entityMap.put(funcImplEntity.qualifiedName, funcImplEntity.getEntitySet());
            }

            typeMap.put(typeEntity.qualifiedName, typeEntity.getEntitySet());
        }

        String[] entityNames = new String[entityList.size()];
        String[] classNames = new String[typeEntityList.size()];

        int i = 0;
        for (AbstractEntity entity : entityList) {
            entityNames[i] = entity.qualifiedName;
            entityIndexMap.put(entityNames[i], i);
            int j = 0;
            for (TypeEntity typeEntity : typeEntityList) {
                classNames[j] = typeEntity.getQualifiedName();
                if (!typeIndexMap.containsKey(classNames[j]))
                    typeIndexMap.put(classNames[j], j);
                j++;
            }
            i++;
        }
    }


    public HashMap<String, HashMap<String, Double>> resolveAllFuncImplJaccardDistanceMatrix(List<FuncImplEntity> funcImplEntities) {
        HashMap<String, HashMap<String, Double>> allFuncImplJaccardDistanceMatrix = new HashMap<>();
        for (FuncImplEntity funcImplEntity : funcImplEntities) {
            allFuncImplJaccardDistanceMatrix.put(funcImplEntity.qualifiedName, resolveSingleFuncImplJaccardDistanceMatrix(funcImplEntity));
        }
        return allFuncImplJaccardDistanceMatrix;
    }

    public HashMap<String, Double> resolveSingleFuncImplJaccardDistanceMatrix(FuncImplEntity funcImplEntity) {
        HashMap<String, Double> funcImplJaccardDistanceMatrix = new HashMap<>();
        for (TypeEntity typeEntity : typeEntityList) {
            double distance = 0;
            // 若 method 不归属于 class 直接计算
            if (funcImplEntity.getOriginEntity() != null && !(funcImplEntity.getOriginEntity() instanceof TypeEntity && funcImplEntity.getOriginEntity().equals(typeEntity))) {
                distance = DistanceCalculator.getDistance(funcImplEntity.getEntitySet(), typeEntity.getEntitySet());
            } else { // 若 method 归属于 class 需要在class的实体集种删去funcImpl
                Set<String> typeEntitySet = typeEntity.getEntitySet();
                boolean removedSet = typeEntitySet.remove(funcImplEntity.qualifiedName);
                if (removedSet) {
                    distance = DistanceCalculator.getDistance(funcImplEntity.getEntitySet(), typeEntitySet);
                }
            }
            funcImplJaccardDistanceMatrix.put(typeEntity.getQualifiedName(), distance);
        }
        return funcImplJaccardDistanceMatrix;
    }

}
