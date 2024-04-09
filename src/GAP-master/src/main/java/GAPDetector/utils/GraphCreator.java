package GAPDetector.utils;

import GAPDetector.Storage;
import GAPDetector.entities.EntityIdentifier;
import GAPDetector.entities.FileEntity;
import GAPDetector.entities.PackageEntity;
import GAPDetector.json.inputDTO.dependencyModel.Location;
import GAPDetector.json.inputDTO.rule.EdgeLevel;
import GAPDetector.json.inputDTO.rule.GraphTarget;
import GAPDetector.json.outputDTO.relations.EntityDependencyRelationDetailWithClass;
import GAPDetector.json.outputDTO.relations.EntityDependencyRelationDetail;
import GAPDetector.json.outputDTO.relations.PackageDependencyRelationDetail;
import GAPDetector.json.util.PathUtil;
import GAPDetector.model.*;
import lombok.Getter;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.SimpleDirectedGraph;

import java.util.*;


public class GraphCreator {
    // packageDependencyRelationDetail中存储了包与包之间的依赖关系。
    @Getter
    private List<PackageDependencyRelationDetail> packageDependencyRelationDetails = new ArrayList<>();
    // details中存储了类与类之间的依赖关系，DependencyRelationDetail则存储了实体与实体之间的依赖关系。 * 需要修改
    @Getter
    private HashMap<Integer, HashMap<Integer, List<EntityDependencyRelationDetail>>> details = new HashMap<>();
    @Getter
    public DefaultDirectedGraph<Vertex, Edge> classGraph;
    @Getter
    private SimpleDirectedGraph<Vertex, Edge> packageGraph;
    @Getter
    private DefaultDirectedGraph<Vertex, Edge> entityGraph;

    @Getter
    private HashMap<Integer, SimpleDirectedGraph<Vertex, Edge>> packageGraphList = new HashMap<>();
    @Getter
    private HashMap<Integer, List<PackageDependencyRelationDetail>> sameLevelPackageDependencyRelationDetails = new HashMap<>();
    // 边的类型全集？
    @Getter
    private List<String> edgeRelations = new ArrayList<>();
    // 结点类别
    @Getter
    private String vertexCategory;
    // id与Vertex的映射
    @Getter
    private HashMap<Integer, Vertex> id2vertex = new HashMap<>();
    // object与Vertex的映射
    @Getter
    private HashMap<String, Vertex> object2vertex = new HashMap<>();
    // 依赖模型经处理后的各种映射关系
    private Storage storage;
    // 图目标
    private GraphTarget graphTarget;

    // 主类：解析图目标并生成对应的图
    public void workflow() {
        resolveGraphTarget();
        createGraph();
    }

    // 解析图目标
    private void resolveGraphTarget() {
        List<String> allEdgeRelations = graphTarget.getEdgeRelations();
        EdgeLevel edgeLevel = graphTarget.getEdgeLevel();
        if (edgeLevel.getAll() != null) {
            edgeRelations.addAll(allEdgeRelations);
        } else if (edgeLevel.getExclude() != null) {
            for (String excludeEdgeRelation : edgeLevel.getExclude()) {
                allEdgeRelations.remove(excludeEdgeRelation);
            }
            edgeRelations.addAll(allEdgeRelations);
        } else if (edgeLevel.getInclude() != null) {
            edgeRelations.addAll(edgeLevel.getInclude());
        }
        vertexCategory = graphTarget.getVertexLevel();
    }

    private void createGraph() {
        switch (vertexCategory) {
            case "entity":
                createEntityGraph();
                break;
            case "class":
                createClassGraph();
                break;
            case "package":
                createPackageGraph();
                break;
        }

    }


    public GraphCreator(Storage storage, GraphTarget graphTarget) {
        this.storage = storage;
        this.graphTarget = graphTarget;
    }

