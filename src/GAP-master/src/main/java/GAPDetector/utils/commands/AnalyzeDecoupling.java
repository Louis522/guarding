package GAPDetector.utils.commands;

import GAPDetector.json.outputDTO.smells.AwD.AbstractionWithoutDecouplingDTO;
import GAPDetector.json.outputDTO.smells.AwD.AbstractionWithoutDecouplingStructure;
import GAPDetector.json.outputDTO.smells.AwD.AbstractionWithoutDecouplingStructureDetail;
import GAPDetector.json.outputDTO.smells.CD.CyclicDependencyDTO;
import GAPDetector.json.outputDTO.smells.CD.CyclicDependencyStructure;
import GAPDetector.json.outputDTO.smells.CH.CyclicHierarchyDTO;
import GAPDetector.json.outputDTO.smells.CH.CyclicHierarchyStructure;
import GAPDetector.json.outputDTO.relations.EntityDependencyRelationDetail;
import GAPDetector.json.outputDTO.smells.MH.MultipathHierarchyDTO;
import GAPDetector.json.outputDTO.smells.MH.MultipathHierarchyStructure;
import GAPDetector.json.util.JSONUtil;
import GAPDetector.json.inputDTO.facade.EVE;
import GAPDetector.json.inputDTO.facade.FacadeResult;
import picocli.CommandLine;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Callable;


@CommandLine.Command(name = "analyzeD", mixinStandardHelpOptions = true, helpCommand = true, version = "1.2.2",
        description = "Analyze general Anti-patterns with decoupling.")
public class AnalyzeDecoupling implements Callable<Integer> {

    @CommandLine.Option(names = {"-n", "--name"}, required = true, description = "project name")
    private String projectName;
    @CommandLine.Option(names = {"-f", "--facade"}, required = true, description = "facade file path")
    private String facade;
    @CommandLine.Option(names = {"-mh", "--MHResult"}, defaultValue = "MH-result.json", description = "MH Result file path")
    private String MHResult;
    @CommandLine.Option(names = {"-ch", "--CHResult"}, defaultValue = "CH-result.json", description = "CH Result file path")
    private String CHResult;
    @CommandLine.Option(names = {"-cd", "--CDResult"}, defaultValue = "CD-result.json", description = "CD Result file path")
    private String CDResult;
    @CommandLine.Option(names = {"-awd", "--AWDResult"}, defaultValue = "AWD-result.json", description = "AWD Result file path")
    private String AWDResult;
    @CommandLine.Option(names = {"-fe", "--FEResult"}, defaultValue = "FE.json", description = "FE Result file path")
    private String FEResult;
    @CommandLine.Option(names = {"-ss", "--SSResult"}, defaultValue = "SS.json", description = "SS Result file path")
    private String SSResult;
    @CommandLine.Option(names = {"-dc", "--DCResult"}, defaultValue = "DC.json", description = "DC Result file path")
    private String DCResult;
    private HashMap<String, Boolean> qn2IsDecoupling = new HashMap<>();


    @Override
    public Integer call() throws Exception { // your business logic goes here...
        long startTime = System.currentTimeMillis();
        FacadeResult facadeResult = JSONUtil.fromJson(new File(facade), FacadeResult.class);
        MultipathHierarchyDTO multipathHierarchyDTO = JSONUtil.fromJson(new File(projectName + "-" + MHResult), MultipathHierarchyDTO.class);
        CyclicHierarchyDTO cyclicHierarchyDTO = JSONUtil.fromJson(new File(projectName + "-" + CHResult), CyclicHierarchyDTO.class);
        CyclicDependencyDTO cyclicDependencyDTO = JSONUtil.fromJson(new File(projectName + "-" + CDResult), CyclicDependencyDTO.class);
        AbstractionWithoutDecouplingDTO abstractionWithoutDecouplingDTO = JSONUtil.fromJson(new File(projectName + "-" + AWDResult), AbstractionWithoutDecouplingDTO.class);
//        FeatureEnvyByMetricDTO featureEnvyByMetricDTO = JSONUtil.fromJson(new File(projectName + "-" + FEResult), FeatureEnvyByMetricDTO.class);
//        ShotgunSurgeryDTO shotgunSurgeryDTO = JSONUtil.fromJson(new File(projectName + "-" + AWDResult), ShotgunSurgeryDTO.class);
//        DataClumpsDTO dataClumpsDTO = JSONUtil.fromJson(new File(projectName + "-" + DCResult), DataClumpsDTO.class);

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

        analyze(multipathHierarchyDTO);
        analyze(cyclicHierarchyDTO);
        analyze(cyclicDependencyDTO);
        analyze(abstractionWithoutDecouplingDTO);

        long endTime = System.currentTimeMillis();
        System.out.println("Running time : " + (endTime - startTime) / 1000.0 + "s");
        return 0;
    }

