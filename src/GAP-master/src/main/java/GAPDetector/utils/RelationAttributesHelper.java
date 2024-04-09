package GAPDetector.utils;

import GAPDetector.Storage;
import GAPDetector.entities.AbstractEntity;
import GAPDetector.entities.FuncImplEntity;
import GAPDetector.entities.TypeEntity;
import GAPDetector.entities.VarEntity;

public class RelationAttributesHelper {
    public Storage storage;

    public RelationAttributesHelper(Storage storage) {
        this.storage = storage;
    }


    public Integer detectModes(AbstractEntity fromEntity, AbstractEntity toEntity) {

        if (fromEntity.category.equals("Method") && toEntity.category.equals("Method")) {
            FuncImplEntity fromFuncImplEntity = storage.id_funcImplEntity.get(fromEntity.id);
            FuncImplEntity toFuncImplEntity = storage.id_funcImplEntity.get(toEntity.id);
            // 传参
            if (isMode1(fromFuncImplEntity, toFuncImplEntity)) {
                return 1;
            }
            // 内聚
            if (isMode2(fromFuncImplEntity, toFuncImplEntity)) {
                return 2;
            }
            // static
            if (toFuncImplEntity.modifier != null && toFuncImplEntity.modifier.contains("static")) {
                return 3;
            }
        }


        return null;
    }

    // mode1 传参
    public boolean isMode1(FuncImplEntity fromFuncImplEntity, FuncImplEntity toFuncImplEntity) {
        if (fromFuncImplEntity.getOriginEntity() instanceof TypeEntity && toFuncImplEntity.getOriginEntity() instanceof TypeEntity) {
            TypeEntity toTypeEntity = (TypeEntity) (toFuncImplEntity.getOriginEntity());
            for (VarEntity varEntity : fromFuncImplEntity.getParameterVarEntity()) {
                if (varEntity.rawType != null && varEntity.rawType.contains(toTypeEntity.qualifiedName)) {
//                    System.out.println(varEntity.object);
//                    System.out.println(toTypeEntity.object);
                    return true;
                }
            }
        }
        return false;
    }

    // mode2 内聚对象
    public boolean isMode2(FuncImplEntity fromFuncImplEntity, FuncImplEntity toFuncImplEntity) {
        if (fromFuncImplEntity.getOriginEntity() instanceof TypeEntity && toFuncImplEntity.getOriginEntity() instanceof TypeEntity) {
            TypeEntity fromTypeEntity = (TypeEntity) (fromFuncImplEntity.getOriginEntity());
            TypeEntity toTypeEntity = (TypeEntity) (toFuncImplEntity.getOriginEntity());
            return fromTypeEntity.getContainTypeEntity().contains(toTypeEntity);
        }

        return false;
    }
    public Integer detectIntrusiveType(AbstractEntity fromEntity, AbstractEntity toEntity) {
        if (fromEntity.isIntrusive && toEntity.isIntrusive){
            return 3;
        }
        else if (fromEntity.isIntrusive){
            return 1;
        }
        else if (toEntity.isIntrusive){
            return 2;
        }
        return 0;
    }
}