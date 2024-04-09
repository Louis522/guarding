package GAPDetector.json;

import GAPDetector.csv.util.CSVUtil;
import GAPDetector.csv.util.dto.MeasureResultDTO;
import GAPDetector.csv.util.dto.OwnerShipDTO;
import GAPDetector.entities.*;
import GAPDetector.json.inputDTO.dependencyModel.*;
import GAPDetector.json.inputDTO.facade.EVE;
import GAPDetector.json.inputDTO.facade.FacadeResult;
import GAPDetector.json.outputDTO.relations.EntityDependencyRelationDetail;
import GAPDetector.json.util.JSONUtil;
import GAPDetector.model.Edge;
import GAPDetector.model.Vertex;
import GAPDetector.json.util.PathUtil;
import org.apache.commons.lang3.mutable.MutableInt;
import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.AllDirectedPaths;
import org.jgrapht.graph.DefaultDirectedGraph;

import java.io.File;
import java.io.IOException;

import java.util.*;

public class JsonProcessor {


    public HashMap<TypeEntity, List<TypeEntity>> super2SubsInherit = new HashMap<>();
    public HashMap<TypeEntity, TypeEntity> subSuperInherit = new HashMap<>();
    public HashMap<TypeEntity, List<TypeEntity>> subSuperImplement = new HashMap<>();
    public HashMap<Integer, List<Integer>> hierarchyRelated = new HashMap<>();

    public HashMap<Integer, AbstractEntity> id2abstractEntity = new HashMap<>();
    public HashMap<Integer, TypeEntity> id2typeEntity = new HashMap<>();
    public HashMap<Integer, VarEntity> id2varEntity = new HashMap<>();
    public HashMap<Integer, FuncImplEntity> id2funcImplEntity = new HashMap<>();
    public HashMap<Integer, PackageEntity> id2packageEntity = new HashMap<>();
    public HashMap<String, PackageEntity> qn2packageEntity = new HashMap<>();

    public HashMap<Integer, FileEntity> id_fileEntity = new HashMap<>();
    public HashMap<String, FileEntity> qn2fileEntity = new HashMap<>();
    public HashMap<String, List<TypeEntity>> object_typeEntities = new HashMap<>();
    public Graph<Vertex, Edge> hierarchyGraph = new DefaultDirectedGraph<>(Edge.class);
    //    public HashMap<Integer, HashMap<String, HashSet<Integer>>> dependencyMap = new HashMap<>();
    public HashMap<Integer, HashMap<String, HashMap<Integer, Location>>> dependencyMapLocation = new HashMap<>();

//    public HashMap<Integer, HashMap<Integer, Location>> dependencyLocation = new HashMap<>();

    public HashMap<Integer, HashMap<Integer, HashMap<String, HashMap<Integer, HashMap<Integer, Location>>>>> dependencyMapClassLevel = new HashMap<>();
    public HashMap<Integer, Integer> entityInClass = new HashMap<>();
    public DependencyData dependencyData;
    public FacadeResult facadeResult;
    public HashMap<Integer, Boolean> id2IsIntrusive = new HashMap<>();

    public HashMap<Integer, String> id2Ownership = new HashMap<>();

    public HashMap<Integer, Integer> id2MCRank = new HashMap<>();
    public HashMap<Integer, Boolean> id2IsDecoupling = new HashMap<>();
    public HashMap<String, Boolean> qn2IsDecoupling = new HashMap<>();

    public JsonProcessor(String dep_json) throws IOException {
        this.dependencyData = JSONUtil.fromJson(new File(dep_json), DependencyData.class);
    }

