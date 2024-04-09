package GAPDetector.utils.commands;

import GAPDetector.json.outputDTO.relations.SingleDependencyRelationDetailWithClassInfo;
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
import GAPDetector.json.outputDTO.smells.SmellOutputFilter;
import GAPDetector.json.util.JSONUtil;
import GAPDetector.json.util.TXTUtil;
import GAPDetector.json.util.PathUtil;
import picocli.CommandLine;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;


@CommandLine.Command(name = "filter", mixinStandardHelpOptions = true, helpCommand = true,
        description = "Filtered by core file list")
public class FilteredByCoreFileList implements Callable<Integer> {
    @CommandLine.Option(names = {"-n", "--name"}, required = true, description = "project name")
    private String projectName;
    @CommandLine.Option(names = {"-s", "--sourcePath"}, required = true, description = "source path")
    private String sourcePath = null;
    @CommandLine.Option(names = {"-cfl", "--coreFileListFilePath"}, defaultValue = "coreFileList.txt", description = "coreFileListFilePath")
    private String coreFileListFilePath;

    private HashMap<String, ArrayList<AbstractionWithoutDecouplingStructure>> filteredAwdResult = new HashMap<>();
    private HashMap<String, HashMap<String, List<SingleDependencyRelationDetailWithClassInfo>>> filteredCdResult = new HashMap<>();
    private HashMap<String, ArrayList<CyclicHierarchyStructure>> filteredChResult = new HashMap<>();
    private HashMap<String, ArrayList<MultipathHierarchyStructure>> filteredMhResult = new HashMap<>();


    @Override
    public Integer call() throws Exception { // your business logic goes here...
        filter();

//        HashMap<String,Object> file
        return 0;
    }

