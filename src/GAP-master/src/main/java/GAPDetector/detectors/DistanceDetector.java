package GAPDetector.detectors;

import GAPDetector.entities.FuncImplEntity;
import GAPDetector.entities.TypeEntity;
import GAPDetector.entities.VarEntity;
import GAPDetector.json.outputDTO.smells.FE.FeatureEnvyStructure;
import GAPDetector.model.Edge;
import GAPDetector.model.Vertex;
import GAPDetector.utils.DistanceMatrix;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.AllDirectedPaths;

import java.util.*;

public class DistanceDetector extends GeneralDetector {
    public DistanceDetector() {
        super(storage, ownershipFlag, measureFlag);
    }

    public void workflow() {
        AllDirectedPaths<Vertex, Edge> allPaths = new AllDirectedPaths<>(graph);
        for (Vertex vertex : graph.vertexSet()) {
            for (Vertex anotherVertex : graph.vertexSet()) {
                if (vertex != anotherVertex) {
                    List<GraphPath<Vertex, Edge>> fullRes = allPaths.getAllPaths(vertex, anotherVertex, true, 1);
                    for (GraphPath<Vertex, Edge> path : fullRes) {
                        List<Edge> edges = path.getEdgeList();
                        for (Edge edge : edges) {
                            String fromType = edge.getSourceType();
                            String toType = edge.getTargetType();
                            String relation = edge.getLabel();
                            if (relation.equals("Call")) {
                                if (fromType.equals("FuncImpl") && toType.equals("FuncImpl")) {
                                    FuncImplEntity fromFuncImplEntity = storage.id_funcImplEntity.get(vertex.getId());
                                    FuncImplEntity toFuncImplEntity = storage.id_funcImplEntity.get(anotherVertex.getId());
                                    fromFuncImplEntity.getCallFuncImplEntities().add(toFuncImplEntity);
                                }

                            } else if (relation.equals("UseVar")) {
                                if (fromType.equals("FuncImpl") && toType.equals("Var")) {
                                    FuncImplEntity funcImplEntity = storage.id_funcImplEntity.get(vertex.getId());
                                    VarEntity varEntity = storage.id_varEntity.get(anotherVertex.getId());
                                    if (varEntity.getExtendVarType() != null && !varEntity.getExtendVarType().equals("LocalVar")) {
                                        funcImplEntity.getUseVarEntities().add(varEntity);
                                        varEntity.getAccessedFuncImpls().add(funcImplEntity);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        resolveEntitySet();

        DistanceMatrix distanceMatrix = new DistanceMatrix(storage);
        HashMap<String, HashMap<String, Double>> allFuncImplJaccardDistanceMatrix = distanceMatrix.getAllFuncImplJaccardDistanceMatrix();
        List<FuncImplEntity> funcImplEntityList = distanceMatrix.getFuncImplEntityList();
        checkFeatureEnvyMethod(funcImplEntityList, allFuncImplJaccardDistanceMatrix);

    }

    private void resolveEntitySet() {
        for (TypeEntity typeEntity : storage.id_typeEntity.values()) {
            HashSet<String> set = new HashSet<>();
            for (FuncImplEntity funcImplEntity : typeEntity.getFuncImplEntities()) {
                if (funcImplEntity.getIsGetter() != null) {
                    if (funcImplEntity.getIsStatic() || funcImplEntity.getIsDelegator() || funcImplEntity.getIsRecursive() || funcImplEntity.getIsOverride()) {

                    } else if (funcImplEntity.getIsGetter()) {
                        if (funcImplEntity.getAttributeOfGetterOrSetter() != null) {
                            set.add(funcImplEntity.getAttributeOfGetterOrSetter());
                        } else {
                            set.add(funcImplEntity.qualifiedName);
                        }
                    } else {
                        set.add(funcImplEntity.qualifiedName);
                    }
                }

            }
            for (VarEntity varEntity : typeEntity.getVarEntities()) {
                set.add(varEntity.qualifiedName);
            }
            typeEntity.setEntitySet(set);
        }
        for (FuncImplEntity funcImplEntity : storage.id_funcImplEntity.values()) {
            HashSet<String> set = new HashSet<>();
            for (VarEntity varEntity : funcImplEntity.getUseVarEntities()) {
                set.add(varEntity.qualifiedName);
            }
            for (FuncImplEntity funcImplEntity1 : funcImplEntity.getCallFuncImplEntities()) {
                if (funcImplEntity1.getIsGetter() != null) {
                    if (funcImplEntity1.getIsStatic() || funcImplEntity1.getIsDelegator() || funcImplEntity1.getIsRecursive() || funcImplEntity1.getIsOverride()) {

                    } else if (funcImplEntity1.getIsGetter()) {
                        if (funcImplEntity1.getAttributeOfGetterOrSetter() != null) {
                            set.add(funcImplEntity1.getAttributeOfGetterOrSetter());
                        } else {
                            set.add(funcImplEntity1.qualifiedName);
                        }
                    } else {
                        set.add(funcImplEntity1.qualifiedName);
                    }
                }
            }
            funcImplEntity.setEntitySet(set);
        }
        for (VarEntity varEntity : storage.id_varEntity.values()) {
            HashSet<String> set = new HashSet<>();
            for (FuncImplEntity funcImplEntity : varEntity.getAccessedFuncImpls()) {
                if (funcImplEntity.getIsGetter() != null) {
                    if (funcImplEntity.getIsStatic() || funcImplEntity.getIsDelegator() || funcImplEntity.getIsRecursive() || funcImplEntity.getIsOverride()) {

                    } else if (funcImplEntity.getIsGetter()) {
                        if (funcImplEntity.getAttributeOfGetterOrSetter() != null) {
                            set.add(funcImplEntity.getAttributeOfGetterOrSetter());
                        } else {
                            set.add(funcImplEntity.qualifiedName);
                        }
                    } else {
                        set.add(funcImplEntity.qualifiedName);
                    }

                }
            }
            varEntity.setEntitySet(set);
        }

    }

    private void checkFeatureEnvyMethod(List<FuncImplEntity> funcImplEntities, HashMap<String, HashMap<String, Double>> allFuncImplJaccardDistanceMatrix) {
        LinkedHashSet<FuncImplEntity> featureEnvyMethod = new LinkedHashSet<>();
        for (FuncImplEntity funcImplEntity : funcImplEntities) {
            HashMap<String, Double> classDistance = allFuncImplJaccardDistanceMatrix.get(funcImplEntity.qualifiedName);
            classDistance = (HashMap<String, Double>) sortByValue(classDistance);
            Map.Entry entry = classDistance.entrySet().iterator().next();
            String key = (String) entry.getKey();
            try {
                if (funcImplEntity.getOriginEntity() instanceof TypeEntity) {
                    TypeEntity originTypeEntity = storage.id_typeEntity.get(funcImplEntity.getOriginEntity().id);
                    if (!key.equals(originTypeEntity.qualifiedName) && classDistance.get(key) != 1.0 && !funcImplEntity.getIsGetter() && !funcImplEntity.getIsSetter() && !funcImplEntity.getIsConstructor() && !funcImplEntity.getIsAssign() && !funcImplEntity.getMethodIsAbstract()) {
                        if (!originTypeEntity.getSuperClassIdentifier().contains(findTypeEntity(key, null).getEntityIdentifier())) {
                            featureEnvyMethod.add(funcImplEntity);
                            funcImplEntity.setTargetClass(key);
                        }
                    }
                }
            } catch (NullPointerException ignored) {
            }
        }

        for (FuncImplEntity funcImplEntity : featureEnvyMethod) {
//            System.out.println(funcImplEntity.object);
//            System.out.println(funcImplEntity.getEntitySet());
            FeatureEnvyStructure featureEnvyStructure = new FeatureEnvyStructure(funcImplEntity.qualifiedName, funcImplEntity.getOriginEntity().qualifiedName, funcImplEntity.getOriginEntity().isIntrusive, funcImplEntity.getTargetClass());
            featureEnvyStructureList.add(featureEnvyStructure);
        }
        smellCounter = featureEnvyMethod.size();
    }

    private static Map sortByValue(Map unsortedMap) {
        List list = new LinkedList(unsortedMap.entrySet());

        Collections.sort(list, (Comparator) (o1, o2) -> ((Comparable) ((Map.Entry) (o1)).getValue())
                .compareTo(((Map.Entry) (o2)).getValue()));

        Map sortedMap = new LinkedHashMap();
        for (Iterator it = list.iterator(); it.hasNext(); ) {
            Map.Entry entry = (Map.Entry) it.next();
            sortedMap.put(entry.getKey(), entry.getValue());
        }
        return sortedMap;
    }

    private TypeEntity findTypeEntity(String typeEntityName, String file) {
        TypeEntity originTypeEntity = null;
        List<TypeEntity> typeEntities = storage.object_typeEntities.get(typeEntityName);
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

}