    public JsonProcessor(String dep_json, String facade_json, String ownership_csv, String measureResult_csv, Integer mode) throws Exception {
        this.dependencyData = JSONUtil.fromJson(new File(dep_json), DependencyData.class);
        if (mode == 4) {
            this.facadeResult = JSONUtil.fromJson(new File(facade_json), FacadeResult.class);
            for (EVE eve : facadeResult.getRes().getE2n()) {
                qn2IsDecoupling.putIfAbsent(eve.getSrc().getQualifiedName(), eve.getSrc().getIs_decoupling() > 0);
            }
            for (EVE eve : facadeResult.getRes().getN2e()) {
                qn2IsDecoupling.putIfAbsent(eve.getSrc().getQualifiedName(), eve.getSrc().getIs_decoupling() > 0);
            }
            for (EVE eve : facadeResult.getRes().getE()) {
                qn2IsDecoupling.putIfAbsent(eve.getSrc().getQualifiedName(), eve.getSrc().getIs_decoupling() > 0);
            }
            for (EVE eve : facadeResult.getRes().getN2n()) {
                qn2IsDecoupling.putIfAbsent(eve.getSrc().getQualifiedName(), eve.getSrc().getIs_decoupling() > 0);
            }
        } else if (mode == 2) {
            List<OwnerShipDTO> ownerShipDTO = CSVUtil.readCSV(ownership_csv, OwnerShipDTO.class);
            for (OwnerShipDTO ownerShip : ownerShipDTO) {
                id2IsIntrusive.put(ownerShip.getId(), ownerShip.getIsIntrusive() > 0);
            }
        } else if (mode == 3) {
            List<OwnerShipDTO> ownerShipDTO = CSVUtil.readCSV(ownership_csv, OwnerShipDTO.class);
            for (OwnerShipDTO ownerShip : ownerShipDTO) {
                id2IsIntrusive.put(ownerShip.getId(), ownerShip.getIsIntrusive() > 0);
                id2Ownership.put(ownerShip.getId(), identifyEntityOwnerShip(ownerShip));

            }

            List<MeasureResultDTO> measureResultDTOS = CSVUtil.readCSV(measureResult_csv, MeasureResultDTO.class);
            for (MeasureResultDTO measureResultDTO : measureResultDTOS) {
                id2MCRank.put(measureResultDTO.getId(), measureResultDTO.getRank());
            }
        }
    }


    public void resolveEntity() {
        assert dependencyData != null;
        List<Variable> variables = dependencyData.getVariables();

        // Add each entity to the corresponding list
        for (int i = 0; i < variables.size(); i++) {
            Variable variable = variables.get(i);
            id2IsIntrusive.putIfAbsent(i, false); // 存在不匹配
            id2Ownership.putIfAbsent(i, "error");
            id2IsDecoupling.putIfAbsent(i, false); // 存在不匹配
            qn2IsDecoupling.putIfAbsent(variable.getQualifiedName(), false);
            id2abstractEntity.put(i, initAbstractEntity(variable, id2IsIntrusive.get(i), qn2IsDecoupling.get(variable.getQualifiedName()), id2Ownership.get(i)));
            if (!variable.getExternal()) {
                switch (variable.getCategory()) {
                    case "Enum":
                    case "Interface":
                    case "Class":
                        TypeEntity typeEntity = new TypeEntity(id2abstractEntity.get(i));
                        id2typeEntity.put(i, typeEntity);
                        EntityIdentifier entityIdentifier = new EntityIdentifier(typeEntity.qualifiedName, typeEntity.file, typeEntity.modifier, typeEntity.isIntrusive, typeEntity.location);
                        typeEntity.setEntityIdentifier(entityIdentifier);
                        object_typeEntities.computeIfAbsent(typeEntity.qualifiedName, k -> new ArrayList<>());
                        object_typeEntities.get(typeEntity.qualifiedName).add(typeEntity);
                        break;
                    case "Variable":
                        id2varEntity.put(i, new VarEntity(id2abstractEntity.get(i)));
                        break;
                    case "Method":
                        id2funcImplEntity.put(i, new FuncImplEntity(id2abstractEntity.get(i)));
                        List<String> parameterNames = List.of(variable.getParameter().getNames().split(" "));
                        int parameterCount = parameterNames.size();
                        List<String> parameterTypes = List.of(variable.getParameter().getTypes().split(" ", parameterCount));
                        for (int temp = 0; temp < parameterTypes.size(); temp++) {
                            id2funcImplEntity.get(i).getParameters().add(parameterTypes.get(temp) + " " + parameterNames.get(temp));
                        }
                        break;
                    case "Package":
                        id2packageEntity.put(i, new PackageEntity(id2abstractEntity.get(i)));
                        qn2packageEntity.put(id2packageEntity.get(i).qualifiedName, id2packageEntity.get(i));
                        break;
                    case "File":
                        id_fileEntity.put(i, new FileEntity(id2abstractEntity.get(i)));
                        qn2fileEntity.put(PathUtil.getLast2StrByDot(variable.getQualifiedName()), new FileEntity(id2abstractEntity.get(i)));

                        break;
                }
            }

        }
        // 处理包的层级关系
        for (PackageEntity packageEntity : id2packageEntity.values()) {
            resolvePackageRelation(packageEntity);
        }

        for (FuncImplEntity funcImplEntity : id2funcImplEntity.values()) {
            Integer parentId = resolveOriginEntity(funcImplEntity.parentId);
            if (parentId != null) {
                funcImplEntity.setOriginEntity(id2typeEntity.get(parentId));
                id2typeEntity.get(parentId).getFuncImplEntities().add(funcImplEntity);
                entityInClass.put(funcImplEntity.id, parentId);
            }
        }
        for (VarEntity varEntity : id2varEntity.values()) {
            Integer parentId = resolveOriginEntity(varEntity.parentId);
            if (parentId != null) {
                varEntity.setOriginEntity(id2typeEntity.get(parentId));
                id2typeEntity.get(parentId).getVarEntities().add(varEntity);
                entityInClass.put(varEntity.id, parentId);
            }
        }
    }