    private void createEntityGraph() {
        entityGraph = new DefaultDirectedGraph<>(Edge.class);
        for (Map.Entry<Integer, HashMap<String, HashMap<Integer, Location>>> entry : storage.dependencyMapLocation.entrySet()) {

            Integer fromEntityId = entry.getKey();
            HashMap<String, HashMap<Integer, Location>> relationToEntities = entry.getValue();
            for (Map.Entry<String, HashMap<Integer, Location>> entry2 : relationToEntities.entrySet()) {
                String relationType = entry2.getKey();
                HashMap<Integer, Location> toEntities = entry2.getValue();
                if (edgeRelations.contains(relationType) && !toEntities.isEmpty()) {
                    for (Map.Entry<Integer, Location> entry3 : toEntities.entrySet()) {
                        Integer toEntityId = entry3.getKey();
                        Vertex fromVertex = createVertex(entityGraph, fromEntityId);
                        Vertex toVertex = createVertex(entityGraph, toEntityId);

                        String fromEntityQN = storage.id_abstractEntity.get(fromEntityId).qualifiedName;
                        String fromEntityCG = storage.id_abstractEntity.get(fromEntityId).category;
                        String fromEntityFile = storage.id_abstractEntity.get(fromEntityId).file;
                        String fromEntityModifier = storage.id_abstractEntity.get(fromEntityId).modifier;
                        Location fromEntityLocation = storage.id_abstractEntity.get(fromEntityId).location;
                        Integer fromEntityParentId = storage.id_abstractEntity.get(fromEntityId).parentId;

                        String toEntityQN = storage.id_abstractEntity.get(toEntityId).qualifiedName;
                        String toEntityCG = storage.id_abstractEntity.get(toEntityId).category;
                        String toEntityFile = storage.id_abstractEntity.get(toEntityId).file;
                        String toEntityModifier = storage.id_abstractEntity.get(toEntityId).modifier;
                        Location toEntityLocation = storage.id_abstractEntity.get(toEntityId).location;
                        Integer toEntityParentId = storage.id_abstractEntity.get(toEntityId).parentId;

                        EntityDependencyRelationDetail entityDependencyRelationDetail = new EntityDependencyRelationDetail(
                                fromEntityId, fromEntityQN, fromEntityCG, fromEntityFile, fromEntityModifier, fromEntityLocation, fromEntityParentId,
                                relationType,
                                toEntityId, toEntityQN, toEntityCG, toEntityFile, toEntityModifier, toEntityLocation, toEntityParentId,
                                null, null, storage.dependencyMapLocation.get(fromEntityId).get(relationType).get(toEntityId));

                        Edge edge = new Edge();
                        edge.setSourceId(fromEntityId);
                        edge.setTargetId(toEntityId);
                        edge.setSourceType(storage.id_abstractEntity.get(fromEntityId).category);
                        edge.setTargetType(storage.id_abstractEntity.get(toEntityId).category);
                        edge.setLabel(relationType);
                        edge.setEntityDependencyRelationDetail(entityDependencyRelationDetail);

                        entityGraph.addEdge(fromVertex, toVertex, edge);

                    }
                }
            }
        }
    }


