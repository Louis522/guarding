package GAPDetector.detectors;

import GAPDetector.entities.AbstractEntity;
import GAPDetector.json.outputDTO.relations.EntityDependencyRelationDetail;
import GAPDetector.json.outputDTO.smells.MH.MultipathHierarchyStructure;
import GAPDetector.model.Edge;
import GAPDetector.model.Vertex;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.AllDirectedPaths;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.min;

public class MHDector extends GeneralDetector {

    public MHDector() {
                super(storage, ownershipFlag, measureFlag);
            }

            public void workflow() {
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
                                for (EntityDependencyRelationDetail entityDependencyRelationDetail : edge.getEntityDependencyRelationClassDetails()){
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


                        if (ownershipFlag && (storage.id_abstractEntity.get(vertex.getId()).isIntrusive != null && storage.id_abstractEntity.get(anotherVertex.getId()).isIntrusive != null) && (storage.id_abstractEntity.get(vertex.getId()).isIntrusive && storage.id_abstractEntity.get(anotherVertex.getId()).isIntrusive)) {

                            AbstractEntity ae1 = storage.id_abstractEntity.get(vertex.getId());
                            AbstractEntity ae2 = storage.id_abstractEntity.get(anotherVertex.getId());
                            smellCounterIsIntrusive += 1;
                            multipathHierarchyStructureIsIntrusiveList.add(multipathHierarchyStructure);
                        }
                    }
                }
            }
        }
    }
}