    public void resolvePackageRelation(PackageEntity packageEntity) {
        if (packageEntity.getParentId() != -1) {
            if (id2packageEntity.get(packageEntity.getParentId()) != null) {
                id2packageEntity.get(packageEntity.getParentId()).getChildrenPackages().add(packageEntity);
                packageEntity.setFatherPackage(id2packageEntity.get(packageEntity.getParentId()));
            }
        } else {
            packageEntity.setTopPackage(true);
        }
    }

    public Integer resolveOriginEntity(Integer parentId) {
        if (parentId != -1) {
            if (id2abstractEntity.get(parentId).getCategory().equals("Class") || id2abstractEntity.get(parentId).getCategory().equals("Interface") || id2abstractEntity.get(parentId).getCategory().equals("Enum")) {
                return parentId;
            } else {
                resolveOriginEntity(id2abstractEntity.get(parentId).parentId);
            }
        }
        return null;
    }

    public void resolveDetails() {
        List<Cells> cellsArray = dependencyData.getCells();
        HashMap<String, MutableInt> relCount = new HashMap<>();
        for (Cells cellsDTO : cellsArray) {
            Integer from = cellsDTO.getSrc();
            Integer to = cellsDTO.getDest();
            Values values = cellsDTO.getValues();

            if (values.getLocation() != null) {
                Location location = values.getLocation();
                if (values.getInherit() != null) {
                    addToDependencyMap(from, to, "Inherit", location);
                    TypeEntity from_entity = id2typeEntity.get(from);
                    TypeEntity to_entity = id2typeEntity.get(to);
                    super2SubsInherit.computeIfAbsent(to_entity, k -> new ArrayList<>());
                    super2SubsInherit.get(to_entity).add(from_entity);
                    subSuperInherit.put(from_entity, to_entity);

                    hierarchyRelated.computeIfAbsent(to, k -> new ArrayList<>());
                    hierarchyRelated.get(to).add(from);

                    hierarchyRelated.computeIfAbsent(from, k -> new ArrayList<>());
                    hierarchyRelated.get(from).add(to);

                    addRelCount(relCount, "Inherit");
                } else if (values.getImplement() != null) {
                    addToDependencyMap(from, to, "Implement", location);
                    TypeEntity from_entity = id2typeEntity.get(from);
                    TypeEntity to_entity = id2typeEntity.get(to);
                    subSuperImplement.computeIfAbsent(from_entity, k -> new ArrayList<>());
                    subSuperImplement.get(from_entity).add(to_entity);

                    hierarchyRelated.computeIfAbsent(to, k -> new ArrayList<>());
                    hierarchyRelated.get(to).add(from);

                    hierarchyRelated.computeIfAbsent(from, k -> new ArrayList<>());
                    hierarchyRelated.get(from).add(to);

                    addRelCount(relCount, "Implement");

                } else if (values.getContain() != null) {
                    addToDependencyMap(from, to, "Contain", location);
                    String fromEntityType = id2abstractEntity.get(from).category;
                    String toEntityType = id2abstractEntity.get(to).category;
                    if (fromEntityType.equals("Package")) {
                        if (toEntityType.equals("File")) {
                            PackageEntity packageEntity = id2packageEntity.get(from);
                            FileEntity fileEntity = id_fileEntity.get(to);
                            packageEntity.getFiles().add(fileEntity);

                            addRelCount(relCount, "Contain");
                        }
                    }


                } else if (values.getDefine() != null) {
                    addToDependencyMap(from, to, "Define", location);
                    String fromEntityType = id2abstractEntity.get(from).category;
                    String toEntityType = id2abstractEntity.get(to).category;
                    switch (fromEntityType) {
                        case "Method":
                            if (toEntityType.equals("Variable")) {
                                FuncImplEntity fromFuncImplEntity = id2funcImplEntity.get(from);
                                VarEntity toVarEntity = id2varEntity.get(to);
                            }
                            break;
                        case "Class":
                        case "Interface":
                        case "Enum":
                            TypeEntity fromTypeEntity = id2typeEntity.get(from);
                            switch (toEntityType) {
                                case "Variable":
                                    VarEntity toVarEntity = id2varEntity.get(to);
                                    toVarEntity.setOriginEntity(fromTypeEntity);
                                    fromTypeEntity.getVarEntities().add(toVarEntity);
                                    entityInClass.put(to, from);
                                    break;
                                case "Method":
                                    FuncImplEntity toFuncImplEntity = id2funcImplEntity.get(to);
                                    toFuncImplEntity.setOriginEntity(fromTypeEntity);
                                    fromTypeEntity.getFuncImplEntities().add(toFuncImplEntity);
                                    entityInClass.put(to, from);
                                    break;
                                case "Class":
                                case "Interface":
                                    TypeEntity toTypeEntity = id2typeEntity.get(to);
                                    fromTypeEntity.getContainTypeEntity().add(toTypeEntity);
                                    break;
                            }
                            addRelCount(relCount, "Define");
                            break;
                    }


                } else if (values.getParameter() != null) {
                    addToDependencyMap(from, to, "Parameter", location);
                    FuncImplEntity fromFuncImplEntity = id2funcImplEntity.get(from);
                    VarEntity toVarEntity = id2varEntity.get(to);
                    if (fromFuncImplEntity != null && toVarEntity != null) {
                        fromFuncImplEntity.getParameterVarEntity().add(toVarEntity);
                        addRelCount(relCount, "Parameter");
                    }


                } else if (values.getReturn() != null) {
                    addToDependencyMap(from, to, "Return", location);
                    FuncImplEntity fromFuncImplEntity = id2funcImplEntity.get(from);
                    VarEntity toVarEntity = id2varEntity.get(to);
                    if (fromFuncImplEntity != null && toVarEntity != null) {
                        fromFuncImplEntity.getParameterVarEntity().add(toVarEntity);
                        addRelCount(relCount, "Return");
                    }


                } else if (values.getTyped() != null) {
                    addToDependencyMap(from, to, "Typed", location);
                    VarEntity fromVarEntity = id2varEntity.get(from);
                    TypeEntity toTypeEntity = id2typeEntity.get(to);
//                        System.out.println(fromVarEntity.rawType);
                    if (fromVarEntity.getOriginEntity() instanceof TypeEntity) {
                        // 说明是成员变量
                        TypeEntity fromTypeEntity = (TypeEntity) fromVarEntity.getOriginEntity();
                        fromTypeEntity.getContainTypeEntity().add(toTypeEntity);
                        addRelCount(relCount, "Typed");
                    }

                } else if (values.getCall() != null) {
                    addToDependencyMap(from, to, "Call", location);
                    if (id2funcImplEntity.get(from) != null && id2funcImplEntity.get(to) != null) {
                        id2funcImplEntity.get(from).getCallSet().add(id2funcImplEntity.get(to));
                        if (id2funcImplEntity.get(to).getOriginEntity() != null && id2typeEntity.get(id2funcImplEntity.get(to).getOriginEntity().id) != null) {
                            id2funcImplEntity.get(from).getCallClassSet().add(id2typeEntity.get(id2funcImplEntity.get(to).getOriginEntity().id));
                        }
                        if (id2funcImplEntity.get(from).getOriginEntity() != null && id2typeEntity.get(id2funcImplEntity.get(from).getOriginEntity().id) != null) {
                            id2funcImplEntity.get(to).getCallByClassSet().add(id2typeEntity.get(id2funcImplEntity.get(from).getOriginEntity().id));
                        }
                        id2funcImplEntity.get(to).getCallBySet().add(id2funcImplEntity.get(from));
                        addRelCount(relCount, "Call");

                    }
                } else if (values.getSet() != null) {
                    addToDependencyMap(from, to, "Set", location);
                    addRelCount(relCount, "Set");

                } else if (values.getUseVar() != null) {
                    addToDependencyMap(from, to, "UseVar", location);
                    if (id2funcImplEntity.get(from) != null && id2varEntity.get(to) != null) {
                        List<String> primitiveTypes = Arrays.asList("void", "int", "int-", "long", "float", "double", "String", "boolean", "Boolean", "Pattern");
                        FuncImplEntity fromFunc = id2funcImplEntity.get(from);
                        VarEntity toVar = id2varEntity.get(to);
                        fromFunc.getVariables().add(toVar);
                        if (!primitiveTypes.contains(toVar.getRawType())) {
                            if (!toVar.getRawType().contains(".")) {
                                EntityIdentifier toEntity = new EntityIdentifier(toVar.qualifiedName, toVar.file, null, toVar.rawType, toVar.isIntrusive, null, toVar.location);
                                fromFunc.getFuncForeignAttributesSet().add(toEntity);
                                fromFunc.getFuncForeignAttributesClassSet().add(toVar.getRawType());
                            } else if (entityInClass.get(toVar.id) == null || (entityInClass.get(toVar.id) != null && toVar.getOriginEntity() != null && toVar.getOriginEntity() == fromFunc)) {
                                EntityIdentifier toEntity = new EntityIdentifier(toVar.qualifiedName, toVar.file, null, toVar.rawType, toVar.isIntrusive, null, toVar.location);
                                fromFunc.getFuncForeignAttributesSet().add(toEntity);
                                fromFunc.getFuncForeignAttributesClassSet().add(toVar.getRawType());
                            }
                        }
                        addRelCount(relCount, "UseVar");

                    }
                } else if (values.getOverride() != null) {
                    addToDependencyMap(from, to, "Override", location);
                    addRelCount(relCount, "Override");

                } else if (values.getReflect() != null) {
                    addToDependencyMap(from, to, "Reflect", location);
                    addRelCount(relCount, "Reflect");

                } else if (values.getModify() != null) {
                    addToDependencyMap(from, to, "Modify", location);
                    addRelCount(relCount, "Modify");

                } else if (values.getCast() != null) {
                    addToDependencyMap(from, to, "Cast", location);
                    addRelCount(relCount, "Cast");

                }
            } else {
                System.out.println("no loc!");
            }

        }

//        for (Map.Entry<String, MutableInt> entry : relCount.entrySet()) {
//            System.out.println(entry.getKey() + " : " + entry.getValue());
//        }
    }

