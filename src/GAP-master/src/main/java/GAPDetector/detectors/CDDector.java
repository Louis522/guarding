package GAPDetector.detectors;

import GAPDetector.entities.AbstractEntity;
import GAPDetector.entities.EntityIdentifier;
import GAPDetector.json.outputDTO.relations.SingleDependencyRelationDetailWithClassInfo;
import GAPDetector.json.outputDTO.smells.CD.CyclicDependencyStructure;
import GAPDetector.json.outputDTO.smells.CD.MutualPackageDependencyRelation;
import GAPDetector.json.outputDTO.smells.CD.PackageDependencyRelationCell;
import GAPDetector.json.outputDTO.relations.EntityDependencyRelationDetail;
import GAPDetector.model.Edge;
import GAPDetector.model.Vertex;
import org.apache.commons.lang3.mutable.MutableInt;
import org.jgrapht.Graph;
import org.jgrapht.alg.connectivity.KosarajuStrongConnectivityInspector;
import org.jgrapht.alg.interfaces.StrongConnectivityAlgorithm;

import java.util.*;

public class CDDector extends GeneralDetector {

    public CDDector() {
        super(storage, ownershipFlag, measureFlag);
    }

    public void workflow() {
        // computes all the strongly connected components of the directed graph
        HashMap<String, PackageDependencyRelationCell> sourcePackageName2PackageDependencyRelationCell = new HashMap<>();
        StrongConnectivityAlgorithm<Vertex, Edge> scAlg = new KosarajuStrongConnectivityInspector<>(graphPKG);
        List<Graph<Vertex, Edge>> stronglyConnectedSubgraphsOneLevel = scAlg.getStronglyConnectedComponents();

        for (Graph<Vertex, Edge> stronglyConnectedSubgraphSameLevel : stronglyConnectedSubgraphsOneLevel) {
            if (stronglyConnectedSubgraphSameLevel.vertexSet().size() >= minCycleCount) {
                HashSet<PackageDependencyRelationCell> packageDependencyRelationCells = new HashSet<>();
                HashMap<String, MutableInt> dependencyRelationCountTotal = new HashMap<>();
                HashSet<String> cyclicDependencyModules = new HashSet<>();
                HashSet<String> intrusivePackages = new HashSet<>();
                for (Vertex vertex : stronglyConnectedSubgraphSameLevel.vertexSet()) {
                    cyclicDependencyModules.add(vertex.getObject());

                    if (ownershipFlag && storage.id_abstractEntity.get(vertex.getId()).isIntrusive) {
                        intrusivePackages.add(vertex.getObject());
                    }
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
                                // TODO
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
                            cyclicDependencyModules.size(), dependencyRelationCountTotal, packageDependencyRelationCells, intrusivePackages, intrusivePackages.size());
                    smellCounter += 1;
                    cyclicDependencyStructureList.add(cyclicDependencyStructure);
                    if (ownershipFlag && intrusivePackages.size() >= 1) {
                        smellCounterIsIntrusive += 1;
                        cyclicDependencyStructureIsIntrusiveList.add(cyclicDependencyStructure);
                    }
                }
            }
        }
    }
}