    private void createClassGraph() {
        classGraph = new DefaultDirectedGraph<>(Edge.class);

        for (Map.Entry<Integer, HashMap<Integer, HashMap<String, HashMap<Integer, HashMap<Integer, Location>>>>> entry : storage.dependencyMapClassLevel.entrySet()) {
            Integer fromClassEntityId = entry.getKey();
            HashMap<Integer, HashMap<String, HashMap<Integer, HashMap<Integer, Location>>>> toClass_relation_fromEntity_toEntity_loc = entry.getValue();
            for (Map.Entry<Integer, HashMap<String, HashMap<Integer, HashMap<Integer, Location>>>> entry2 : toClass_relation_fromEntity_toEntity_loc.entrySet()) {
                Integer toClassEntityId = entry2.getKey();
                List<EntityDependencyRelationDetail> entityDependencyRelationDetails = new ArrayList<>();
                HashMap<String, HashMap<Integer, HashMap<Integer, Location>>> relation_fromEntity_toEntity = entry2.getValue();
                for (Map.Entry<String, HashMap<Integer, HashMap<Integer, Location>>> entry3 : relation_fromEntity_toEntity.entrySet()) {
                    String relation = entry3.getKey();
                    if (edgeRelations.contains(relation) && !entry3.getValue().isEmpty()) {
                        Vertex fromVertex = createVertex(classGraph, fromClassEntityId);
                        Vertex toVertex = createVertex(classGraph, toClassEntityId);
                        String fromClassEntityQN = storage.id_abstractEntity.get(fromClassEntityId).qualifiedName;
                        String fromClassEntityFile = storage.id_abstractEntity.get(fromClassEntityId).file;
                        String toClassEntityQN = storage.id_abstractEntity.get(toClassEntityId).qualifiedName;
                        String toClassEntityFile = storage.id_abstractEntity.get(toClassEntityId).file;


                        Edge edge = new Edge();
                        edge.setSourceId(fromClassEntityId);
                        edge.setTargetId(toClassEntityId);
                        edge.setSource(fromClassEntityQN);
                        edge.setSourceFile(fromClassEntityFile);
                        edge.setTarget(toClassEntityQN);
                        edge.setTargetFile(toClassEntityFile);
                        edge.setLabel(relation);

                        HashMap<Integer, HashMap<Integer, Location>> fromEntity_toEntity_loc = entry3.getValue();
                        for (Map.Entry<Integer, HashMap<Integer, Location>> entry4 : fromEntity_toEntity_loc.entrySet()) {
                            Integer fromEntityId = entry4.getKey();
                            HashMap<Integer, Location> toEntities = entry4.getValue();
                            for (Map.Entry<Integer, Location> entry5 : toEntities.entrySet()) {
                                Integer toEntityId = entry5.getKey();
                                RelationAttributesHelper relationAttributesHelper = new RelationAttributesHelper(storage);
                                Integer mode = relationAttributesHelper.detectModes(storage.id_abstractEntity.get(fromEntityId), storage.id_abstractEntity.get(toEntityId));
                                Integer intrusiveType = relationAttributesHelper.detectIntrusiveType(storage.id_abstractEntity.get(fromEntityId), storage.id_abstractEntity.get(toEntityId));
                                String fromEntityQN = storage.id_abstractEntity.get(fromEntityId).qualifiedName;
                                String fromEntityCG = storage.id_abstractEntity.get(fromEntityId).category;
                                String fromEntityFile = storage.id_abstractEntity.get(fromEntityId).file;
                                String fromEntityModifier = storage.id_abstractEntity.get(fromEntityId).modifier;
                                Location fromEntityLocation = storage.id_abstractEntity.get(fromEntityId).location;
                                Integer fromEntityParentId = storage.id_abstractEntity.get(fromEntityId).parentId;

                                String toEntityQN = storage.id_abstractEntity.get(toEntityId).qualifiedName;
                                String toEntityCG = storage.id_abstractEntity.get(toEntityId).category;
                                String toEntityFile = storage.id_abstractEntity.get(toEntityId).file;
                                String toEntityModifier = storage.id_abstractEntity.get(toEntityId).modifier;
                                Location toEntityLocation = storage.id_abstractEntity.get(toEntityId).location;
                                Integer toEntiytParentId = storage.id_abstractEntity.get(toEntityId).parentId;

                                EntityDependencyRelationDetail entityDependencyRelationDetail = new EntityDependencyRelationDetail(
                                        fromEntityId, fromEntityQN, fromEntityCG, fromEntityFile, fromEntityModifier, fromEntityLocation, fromEntityParentId,
                                        relation,
                                        toEntityId, toEntityQN, toEntityCG, toEntityFile, toEntityModifier, toEntityLocation, toEntiytParentId,
                                        mode, intrusiveType, storage.dependencyMapLocation.get(fromEntityId).get(relation).get(toEntityId));
                                entityDependencyRelationDetails.add(entityDependencyRelationDetail);
                            }
                        }
                        edge.setEntityDependencyRelationClassDetails(entityDependencyRelationDetails);

                        classGraph.addEdge(fromVertex, toVertex, edge);

                    }
                }
                details.computeIfAbsent(fromClassEntityId, k -> new HashMap<>());
                details.get(fromClassEntityId).computeIfAbsent(toClassEntityId, k -> new ArrayList<>());
                details.get(fromClassEntityId).get(toClassEntityId).addAll(entityDependencyRelationDetails);
            }
        }
    }

