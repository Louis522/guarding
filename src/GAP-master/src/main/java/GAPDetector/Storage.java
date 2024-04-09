package GAPDetector;

import GAPDetector.entities.FileEntity;
import GAPDetector.entities.PackageEntity;
import GAPDetector.entities.*;
import GAPDetector.json.inputDTO.dependencyModel.Location;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
public class Storage implements Serializable {
    public HashMap<TypeEntity, List<TypeEntity>> sub_class;
    public HashMap<TypeEntity, TypeEntity> super_class;
    public HashMap<Integer, List<Integer>> hierarchyRelated;
    public HashMap<Integer, AbstractEntity> id_abstractEntity;
    public HashMap<Integer, TypeEntity> id_typeEntity;
    public HashMap<Integer, VarEntity> id_varEntity;
    public HashMap<Integer, FuncImplEntity> id_funcImplEntity;
    public HashMap<String, List<TypeEntity>> object_typeEntities;
    public HashMap<Integer, PackageEntity> id_package;
    public HashMap<Integer, FileEntity> id_file;
    public HashMap<String, FileEntity> qn_file;
    //    public HashMap<Integer, HashMap<String, HashSet<Integer>>> dependencyMap;
    public HashMap<Integer, HashMap<String, HashMap<Integer, Location>>> dependencyMapLocation;
    public HashMap<Integer, HashMap<Integer, HashMap<String, HashMap<Integer, HashMap<Integer, Location>>>>> dependencyMapClassLevel;

}
