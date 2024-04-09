package GAPDetector.utils.commands;

import GAPDetector.entities.EntityIdentifier;
import GAPDetector.json.outputDTO.analyzers.MHCounter;
import GAPDetector.json.outputDTO.smells.MH.MultipathHierarchyDTO;
import GAPDetector.json.outputDTO.smells.MH.MultipathHierarchyStructure;
import GAPDetector.json.util.JSONUtil;
import org.supercsv.cellprocessor.constraint.NotNull;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.io.CsvBeanWriter;
import org.supercsv.io.ICsvBeanWriter;
import org.supercsv.prefs.CsvPreference;
import picocli.CommandLine;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.Callable;

import static GAPDetector.json.util.CSVUtil.outputCSVFile;


@CommandLine.Command(name = "mhCount", mixinStandardHelpOptions = true, helpCommand = true,
        description = "Count Multipath Hierarchy result.")
public class MHCount implements Callable<Integer> {

    @CommandLine.Parameters(description = "MH result file path")
    private String mhFilePath;

    @Override
    public Integer call() throws Exception { // your business logic goes here...
        countMH(mhFilePath);
        return 0;
    }

    public static void countMH(String input) throws IOException {
        MultipathHierarchyDTO mhResult = JSONUtil.fromJson(new File(input), MultipathHierarchyDTO.class);
        assert mhResult != null;
        HashSet<MHCounter> mhCounters = new HashSet<>();

        List<MultipathHierarchyStructure> mhList = mhResult.getInstances();
        for (MultipathHierarchyStructure mh : mhList) {
            EntityIdentifier start = mh.getStart();
            String startModifier = start.getModifier();
            if (startModifier == null) {
                startModifier = "null";
            }
            if (startModifier.equals("")) {
                startModifier = "blank";
            }
            EntityIdentifier end = mh.getEnd();
            String endModifier = end.getModifier();
            if (endModifier == null) {
                endModifier = "null";
            }
            if (endModifier.equals("")) {
                endModifier = "blank";
            }
            MHCounter mhCounter = createMHCounter(mhCounters, startModifier, endModifier);
            mhCounter.setCount(mhCounter.getCount() + 1);
        }
        final String[] header = new String[]{"startModifier", "endModifier", "Count"};
        final CellProcessor[] processors = getModifierProcessors();
        StringWriter writer = writeCSVMHContext(header, processors, mhCounters);
        File file = new File("mh-result.csv");
        outputCSVFile(writer, file);
    }

    private static MHCounter createMHCounter(HashSet<MHCounter> mhCounters, String start, String end) {
        for (MHCounter mhCounter : mhCounters) {
            if (mhCounter.getStartModifier().equals(start) && mhCounter.getEndModifier().equals(end)) {
                return mhCounter;
            }
        }
        MHCounter mhCounter = new MHCounter(start, end, 0);
        mhCounters.add(mhCounter);
        return mhCounter;
    }

    private static StringWriter writeCSVMHContext(String[] header, CellProcessor[] processors, HashSet<MHCounter> mhCounters) throws IOException {
        StringWriter writer = new StringWriter();
        ICsvBeanWriter beanWriter = new CsvBeanWriter(writer, CsvPreference.STANDARD_PREFERENCE);
        beanWriter.writeHeader(header);
        for (MHCounter mhCounter : mhCounters) {
            beanWriter.write(mhCounter, header, processors);
        }
        beanWriter.close();
        return writer;
    }


    public static void main(String... args) {
        int exitCode = new CommandLine(new Rebuild()).execute(args);
        System.exit(exitCode);
    }


    private static CellProcessor[] getModifierProcessors() {
        return new CellProcessor[]{
                new NotNull(), // start_modifier
                new NotNull(), // end_modifier
                new NotNull(), // count
        };
    }
}