    private void createPackageGraph() {
        packageGraph = new SimpleDirectedGraph<>(Edge.class);
        HashSet<EntityDependencyRelationDetailWithClass> entityDependencyRelationDetailWithClasses = new HashSet<>();


        for (Map.Entry<Integer, HashMap<Integer, HashMap<String, HashMap<Integer, HashMap<Integer, Location>>>>> entry : storage.dependencyMapClassLevel.entrySet()) {
            Integer fromClassId = entry.getKey();
            HashMap<Integer, HashMap<String, HashMap<Integer, HashMap<Integer, Location>>>> toClassId_relation_fromEntityId_toEntityIds = entry.getValue();
            for (Map.Entry<Integer, HashMap<String, HashMap<Integer, HashMap<Integer, Location>>>> entry2 : toClassId_relation_fromEntityId_toEntityIds.entrySet()) {
                Integer toClassId = entry2.getKey();
                HashMap<String, HashMap<Integer, HashMap<Integer, Location>>> relation_fromEntityId_toEntityIds = entry2.getValue();
                for (Map.Entry<String, HashMap<Integer, HashMap<Integer, Location>>> entry3 : relation_fromEntityId_toEntityIds.entrySet()) {
                    String relation = entry3.getKey();
                    if (edgeRelations.contains(relation) && !entry3.getValue().isEmpty()) {
                        String fromFile = storage.id_typeEntity.get(fromClassId).file;
                        String toFile = storage.id_typeEntity.get(toClassId).file;
//                        FileEntity fromFileEntity = findFile(fromFile);
//                        FileEntity toFileEntity = findFile(toFile);

                        FileEntity fromFileEntity = storage.qn_file.get(PathUtil.getLastStrByPathDelimiter(fromFile));
                        FileEntity toFileEntity = storage.qn_file.get(PathUtil.getLastStrByPathDelimiter(toFile));

                        PackageEntity fromPackage = findPackage(fromFileEntity);
                        PackageEntity toPackage = findPackage(toFileEntity);

                        // test for same level package dependency graph
                        if (fromPackage != null && toPackage != null && fromPackage != toPackage) {
                            Vertex fromVertex = createVertex(packageGraph, fromPackage.getId(), fromPackage.getQualifiedName());
                            Vertex toVertex = createVertex(packageGraph, toPackage.getId(), toPackage.getQualifiedName());
                            Edge edge = new Edge();
                            edge.setSourcePackage(fromPackage.getQualifiedName());
                            edge.setTargetPackage(toPackage.getQualifiedName());
                            edge.setLabel(relation);

                            // 实体依赖关系处理
                            List<EntityDependencyRelationDetail> entityDependencyRelationDetails = new ArrayList<>();
                            // 1. entity level
                            HashMap<Integer, HashMap<Integer, Location>> fromEntityId_toEntityIds = entry3.getValue();
                            for (Map.Entry<Integer, HashMap<Integer, Location>> entry4 : fromEntityId_toEntityIds.entrySet()) {
                                Integer fromEntityId = entry4.getKey();
                                HashMap<Integer, Location> toEntityIds = entry4.getValue();
                                for (Map.Entry<Integer, Location> entry5 : toEntityIds.entrySet()) {
                                    Integer toEntityId = entry5.getKey();
                                    RelationAttributesHelper relationAttributesHelper = new RelationAttributesHelper(storage);
                                    Integer mode = relationAttributesHelper.detectModes(storage.id_abstractEntity.get(fromEntityId), storage.id_abstractEntity.get(toEntityId));
                                    Integer intrusiveType = relationAttributesHelper.detectIntrusiveType(storage.id_abstractEntity.get(fromEntityId), storage.id_abstractEntity.get(toEntityId));
                                    EntityDependencyRelationDetail entityDependencyRelationDetail = new EntityDependencyRelationDetail(
                                            fromEntityId, storage.id_abstractEntity.get(fromEntityId).qualifiedName, storage.id_abstractEntity.get(fromEntityId).category,
                                            storage.id_abstractEntity.get(fromEntityId).file, storage.id_abstractEntity.get(fromEntityId).modifier,
                                            storage.id_abstractEntity.get(fromEntityId).location, storage.id_abstractEntity.get(fromEntityId).parentId,
                                            relation,
                                            toEntityId, storage.id_abstractEntity.get(toEntityId).qualifiedName, storage.id_abstractEntity.get(toEntityId).category,
                                            storage.id_abstractEntity.get(toEntityId).file, storage.id_abstractEntity.get(toEntityId).modifier,
                                            storage.id_abstractEntity.get(toEntityId).location, storage.id_abstractEntity.get(toEntityId).parentId,
                                            mode, intrusiveType, storage.dependencyMapLocation.get(fromEntityId).get(relation).get(toEntityId));
                                    entityDependencyRelationDetails.add(entityDependencyRelationDetail);
                                }
                            }

                            edge.setEntityDependencyRelationPackageDetails(entityDependencyRelationDetails);
                            packageGraph.addEdge(fromVertex, toVertex, edge);


                            // 2. class level
                            EntityDependencyRelationDetailWithClass entityDependencyRelationDetailWithClass = createClassDependencyRelation(fromClassId, toClassId);
                            EntityIdentifier fromClass = new EntityIdentifier(storage.id_abstractEntity.get(fromClassId).qualifiedName, storage.id_abstractEntity.get(fromClassId).file, storage.id_abstractEntity.get(fromClassId).modifier, null, storage.id_abstractEntity.get(fromClassId).location);
                            entityDependencyRelationDetailWithClass.setFromClass(fromClass);
                            EntityIdentifier toClass = new EntityIdentifier(storage.id_abstractEntity.get(toClassId).qualifiedName, storage.id_abstractEntity.get(toClassId).file, storage.id_abstractEntity.get(toClassId).modifier, null, storage.id_abstractEntity.get(toClassId).location);
                            entityDependencyRelationDetailWithClass.setToClass(toClass);
                            entityDependencyRelationDetailWithClasses.add(entityDependencyRelationDetailWithClass);
                            entityDependencyRelationDetailWithClass.setEntityDependencyRelationDetails(entityDependencyRelationDetails);

                            // 3. package level
                            PackageDependencyRelationDetail packageDependencyRelationDetail = createPackageDependencyRelation(fromPackage.getId(), toPackage.getId());
                            packageDependencyRelationDetail.setFromPackage(fromPackage.getQualifiedName());
                            packageDependencyRelationDetail.setToPackage(toPackage.getQualifiedName());
                            packageDependencyRelationDetail.setEntityDependencyRelationDetailWithClasses(entityDependencyRelationDetailWithClasses);
                            packageDependencyRelationDetails.add(packageDependencyRelationDetail);

                        }
                    }
                }
            }
        }
    }