    private void analyze(MultipathHierarchyDTO multipathHierarchyDTO) {

        List<MultipathHierarchyStructure> multipathHierarchyStructureList = new ArrayList<>();
        for (MultipathHierarchyStructure multipathHierarchyStructure : multipathHierarchyDTO.getInstances()) {
            String startQN = multipathHierarchyStructure.getStart().getObject();
            String endQN = multipathHierarchyStructure.getEnd().getObject();
            qn2IsDecoupling.putIfAbsent(startQN, false);
            qn2IsDecoupling.putIfAbsent(endQN, false);

            if (qn2IsDecoupling.get(startQN) && qn2IsDecoupling.get(endQN)) {
                multipathHierarchyStructureList.add(multipathHierarchyStructure);
            }
        }
        MultipathHierarchyDTO multipathHierarchyDTO_D = new MultipathHierarchyDTO(multipathHierarchyStructureList.size(), multipathHierarchyStructureList);
        JSONUtil.toJson(multipathHierarchyDTO_D, "./", "mh-d");
    }

    private void analyze(CyclicHierarchyDTO cyclicHierarchyDTO) {
        int smellCounter = 0;
        List<CyclicHierarchyStructure> cyclicHierarchyStructureList = new ArrayList<>();
        for (CyclicHierarchyStructure cyclicHierarchyStructure : cyclicHierarchyDTO.getInstances()) {
            String superTypeQN = cyclicHierarchyStructure.getSuperType().getObject();
            String subTypeQN = cyclicHierarchyStructure.getSubType().getObject();
            qn2IsDecoupling.putIfAbsent(superTypeQN, false);
            qn2IsDecoupling.putIfAbsent(subTypeQN, false);
            if (qn2IsDecoupling.get(superTypeQN) && qn2IsDecoupling.get(subTypeQN)) {
                List<EntityDependencyRelationDetail> decouplingDetail = new ArrayList<>();
                for (EntityDependencyRelationDetail entityDependencyRelationDetail : cyclicHierarchyStructure.getDetails()) {
                    qn2IsDecoupling.putIfAbsent(entityDependencyRelationDetail.getFromEntity(), false);
                    qn2IsDecoupling.putIfAbsent(entityDependencyRelationDetail.getToEntity(), false);
                    if (qn2IsDecoupling.get(entityDependencyRelationDetail.getFromEntity()) && qn2IsDecoupling.get(entityDependencyRelationDetail.getToEntity())) {
                        decouplingDetail.add(entityDependencyRelationDetail);
                    }
                }
                if (decouplingDetail.size() != 0) {
                    CyclicHierarchyStructure cyclicHierarchyStructureIsIntrusive = new CyclicHierarchyStructure(smellCounter, cyclicHierarchyStructure.getSuperType(), cyclicHierarchyStructure.getSubType(), decouplingDetail);
                    cyclicHierarchyStructureList.add(cyclicHierarchyStructureIsIntrusive);
                    smellCounter += 1;
                }

            }
        }
        System.out.println("ch " + smellCounter);
        CyclicHierarchyDTO cyclicHierarchyDTO_D = new CyclicHierarchyDTO(cyclicHierarchyStructureList.size(), cyclicHierarchyStructureList);
        JSONUtil.toJson(cyclicHierarchyDTO_D, "./", "ch-d");

    }

    private void analyze(CyclicDependencyDTO cyclicDependencyDTO) {

        List<CyclicDependencyStructure> cyclicDependencyStructureList = new ArrayList<>();

        for (CyclicDependencyStructure cyclicDependencyStructure : cyclicDependencyDTO.getInstances()) {
            for (String cdModule : cyclicDependencyStructure.getModules()) {
                qn2IsDecoupling.putIfAbsent(cdModule, false);
                if (qn2IsDecoupling.get(cdModule)) {
                    cyclicDependencyStructureList.add(cyclicDependencyStructure);
                    System.out.println("cd d");
                }
            }
        }
        CyclicDependencyDTO cyclicDependencyDTO_D = new CyclicDependencyDTO(cyclicDependencyStructureList.size(), cyclicDependencyStructureList);
        JSONUtil.toJson(cyclicDependencyDTO_D, "./", "cd-d");

    }