    private static void addRelCount(HashMap<String, MutableInt> relCount, String Return) {
        MutableInt initValue = new MutableInt(1);
        MutableInt oldValue = relCount.put(Return, initValue);
        if (oldValue != null) {
            initValue.setValue(oldValue.getValue() + 1);
        }
    }

    private void addToDependencyMap(Integer from, Integer to, String relationType, Location location) {
        dependencyMapLocation.computeIfAbsent(from, k -> new HashMap<>());
        dependencyMapLocation.get(from).computeIfAbsent(relationType, k -> new HashMap<>());
        dependencyMapLocation.get(from).get(relationType).computeIfAbsent(to, k -> location);
    }


    public TypeEntity findTypeEntity(String typeEntityName, String file) {
        TypeEntity originTypeEntity = null;
        List<TypeEntity> typeEntities = object_typeEntities.get(typeEntityName);
        if (typeEntities != null) {
            if (typeEntities.size() == 1) {
                originTypeEntity = typeEntities.get(0);

            } else if (typeEntities.size() > 1) {
                for (TypeEntity typeEntity : typeEntities) {
                    if (typeEntity.qualifiedName.equals(typeEntityName) && typeEntity.file.equals(file)) {
                        originTypeEntity = typeEntity;
                    }
                }
            }
        }
        return originTypeEntity;
    }