    // search FileEntity by filename
    private FileEntity findFile(String fileName) {
        for (FileEntity fileEntity : storage.id_file.values()) {
            if (PathUtil.getLast2StrByDot(fileEntity.getQualifiedName()).equals(PathUtil.getLastStrByPathDelimiter(fileName))) {
                return fileEntity;
            }
        }
        return null;
    }

    // search PackageEntity by FileEntity
    private PackageEntity findPackage(FileEntity fileEntity) {
        for (PackageEntity packageEntity : storage.id_package.values()) {
            if (packageEntity.getFiles().contains(fileEntity)) {
                return packageEntity;
            }
        }
        return null;
    }

    // create Vertex in graph by id
    private Vertex createVertex(DefaultDirectedGraph<Vertex, Edge> graph, Integer id) {
        if (id2vertex.get(id) != null) {
            return id2vertex.get(id);
        } else {
            Vertex vertex = new Vertex();
            vertex.setId(id);
            id2vertex.put(id, vertex);
            graph.addVertex(vertex);
            return vertex;
        }
    }



    // create Vertex in graph by object name
    private Vertex createVertex(DefaultDirectedGraph<Vertex, Edge> graph, String object) {
        if (object2vertex.get(object) != null) {
            return object2vertex.get(object);
        } else {
            Vertex vertex = new Vertex();
            vertex.setObject(object);
            object2vertex.put(object, vertex);
            graph.addVertex(vertex);
            return vertex;
        }
    }

    // create Vertex in graph by object name and id
    private Vertex createVertex(SimpleDirectedGraph<Vertex, Edge> graph, Integer id, String object) {
        if (id2vertex.get(id) != null) {
            return id2vertex.get(id);
        } else {
            Vertex vertex = new Vertex();
            vertex.setId(id);
            vertex.setObject(object);
            id2vertex.put(id, vertex);
            graph.addVertex(vertex);
            return vertex;
        }
    }


    private HashMap<Integer, HashMap<Integer, EntityDependencyRelationDetailWithClass>> clsDRDMap = new HashMap<>();
    private HashMap<Integer, HashMap<Integer, PackageDependencyRelationDetail>> pkgDRDMap = new HashMap<>();

    // create ClassDependencyRelation
    private EntityDependencyRelationDetailWithClass createClassDependencyRelation(Integer fromClassId, Integer toClassId) {
        clsDRDMap.computeIfAbsent(fromClassId, k -> new HashMap<>());
        clsDRDMap.get(fromClassId).computeIfAbsent(toClassId, k -> new EntityDependencyRelationDetailWithClass(fromClassId, toClassId));
        return clsDRDMap.get(fromClassId).get(toClassId);
    }

    // create ClassDependencyRelation
    private PackageDependencyRelationDetail createPackageDependencyRelation(Integer fromPackageId, Integer toPackageId) {
        pkgDRDMap.computeIfAbsent(fromPackageId, k -> new HashMap<>());
        pkgDRDMap.get(fromPackageId).computeIfAbsent(toPackageId, k -> new PackageDependencyRelationDetail(fromPackageId, toPackageId));
        return pkgDRDMap.get(fromPackageId).get(toPackageId);
    }
}
