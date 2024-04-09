package GAPDetector.detectors;


import GAPDetector.Storage;
import GAPDetector.entities.AbstractEntity;
import GAPDetector.entities.EntityIdentifier;
import GAPDetector.entities.TypeEntity;
import GAPDetector.json.inputDTO.rule.DetectTarget;
import GAPDetector.json.inputDTO.rule.Rule;
import GAPDetector.json.outputDTO.relations.SingleDependencyRelationDetailWithClassInfo;
import GAPDetector.json.outputDTO.relations.EntityDependencyRelationDetail;
import GAPDetector.json.outputDTO.smells.AwD.AbstractionWithoutDecouplingDTO;
import GAPDetector.json.outputDTO.smells.AwD.AbstractionWithoutDecouplingStructure;
import GAPDetector.json.outputDTO.smells.AwD.AbstractionWithoutDecouplingStructureDetail;
import GAPDetector.json.outputDTO.smells.CD.CyclicDependencyDTO;
import GAPDetector.json.outputDTO.smells.CD.CyclicDependencyStructure;
import GAPDetector.json.outputDTO.smells.CD.MutualPackageDependencyRelation;
import GAPDetector.json.outputDTO.smells.CD.PackageDependencyRelationCell;
import GAPDetector.json.outputDTO.smells.CH.CyclicHierarchyDTO;
import GAPDetector.json.outputDTO.smells.CH.CyclicHierarchyStructure;
import GAPDetector.json.outputDTO.smells.MH.MultipathHierarchyDTO;
import GAPDetector.json.outputDTO.smells.MH.MultipathHierarchyStructure;
import GAPDetector.json.util.JSONUtil;
import GAPDetector.model.Edge;
import GAPDetector.model.Vertex;
import GAPDetector.utils.GraphCreator;
import GAPDetector.utils.RuleSolver;
import org.apache.commons.lang3.mutable.MutableInt;
import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.connectivity.KosarajuStrongConnectivityInspector;
import org.jgrapht.alg.interfaces.StrongConnectivityAlgorithm;
import org.jgrapht.alg.shortestpath.AllDirectedPaths;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.SimpleDirectedGraph;

import java.util.*;

import static java.lang.Math.min;

public class GeneralDetectorMulti {


