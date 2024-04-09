package GAPDetector.detectors;

import GAPDetector.entities.EntityIdentifier;
import GAPDetector.entities.TypeEntity;
import GAPDetector.json.outputDTO.smells.AwD.AbstractionWithoutDecouplingStructure;
import GAPDetector.json.outputDTO.smells.AwD.AbstractionWithoutDecouplingStructureDetail;
import GAPDetector.json.outputDTO.smells.CH.CyclicHierarchyStructure;
import GAPDetector.json.outputDTO.relations.EntityDependencyRelationDetail;
import GAPDetector.model.Edge;
import GAPDetector.model.Vertex;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.AllDirectedPaths;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FindVertexDector extends GeneralDetector {
    public FindVertexDector() {
        super(storage, ownershipFlag, measureFlag);
    }

    public void workflow() {
        AllDirectedPaths<Vertex, Edge> allPaths = new AllDirectedPaths<>(graph);
        if (target.equals("oneForOne")) {
            for (Map.Entry<TypeEntity, List<TypeEntity>> entry : storage.sub_class.entrySet()) {
                TypeEntity superTypeEntity = entry.getKey();
                Vertex superVertex = id2vertex.get(superTypeEntity.id);
                List<TypeEntity> subTypeEntities = entry.getValue();
                for (TypeEntity subTypeEntity : subTypeEntities) {
                    Vertex subVertex = id2vertex.get(subTypeEntity.id);
                    if (superVertex != null && subVertex != null) {

//                    if (superVertex != null && subVertex != null && !subTypeEntity.modifier.contains("static")) {
                        List<GraphPath<Vertex, Edge>> super2sub = allPaths.getAllPaths(superVertex, subVertex, true, 1);
                        if (!super2sub.isEmpty()) {
                            List<EntityDependencyRelationDetail> detail = details.get(superVertex.getId()).get(subVertex.getId());
                            CyclicHierarchyStructure cyclicHierarchyStructure = new CyclicHierarchyStructure(smellCounter, superTypeEntity.getEntityIdentifier(), subTypeEntity.getEntityIdentifier(), detail);
                            cyclicHierarchyStructureList.add(cyclicHierarchyStructure);
                            smellCounter += 1;
                            if (ownershipFlag && (superTypeEntity.isIntrusive || subTypeEntity.isIntrusive)) {
                                List<EntityDependencyRelationDetail> intrusiveDetail = new ArrayList<>();
                                for (EntityDependencyRelationDetail entityDependencyRelationDetail : detail) {
                                    if (entityDependencyRelationDetail.getIntrusiveType() != 0) {
                                        intrusiveDetail.add(entityDependencyRelationDetail);
                                    }
                                }
                                if (intrusiveDetail.size() != 0) {
                                    CyclicHierarchyStructure cyclicHierarchyStructureIsIntrusive = new CyclicHierarchyStructure(smellCounter, superTypeEntity.getEntityIdentifier(), subTypeEntity.getEntityIdentifier(), intrusiveDetail);
                                    cyclicHierarchyStructureIsIntrusiveList.add(cyclicHierarchyStructureIsIntrusive);
                                    smellCounterIsIntrusive += 1;
                                }

                            }
                        }
                    }
                }
            }
        } else if (target.equals("twoForOne")) {
            for (Map.Entry<TypeEntity, TypeEntity> entry : storage.super_class.entrySet()) {
                TypeEntity subTypeEntity = entry.getKey();
                Vertex subVertex = id2vertex.get(subTypeEntity.id);
                TypeEntity superTypeEntity = entry.getValue();
                Vertex superVertex = id2vertex.get(superTypeEntity.id);
                if (superVertex != null && subVertex != null) {
                    for (Vertex clientVertex : graph.vertexSet()) {
                        if (graph.edgesOf(clientVertex).size() > 0 && clientVertex != subVertex && clientVertex != superVertex && (subTypeEntity.modifier == null || !subTypeEntity.modifier.contains("abstract")) && !storage.id_typeEntity.get(clientVertex.getId()).getSuperClassIdentifier().contains(storage.id_typeEntity.get(superTypeEntity.id).getEntityIdentifier())
                                && !storage.id_typeEntity.get(clientVertex.getId()).getSuperClassIdentifier().contains(storage.id_typeEntity.get(subTypeEntity.id).getEntityIdentifier())
                        ) {
                            Edge client2sub = graph.getEdge(clientVertex, subVertex);
                            Edge client2super = graph.getEdge(clientVertex, superVertex);
                            if (client2sub != null && client2super != null) {
                                EntityIdentifier superTypeEntityInfo = superTypeEntity.getEntityIdentifier();
                                EntityIdentifier clientTypeEntityInfo = storage.id_typeEntity.get(clientVertex.getId()).getEntityIdentifier();
                                EntityIdentifier subTypeEntityInfo =subTypeEntity.getEntityIdentifier();
                                List<EntityDependencyRelationDetail> clientClass2SuperTypeDependencyRelationDetails = details.get((clientVertex.getId())).get(superVertex.getId());
                                List<EntityDependencyRelationDetail> clientClass2SubTypeDependencyRelationDetails = details.get((clientVertex.getId())).get(subVertex.getId());
                                AbstractionWithoutDecouplingStructureDetail details = new AbstractionWithoutDecouplingStructureDetail(clientClass2SuperTypeDependencyRelationDetails, clientClass2SubTypeDependencyRelationDetails);
                                AbstractionWithoutDecouplingStructure abstractionWithoutDecouplingStructure = new AbstractionWithoutDecouplingStructure(smellCounter, superTypeEntityInfo, clientTypeEntityInfo, subTypeEntityInfo, details);
                                smellCounter += 1;
                                abstractionWithoutDecouplingStructureList.add(abstractionWithoutDecouplingStructure);
                                if (ownershipFlag && (superTypeEntity.isIntrusive || subTypeEntity.isIntrusive || storage.id_typeEntity.get(clientVertex.getId()).isIntrusive)) {
                                    List<EntityDependencyRelationDetail> clientClass2SuperTypeIntrusiveDetail = new ArrayList<>();
                                    List<EntityDependencyRelationDetail> clientClass2SubTypeIntrusiveDetail = new ArrayList<>();

                                    for (EntityDependencyRelationDetail entityDependencyRelationDetail : clientClass2SuperTypeDependencyRelationDetails) {
                                        if (entityDependencyRelationDetail.getIntrusiveType() != 0) {
                                            clientClass2SuperTypeIntrusiveDetail.add(entityDependencyRelationDetail);
                                        }
                                    }
                                    for (EntityDependencyRelationDetail entityDependencyRelationDetail : clientClass2SubTypeDependencyRelationDetails) {
                                        if (entityDependencyRelationDetail.getIntrusiveType() != 0) {
                                            clientClass2SubTypeIntrusiveDetail.add(entityDependencyRelationDetail);
                                        }
                                    }
                                    if (clientClass2SuperTypeIntrusiveDetail.size()!=0 && clientClass2SubTypeIntrusiveDetail.size()!=0){
                                        smellCounterIsIntrusive += 1;
                                        AbstractionWithoutDecouplingStructureDetail intrusiveDetails = new AbstractionWithoutDecouplingStructureDetail(clientClass2SuperTypeIntrusiveDetail, clientClass2SubTypeIntrusiveDetail);
                                        AbstractionWithoutDecouplingStructure intrusiveAbstractionWithoutDecouplingStructure = new AbstractionWithoutDecouplingStructure(smellCounterIsIntrusive, superTypeEntityInfo, clientTypeEntityInfo, subTypeEntityInfo, intrusiveDetails);
                                        abstractionWithoutDecouplingStructureIsIntrusiveList.add(intrusiveAbstractionWithoutDecouplingStructure);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
