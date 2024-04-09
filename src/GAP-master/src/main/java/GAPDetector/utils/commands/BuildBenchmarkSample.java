package GAPDetector.utils.commands;

import GAPDetector.entities.EntityIdentifier;
import GAPDetector.json.outputDTO.relations.SingleDependencyRelationDetailWithClassInfo;
import GAPDetector.json.outputDTO.relations.EntityDependencyRelationDetail;
import GAPDetector.json.outputDTO.smells.AwD.AbstractionWithoutDecouplingDTO;
import GAPDetector.json.outputDTO.smells.AwD.AbstractionWithoutDecouplingStructure;
import GAPDetector.json.outputDTO.smells.CD.CyclicDependencyDTO;
import GAPDetector.json.outputDTO.smells.CD.CyclicDependencyStructure;
import GAPDetector.json.outputDTO.smells.CD.MutualPackageDependencyRelation;
import GAPDetector.json.outputDTO.smells.CD.PackageDependencyRelationCell;
import GAPDetector.json.outputDTO.smells.CH.CyclicHierarchyDTO;
import GAPDetector.json.outputDTO.smells.CH.CyclicHierarchyStructure;
import GAPDetector.json.outputDTO.smells.MH.MultipathHierarchyDTO;
import GAPDetector.json.outputDTO.smells.MH.MultipathHierarchyStructure;
import GAPDetector.json.util.JSONUtil;
import GAPDetector.json.util.PathUtil;
import picocli.CommandLine;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;

import static GAPDetector.json.util.JSONUtil.writeStringToFile;

@CommandLine.Command(name = "buildBenchmarkSample", mixinStandardHelpOptions = true, helpCommand = true,
        description = "BuildBenchmarkSample")
public class BuildBenchmarkSample implements Callable<Integer> {
    @CommandLine.Option(names = {"-n", "--name"}, description = "project name")
    private String projectName = null;
    @CommandLine.Option(names = {"-s", "--sourceRootPath"}, description = "source root path")
    private String sourceRootPath = null;
    @CommandLine.Option(names = {"-a", "--awdFile"}, description = "AWD result file")
    private String awdResultFilePath = null;
    @CommandLine.Option(names = {"-cd", "--cdFile"}, description = "CD result file")
    private String cdResultFilePath = null;
    @CommandLine.Option(names = {"-ch", "--chFile"}, description = "CH result file")
    private String chResultFilePath = null;
    @CommandLine.Option(names = {"-m", "--mhFile"}, description = "MH result file")
    private String mhResultFilePath = null;
    @CommandLine.Option(names = {"-sn", "--sampleNumber"}, description = "Sample Number", defaultValue = "3")
    private int sampleNumber;
    //    @CommandLine.Option(names = {"-w", "--wd"}, description = "Working Directory", defaultValue = "", required = false)
//    private int workingDirectory;
    private final String blanklineCommand = "echo ' ' >> ";

    @Override
    public Integer call() throws Exception { // your business logic goes here...
        // SSH 连接远程主机并执行命令
        String outputFolder = "./" + projectName + "-dataset-out/";

        buildBenchmarkCommand(outputFolder, "AWD");
        buildBenchmarkCommand(outputFolder, "CH");
        buildBenchmarkCommand(outputFolder, "CD");
        buildBenchmarkCommand(outputFolder, "MH");

        return 0;
    }