    public void filter() throws IOException {
        HashMap<String, ArrayList<String>> coreFileMap = TXTUtil.readCoreFile(coreFileListFilePath);
        System.out.println(coreFileMap);

        String MHResult = sourcePath + "/" + projectName + "-MH.json";
        String CDResult = sourcePath + "/" + projectName + "-CD.json";
        String CHResult = sourcePath + "/" + projectName + "-CH.json";
        String AWDResult = sourcePath + "/" + projectName + "-AWD.json";

        ArrayList<String> coreFileList = new ArrayList<>();
        for (Map.Entry<String, ArrayList<String>> entry : coreFileMap.entrySet()) {
            coreFileList.addAll(entry.getValue());
        }

        AbstractionWithoutDecouplingDTO awdResult = JSONUtil.fromJson(new File(AWDResult), AbstractionWithoutDecouplingDTO.class);
        for (AbstractionWithoutDecouplingStructure abstractionWithoutDecouplingStructure : awdResult.getInstances()) {
            String file = abstractionWithoutDecouplingStructure.getSuperType().getFile();

            if (coreFileList.contains(file)) {
                filter(abstractionWithoutDecouplingStructure, "SuperType", file);
            }
            if (coreFileList.contains(abstractionWithoutDecouplingStructure.getSubType().getFile())) {
                filter(abstractionWithoutDecouplingStructure, "SubType", file);
            }
            if (coreFileList.contains(abstractionWithoutDecouplingStructure.getClientClass().getFile())) {
                filter(abstractionWithoutDecouplingStructure, "ClientType", file);
            }
        }

        CyclicDependencyDTO cdResult = JSONUtil.fromJson(new File(CDResult), CyclicDependencyDTO.class);
        for (CyclicDependencyStructure cyclicDependencyStructure : cdResult.getInstances()) {
            for (PackageDependencyRelationCell packageDependencyRelationCell : cyclicDependencyStructure.getPackageDependencyRelationCells()) {
                for (MutualPackageDependencyRelation mutualPackageDependencyRelation : packageDependencyRelationCell.getMutualPackageDependencyRelations()) {

                    for (SingleDependencyRelationDetailWithClassInfo singleDependencyRelationDetailWithClassInfo : mutualPackageDependencyRelation.getEntityDependencies()) {
                        if (coreFileList.contains(singleDependencyRelationDetailWithClassInfo.getFromClass().getFile())) {
                            filterCD(singleDependencyRelationDetailWithClassInfo, "source", singleDependencyRelationDetailWithClassInfo.getFromClass().getFile());
                        }
                        if (coreFileList.contains(singleDependencyRelationDetailWithClassInfo.getToClass().getFile())) {
                            filterCD(singleDependencyRelationDetailWithClassInfo, "target", singleDependencyRelationDetailWithClassInfo.getToClass().getFile());
                        }
                    }

                }

            }
        }

        CyclicHierarchyDTO chResult = JSONUtil.fromJson(new File(CHResult), CyclicHierarchyDTO.class);
        for (CyclicHierarchyStructure cyclicHierarchyStructure : chResult.getInstances()) {
            String file = cyclicHierarchyStructure.getSuperType().getFile();

            if (coreFileList.contains(file)) {
                filterCH(cyclicHierarchyStructure, "SuperType", file);
            }
            if (coreFileList.contains(file)) {
                filterCH(cyclicHierarchyStructure, "SubType", file);
            }
        }

        MultipathHierarchyDTO mhResult = JSONUtil.fromJson(new File(MHResult), MultipathHierarchyDTO.class);
        for (MultipathHierarchyStructure multipathHierarchyStructure : mhResult.getInstances()) {
            String file = multipathHierarchyStructure.getStart().getFile();

            if (coreFileList.contains(file)) {
                filterMH(multipathHierarchyStructure, "start", file);
            }
            if (coreFileList.contains(file)) {
                filterMH(multipathHierarchyStructure, "end", file);
            }
        }

        String countFileName = projectName + "-coreFile-count.csv";
        File file = new File(countFileName);
        if (file.exists()) { // 如果已存在,删除旧文件
            file.delete();
        }
        file.createNewFile();
        JSONUtil.writeCSVStringToFile("module,coreFileName,AWD,CD,CH,MH\n", file);


        for (Map.Entry<String, ArrayList<String>> entry : coreFileMap.entrySet()) {
            for (String coreFile : entry.getValue()) {
                SmellOutputFilter smellOutputFilter = new SmellOutputFilter(coreFile);
                int AWDcount = 0;
                int CDcount = 0;
                int CHcount = 0;
                int MHcount = 0;
                if (filteredAwdResult.get(coreFile) == null && filteredCdResult.get(coreFile) == null && filteredChResult.get(coreFile) == null && filteredCdResult.get(coreFile) == null) {
                    continue;
                }
                JSONUtil.writeCSVStringToFile(coreFile + ",", new File("count.csv"));
                if (filteredAwdResult.get(coreFile) != null) {
                    smellOutputFilter.setAbstractionWithoutDecoupling(filteredAwdResult.get(coreFile));
                    smellOutputFilter.setAbstractionWithoutDecouplingCount(filteredAwdResult.get(coreFile).size());
                    AWDcount = filteredAwdResult.get(coreFile).size();
                }
                if (filteredCdResult.get(coreFile) != null) {
                    smellOutputFilter.setCyclicDependency(filteredCdResult.get(coreFile));
                    smellOutputFilter.setCyclicDependencyCount(filteredCdResult.get(coreFile).size());
                    CDcount = filteredCdResult.get(coreFile).size();

                }
                if (filteredChResult.get(coreFile) != null) {
                    smellOutputFilter.setCyclicHierarchy(filteredChResult.get(coreFile));
                    smellOutputFilter.setCyclicHierarchyCount(filteredChResult.get(coreFile).size());
                    CHcount = filteredChResult.get(coreFile).size();

                }
                if (filteredMhResult.get(coreFile) != null) {
                    smellOutputFilter.setMultipathHierarchy(filteredMhResult.get(coreFile));
                    smellOutputFilter.setMultipathHierarchyCount(filteredMhResult.get(coreFile).size());
                    MHcount = filteredMhResult.get(coreFile).size();
                }
                JSONUtil.toJson(smellOutputFilter, "./filterByCoreFile", PathUtil.getLastStrByPathDelimiter(coreFile).split("\\.")[0]);
                JSONUtil.writeCSVStringToFile(entry.getKey() +"," + PathUtil.getLastStrByPathDelimiter(coreFile).split("\\.")[0] + "," + AWDcount + "," + CDcount + "," + CHcount + "," + +MHcount + "\n", file);


            }
        }
    }


    private void filter(AbstractionWithoutDecouplingStructure abstractionWithoutDecouplingStructure, String mode, String file) {
        abstractionWithoutDecouplingStructure.setMode(mode);
        filteredAwdResult.computeIfAbsent(file, k -> new ArrayList<>());
        filteredAwdResult.get(file).add(abstractionWithoutDecouplingStructure);
    }

    private void filterCD(SingleDependencyRelationDetailWithClassInfo singleDependencyRelationDetailWithClassInfo, String mode, String file) {
        filteredCdResult.computeIfAbsent(file, k -> new HashMap<>());
        filteredCdResult.get(file).computeIfAbsent(mode, k -> new ArrayList<>());
        filteredCdResult.get(file).get(mode).add(singleDependencyRelationDetailWithClassInfo);
    }

    private void filterCH(CyclicHierarchyStructure cyclicHierarchyStructure, String mode, String file) {
        cyclicHierarchyStructure.setMode(mode);
        filteredChResult.computeIfAbsent(file, k -> new ArrayList<>());
        filteredChResult.get(file).add(cyclicHierarchyStructure);
    }

    private void filterMH(MultipathHierarchyStructure multipathHierarchyStructure, String mode, String file) {
        multipathHierarchyStructure.setMode(mode);
        filteredMhResult.computeIfAbsent(file, k -> new ArrayList<>());
        filteredMhResult.get(file).add(multipathHierarchyStructure);
    }

}
