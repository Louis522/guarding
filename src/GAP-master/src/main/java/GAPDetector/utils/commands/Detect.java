package GAPDetector.utils.commands;

import GAPDetector.Storage;
import GAPDetector.detectors.GeneralDetector;
import GAPDetector.entities.*;
import GAPDetector.json.JsonProcessor;
import GAPDetector.json.inputDTO.dependencyModel.Location;
import GAPDetector.json.inputDTO.rule.Rule;
import GAPDetector.json.outputDTO.smells.AwD.AbstractionWithoutDecouplingDTO;
import GAPDetector.json.outputDTO.smells.CD.CyclicDependencyDTO;
import GAPDetector.json.outputDTO.smells.CH.CyclicHierarchyDTO;
import GAPDetector.json.outputDTO.smells.DC.DataClumpsDTO;
import GAPDetector.json.outputDTO.smells.FE.FeatureEnvyDTO;
import GAPDetector.json.outputDTO.analyzers.GAPAnalyzer;
import GAPDetector.json.outputDTO.smells.MH.MultipathHierarchyDTO;
import GAPDetector.json.outputDTO.smells.SS.ShotgunSurgeryDTO;
import GAPDetector.json.util.JSONUtil;
import GAPDetector.json.util.YAMLUtil;
import org.supercsv.cellprocessor.Optional;
import org.supercsv.cellprocessor.constraint.NotNull;
import org.supercsv.cellprocessor.ift.CellProcessor;
import picocli.CommandLine;

import java.io.File;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Callable;

import static GAPDetector.json.util.CSVUtil.writeCSVAnalyzerContext;
import static GAPDetector.json.util.GAPUtil.analyzeReflect;


@CommandLine.Command(name = "detect", mixinStandardHelpOptions = true, helpCommand = true, version = "1.2.2",
        description = "Detect general Anti-patterns in the software system.")
public class Detect implements Callable<Integer> {

    @CommandLine.Option(names = {"-n", "--name"}, defaultValue = "Example", description = "project name")
    private String projectName;
    @CommandLine.Option(names = {"-d", "--dependency"}, required = true, description = "dependency model file path")
    private String dependencyModelFilePath;
    @CommandLine.Option(names = {"-f", "--facade"}, required = true, description = "dependency model file path")
    private String facadeFilePath;
    @CommandLine.Option(names = {"-o", "--ownership"}, description = "ownership file path")
    private String ownershipFilePath;
    @CommandLine.Option(names = {"-m", "--measureResult"}, description = "measure result file path")
    private String measureResultFile;
    @CommandLine.Option(names = {"-mh", "--MHRule"}, defaultValue = "MH-rule.yml", description = "MH rule file path")
    private String MHRuleFile;
    @CommandLine.Option(names = {"-ch", "--CHRule"}, defaultValue = "CH-rule.yml", description = "CH rule file path")
    private String CHRuleFile;
    @CommandLine.Option(names = {"-cd", "--CDRule"}, defaultValue = "CD-rule.yml", description = "CD rule file path")
    private String CDRuleFile;
    @CommandLine.Option(names = {"-cdnr", "--CDNRRule"}, defaultValue = "CD-NoReflect-rule.yml", description = "CD(No Reflect) rule file path")
    private String CDNoReflectRuleFile;
    @CommandLine.Option(names = {"-awd", "--AWDRule"}, defaultValue = "AWD-rule.yml", description = "AWD rule file path")
    private String AWDRuleFile;
    @CommandLine.Option(names = {"-fe", "--FERule"}, defaultValue = "FE-rule.yml", description = "FE rule file path")
    private String FERuleFile;
    @CommandLine.Option(names = {"-ss", "--SSRule"}, defaultValue = "SS-rule.yml", description = "SS rule file path")
    private String SSRuleFile;
    @CommandLine.Option(names = {"-dc", "--DCRule"}, defaultValue = "DC-rule.yml", description = "DC rule file path")
    private String DCRuleFile;
    @CommandLine.Option(names = {"-om", "--outputMode"}, defaultValue = "detailed", description = "output mode, detailed, benchmark, simple")
    private String outputMode;


