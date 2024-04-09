package GAPDetector.utils.commands;

import GAPDetector.json.ArchitectureSmellCompareDTO.*;
import GAPDetector.json.outputDTO.smells.AwD.AbstractionWithoutDecouplingDTO;
import GAPDetector.json.outputDTO.smells.CD.CyclicDependencyDTO;
import GAPDetector.json.outputDTO.smells.CH.CyclicHierarchyDTO;
import GAPDetector.json.outputDTO.smells.MH.MultipathHierarchyDTO;
import GAPDetector.json.util.JSONUtil;
import picocli.CommandLine;

import java.io.File;
import java.util.concurrent.Callable;

@CommandLine.Command(name = "compare", mixinStandardHelpOptions = true, helpCommand = true,
        version = "compare 1.0",
        description = "Compare the general anti-patterns between 2 results.")
public class Compare implements Callable<Integer> {

    @CommandLine.Option(names = {"-a1", "--awdFile1"}, description = "AWD result file[before] to compare")
    private String awdResult1 = null;
    @CommandLine.Option(names = {"-a2", "--awdFile2"}, description = "AWD result file[after] to compare")
    private String awdResult2 = null;
    @CommandLine.Option(names = {"-cd1", "--cdFile1"}, description = "CD result file[before] to compare")
    private String cdResult1 = null;
    @CommandLine.Option(names = {"-cd2", "--cdFile2"}, description = "CD result file[after] to compare")
    private String cdResult2 = null;
    @CommandLine.Option(names = {"-ch1", "--chFile1"}, description = "CH result file[before] to compare")
    private String chResult1 = null;
    @CommandLine.Option(names = {"-ch2", "--chFile2"}, description = "CH result file[after] to compare")
    private String chResult2 = null;
    @CommandLine.Option(names = {"-m1", "--mhFile1"}, description = "MH result file[before] to compare")
    private String mhResult1 = null;
    @CommandLine.Option(names = {"-m2", "--mhFile2"}, description = "MH result file[after] to compare")
    private String mhResult2 = null;
    @CommandLine.Option(names = {"-cn", "--name"}, description = "compare project name")
    private String compare_name = null;


    @Override
    public Integer call() throws Exception {
        String[] input = {
                awdResult1, awdResult2, cdResult1, cdResult2, chResult1, chResult2, mhResult1, mhResult2, compare_name
        };

        String name = input[8];
        AbstractionWithoutDecouplingCompareDTO abstractionWithoutDecouplingCompareDTO = null;
        CyclicDependencyCompareDTO cyclicDependencyCompareDTO = null;
        CyclicHierarchyCompareDTO cyclicHierarchyCompareDTO = null;
        MultipathHierarchyCompareDTO multipathHierarchyCompareDTO = null;


        if (input[0] != null && input[1] != null) {
            AbstractionWithoutDecouplingDTO awdResult1 = JSONUtil.fromJson(new File(input[0]), AbstractionWithoutDecouplingDTO.class);
            AbstractionWithoutDecouplingDTO awdResult2 = JSONUtil.fromJson(new File(input[1]), AbstractionWithoutDecouplingDTO.class);
            assert awdResult1 != null;
            assert awdResult2 != null;
            abstractionWithoutDecouplingCompareDTO = CompareComputer.compare(awdResult1, awdResult2);
        }

        if (input[2] != null && input[3] != null) {

            CyclicDependencyDTO cdResult1 = JSONUtil.fromJson(new File(input[2]), CyclicDependencyDTO.class);
            CyclicDependencyDTO cdResult2 = JSONUtil.fromJson(new File(input[3]), CyclicDependencyDTO.class);
            assert cdResult1 != null;
            assert cdResult2 != null;
            cyclicDependencyCompareDTO = CompareComputer.compare(cdResult1, cdResult2);
        }

        if (input[4] != null && input[5] != null) {

            CyclicHierarchyDTO chResult1 = JSONUtil.fromJson(new File(input[4]), CyclicHierarchyDTO.class);
            CyclicHierarchyDTO chResult2 = JSONUtil.fromJson(new File(input[5]), CyclicHierarchyDTO.class);
            assert chResult1 != null;
            assert chResult2 != null;
            cyclicHierarchyCompareDTO = CompareComputer.compare(chResult1, chResult2);

        }

        if (input[6] != null && input[7] != null) {

            MultipathHierarchyDTO mhResult1 = JSONUtil.fromJson(new File(input[6]), MultipathHierarchyDTO.class);
            MultipathHierarchyDTO mhResult2 = JSONUtil.fromJson(new File(input[7]), MultipathHierarchyDTO.class);
            assert mhResult1 != null;
            assert mhResult2 != null;
            multipathHierarchyCompareDTO = CompareComputer.compare(mhResult1, mhResult2);
        }

//        AbstractionWithoutDecouplingCompareDTO abstractionWithoutDecouplingCompareDTO2 = CompareComputer.compare2(awdResult1, awdResult2);

        if (abstractionWithoutDecouplingCompareDTO != null) {
            JSONUtil.toJson(abstractionWithoutDecouplingCompareDTO, "./", name + "-AWD-Compare");
        }
        if (cyclicDependencyCompareDTO != null) {
            JSONUtil.toJson(cyclicDependencyCompareDTO, "./", name + "-CD-Compare");
        }
        if (cyclicHierarchyCompareDTO != null) {
            JSONUtil.toJson(cyclicHierarchyCompareDTO, "./", name + "-CH-Compare");
        }
        if (multipathHierarchyCompareDTO != null) {
            JSONUtil.toJson(multipathHierarchyCompareDTO, "./", name + "-MH-Compare");
        }

        return 0;
    }

    public static void main(String... args) {
        int exitCode = new CommandLine(new Compare()).execute(args);
        System.exit(exitCode);
    }
}
