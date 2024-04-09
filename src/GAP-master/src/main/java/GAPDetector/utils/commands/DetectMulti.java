package GAPDetector.utils.commands;

import GAPDetector.Storage;
import GAPDetector.detectors.GeneralDetectorMulti;
import GAPDetector.entities.*;
import GAPDetector.json.JsonProcessor;
import GAPDetector.json.inputDTO.dependencyModel.Location;
import GAPDetector.json.inputDTO.rule.Rule;
import GAPDetector.json.util.YAMLUtil;
import picocli.CommandLine;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;


@CommandLine.Command(name = "detectMulti", mixinStandardHelpOptions = true, helpCommand = true, version = "1.2.2",
        description = "Detect general Anti-patterns in the software system.")
public class DetectMulti implements Callable<Integer> {

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
    public Integer call() throws Exception {
        long startTime = System.currentTimeMillis();
        detect(projectName);
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

    public void detect(String projectName) throws Exception {
        AtomicInteger AWDCount = new AtomicInteger();
        AtomicInteger CDCount = new AtomicInteger();
        AtomicInteger CDNoReflectCount = new AtomicInteger();
        AtomicInteger CHCount = new AtomicInteger();
        AtomicInteger MHCount = new AtomicInteger();

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
//        System.out.println("Resolve hierarchy chain ...");
//        jsonProcessor.resolveHierarchyChain();
        jsonProcessor.resolveDependencyMapClassLevel();
        HashMap<Integer, HashMap<String, HashMap<Integer, Location>>> dependencyMapLocation = jsonProcessor.dependencyMapLocation;

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
        List<CompletableFuture<Void>> futures = new ArrayList<>();

        if (!MHRuleFile.equals("null")) {
            futures.add(CompletableFuture.runAsync(() -> {
                Rule MHRule = YAMLUtil.resolveRuleFile(new File(MHRuleFile));
                System.out.println("Start detecting Multipath Hierarchy...");
                MHCount.set(GeneralDetectorMulti.workflow(storage, MHRule, projectName, outputMode));
            }));
        }

        if (!CDRuleFile.equals("null")) {
            futures.add(CompletableFuture.runAsync(() -> {
                // Put the CD detection code here
                Rule CDRule = YAMLUtil.resolveRuleFile(new File(CDRuleFile));
                System.out.println("Start detecting Cyclic Dependency...");
                CDCount.set(GeneralDetectorMulti.workflow(storage, CDRule, projectName, outputMode));
            }));
        }

        if (!CDNoReflectRuleFile.equals("null")) {
            futures.add(CompletableFuture.runAsync(() -> {
                // Put the CDNoReflect detection code here
                Rule CDRule = YAMLUtil.resolveRuleFile(new File(CDNoReflectRuleFile));
                System.out.println("Start detecting Cyclic Dependency (Not include Reflect in cycle)...");
                CDNoReflectCount.set(GeneralDetectorMulti.workflow(storage, CDRule, projectName, outputMode));
            }));
        }

        if (!CHRuleFile.equals("null")) {
            futures.add(CompletableFuture.runAsync(() -> {
                // Put the CH detection code here
                Rule CHRule = YAMLUtil.resolveRuleFile(new File(CHRuleFile));
                System.out.println("Start detecting Cyclic Hierarchy...");
                CHCount.set(GeneralDetectorMulti.workflow(storage, CHRule, projectName, outputMode));
            }));
        }

        if (!AWDRuleFile.equals("null")) {
            futures.add(CompletableFuture.runAsync(() -> {
                // Put the AWD detection code here
                Rule AWDRule = YAMLUtil.resolveRuleFile(new File(AWDRuleFile));
                System.out.println("Start detecting Abstraction Without Decoupling...");
                AWDCount.set(GeneralDetectorMulti.workflow(storage, AWDRule, projectName, outputMode));
            }));
        }


        // Wait for all futures to complete
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

        // ... The rest of the code
        System.out.println("\nMultipath Hierarchy : " + MHCount);
        System.out.println("Cyclic Dependency : " + CDCount);
        System.out.println("Cyclic Dependency (Not include Reflect in cycle) : " + CDNoReflectCount);
        System.out.println("Cyclic Hierarchy: " + CHCount);
        System.out.println("Abstraction Without Decoupling: " + AWDCount);

    }

}