    @Override
    public Integer call() throws Exception { // your business logic goes here...
        long startTime = System.currentTimeMillis();
        detect();
        long endTime = System.currentTimeMillis();
        System.out.println("Running time : " + (endTime - startTime) / 1000.0 + "s");
        return 0;
    }

    private JsonProcessor createJsonProcessor(boolean facadeFlag, boolean ownershipFlag, boolean measureFlag) throws Exception {
        int facadeValue = facadeFlag ? 1 : 0;
        int ownershipValue = ownershipFlag ? 1 : 0;
        int measureValue = measureFlag ? 1 : 0;
        int mode = facadeValue * 4 + ownershipValue * 2 + measureValue;
        return new JsonProcessor(dependencyModelFilePath, facadeFilePath, ownershipFilePath, measureResultFile, mode);

    }

    public void detect() throws Exception {
        String outputFolder = projectName + "-gap-out";
        int AWDCount = 0;
        int AWDCountIsIntrusive = 0;
        int CDCount = 0;
        int CDCountIsIntrusive = 0;
        int CHCount = 0;
        int CHCountIsIntrusive = 0;
        int MHCount = 0;
        int MHCountIsIntrusive = 0;
        int SSCount = 0;
        int SSCountIsIntrusive = 0;
        int DCCount = 0;
        int DCCountIsIntrusive = 0;
        int FECount = 0;
        int FECountIsIntrusive = 0;
        int CDNoReflectCount = 0;
        int CDNoReflectIsIntrusiveCount = 0;

        System.out.println("Parsing...");
        boolean facadeFlag = (facadeFilePath != null && !facadeFilePath.isEmpty());
        boolean ownershipFlag = (ownershipFilePath != null && !ownershipFilePath.isEmpty());
        boolean measureFlag = (measureResultFile != null && !measureResultFile.isEmpty());

        JsonProcessor jsonProcessor = createJsonProcessor(facadeFlag, ownershipFlag, measureFlag);

        System.out.println("Resolve entity ...");
        jsonProcessor.resolveEntity();
        System.out.println("Resolve dependencies ...");
        jsonProcessor.resolveDetails();

        HashMap<TypeEntity, List<TypeEntity>> super_subs_class = jsonProcessor.super2SubsInherit;
        HashMap<TypeEntity, TypeEntity> sub_super_class = jsonProcessor.subSuperInherit;
        HashMap<Integer, TypeEntity> typeEntities = jsonProcessor.id2typeEntity;
        HashMap<Integer, VarEntity> varEntities = jsonProcessor.id2varEntity;
        HashMap<Integer, FuncImplEntity> funcImplEntities = jsonProcessor.id2funcImplEntity;
        HashMap<String, List<TypeEntity>> object_typeEntities = jsonProcessor.object_typeEntities;
        System.out.println("Resolve hierarchy chain ...");
        jsonProcessor.resolveHierarchyChain();
        jsonProcessor.resolveDependencyMapClassLevel();
        HashMap<Integer, HashMap<String, HashMap<Integer, Location>>> dependencyMapLocation = jsonProcessor.dependencyMapLocation;
//        HashMap<Integer, HashMap<Integer, Location>> dependencyLocation = jsonProcessor.dependencyLocation;

        HashMap<Integer, HashMap<Integer, HashMap<String, HashMap<Integer, HashMap<Integer, Location>>>>> dependencyMapClassLevel = jsonProcessor.dependencyMapClassLevel;
        HashMap<Integer, AbstractEntity> id_abstractEntity = jsonProcessor.id2abstractEntity;
        HashMap<Integer, PackageEntity> id_package = jsonProcessor.id2packageEntity;
        HashMap<Integer, FileEntity> id_file = jsonProcessor.id_fileEntity;
        HashMap<String, FileEntity> qn_file = jsonProcessor.qn2fileEntity;
        HashMap<Integer, List<Integer>> hierarchyRelated = jsonProcessor.hierarchyRelated;

        Storage storage = new Storage(super_subs_class, sub_super_class, hierarchyRelated,
                id_abstractEntity, typeEntities, varEntities, funcImplEntities,
                object_typeEntities, id_package, id_file, qn_file,
                dependencyMapLocation, dependencyMapClassLevel);

        if (!MHRuleFile.equals("null")) {
            Rule MHRule = YAMLUtil.resolveRuleFile(new File(MHRuleFile));
            System.out.println("Start detecting Multipath Hierarchy...");
            GeneralDetector MHDetector = new GeneralDetector(storage, ownershipFlag, measureFlag);
            MHDetector.workflow(MHRule);
            MultipathHierarchyDTO multipathHierarchyDTO = new MultipathHierarchyDTO(MHDetector.smellCounter, MHDetector.multipathHierarchyStructureList);
            JSONUtil.toJson(multipathHierarchyDTO, outputFolder, projectName + "-" + multipathHierarchyDTO, outputMode);
            MHCount = MHDetector.smellCounter;

            if (ownershipFlag) {
                MultipathHierarchyDTO multipathHierarchyDTOIsIntrusive = new MultipathHierarchyDTO(MHDetector.smellCounterIsIntrusive, MHDetector.multipathHierarchyStructureIsIntrusiveList);
                JSONUtil.toJson(multipathHierarchyDTOIsIntrusive, outputFolder, projectName + "-" + multipathHierarchyDTOIsIntrusive + "-IsIntrusive");
                MHCountIsIntrusive = MHDetector.smellCounterIsIntrusive;
            }

        }


        if (!CDRuleFile.equals("null")) {
            Rule CDRule = YAMLUtil.resolveRuleFile(new File(CDRuleFile));
            System.out.println("Start detecting Cyclic Dependency...");
            GeneralDetector CDDetector = new GeneralDetector(storage, ownershipFlag, measureFlag);
            CDDetector.workflow(CDRule);
            CyclicDependencyDTO cyclicDependencyDTO = new CyclicDependencyDTO(CDDetector.smellCounter, CDDetector.cyclicDependencyStructureList);
            JSONUtil.toJson(cyclicDependencyDTO, outputFolder, projectName + "-" + cyclicDependencyDTO, outputMode);
            CDCount = CDDetector.smellCounter;

            if (ownershipFlag) {
                CyclicDependencyDTO cyclicDependencyDTOIsIntrusive = new CyclicDependencyDTO(CDDetector.smellCounterIsIntrusive, CDDetector.cyclicDependencyStructureIsIntrusiveList);
                JSONUtil.toJson(cyclicDependencyDTOIsIntrusive, outputFolder, projectName + "-" + cyclicDependencyDTO + "-IsIntrusive");
                CDCountIsIntrusive = CDDetector.smellCounterIsIntrusive;
            }
        }

        if (!CDNoReflectRuleFile.equals("null")) {
            Rule CDRule = YAMLUtil.resolveRuleFile(new File(CDNoReflectRuleFile));
            System.out.println("Start detecting Cyclic Dependency (Not include Reflect in cycle)...");
            GeneralDetector CDDetector = new GeneralDetector(storage, ownershipFlag, measureFlag);
            CDDetector.workflow(CDRule);
            CyclicDependencyDTO cyclicDependencyDTO = new CyclicDependencyDTO(CDDetector.smellCounter, CDDetector.cyclicDependencyStructureList);
            JSONUtil.toJson(cyclicDependencyDTO, outputFolder, projectName + "-" + cyclicDependencyDTO + "NoReflect", outputMode);
            CDNoReflectCount = CDDetector.smellCounter;

            if (ownershipFlag) {
                CyclicDependencyDTO cyclicDependencyDTOIsIntrusive = new CyclicDependencyDTO(CDDetector.smellCounterIsIntrusive, CDDetector.cyclicDependencyStructureIsIntrusiveList);
                JSONUtil.toJson(cyclicDependencyDTOIsIntrusive, outputFolder, projectName + "-" + cyclicDependencyDTO + "NoReflectIsIntrusive");
                CDNoReflectIsIntrusiveCount = CDDetector.smellCounterIsIntrusive;
            }
        }

        if (!CHRuleFile.equals("null")) {
            Rule CHRule = YAMLUtil.resolveRuleFile(new File(CHRuleFile));
            System.out.println("Start detecting Cyclic Hierarchy...");
            GeneralDetector CHDetector = new GeneralDetector(storage, ownershipFlag, measureFlag);
            CHDetector.workflow(CHRule);
            CyclicHierarchyDTO cyclicHierarchyDTO = new CyclicHierarchyDTO(CHDetector.smellCounter, CHDetector.cyclicHierarchyStructureList);
            JSONUtil.toJson(cyclicHierarchyDTO, outputFolder, projectName + "-" + cyclicHierarchyDTO, outputMode);
            CHCount = CHDetector.smellCounter;
            if (ownershipFlag) {
                CyclicHierarchyDTO cyclicHierarchyDTOIsIntrusive = new CyclicHierarchyDTO(CHDetector.smellCounterIsIntrusive, CHDetector.cyclicHierarchyStructureIsIntrusiveList);
                JSONUtil.toJson(cyclicHierarchyDTOIsIntrusive, outputFolder, projectName + "-" + cyclicHierarchyDTO + "-IsIntrusive");
                CHCountIsIntrusive = CHDetector.smellCounterIsIntrusive;
            }
        }
        if (!AWDRuleFile.equals("null")) {
            Rule AWDRule = YAMLUtil.resolveRuleFile(new File(AWDRuleFile));
            System.out.println("Start detecting Abstraction Without Decoupling...");
            GeneralDetector AWDDetector = new GeneralDetector(storage, ownershipFlag, measureFlag);
            AWDDetector.workflow(AWDRule);
            AbstractionWithoutDecouplingDTO abstractionWithoutDecouplingDTO = new AbstractionWithoutDecouplingDTO(AWDDetector.smellCounter, AWDDetector.abstractionWithoutDecouplingStructureList);
//            JSONUtil.toJson(abstractionWithoutDecouplingDTO, outputFolder, projectName + "-" + abstractionWithoutDecouplingDTO);
            JSONUtil.toJson(abstractionWithoutDecouplingDTO, outputFolder, projectName + "-" + abstractionWithoutDecouplingDTO, outputMode);

            AWDCount = AWDDetector.smellCounter;
            if (ownershipFlag) {
                AbstractionWithoutDecouplingDTO abstractionWithoutDecouplingDTOIsIntrusive = new AbstractionWithoutDecouplingDTO(AWDDetector.smellCounterIsIntrusive, AWDDetector.abstractionWithoutDecouplingStructureIsIntrusiveList);
                JSONUtil.toJson(abstractionWithoutDecouplingDTOIsIntrusive, outputFolder, projectName + "-" + abstractionWithoutDecouplingDTO + "-IsIntrusive");
                AWDCountIsIntrusive = AWDDetector.smellCounterIsIntrusive;
            }
        }

        if (!SSRuleFile.equals("null")) {
            Rule SSRule = YAMLUtil.resolveRuleFile(new File(SSRuleFile));
            System.out.println("Start detecting Shotgun Surgery...");
            GeneralDetector SSDetector = new GeneralDetector(storage, ownershipFlag, measureFlag);
            SSDetector.workflow(SSRule);
            ShotgunSurgeryDTO shotgunSurgeryDTO = new ShotgunSurgeryDTO(SSDetector.smellCounter, SSDetector.shotgunSurgeryStructureList);
            JSONUtil.toJson(shotgunSurgeryDTO, outputFolder, projectName + "-" + shotgunSurgeryDTO, outputMode);
            SSCount = SSDetector.smellCounter;
            if (ownershipFlag) {
                ShotgunSurgeryDTO shotgunSurgeryDTOIsIntrusive = new ShotgunSurgeryDTO(SSDetector.smellCounterIsIntrusive, SSDetector.shotgunSurgeryStructureIsIntrusiveList);
                JSONUtil.toJson(shotgunSurgeryDTOIsIntrusive, outputFolder, projectName + "-" + shotgunSurgeryDTO + "-IsIntrusive");
                SSCountIsIntrusive = SSDetector.smellCounterIsIntrusive;
            }
        }

        if (!DCRuleFile.equals("null")) {
            Rule DCRule = YAMLUtil.resolveRuleFile(new File(DCRuleFile));
            System.out.println("Start detecting Data Clumps...");
            GeneralDetector DCDetector = new GeneralDetector(storage, ownershipFlag, measureFlag);
            DCDetector.workflow(DCRule);
            DataClumpsDTO dataClumpsDTO = new DataClumpsDTO(DCDetector.smellCounter, DCDetector.dataClumpsStructureList);
            JSONUtil.toJson(dataClumpsDTO, outputFolder, projectName + "-" + dataClumpsDTO, outputMode);
            DCCount = DCDetector.smellCounter;
            if (ownershipFlag) {
                DataClumpsDTO dataClumpsDTOIsIntrusive = new DataClumpsDTO(DCDetector.smellCounterIsIntrusive, DCDetector.dataClumpsStructureIsIntrusiveList);
                JSONUtil.toJson(dataClumpsDTOIsIntrusive, outputFolder, projectName + "-" + dataClumpsDTO + "-IsIntrusive");
                DCCountIsIntrusive = DCDetector.smellCounterIsIntrusive;
            }
        }

        if (!FERuleFile.equals("null")) {
            Rule FERule = YAMLUtil.resolveRuleFile(new File(FERuleFile));
            System.out.println("Start detecting Feature Envy...");
            GeneralDetector FEDetector = new GeneralDetector(storage, ownershipFlag, measureFlag);
            FEDetector.workflow(FERule);
            FeatureEnvyDTO featureEnvyDTO = new FeatureEnvyDTO(FEDetector.smellCounter, FEDetector.featureEnvyStructureList);
            JSONUtil.toJson(featureEnvyDTO, outputFolder, projectName + "-" + featureEnvyDTO, outputMode);
            FECount = FEDetector.smellCounter;
            if (ownershipFlag) {
                FeatureEnvyDTO featureEnvyDTOIsIntrusive = new FeatureEnvyDTO(FEDetector.smellCounterIsIntrusive, FEDetector.featureEnvyStructureIsIntrusiveList);
                JSONUtil.toJson(featureEnvyDTOIsIntrusive, outputFolder, projectName + "-" + featureEnvyDTO + "-IsIntrusive");
                FECountIsIntrusive = FEDetector.smellCounterIsIntrusive;
            }
        }

        System.out.println("\nMultipath Hierarchy : " + MHCount);
        System.out.println("Cyclic Dependency : " + CDCount);
        System.out.println("Cyclic Dependency (Not include Reflect in cycle) : " + CDNoReflectCount);

        System.out.println("Cyclic Hierarchy: " + CHCount);
        System.out.println("Abstraction Without Decoupling: " + AWDCount);
        System.out.println("Shotgun Surgery: " + SSCount);
        System.out.println("Data Clumps: " + DCCount);
        System.out.println("Feature Envy: " + FECount);
        int total = MHCount + CDCount + CHCount + AWDCount + SSCount + DCCount + FECount;
        System.out.println("Total code smells count (MH,CH,AWD,SS,DC,FE,CD(include Reflect in cycle) : " + total + "\n");
        final String[] header = new String[]{"Project", "Total", "AWD", "CD", "CH", "DC", "FE", "MH", "SS"};
        final CellProcessor[] processors = new CellProcessor[]{new NotNull(), new Optional(), new Optional(), new Optional(), new Optional(), new Optional(), new Optional(), new Optional(), new Optional()};

        GAPAnalyzer gapAnalyzer = new GAPAnalyzer(projectName, total, MHCount, CDCount, CHCount, AWDCount, SSCount, DCCount, FECount);
        StringWriter writer = writeCSVAnalyzerContext(header, processors, gapAnalyzer);
        JSONUtil.outputCSVFile(writer, outputFolder, projectName + "-" + "GAP-Analyzer");

        if (false) {
            analyzeReflect(outputFolder + "/" + projectName + "-CD.json", dependencyModelFilePath, outputFolder, projectName);

        }
    }

}