    private void analyze(AbstractionWithoutDecouplingDTO abstractionWithoutDecouplingDTO) {
        int smellCounterIsIntrusive = 0;
        List<AbstractionWithoutDecouplingStructure> abstractionWithoutDecouplingStructureIsIntrusiveList = new ArrayList<>();
        for (AbstractionWithoutDecouplingStructure abstractionWithoutDecouplingStructure : abstractionWithoutDecouplingDTO.getInstances()) {
            String clientClassQN = abstractionWithoutDecouplingStructure.getClientClass().getObject();
            String superTypeQN = abstractionWithoutDecouplingStructure.getSuperType().getObject();
            String subTypeQN = abstractionWithoutDecouplingStructure.getSubType().getObject();
            qn2IsDecoupling.putIfAbsent(clientClassQN, false);
            qn2IsDecoupling.putIfAbsent(superTypeQN, false);
            qn2IsDecoupling.putIfAbsent(subTypeQN, false);
            if (qn2IsDecoupling.get(clientClassQN) && qn2IsDecoupling.get(superTypeQN) && qn2IsDecoupling.get(subTypeQN)) {
                List<EntityDependencyRelationDetail> clientClass2SuperTypeIntrusiveDetail = new ArrayList<>();
                List<EntityDependencyRelationDetail> clientClass2SubTypeIntrusiveDetail = new ArrayList<>();
                for (EntityDependencyRelationDetail entityDependencyRelationDetail : abstractionWithoutDecouplingStructure.getDetails().getClientClass2subType()) {
                    qn2IsDecoupling.putIfAbsent(entityDependencyRelationDetail.getFromEntity(), false);
                    qn2IsDecoupling.putIfAbsent(entityDependencyRelationDetail.getToEntity(), false);
                    if (qn2IsDecoupling.get(entityDependencyRelationDetail.getFromEntity()) && qn2IsDecoupling.get(entityDependencyRelationDetail.getToEntity())) {
                        clientClass2SubTypeIntrusiveDetail.add(entityDependencyRelationDetail);
                    }
                }
                for (EntityDependencyRelationDetail entityDependencyRelationDetail : abstractionWithoutDecouplingStructure.getDetails().getClientClass2superType()) {
                    qn2IsDecoupling.putIfAbsent(entityDependencyRelationDetail.getFromEntity(), false);
                    qn2IsDecoupling.putIfAbsent(entityDependencyRelationDetail.getToEntity(), false);
                    if (qn2IsDecoupling.get(entityDependencyRelationDetail.getFromEntity()) && qn2IsDecoupling.get(entityDependencyRelationDetail.getToEntity())) {
                        clientClass2SuperTypeIntrusiveDetail.add(entityDependencyRelationDetail);
                    }
                }
                if (clientClass2SuperTypeIntrusiveDetail.size() != 0 && clientClass2SubTypeIntrusiveDetail.size() != 0) {
                    smellCounterIsIntrusive += 1;
                    AbstractionWithoutDecouplingStructureDetail intrusiveDetails = new AbstractionWithoutDecouplingStructureDetail(clientClass2SuperTypeIntrusiveDetail, clientClass2SubTypeIntrusiveDetail);
                    AbstractionWithoutDecouplingStructure intrusiveAbstractionWithoutDecouplingStructure = new AbstractionWithoutDecouplingStructure(smellCounterIsIntrusive, abstractionWithoutDecouplingStructure.getSuperType(), abstractionWithoutDecouplingStructure.getClientClass(), abstractionWithoutDecouplingStructure.getSubType(), intrusiveDetails);
                    abstractionWithoutDecouplingStructureIsIntrusiveList.add(intrusiveAbstractionWithoutDecouplingStructure);
                    if (clientClass2SuperTypeIntrusiveDetail.size() == abstractionWithoutDecouplingStructure.getDetails().getClientClass2superType().size()) {
                        System.out.println(1);
                        if (clientClass2SubTypeIntrusiveDetail.size() == abstractionWithoutDecouplingStructure.getDetails().getClientClass2subType().size()) {
                            System.out.println(12);
                        }
                    }
                    if (clientClass2SubTypeIntrusiveDetail.size() == abstractionWithoutDecouplingStructure.getDetails().getClientClass2subType().size()) {
                        System.out.println(2);
                        if (clientClass2SuperTypeIntrusiveDetail.size() == abstractionWithoutDecouplingStructure.getDetails().getClientClass2superType().size()) {
                            System.out.println(21);
                        }
                    }
                }
            }
        }
        AbstractionWithoutDecouplingDTO abstractionWithoutDecouplingDTO_d = new AbstractionWithoutDecouplingDTO(abstractionWithoutDecouplingStructureIsIntrusiveList.size(), abstractionWithoutDecouplingStructureIsIntrusiveList);
        System.out.println("awd: " + abstractionWithoutDecouplingStructureIsIntrusiveList.size());
        JSONUtil.toJson(abstractionWithoutDecouplingDTO_d, "./", "awd-d");

    }
}
