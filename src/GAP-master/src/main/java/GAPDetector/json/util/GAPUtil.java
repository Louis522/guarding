package GAPDetector.json.util;

import GAPDetector.json.inputDTO.dependencyModel.Cells;
import GAPDetector.json.inputDTO.dependencyModel.DependencyData;
import GAPDetector.json.inputDTO.dependencyModel.Values;
import GAPDetector.json.outputDTO.analyzers.Analyzer;
import GAPDetector.json.outputDTO.relations.SingleDependencyRelationDetailWithClassInfo;
import GAPDetector.json.outputDTO.relations.EntityDependencyRelationDetail;
import GAPDetector.json.outputDTO.smells.CD.CyclicDependencyDTO;
import GAPDetector.json.outputDTO.smells.CD.CyclicDependencyStructure;
import GAPDetector.json.outputDTO.smells.CD.MutualPackageDependencyRelation;
import GAPDetector.json.outputDTO.smells.CD.PackageDependencyRelationCell;
import GAPDetector.json.util.JSONUtil;
import org.apache.commons.lang3.mutable.MutableInt;
import org.supercsv.cellprocessor.constraint.NotNull;
import org.supercsv.cellprocessor.ift.CellProcessor;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.List;

import static GAPDetector.json.util.CSVUtil.writeCSVAnalyzerContext;

public class GAPUtil {

    public static void analyzeReflect(String cdFilePath, String dependencyModelFilePath, String outputFolder, String projectName) throws IOException {
        CyclicDependencyDTO cdResult = JSONUtil.fromJson(new File(cdFilePath), CyclicDependencyDTO.class);
        DependencyData dependencyModel = JSONUtil.fromJson(new File(dependencyModelFilePath), DependencyData.class);
        assert cdResult != null;
        assert dependencyModel != null;
//        Integer reflectRelationCount = dependencyModel.getRelationNum().getReflect();
        Integer reflectRelationCount = 1;

        Integer reflectRelationInCDCount = 0;
        Integer reflectRelationInCDTestCount = 0;
        Integer reflectRelationInCDM2M = 0;
        Integer reflectRelationInCDM2C = 0;
        Integer cdResultWithReflect = 0;
        HashMap<Integer, HashMap<Integer, MutableInt>> srcDestVisitList = revertSpecificDependencyRelation(dependencyModel, "Reflect");
        for (CyclicDependencyStructure cyclicDependencyStructure : cdResult.getInstances()) {
            HashMap<String, MutableInt> dependencyRelationCountTotal = cyclicDependencyStructure.getDependencyRelationCountTotal();
            if (dependencyRelationCountTotal.get("Reflect") != null) {
                cdResultWithReflect++;
                for (PackageDependencyRelationCell packageDependencyRelationCell : cyclicDependencyStructure.getPackageDependencyRelationCells()) {
                    if (packageDependencyRelationCell.getDependencyRelationCountFromSrcToAllDest().get("Reflect") != null) {
                        for (MutualPackageDependencyRelation mutualPackageDependencyRelation : packageDependencyRelationCell.getMutualPackageDependencyRelations()) {
                            if (mutualPackageDependencyRelation.getDependencyRelationCount().get("Reflect") != null) {
                                for (SingleDependencyRelationDetailWithClassInfo singleDependencyRelationDetailWithClassInfo : mutualPackageDependencyRelation.getEntityDependencies()) {
                                    if (singleDependencyRelationDetailWithClassInfo.getEntityDependencyRelationDetail().getRelationType().equals("Reflect")) {
                                        EntityDependencyRelationDetail singleEntityDependencyRelationDetail = singleDependencyRelationDetailWithClassInfo.getEntityDependencyRelationDetail();
                                        Integer fromEntityId = singleEntityDependencyRelationDetail.getFromEntityId();
                                        Integer toEntityId = singleEntityDependencyRelationDetail.getToEntityId();
                                        srcDestVisitList.get(fromEntityId).get(toEntityId).setValue(1);
                                        reflectRelationInCDCount++;
                                        if (singleEntityDependencyRelationDetail.getFromEntity().contains("test") || singleEntityDependencyRelationDetail.getFromEntity().contains("Test")) {
                                            reflectRelationInCDTestCount++;
                                        }
                                        if (singleEntityDependencyRelationDetail.getFromEntityType().equals("Method") && singleEntityDependencyRelationDetail.getToEntityType().equals("Method")) {
                                            reflectRelationInCDM2M++;
                                        }
                                        if (singleEntityDependencyRelationDetail.getFromEntityType().equals("Method") && singleEntityDependencyRelationDetail.getToEntityType().equals("Class")) {
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
        Analyzer analyzer = new Analyzer(projectName,
                cdResult.getCount(),
                cdResultWithReflect,
                String.format("%.2f", cdResultWithReflect.floatValue() / cdResult.getCount() * 100),
                reflectRelationCount,
                reflectRelationInCDCount,
                String.format("%.2f", reflectRelationInCDCount.floatValue() / reflectRelationCount * 100),
                reflectRelationInCDTestCount,
                String.format("%.2f", reflectRelationInCDTestCount.floatValue() / reflectRelationInCDCount * 100)
        );
        final String[] header = new String[]{"Project", "CDInSystem", "CDWithReflectInSystem", "CDWithReflectInSystemPercent", "ReflectCount", "ReflectInCD", "ReflectInCDPercent", "ReflectInCDRelatedToTEST", "ReflectInCDRelatedToTESTPercent"};
        final CellProcessor[] processors = new CellProcessor[]{new NotNull(), new NotNull(), new NotNull(), new NotNull(), new NotNull(), new NotNull(), new NotNull(), new NotNull(), new NotNull()};
        StringWriter writer = writeCSVAnalyzerContext(header, processors, analyzer);
        JSONUtil.outputCSVFile(writer, outputFolder, projectName + "-" + "CD-Reflect-Analyzer");
    }

    public static HashMap<Integer, HashMap<Integer, MutableInt>> revertSpecificDependencyRelation(DependencyData dependencyData, String dependencyRelation) {
        List<Cells> cellsArray = dependencyData.getCells();
        HashMap<Integer, HashMap<Integer, MutableInt>> srcDestVisitList = new HashMap<>();
        for (Cells cellsDTO : cellsArray) {
            Integer from = cellsDTO.getSrc();
            Integer to = cellsDTO.getDest();
            Values values = cellsDTO.getValues();
            if (values.getReflect() != null) {
                srcDestVisitList.computeIfAbsent(from, k -> new HashMap<>());
                srcDestVisitList.get(from).computeIfAbsent(to, k -> new MutableInt(0));
            }
        }
        return srcDestVisitList;
    }


}
