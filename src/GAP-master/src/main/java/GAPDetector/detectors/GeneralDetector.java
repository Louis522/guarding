package GAPDetector.detectors;


import GAPDetector.Storage;
import GAPDetector.json.inputDTO.rule.*;
import GAPDetector.json.outputDTO.smells.AwD.AbstractionWithoutDecouplingStructure;
import GAPDetector.json.outputDTO.smells.CD.CyclicDependencyStructure;
import GAPDetector.json.outputDTO.smells.CH.CyclicHierarchyStructure;
import GAPDetector.json.outputDTO.smells.DC.DataClumpsStructure;
import GAPDetector.json.outputDTO.relations.EntityDependencyRelationDetail;
import GAPDetector.json.outputDTO.smells.FE.FeatureEnvyStructure;
import GAPDetector.json.outputDTO.smells.MH.MultipathHierarchyStructure;
import GAPDetector.json.outputDTO.relations.PackageDependencyRelationDetail;
import GAPDetector.json.outputDTO.smells.SS.ShotgunSurgeryStructure;
import GAPDetector.model.Edge;
import GAPDetector.model.Vertex;
import GAPDetector.utils.GraphCreator;
import GAPDetector.utils.RuleSolver;
import lombok.Getter;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.SimpleDirectedGraph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class GeneralDetector {
    public static Storage storage;
    public static DefaultDirectedGraph<Vertex, Edge> graph = new DefaultDirectedGraph<>(Edge.class);
    public static SimpleDirectedGraph<Vertex, Edge> graphPKG = new SimpleDirectedGraph<>(Edge.class);

    public static HashMap<Integer, SimpleDirectedGraph<Vertex, Edge>> packageGraphList;
    public static HashMap<Integer, List<PackageDependencyRelationDetail>> sameLevelPackageDependencyRelationDetails;

    @Getter
    public static HashMap<Integer, HashMap<Integer, List<EntityDependencyRelationDetail>>> details;
    @Getter
    public static HashMap<Integer, Vertex> id2vertex = new HashMap<>();
    @Getter
    public static HashMap<String, Vertex> object2vertex = new HashMap<>();

    public static TwoForOne twoForOne;
    public static OneForOne oneForOne;
    public static Threshold threshold;
    @Getter
    public static List<PackageDependencyRelationDetail> packageDependencyRelationDetails;

    public static Integer minCycleCount;
    public static Integer maxPathLength;
    public static String target;

    public static boolean ownershipFlag;
    public static boolean measureFlag;

    public List<AbstractionWithoutDecouplingStructure> abstractionWithoutDecouplingStructureList = new ArrayList<>();
    public List<CyclicDependencyStructure> cyclicDependencyStructureList = new ArrayList<>();
    public List<CyclicHierarchyStructure> cyclicHierarchyStructureList = new ArrayList<>();
    public List<FeatureEnvyStructure> featureEnvyStructureList = new ArrayList<>();
    public List<MultipathHierarchyStructure> multipathHierarchyStructureList = new ArrayList<>();
    public List<ShotgunSurgeryStructure> shotgunSurgeryStructureList = new ArrayList<>();
    public List<DataClumpsStructure> dataClumpsStructureList = new ArrayList<>();

    public List<AbstractionWithoutDecouplingStructure> abstractionWithoutDecouplingStructureIsIntrusiveList = new ArrayList<>();
    public List<CyclicDependencyStructure> cyclicDependencyStructureIsIntrusiveList;
    public List<CyclicHierarchyStructure> cyclicHierarchyStructureIsIntrusiveList = new ArrayList<>();
    public List<FeatureEnvyStructure> featureEnvyStructureIsIntrusiveList = new ArrayList<>();
    public List<MultipathHierarchyStructure> multipathHierarchyStructureIsIntrusiveList = new ArrayList<>();
    public List<ShotgunSurgeryStructure> shotgunSurgeryStructureIsIntrusiveList = new ArrayList<>();
    public List<DataClumpsStructure> dataClumpsStructureIsIntrusiveList = new ArrayList<>();


    public int smellCounter = 0;
    public int smellCounterIsIntrusive;

    public GeneralDetector(Storage storage, boolean ownershipFlag, boolean measureFlag) {
        GeneralDetector.storage = storage;
        GeneralDetector.ownershipFlag = ownershipFlag;
        GeneralDetector.measureFlag = measureFlag;

    }

    public void workflow(Rule rule) {
        RuleSolver ruleSolver = new RuleSolver(rule);
        if (ruleSolver.getGraphTarget() != null) {
            resolveGraphTarget(ruleSolver.getGraphTarget());
        }
        resolveDetectTarget(ruleSolver.getDetectTarget());
        detect();
    }

    private void resolveGraphTarget(GraphTarget graphTarget) {
        GraphCreator graphCreator = new GraphCreator(storage, graphTarget);
        graphCreator.workflow();
        if (graphCreator.getEntityGraph() != null) {
            graph = graphCreator.getEntityGraph();
        } else if (graphCreator.getClassGraph() != null) {
            graph = graphCreator.getClassGraph();
        } else if (graphCreator.getPackageGraph() != null) {
            graphPKG = graphCreator.getPackageGraph();
        }
        details = graphCreator.getDetails();
        packageDependencyRelationDetails = graphCreator.getPackageDependencyRelationDetails();
        id2vertex = graphCreator.getId2vertex();
        object2vertex = graphCreator.getObject2vertex();
        packageGraphList = graphCreator.getPackageGraphList();
        sameLevelPackageDependencyRelationDetails = graphCreator.getSameLevelPackageDependencyRelationDetails();
    }

    private void resolveDetectTarget(DetectTarget detectTarget) {
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
        } else if (detectTarget.getThreshold() != null) {
            threshold = detectTarget.getThreshold();
            if (detectTarget.getThreshold().getTarget().equals("SS")) {
                target = "SS";
            } else if (detectTarget.getThreshold().getTarget().equals("DC")) {
                target = "DC";
            } else if (detectTarget.getThreshold().getTarget().equals("FE")) {
                target = "FE";
            }
        }
    }

    private void detect() {
        switch (target) {
            case "Cycle":
                CDDector cdDector = new CDDector();
                cdDector.workflow();
                this.smellCounter = cdDector.smellCounter;
                this.cyclicDependencyStructureList = cdDector.cyclicDependencyStructureList;
                this.smellCounterIsIntrusive = cdDector.smellCounterIsIntrusive;
                this.cyclicDependencyStructureIsIntrusiveList = cdDector.cyclicDependencyStructureIsIntrusiveList;
                break;

            case "Multipath":
                MHDector mhDector = new MHDector();
                mhDector.workflow();
                this.smellCounter = mhDector.smellCounter;
                this.multipathHierarchyStructureList = mhDector.multipathHierarchyStructureList;
                this.smellCounterIsIntrusive = mhDector.smellCounterIsIntrusive;
                this.multipathHierarchyStructureIsIntrusiveList = mhDector.multipathHierarchyStructureIsIntrusiveList;
                break;
            case "oneForOne":
                FindVertexDector oneForOneDetector = new FindVertexDector();
                oneForOneDetector.workflow();
                this.smellCounter = oneForOneDetector.smellCounter;
                this.cyclicHierarchyStructureList = oneForOneDetector.cyclicHierarchyStructureList;
                this.smellCounterIsIntrusive = oneForOneDetector.smellCounterIsIntrusive;
                this.cyclicHierarchyStructureIsIntrusiveList = oneForOneDetector.cyclicHierarchyStructureIsIntrusiveList;
                break;
            case "twoForOne":
                FindVertexDector twoForOneDetector = new FindVertexDector();
                twoForOneDetector.workflow();
                this.smellCounter = twoForOneDetector.smellCounter;
                this.abstractionWithoutDecouplingStructureList = twoForOneDetector.abstractionWithoutDecouplingStructureList;
                this.smellCounterIsIntrusive = twoForOneDetector.smellCounterIsIntrusive;
                this.abstractionWithoutDecouplingStructureIsIntrusiveList = twoForOneDetector.abstractionWithoutDecouplingStructureIsIntrusiveList;
                break;
            case "Distance":
                DistanceDetector distanceDetector = new DistanceDetector();
                distanceDetector.workflow();
                this.smellCounter = distanceDetector.smellCounter;
                this.featureEnvyStructureList = distanceDetector.featureEnvyStructureList;
                this.smellCounterIsIntrusive = distanceDetector.smellCounterIsIntrusive;
                this.featureEnvyStructureIsIntrusiveList = distanceDetector.featureEnvyStructureIsIntrusiveList;
                break;
            case "SS":
                SSDetector ssDetector = new SSDetector();
                ssDetector.workflow();
                this.smellCounter = ssDetector.smellCounter;
                this.shotgunSurgeryStructureList = ssDetector.shotgunSurgeryStructureList;
                this.smellCounterIsIntrusive = ssDetector.smellCounterIsIntrusive;
                this.shotgunSurgeryStructureIsIntrusiveList = ssDetector.shotgunSurgeryStructureIsIntrusiveList;
                break;
            case "DC":
                DCDetector dcDetector = new DCDetector();
                dcDetector.workflow();
                this.smellCounter = dcDetector.smellCounter;
                this.dataClumpsStructureList = dcDetector.dataClumpsStructureList;
                this.smellCounterIsIntrusive = dcDetector.smellCounterIsIntrusive;
                this.dataClumpsStructureIsIntrusiveList = dcDetector.dataClumpsStructureIsIntrusiveList;
                break;
            case "FE":
                FEDetector feDetector = new FEDetector();
                feDetector.workflow();
                this.smellCounter = feDetector.smellCounter;
                this.featureEnvyStructureList = feDetector.featureEnvyStructureList;
                this.smellCounterIsIntrusive = feDetector.smellCounterIsIntrusive;
                this.featureEnvyStructureIsIntrusiveList = feDetector.featureEnvyStructureIsIntrusiveList;
                break;

        }
    }
}