    public void resolveHierarchyChain() {
        for (Map.Entry<TypeEntity, TypeEntity> entry : subSuperInherit.entrySet()) {
            String from_entity = entry.getKey().qualifiedName;
            String from_file = entry.getKey().file;
            Boolean from_IsIntrusive = entry.getKey().isIntrusive;
            String to_entity = entry.getValue().qualifiedName;
            String to_file = entry.getValue().file;
            Boolean to_IsIntrusive = entry.getValue().isIntrusive;

            Vertex fromVertex = createVertex(from_entity, from_file);
            Vertex toVertex = createVertex(to_entity, to_file);
            Edge edge = new Edge();
            edge.setSource(from_entity);
            edge.setSourceIsIntrusive(from_IsIntrusive);
            edge.setSourceFile(from_file);
            edge.setTarget(to_entity);
            edge.setTargetIsIntrusive(to_IsIntrusive);
            edge.setTargetFile(to_file);
            edge.setLabel("Inherit");

            hierarchyGraph.addEdge(fromVertex, toVertex, edge);
        }
        for (Map.Entry<TypeEntity, List<TypeEntity>> entry : subSuperImplement.entrySet()) {
            String from_entity = entry.getKey().qualifiedName;
            Boolean from_IsIntrusive = entry.getKey().isIntrusive;
            String from_file = entry.getKey().file;
            List<TypeEntity> to_entities = entry.getValue();
            for (TypeEntity to_typeEntity : to_entities) {
                String to_entity = to_typeEntity.qualifiedName;
                Boolean to_IsIntrusive = to_typeEntity.isIntrusive;
                String to_file = to_typeEntity.file;
                Vertex fromVertex = createVertex(from_entity, from_file);
                Vertex toVertex = createVertex(to_entity, to_file);
                Edge edge = new Edge();
                edge.setSource(from_entity);
                edge.setSourceIsIntrusive(from_IsIntrusive);
                edge.setSourceFile(from_file);
                edge.setTarget(to_entity);
                edge.setSourceIsIntrusive(to_IsIntrusive);
                edge.setTargetFile(to_file);
                edge.setLabel("Implement");
                hierarchyGraph.addEdge(fromVertex, toVertex, edge);
            }
        }

        AllDirectedPaths<Vertex, Edge> allPaths = new AllDirectedPaths<>(hierarchyGraph);
        for (Vertex vertex : hierarchyGraph.vertexSet()) {
            for (Vertex anotherVertex : hierarchyGraph.vertexSet()) {
                if (vertex != anotherVertex) {
                    List<GraphPath<Vertex, Edge>> fullRes = allPaths.getAllPaths(vertex, anotherVertex, true, hierarchyGraph.vertexSet().size() - 1);
                    TypeEntity targetTypeEntity = findTypeEntity(vertex.getObject(), vertex.getFile());
                    assert targetTypeEntity != null;
                    HashSet<EntityIdentifier> superTypeEntityIdentifiers;
                    if (targetTypeEntity.getSuperClassIdentifier() == null) {
                        superTypeEntityIdentifiers = new HashSet<>();
                    } else {
                        superTypeEntityIdentifiers = targetTypeEntity.getSuperClassIdentifier();
                    }

                    List<List<EntityDependencyRelationDetail>> multiHierarchyPath;
                    if (targetTypeEntity.getHierarchyChain() == null) {
                        multiHierarchyPath = new ArrayList<>();
                    } else {
                        multiHierarchyPath = targetTypeEntity.getHierarchyChain();
                    }

                    if (fullRes.size() == 1) {
                        List<EntityDependencyRelationDetail> hierarchyPaths = new ArrayList<>();
                        for (Edge edge : fullRes.get(0).getEdgeList()) {
                            EntityIdentifier singleSuperTypeEntityIdentifier = new EntityIdentifier(edge.getTarget(), edge.getTargetFile(), null, edge.getTargetIsIntrusive(), null);
                            superTypeEntityIdentifiers.add(singleSuperTypeEntityIdentifier);
                            EntityDependencyRelationDetail singleHierarchyPath = new EntityDependencyRelationDetail(edge.getSource(), edge.getSourceFile(), edge.getLabel(), edge.getTarget(), edge.getTargetFile(), null);
                            hierarchyPaths.add(singleHierarchyPath);
                        }
                        multiHierarchyPath.add(hierarchyPaths);
                    } else if (fullRes.size() >= 2) {
                        List<EntityDependencyRelationDetail> hierarchyPaths = new ArrayList<>();
                        for (GraphPath<Vertex, Edge> path : fullRes) {
                            List<Edge> edges = path.getEdgeList();
                            for (Edge edge : edges) {
                                EntityIdentifier singleSuperTypeEntityIdentifier = new EntityIdentifier(edge.getTarget(), edge.getTargetFile(), null, edge.getSourceIsIntrusive(), null);
                                superTypeEntityIdentifiers.add(singleSuperTypeEntityIdentifier);
                                EntityDependencyRelationDetail singleHierarchyPath = new EntityDependencyRelationDetail(edge.getSource(), edge.getSourceFile(), edge.getLabel(), edge.getTarget(), edge.getTargetFile(), null);
                                hierarchyPaths.add(singleHierarchyPath);
                            }
                            multiHierarchyPath.add(hierarchyPaths);
                        }
                    }
                    if (superTypeEntityIdentifiers.size() > 0) {
                        targetTypeEntity.setHierarchyChain(multiHierarchyPath);
                        targetTypeEntity.setSuperClassIdentifier(superTypeEntityIdentifiers);
                    }

                }
            }
        }
    }