    private void buildBenchmarkCommand(String outputFolder, String smellType) throws FileNotFoundException {
        String commandFix = "command";
        String infix = "-";
        String benchmarkFix = "bm";
        String shFilePostfix = "-dataset.sh";
        String txtFilePostfix = "-dataset.txt";

        String commandFilePath = outputFolder + projectName + infix + smellType + infix + commandFix + shFilePostfix;
        File commandFile = new File(commandFilePath);
        try {
            File file = new File(commandFilePath);
            if (!file.getParentFile().exists()) { // 如果父目录不存在，创建父目录
                file.getParentFile().mkdirs();
            }
            if (file.exists()) { // 如果已存在,删除旧文件
                file.delete();
            }
            file.createNewFile();
            // 使用FileWriter不需要考虑原文件不存在的情况
            // 当该文件不存在时，new FileWriter(file)会自动创建一个真实存在的空文件
            FileWriter fileWriter = new FileWriter(commandFile);
            // 往文件重写内容
            fileWriter.write("");// 清空
            fileWriter.flush();
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
//        String smellOutputFilePath = "./" + projectName + "-dataset-out-txt/AWD/"+ projectName + infix + smellType + infix + benchmarkFix;
//        String smellOutputFilePath = workingDirectory+"/" + projectName + "-dataset-out-txt/"+ projectName + infix + smellType + infix + benchmarkFix;

        String smellOutputFilePath = projectName + infix + smellType + infix + benchmarkFix;
//        String outputFilePath = projectName + infix + smellType + infix + benchmarkFix + txtFilePostfix;

        switch (smellType) {
            case "AWD":
                buildAwdBenchmark(smellOutputFilePath, commandFile);
                break;
            case "CH":
                buildChBenchmark(smellOutputFilePath, commandFile);
                break;
            case "CD":
                buildCdBenchmark(smellOutputFilePath, commandFile);
                break;
            case "MH":
                buildMhBenchmark(smellOutputFilePath, commandFile);
                break;
        }
    }

    private void buildAwdBenchmark(String outputFilePath, File commandFile) throws FileNotFoundException {
        String oldOutputFilePath = outputFilePath;
        if (awdResultFilePath == null) {
            awdResultFilePath = projectName + "-AWD.json";
        }
        AbstractionWithoutDecouplingDTO awdResult = JSONUtil.fromJson(new File(awdResultFilePath), AbstractionWithoutDecouplingDTO.class);
        String txtFilePostfix = ".txt";

        int curInstance = 0;
        for (AbstractionWithoutDecouplingStructure instance : awdResult.getInstances()) {
            if (curInstance >= sampleNumber) {
                break;
            }
            List<AbstractionWithoutDecouplingStructure> abstractionWithoutDecouplingStructuresSample = new ArrayList<>();
            abstractionWithoutDecouplingStructuresSample.add(instance);
            curInstance++;
            outputFilePath = oldOutputFilePath + "#" + curInstance + txtFilePostfix;
            String clearCommand = "echo ' ' > " + outputFilePath;
            writeStringToFile(clearCommand, commandFile);

            // Super, Client, Sub Hierarchy Info
            String echoSuperInfoCommand = buildInfoCommand("SuperClass : " + PathUtil.getLastStrByDot(instance.getSuperType().getObject()), outputFilePath);
            writeStringToFile(echoSuperInfoCommand, commandFile);

            buildClassHierarchyCommand(
                    instance.getSuperType().getLocation().getStartLine(),
                    instance.getSuperType().getLocation().getEndLine(),
                    sourceRootPath + "/" + instance.getSuperType().getFile(),
                    instance.getSuperType().getObject(), outputFilePath, commandFile
            );
            writeStringToFile(blanklineCommand + outputFilePath, commandFile);

            String echoSubInfoCommand = buildInfoCommand("SubClass : " + PathUtil.getLastStrByDot(instance.getSubType().getObject()), outputFilePath);
            writeStringToFile(echoSubInfoCommand, commandFile);
            buildClassHierarchyCommand(
                    instance.getSubType().getLocation().getStartLine(),
                    instance.getSubType().getLocation().getEndLine(),
                    sourceRootPath + "/" + instance.getSubType().getFile(),
                    instance.getSubType().getObject(), outputFilePath, commandFile
            );
            writeStringToFile(blanklineCommand + outputFilePath, commandFile);

            String echoClientInfoCommand = buildInfoCommand("ClientClass : " + PathUtil.getLastStrByDot(instance.getClientClass().getObject()), outputFilePath);
            writeStringToFile(echoClientInfoCommand, commandFile);

            buildClassHierarchyCommand(
                    instance.getClientClass().getLocation().getStartLine(),
                    instance.getClientClass().getLocation().getEndLine(),
                    sourceRootPath + "/" + instance.getClientClass().getFile(),
                    instance.getClientClass().getObject(), outputFilePath, commandFile
            );
            writeStringToFile(blanklineCommand + outputFilePath, commandFile);

            // C2Super
            String echoC2SuperInfoCommand = buildInfoCommand("client2Super", outputFilePath);
            writeStringToFile(echoC2SuperInfoCommand, commandFile);

//            buildClassHierarchyCommand(
//                    instance.getClientClass().getLocation().getStartLine(),
//                    instance.getClientClass().getLocation().getEndLine(),
//                    sourceRootPath + "/" + instance.getClientClass().getFile(),
//                    instance.getClientClass().getObject(), outputFilePath, commandFile
//            );

            int C2SuperCount = 1;

            for (EntityDependencyRelationDetail entityDependencyRelationDetail : instance.getDetails().getClientClass2superType()) {
                getRelationLocationDetail(outputFilePath, commandFile, C2SuperCount, entityDependencyRelationDetail);
                C2SuperCount++;
            }
            // C2Sub
            writeStringToFile(blanklineCommand + outputFilePath, commandFile);
            String echoC2SubInfoCommand = buildInfoCommand("client2Sub", outputFilePath);
            writeStringToFile(echoC2SubInfoCommand, commandFile);

//            buildClassHierarchyCommand(
//                    instance.getClientClass().getDetails().getLocation().getStartLine(),
//                    instance.getClientClass().getDetails().getLocation().getEndLine(),
//                    sourceRootPath + "/" + instance.getClientClass().getDetails().getFile(),
//                    instance.getClientClass().getDetails().getObject(), outputFilePath, commandFile
//            );
            int C2SubCount = 1;

            for (EntityDependencyRelationDetail entityDependencyRelationDetail : instance.getDetails().getClientClass2subType()) {
                getRelationLocationDetail(outputFilePath, commandFile, C2SubCount, entityDependencyRelationDetail);
                C2SubCount++;
            }
            AbstractionWithoutDecouplingDTO awdResultSample = new AbstractionWithoutDecouplingDTO(abstractionWithoutDecouplingStructuresSample.size(), abstractionWithoutDecouplingStructuresSample);
            JSONUtil.toJson(awdResultSample, "./" + projectName + "-dataset-out-json/AWD/", projectName + "-bm-sample-" + awdResultSample + "#" + curInstance, "benchmark");
        }
    }


    private void buildChBenchmark(String outputFilePath, File commandFile) throws FileNotFoundException {
        String oldOutputFilePath = outputFilePath;
        if (chResultFilePath == null) {
            chResultFilePath = projectName + "-CH.json";
        }
        CyclicHierarchyDTO chResult = JSONUtil.fromJson(new File(chResultFilePath), CyclicHierarchyDTO.class);
        String txtFilePostfix = ".txt";

        int curInstance = 0;
        for (CyclicHierarchyStructure instance : chResult.getInstances()) {
            if (curInstance >= sampleNumber) {
                break;
            }
            List<CyclicHierarchyStructure> cyclicHierarchyStructuresSample = new ArrayList<>();
            cyclicHierarchyStructuresSample.add(instance);
            curInstance++;
            outputFilePath = oldOutputFilePath + "#" + curInstance + txtFilePostfix;
            String clearCommand = "echo ' ' > " + outputFilePath;
            writeStringToFile(clearCommand, commandFile);
            // Super2Sub
            String echoC2SuperInfoCommand = buildInfoCommand("Super2Sub", outputFilePath);
            writeStringToFile(echoC2SuperInfoCommand, commandFile);

            buildClassHierarchyCommand(
                    instance.getSuperType().getLocation().getStartLine(),
                    instance.getSuperType().getLocation().getEndLine(),
                    sourceRootPath + "/" + instance.getSuperType().getFile(),
                    instance.getSuperType().getObject(), outputFilePath, commandFile
            );

            int Super2SubCount = 1;
            for (EntityDependencyRelationDetail entityDependencyRelationDetail : instance.getDetails()) {
                getRelationLocationDetail(outputFilePath, commandFile, Super2SubCount, entityDependencyRelationDetail);
                Super2SubCount++;
            }
            CyclicHierarchyDTO chResultSample = new CyclicHierarchyDTO(cyclicHierarchyStructuresSample.size(), cyclicHierarchyStructuresSample);
            JSONUtil.toJson(chResultSample, "./" + projectName + "-dataset-out-json/CH/", projectName + "-bm-sample-" + chResultSample + "#" + curInstance, "benchmark");
        }
    }

    private void buildCdBenchmark(String outputFilePath, File commandFile) throws FileNotFoundException {
        String oldOutputFilePath = outputFilePath;

        if (cdResultFilePath == null) {
            cdResultFilePath = projectName + "-CD.json";
        }
        String txtFilePostfix = ".txt";

        CyclicDependencyDTO cdResult = JSONUtil.fromJson(new File(cdResultFilePath), CyclicDependencyDTO.class);
        int curInstance = 0;
        for (CyclicDependencyStructure instance : cdResult.getInstances()) {
            if (curInstance >= sampleNumber) {
                break;
            }
            List<CyclicDependencyStructure> cyclicDependencyStructuresSample = new ArrayList<>();
            cyclicDependencyStructuresSample.add(instance);
            curInstance++;
            outputFilePath = oldOutputFilePath + "#" + curInstance + txtFilePostfix;
            String clearCommand = "echo ' ' > " + outputFilePath;
            writeStringToFile(clearCommand, commandFile);
            for (PackageDependencyRelationCell packageDependencyRelationCell : instance.getPackageDependencyRelationCells()) {
                // srcPackage
                String echoSrcPackageInfoCommand = buildInfoCommand("srcPackage " + packageDependencyRelationCell.getSourcePackage(), outputFilePath);
                writeStringToFile(echoSrcPackageInfoCommand, commandFile);
                for (MutualPackageDependencyRelation mutualPackageDependencyRelation : packageDependencyRelationCell.getMutualPackageDependencyRelations()) {
                    String echoDestPackageInfoCommand = buildInfoCommand("destPackage " + mutualPackageDependencyRelation.getTargetPackage().getObject(), outputFilePath);
                    writeStringToFile(echoDestPackageInfoCommand, commandFile);
                    int curRelationCount = 1;
                    for (SingleDependencyRelationDetailWithClassInfo singleDependencyRelationDetailWithClassInfo : mutualPackageDependencyRelation.getEntityDependencies()) {
                        EntityIdentifier fromClass = singleDependencyRelationDetailWithClassInfo.getFromClass();
                        EntityDependencyRelationDetail entityDependencyRelationDetail = singleDependencyRelationDetailWithClassInfo.getEntityDependencyRelationDetail();
                        buildClassHierarchyCommand(
                                fromClass.getLocation().getStartLine(),
                                fromClass.getLocation().getEndLine(),
                                sourceRootPath + "/" + fromClass.getFile(),
                                fromClass.getObject(), outputFilePath, commandFile
                        );
                        getRelationLocationDetail(outputFilePath, commandFile, curRelationCount, entityDependencyRelationDetail);
                        curRelationCount += 1;
                    }
                }
            }
            CyclicDependencyDTO cdResultSample = new CyclicDependencyDTO(cyclicDependencyStructuresSample.size(), cyclicDependencyStructuresSample);
            JSONUtil.toJson(cdResultSample, "./" + projectName + "-dataset-out-json/CD/", projectName + "-bm-sample-" + cdResultSample + "#" + curInstance, "benchmark");
        }
    }

    private void buildMhBenchmark(String outputFilePath, File commandFile) throws FileNotFoundException {
        String oldOutputFilePath = outputFilePath;

        if (mhResultFilePath == null) {
            mhResultFilePath = projectName + "-MH.json";
        }
        String txtFilePostfix = ".txt";

        MultipathHierarchyDTO mhResult = JSONUtil.fromJson(new File(mhResultFilePath), MultipathHierarchyDTO.class);
        int curInstance = 0;

        for (MultipathHierarchyStructure instance : mhResult.getInstances()) {
            if (curInstance >= sampleNumber) {
                break;
            }
            List<MultipathHierarchyStructure> multipathHierarchyStructuresSample = new ArrayList<>();
            multipathHierarchyStructuresSample.add(instance);
            curInstance++;
            outputFilePath = oldOutputFilePath + "#" + curInstance + txtFilePostfix;
            String clearCommand = "echo ' ' > " + outputFilePath;
            writeStringToFile(clearCommand, commandFile);

            String echoSrcInfoCommand = buildInfoCommand("Multipath src " + instance.getStart().getObject(), outputFilePath);
            String echoDestInfoCommand = buildInfoCommand("Multipath dest " + instance.getEnd().getObject(), outputFilePath);
            writeStringToFile(echoSrcInfoCommand, commandFile);
            writeStringToFile(echoDestInfoCommand, commandFile);

            int pathCount = 0;
            for (List<EntityDependencyRelationDetail> entityDependencyRelationDetails : instance.getMultipath()) {
                pathCount++;
                writeStringToFile(blanklineCommand + outputFilePath, commandFile);
                String pathCountInfoCommand = buildInfoCommand("Path  " + pathCount, outputFilePath);
                writeStringToFile(pathCountInfoCommand, commandFile);
                int subPathCount = 0;
                for (EntityDependencyRelationDetail entityDependencyRelationDetail : entityDependencyRelationDetails) {
                    subPathCount++;
                    writeStringToFile(blanklineCommand + outputFilePath, commandFile);
                    String echoCurrentPathInfoCommand = buildInfoCommand("SubPath " + subPathCount + " Src " + PathUtil.getLastStrByDot(entityDependencyRelationDetail.getFromEntity()) + " " + entityDependencyRelationDetail.getRelationType() + " " + PathUtil.getLastStrByDot(entityDependencyRelationDetail.getToEntity()), outputFilePath);
                    writeStringToFile(echoCurrentPathInfoCommand, commandFile);
                    writeStringToFile(blanklineCommand + outputFilePath, commandFile);

                    buildClassHierarchyCommand(
                            entityDependencyRelationDetail.getFromEntityLocation().getStartLine(),
                            entityDependencyRelationDetail.getFromEntityLocation().getEndLine(),
                            sourceRootPath + "/" + entityDependencyRelationDetail.getFromEntityFile(),
                            entityDependencyRelationDetail.getFromEntity(), outputFilePath, commandFile
                    );
                }

            }
            MultipathHierarchyDTO mhResultSample = new MultipathHierarchyDTO(multipathHierarchyStructuresSample.size(), multipathHierarchyStructuresSample);
            JSONUtil.toJson(mhResultSample, "./" + projectName + "-dataset-out-json/MH/", projectName + "-bm-sample-" + mhResultSample + "#" + curInstance, "benchmark");
        }
    }

    private void getRelationLocationDetail(String outputFilePath, File commandFile, int curRelationCount, EntityDependencyRelationDetail entityDependencyRelationDetail) {
        writeStringToFile(blanklineCommand + outputFilePath, commandFile);
        String echoRelationInfoCommand = buildInfoCommand("Relation " + curRelationCount + " " + PathUtil.getLast2StrByDot(entityDependencyRelationDetail.getFromEntity()) + " " + entityDependencyRelationDetail.getRelationType() + " " + PathUtil.getLast2StrByDot(entityDependencyRelationDetail.getToEntity()), outputFilePath);
        writeStringToFile(echoRelationInfoCommand, commandFile);
        writeStringToFile(blanklineCommand + outputFilePath, commandFile);

        String srcDefineCommand = buildDepLineCommand(
                entityDependencyRelationDetail.getFromEntityLocation().getStartLine(), entityDependencyRelationDetail.getFromEntityLocation().getEndLine(),
                sourceRootPath + "/" + entityDependencyRelationDetail.getFromEntityFile(), entityDependencyRelationDetail.getFromEntity(), outputFilePath
        );
        writeStringToFile(srcDefineCommand, commandFile);
        writeStringToFile(blanklineCommand + outputFilePath, commandFile);


        String depCommand = buildDepLineCommand(
                entityDependencyRelationDetail.getDepLocation().getStartLine(), entityDependencyRelationDetail.getDepLocation().getEndLine(),
                sourceRootPath + "/" + entityDependencyRelationDetail.getFromEntityFile(), entityDependencyRelationDetail.getToEntity(), outputFilePath
        );
        writeStringToFile(depCommand, commandFile);
        writeStringToFile(blanklineCommand + outputFilePath, commandFile);


        String depDefineToCommand = buildDepLineCommand(
                entityDependencyRelationDetail.getToEntityLocation().getStartLine(), entityDependencyRelationDetail.getToEntityLocation().getEndLine(),
                sourceRootPath + "/" + entityDependencyRelationDetail.getToEntityFile(), entityDependencyRelationDetail.getToEntity(), outputFilePath
        );
        writeStringToFile(depDefineToCommand, commandFile);
        writeStringToFile(blanklineCommand + outputFilePath, commandFile);


    }

    private String buildInfoCommand(String info, String outputFilePath) {
        return "echo " + info + " >> " + outputFilePath;
    }

    private void buildClassHierarchyCommand(Integer start, Integer end, String filepath, String entity, String outputFilePath, File commandFile) {
//        List<String> keywords = Arrays.asList("extends", "implements", "public class", "public interface", PathUtil.getLastStrByPathDelimiter(entity));
        List<String> keywords = Arrays.asList("public class", "public interface", "class", "enum", "public abstract", "final class");

        for (String keyword : keywords) {
            String command = buildClassStartCommand(start, end, filepath, outputFilePath, keyword);
            writeStringToFile(command, commandFile);
        }
    }

    private String buildClassStartCommand(Integer start, Integer end, String filepath, String outputFilePath, String keyword) {
        return "sed -n " + start + "," + end + "p " + filepath + " | " + "grep -h -n -m 1 -w \"" + keyword + PathUtil.getLastStrByDot("") + "\" >> " + outputFilePath;
    }

    private String buildDepLineCommand(Integer start, Integer end, String filepath, String entity, String outputFilePath) {
//        return "sed -n '" + start + "," + end + "{ =; p; }' " + filepath + " | awk 'NR%2==1 {printf \"%d:\", $0} NR%2==0'" + " | " + "grep -n -A 2 -B 2 -m 1 -w \"" + PathUtil.getLastStrByDot(entity) + "\"  | awk -F: '{print $2\":\"$3}' OFS=\":\" >> " + outputFilePath;
        String sedCommand = "sed -n '" + start + "," + end + "p' " + filepath;
//        String awkCommand = "awk '{print NR+43, $0}'";
        String awkCommand = "awk '{print NR+" + (start - 1) + " \":\" $0}'";

        String grepCommand = "grep -m 1 -w \"" + PathUtil.getLastStrByDot(entity) + "\"";
        String finalCommand = sedCommand + " | " + awkCommand + " | " + grepCommand + " >> " + outputFilePath;
//        return "sed -n '" + start + "," + end + "' " + filepath + " | awk 'NR%2==1 {printf \"%d:\", $0} NR%2==0'" + " | " + "grep -n -A 2 -B 2 -m 1 -w \"" + PathUtil.getLastStrByDot(entity) + "\"  | awk -F: '{print $2\":\"$3}' OFS=\":\" >> " + outputFilePath;

        return finalCommand;

    }

    public static void main(String... args) {
        int exitCode = new CommandLine(new Rebuild()).execute(args);
        System.exit(exitCode);
    }
}