    public static Integer workflow(Storage storage, Rule rule, String projectName, String outputMode) {
        DefaultDirectedGraph<Vertex, Edge> graph = new DefaultDirectedGraph<>(Edge.class);
        SimpleDirectedGraph<Vertex, Edge> graphPKG = new SimpleDirectedGraph<>(Edge.class);
        HashMap<Integer, HashMap<Integer, List<EntityDependencyRelationDetail>>> details;
        HashMap<Integer, Vertex> id2vertex;
        String outputFolder = projectName + "-gap-out";


        Integer minCycleCount = 2;
        Integer maxPathLength = 0;
        String target = "";
        int smellCounter = 0;

        RuleSolver ruleSolver = new RuleSolver(rule);
        DetectTarget detectTarget = ruleSolver.getDetectTarget();

        GraphCreator graphCreator = new GraphCreator(storage, ruleSolver.getGraphTarget());
        graphCreator.workflow();

        if (graphCreator.getEntityGraph() != null) {
            graph = graphCreator.getEntityGraph();
        } else if (graphCreator.getClassGraph() != null) {
            graph = graphCreator.getClassGraph();
        } else if (graphCreator.getPackageGraph() != null) {
            graphPKG = graphCreator.getPackageGraph();
        }
        details = graphCreator.getDetails();
        id2vertex = graphCreator.getId2vertex();

        if (detectTarget.getCycle() != null) {
            target = "Cycle";
            minCycleCount = detectTarget.getCycle().getMinCycleCount();
        } else if (detectTarget.getMultipath() != null) {
            target = "Multipath";
            if (detectTarget.getMultipath().getMaxPathLength() == -1) {
                maxPathLength = graph.vertexSet().size() - 1;
            } else if (detectTarget.getMultipath().getMaxPathLength() > 0) {
                maxPathLength = detectTarget.getMultipath().getMaxPathLength();
            }
        } else if (detectTarget.getDistance() != null) {
            target = "Distance";
        } else if (detectTarget.getFindVertex() != null) {
            if (detectTarget.getFindVertex().getOneForOne() != null) {
                target = "oneForOne";
            } else if (detectTarget.getFindVertex().getTwoForOne() != null) {
                target = "twoForOne";
            }
        }


        switch (target) {
            case "Cycle":
                List<CyclicDependencyStructure> cyclicDependencyStructureList = new ArrayList<>();

                HashMap<String, PackageDependencyRelationCell> sourcePackageName2PackageDependencyRelationCell = new HashMap<>();
                StrongConnectivityAlgorithm<Vertex, Edge> scAlg = new KosarajuStrongConnectivityInspector<>(graphPKG);
                List<Graph<Vertex, Edge>> stronglyConnectedSubgraphsOneLevel = scAlg.getStronglyConnectedComponents();

                for (Graph<Vertex, Edge> stronglyConnectedSubgraphSameLevel : stronglyConnectedSubgraphsOneLevel) {
                    if (stronglyConnectedSubgraphSameLevel.vertexSet().size() >= minCycleCount) {
                        HashSet<PackageDependencyRelationCell> packageDependencyRelationCells = new HashSet<>();
                        HashMap<String, MutableInt> dependencyRelationCountTotal = new HashMap<>();
                        HashSet<String> cyclicDependencyModules = new HashSet<>();
                        for (Vertex vertex : stronglyConnectedSubgraphSameLevel.vertexSet()) {
                            cyclicDependencyModules.add(vertex.getObject());
                        }
                        if (cyclicDependencyModules.size() >= 2) {
                            // 所有包之间的依赖关系
                            HashMap<String, HashMap<String, HashMap<String, MutableInt>>> packageDependencyRelationCount = new HashMap<>();
                            for (Vertex vertex : stronglyConnectedSubgraphSameLevel.vertexSet()) {  // 遍历强分图的每个结点
                                packageDependencyRelationCount.computeIfAbsent(vertex.getObject(), k -> new HashMap<>());   // 输出结构初始化
                                Set<Edge> edges = stronglyConnectedSubgraphSameLevel.outgoingEdgesOf(vertex);   // 向外的边
                                HashMap<String, MutableInt> dependencyRelationCountFromSrcToAllDest = new HashMap<>();
                                for (Edge edge : edges) { // 遍历每条外向的边
                                    HashMap<String, MutableInt> dependencyRelationCountInTwoPKG = new HashMap<>();
                                    if (!edge.getSourcePackage().equals(edge.getTargetPackage())) {
                                        String sourcePackageName = edge.getSourcePackage();
                                        Boolean sourcePackageIsIntrusive = edge.getSourceIsIntrusive();
                                        String targetPackageName = edge.getTargetPackage();
                                        Boolean targetPackageIsIntrusive = edge.getTargetIsIntrusive();
                                        sourcePackageName2PackageDependencyRelationCell.computeIfAbsent(sourcePackageName, k -> new PackageDependencyRelationCell(sourcePackageName));
                                        PackageDependencyRelationCell packageDependencyRelationCell = sourcePackageName2PackageDependencyRelationCell.get(sourcePackageName);
                                        packageDependencyRelationCell.getTargetPackage().add(targetPackageName);
                                        packageDependencyRelationCell.setOutDegree(packageDependencyRelationCell.getOutDegree() + 1);

                                        MutualPackageDependencyRelation mutualPackageDependencyRelation = new MutualPackageDependencyRelation();  // 两个之间的依赖关系
                                        mutualPackageDependencyRelation.setSourcePackage(new EntityIdentifier(sourcePackageName, sourcePackageIsIntrusive, null)); // 设置两个包之间的依赖关系的起点
                                        mutualPackageDependencyRelation.setTargetPackage(new EntityIdentifier(targetPackageName, targetPackageIsIntrusive, null));    // 设置两个包之间的依赖关系的终点

                                        List<SingleDependencyRelationDetailWithClassInfo> dependencyRelations = new ArrayList<>();
                                        for (EntityDependencyRelationDetail entityDependencyRelationDetail : edge.getEntityDependencyRelationPackageDetails()) {   // 遍历依赖关系
                                            AbstractEntity fromParent = storage.id_abstractEntity.get(entityDependencyRelationDetail.getFromParentId());
                                            EntityIdentifier fromClass = new EntityIdentifier(fromParent.qualifiedName, fromParent.file, fromParent.modifier, null, fromParent.location);
                                            AbstractEntity toParent = storage.id_abstractEntity.get(entityDependencyRelationDetail.getToParentId());
                                            EntityIdentifier toClass = new EntityIdentifier(toParent.qualifiedName, toParent.file, toParent.modifier, null, toParent.location);
                                            SingleDependencyRelationDetailWithClassInfo singleDependencyRelationDetailWithClassInfo = new SingleDependencyRelationDetailWithClassInfo(fromClass, toClass, entityDependencyRelationDetail);
                                            dependencyRelations.add(singleDependencyRelationDetailWithClassInfo);
                                            // 两个包之间的依赖关系的计数
                                            MutableInt initValue = new MutableInt(1);
                                            MutableInt oldValue = dependencyRelationCountInTwoPKG.put(entityDependencyRelationDetail.getRelationType(), initValue);
                                            if (oldValue != null) {
                                                initValue.setValue(oldValue.getValue() + 1);
                                            }

                                            MutableInt initValueSrcToAllDest = new MutableInt(1);
                                            MutableInt oldValueSrcToAllDest = dependencyRelationCountFromSrcToAllDest.put(entityDependencyRelationDetail.getRelationType(), initValueSrcToAllDest);
                                            if (oldValueSrcToAllDest != null) {
                                                initValueSrcToAllDest.setValue(oldValueSrcToAllDest.getValue() + 1);
                                            }

                                            // 这个循环依赖里面所有依赖关系的计数
                                            MutableInt initValueTotal = new MutableInt(1);
                                            MutableInt oldValueTotal = dependencyRelationCountTotal.put(entityDependencyRelationDetail.getRelationType(), initValueTotal);
                                            if (oldValueTotal != null) {
                                                initValueTotal.setValue(oldValueTotal.getValue() + 1);
                                            }


                                        }
                                        mutualPackageDependencyRelation.setEntityDependencies(dependencyRelations);
                                        mutualPackageDependencyRelation.setDependencyRelationCount(dependencyRelationCountInTwoPKG);
                                        packageDependencyRelationCell.getMutualPackageDependencyRelations().add(mutualPackageDependencyRelation);
                                        packageDependencyRelationCell.setDependencyRelationCountFromSrcToAllDest(dependencyRelationCountFromSrcToAllDest);
                                        packageDependencyRelationCells.add(packageDependencyRelationCell);
                                    }
                                }
                            }
                            CyclicDependencyStructure cyclicDependencyStructure = new CyclicDependencyStructure(smellCounter, cyclicDependencyModules,
                                    cyclicDependencyModules.size(), dependencyRelationCountTotal, packageDependencyRelationCells, null, null);
                            smellCounter += 1;
                            cyclicDependencyStructureList.add(cyclicDependencyStructure);
                        }
                    }
                }

                CyclicDependencyDTO cyclicDependencyDTO = new CyclicDependencyDTO(smellCounter, cyclicDependencyStructureList);
                JSONUtil.toJson(cyclicDependencyDTO, outputFolder, projectName + "-" + cyclicDependencyDTO, outputMode);

                break;
            case "Multipath":
                List<MultipathHierarchyStructure> multipathHierarchyStructureList = new ArrayList<>();

                AllDirectedPaths<Vertex, Edge> allPaths = new AllDirectedPaths<>(graph);
                for (Vertex vertex : graph.vertexSet()) {
                    for (Vertex anotherVertex : graph.vertexSet()) {
                        if (vertex != anotherVertex) {
                            List<GraphPath<Vertex, Edge>> fullRes = allPaths.getAllPaths(vertex, anotherVertex, true, min(maxPathLength, graph.vertexSet().size() - 1));
                            if (fullRes.size() >= 2) {
                                List<List<EntityDependencyRelationDetail>> multipath = new ArrayList<>();
                                MultipathHierarchyStructure multipathHierarchyStructure = new MultipathHierarchyStructure();
                                multipathHierarchyStructure.setId(smellCounter);
                                multipathHierarchyStructure.setStart(storage.id_typeEntity.get(vertex.getId()).getEntityIdentifier());
                                multipathHierarchyStructure.setEnd(storage.id_typeEntity.get(anotherVertex.getId()).getEntityIdentifier());
                                for (GraphPath<Vertex, Edge> path : fullRes) {
                                    List<Edge> edges = path.getEdgeList();
                                    List<EntityDependencyRelationDetail> innerMulitpathHierarchyInnerPath = new ArrayList<>();
                                    for (Edge edge : edges) {
                                        for (EntityDependencyRelationDetail entityDependencyRelationDetail : edge.getEntityDependencyRelationClassDetails()) {
                                            entityDependencyRelationDetail.setFromEntityLocation(storage.object_typeEntities.get(edge.getSource()).get(0).location);
                                            entityDependencyRelationDetail.setToEntityLocation(storage.object_typeEntities.get(edge.getSource()).get(0).location);
                                            innerMulitpathHierarchyInnerPath.add(entityDependencyRelationDetail);
                                        }
                                    }
                                    multipath.add(innerMulitpathHierarchyInnerPath);
                                }
                                multipathHierarchyStructure.setMultipath(multipath);
                                smellCounter += 1;
                                multipathHierarchyStructureList.add(multipathHierarchyStructure);
                            }
                        }
                    }
                }

                MultipathHierarchyDTO multipathHierarchyDTO = new MultipathHierarchyDTO(smellCounter, multipathHierarchyStructureList);
                JSONUtil.toJson(multipathHierarchyDTO, outputFolder, projectName + "-" + multipathHierarchyDTO, outputMode);
                break;
            case "oneForOne":
                List<CyclicHierarchyStructure> cyclicHierarchyStructureList = new ArrayList<>();
                AllDirectedPaths<Vertex, Edge> allPaths2 = new AllDirectedPaths<>(graph);

                for (Map.Entry<TypeEntity, List<TypeEntity>> entry : storage.sub_class.entrySet()) {
                    TypeEntity superTypeEntity = entry.getKey();
                    Vertex superVertex = id2vertex.get(superTypeEntity.id);
                    List<TypeEntity> subTypeEntities = entry.getValue();
                    for (TypeEntity subTypeEntity : subTypeEntities) {
                        Vertex subVertex = id2vertex.get(subTypeEntity.id);
                        if (superVertex != null && subVertex != null) {
//                    if (superVertex != null && subVertex != null && !subTypeEntity.modifier.contains("static")) {
                            List<GraphPath<Vertex, Edge>> super2sub = allPaths2.getAllPaths(superVertex, subVertex, true, 1);
                            if (!super2sub.isEmpty()) {
                                List<EntityDependencyRelationDetail> detail = details.get(superVertex.getId()).get(subVertex.getId());
                                CyclicHierarchyStructure cyclicHierarchyStructure = new CyclicHierarchyStructure(smellCounter, superTypeEntity.getEntityIdentifier(), subTypeEntity.getEntityIdentifier(), detail);
                                cyclicHierarchyStructureList.add(cyclicHierarchyStructure);
                                smellCounter += 1;
                            }
                        }
                    }
                }
                CyclicHierarchyDTO cyclicHierarchyDTO = new CyclicHierarchyDTO(smellCounter, cyclicHierarchyStructureList);
                JSONUtil.toJson(cyclicHierarchyDTO, outputFolder, projectName + "-" + cyclicHierarchyDTO, outputMode);

                break;
            case "twoForOne":
                List<AbstractionWithoutDecouplingStructure> abstractionWithoutDecouplingStructureList = new ArrayList<>();

                for (Map.Entry<TypeEntity, TypeEntity> entry : storage.super_class.entrySet()) {
                    TypeEntity subTypeEntity = entry.getKey();
                    Vertex subVertex = id2vertex.get(subTypeEntity.id);
                    TypeEntity superTypeEntity = entry.getValue();
                    Vertex superVertex = id2vertex.get(superTypeEntity.id);
                    if (superVertex != null && subVertex != null) {
                        for (Vertex clientVertex : graph.vertexSet()) {
                            if (
                                    graph.edgesOf(clientVertex).size() > 0 && clientVertex != subVertex && clientVertex != superVertex
                                            && (subTypeEntity.modifier == null || !subTypeEntity.modifier.contains("abstract"))
                                            && (storage.hierarchyRelated.get(clientVertex.getId()) != null
                                            && !storage.hierarchyRelated.get(clientVertex.getId()).contains(superVertex.getId())
                                            && !storage.hierarchyRelated.get(clientVertex.getId()).contains(subVertex.getId()))

                            ) {
                                Edge client2sub = graph.getEdge(clientVertex, subVertex);
                                Edge client2super = graph.getEdge(clientVertex, superVertex);
                                if (client2sub != null && client2super != null) {
                                    EntityIdentifier superTypeEntityInfo = superTypeEntity.getEntityIdentifier();
                                    EntityIdentifier clientTypeEntityInfo = storage.id_typeEntity.get(clientVertex.getId()).getEntityIdentifier();
                                    EntityIdentifier subTypeEntityInfo = subTypeEntity.getEntityIdentifier();
                                    List<EntityDependencyRelationDetail> clientClass2SuperTypeDependencyRelationDetails = details.get((clientVertex.getId())).get(superVertex.getId());
                                    List<EntityDependencyRelationDetail> clientClass2SubTypeDependencyRelationDetails = details.get((clientVertex.getId())).get(subVertex.getId());
                                    AbstractionWithoutDecouplingStructureDetail details2 = new AbstractionWithoutDecouplingStructureDetail(clientClass2SuperTypeDependencyRelationDetails, clientClass2SubTypeDependencyRelationDetails);
                                    AbstractionWithoutDecouplingStructure abstractionWithoutDecouplingStructure = new AbstractionWithoutDecouplingStructure(smellCounter, superTypeEntityInfo, clientTypeEntityInfo, subTypeEntityInfo, details2);
                                    smellCounter += 1;
                                    abstractionWithoutDecouplingStructureList.add(abstractionWithoutDecouplingStructure);
                                }
                            }
                        }
                    }
                }
                AbstractionWithoutDecouplingDTO abstractionWithoutDecouplingDTO = new AbstractionWithoutDecouplingDTO(smellCounter, abstractionWithoutDecouplingStructureList);
                JSONUtil.toJson(abstractionWithoutDecouplingDTO, outputFolder, projectName + "-" + abstractionWithoutDecouplingDTO, outputMode);

                break;
        }
        return smellCounter;
    }
}