    public Vertex createVertex(String object, String file) {
        for (Vertex vertex : hierarchyGraph.vertexSet()) {
            if (vertex.getObject().equals(object) && vertex.getFile().equals(file)) {
                return vertex;
            }
        }
        Vertex vertex = new Vertex();
        vertex.setObject(object);
        vertex.setFile(file);
        hierarchyGraph.addVertex(vertex);
        return vertex;
    }


    public void resolveDependencyMapClassLevel() {
        for (Map.Entry<Integer, HashMap<String, HashMap<Integer, Location>>> entry : dependencyMapLocation.entrySet()) {
            Integer fromEntityId = entry.getKey();

            if (entityInClass.get(fromEntityId) != null) { // 如果该实体属于某个类
                Integer fromEntityClass = entityInClass.get(fromEntityId); // 定位from实体所属的类
                HashMap<String, HashMap<Integer, Location>> relation2EntityLocationMap = entry.getValue();
                for (Map.Entry<String, HashMap<Integer, Location>> entry2 : relation2EntityLocationMap.entrySet()) {
                    String relationType = entry2.getKey();
                    HashMap<Integer, Location> toEntity2Location = entry2.getValue();
                    for (Map.Entry<Integer, Location> entry3 : toEntity2Location.entrySet()) {
                        Integer toEntityId = entry3.getKey();
                        Location location = entry3.getValue();
                        if (entityInClass.get(toEntityId) != null) {
                            Integer toEntityClass = entityInClass.get(toEntityId); // 定位to实体所属的类
                            dependencyMapClassLevel.computeIfAbsent(fromEntityClass, k -> new HashMap<>()); // from class
                            dependencyMapClassLevel.get(fromEntityClass).computeIfAbsent(toEntityClass, k -> new HashMap<>());  // to class
                            dependencyMapClassLevel.get(fromEntityClass).get(toEntityClass).computeIfAbsent(relationType, k -> new HashMap<>()); // 类内部的 实体调用关系
                            dependencyMapClassLevel.get(fromEntityClass).get(toEntityClass).get(relationType).computeIfAbsent(fromEntityId, k -> new HashMap<>()); // 依赖主体
                            dependencyMapClassLevel.get(fromEntityClass).get(toEntityClass).get(relationType).get(fromEntityId).computeIfAbsent(toEntityId, k -> location);
                        } else if (relationType.equals("Reflect")) {
                            if (id2abstractEntity.get(toEntityId).category.equals("Class") || id2abstractEntity.get(toEntityId).category.equals("Enum") || id2abstractEntity.get(toEntityId).category.equals("Interface")) {
//                                System.out.println("Reflect : Method to Class/Enum/Interface");
                                dependencyMapClassLevel.computeIfAbsent(fromEntityClass, k -> new HashMap<>()); // from class
                                dependencyMapClassLevel.get(fromEntityClass).computeIfAbsent(toEntityId, k -> new HashMap<>());  // to class
                                dependencyMapClassLevel.get(fromEntityClass).get(toEntityId).computeIfAbsent(relationType, k -> new HashMap<>()); // 类内部的 实体调用关系
                                dependencyMapClassLevel.get(fromEntityClass).get(toEntityId).get(relationType).computeIfAbsent(fromEntityId, k -> new HashMap<>()); // 依赖主体
                                dependencyMapClassLevel.get(fromEntityClass).get(toEntityId).get(relationType).get(fromEntityId).computeIfAbsent(toEntityId, k -> location);
                            }
                        }
                    }
                }
            } else if (id2typeEntity.get(fromEntityId) != null) {
                HashMap<String, HashMap<Integer, Location>> relation2EntityLocationMap = entry.getValue();
                for (Map.Entry<String, HashMap<Integer, Location>> entry2 : relation2EntityLocationMap.entrySet()) {
                    String relationType = entry2.getKey();
                    if (relationType.equals("Implement") || relationType.equals("Inherit")) {
                        HashMap<Integer, Location> toEntityIds = entry2.getValue();
                        for (Map.Entry<Integer, Location> entry3 : toEntityIds.entrySet()) {
                            Integer toEntityId = entry3.getKey();
                            Location location = entry3.getValue();
                            if (id2typeEntity.get(toEntityId) != null) {
                                dependencyMapClassLevel.computeIfAbsent(fromEntityId, k -> new HashMap<>()); // from class
                                dependencyMapClassLevel.get(fromEntityId).computeIfAbsent(toEntityId, k -> new HashMap<>());  // to class
                                dependencyMapClassLevel.get(fromEntityId).get(toEntityId).computeIfAbsent(relationType, k -> new HashMap<>()); // 类内部的 实体调用关系
                                dependencyMapClassLevel.get(fromEntityId).get(toEntityId).get(relationType).computeIfAbsent(fromEntityId, k -> new HashMap<>()); // 依赖主体
                                dependencyMapClassLevel.get(fromEntityId).get(toEntityId).get(relationType).get(fromEntityId).computeIfAbsent(toEntityId, k -> location);
                            }
                        }
                    }
                }
            }


        }
    }

    private AbstractEntity initAbstractEntity(Variable variable, Boolean isIntrusive, Boolean isDecoupling, String ownership) {
        return new AbstractEntity(variable.getId(), variable.getQualifiedName(), variable.getFile(), variable.getCategory(), variable.getModifiers(), variable.getRawType(), isIntrusive, isDecoupling, ownership, variable.getLocation(), variable.getParentId());
    }

    private String identifyEntityOwnerShip(OwnerShipDTO ownerShipDTO) {
        if (ownerShipDTO.getNot_aosp() == 1) {
            return "intrusively native";
        } else if (ownerShipDTO.getNot_aosp() == 0) {
            if (ownerShipDTO.getIsIntrusive() == 1) {
                return "intrusively native";
            } else if (ownerShipDTO.getIsIntrusive() == 0) {
                if (ownerShipDTO.getOld_aosp() == 1) {
                    return "obsoletely native";
                } else
                    return "actively native";
            }
        }
        return "error";
    }

}
