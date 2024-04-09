package GAPDetector.utils.commands;

import GAPDetector.json.inputDTO.dependencyModel.DependencyData;
import GAPDetector.json.outputDTO.relations.SingleDependencyRelationDetailWithClassInfo;
import GAPDetector.json.outputDTO.relations.EntityDependencyRelationDetail;
import GAPDetector.json.outputDTO.smells.CD.CyclicDependencyDTO;
import GAPDetector.json.outputDTO.smells.CD.CyclicDependencyStructure;
import GAPDetector.json.outputDTO.smells.CD.MutualPackageDependencyRelation;
import GAPDetector.json.outputDTO.smells.CD.PackageDependencyRelationCell;
import GAPDetector.json.util.JSONUtil;
import org.apache.commons.lang3.mutable.MutableInt;
import picocli.CommandLine;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Formatter;
import java.util.HashMap;
import java.util.concurrent.Callable;

import static GAPDetector.json.util.GAPUtil.revertSpecificDependencyRelation;


@CommandLine.Command(name = "reflect", mixinStandardHelpOptions = true, helpCommand = true,
        description = "Analyze Reflect dependency relation between in CD or not.")
public class Reflect implements Callable<Integer> {

    @CommandLine.Option(names = {"-c", "--cd"}, required = true, description = "cd result file path")
    public String cdFilePath;
    @CommandLine.Option(names = {"-d", "--dependencyModel"}, required = true, description = "dependency model produced by ENRE-Java")
    public String dependencyModelFilePath;

    @Override
    public Integer call() throws Exception { // your business logic goes here...
        analyzeReflect();
        return 0;
    }

    private void analyzeReflect() throws FileNotFoundException {
        CyclicDependencyDTO cdResult = JSONUtil.fromJson(new File(cdFilePath), CyclicDependencyDTO.class);
        DependencyData dependencyModel = JSONUtil.fromJson(new File(dependencyModelFilePath), DependencyData.class);
        assert cdResult != null;
        assert dependencyModel != null;
        int reflectRelationCount = 1;
        int reflectRelationInCDCount = 0;
        int reflectRelationInCDTestCount = 0;
        int reflectRelationInCDM2M = 0;
        int reflectRelationInCDM2C = 0;

        Integer count = 0;

        HashMap<Integer, HashMap<Integer, MutableInt>> srcDestVisitList = revertSpecificDependencyRelation(dependencyModel, "Reflect");
        for (CyclicDependencyStructure cyclicDependencyStructure : cdResult.getInstances()) {
            HashMap<String, MutableInt> dependencyRelationCountTotal = cyclicDependencyStructure.getDependencyRelationCountTotal();
            if (dependencyRelationCountTotal.get("Reflect") != null) {
                count++;
                for (PackageDependencyRelationCell packageDependencyRelationCell : cyclicDependencyStructure.getPackageDependencyRelationCells()) {
                    if (packageDependencyRelationCell.getDependencyRelationCountFromSrcToAllDest().get("Reflect") != null) {
                        for (MutualPackageDependencyRelation mutualPackageDependencyRelation : packageDependencyRelationCell.getMutualPackageDependencyRelations()) {
                            if (mutualPackageDependencyRelation.getDependencyRelationCount().get("Reflect") != null) {
                                for (SingleDependencyRelationDetailWithClassInfo singleDependencyRelationDetailWithClassInfo : mutualPackageDependencyRelation.getEntityDependencies()) {
                                    EntityDependencyRelationDetail entityDependencyRelationDetail = singleDependencyRelationDetailWithClassInfo.getEntityDependencyRelationDetail();
                                    if (entityDependencyRelationDetail.getRelationType().equals("Reflect")) {
                                        Integer fromEntityId = entityDependencyRelationDetail.getFromEntityId();
                                        Integer toEntityId = entityDependencyRelationDetail.getToEntityId();
                                        srcDestVisitList.get(fromEntityId).get(toEntityId).setValue(1);
                                        reflectRelationInCDCount++;
                                        if (entityDependencyRelationDetail.getFromEntity().contains("test") || entityDependencyRelationDetail.getFromEntity().contains("Test")) {
                                            reflectRelationInCDTestCount++;
                                        }
                                        if (entityDependencyRelationDetail.getFromEntityType().equals("Method") && entityDependencyRelationDetail.getToEntityType().equals("Method")) {
                                            reflectRelationInCDM2M++;
                                        }
                                        if (entityDependencyRelationDetail.getFromEntityType().equals("Method") && entityDependencyRelationDetail.getToEntityType().equals("Class")) {
                                            reflectRelationInCDM2C++;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        System.out.println(count);
        System.out.println(cdResult.getCount());


        System.out.println("Reflect dependency relation in CD: " + reflectRelationInCDCount + ", while detecting " + reflectRelationCount + " reflect dependency relation in this project.");
        System.out.println("Reflect dependency relation in CD account for " + new Formatter().format("%.2f", (float) reflectRelationInCDCount / reflectRelationCount * 100) + " % approximately.");
        System.out.println("Reflect dependency relation about test: " + reflectRelationInCDTestCount + ", while detecting " + reflectRelationInCDCount + " reflect dependency relation in CD.");
        System.out.println("Reflect dependency relation about test account for " + new Formatter().format("%.2f", (float) reflectRelationInCDTestCount / reflectRelationInCDCount * 100) + " % approximately.");
        System.out.println("Reflect dependency relation in CD M2M: " + reflectRelationInCDM2M + ", while detecting " + reflectRelationInCDCount + " reflect dependency relation in CD.");
        System.out.println("Reflect dependency relation in CD M2C: " + reflectRelationInCDM2C + ", while detecting " + reflectRelationInCDCount + " reflect dependency relation in CD.");
    }

    public static void main(String... args) {
        int exitCode = new CommandLine(new Reflect()).execute(args);
        System.exit(exitCode);
    }
}
