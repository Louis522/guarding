package GAPDetector.utils.commands;

import GAPDetector.json.outputDTO.analyzers.FileCounter;
import GAPDetector.json.outputDTO.analyzers.MHCounter;
import org.supercsv.cellprocessor.Optional;
import org.supercsv.cellprocessor.constraint.NotNull;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.io.CsvBeanWriter;
import org.supercsv.io.ICsvBeanWriter;
import org.supercsv.prefs.CsvPreference;

import java.io.IOException;
import java.io.StringWriter;
import java.util.HashSet;

public class MethodUtils {
    private static FileCounter createFileCounter(HashSet<FileCounter> fileCounters, String file) {
        for (FileCounter fileCounter : fileCounters) {
            if (fileCounter.getFile().equals(file)) {
                return fileCounter;
            }

        }
        FileCounter fileCounter = new FileCounter(file, 0, 0, 0);
        fileCounters.add(fileCounter);
        return fileCounter;
    }

    private static CellProcessor[] getEntityIdentifierProcessors() {
        return new CellProcessor[]{
                new NotNull(), // object
                new NotNull(), // file
                new Optional(), // superTypeCount
                new Optional(), // subTypeCount
                new Optional(), // clientCount
        };
    }

    private static CellProcessor[] getFileCounterProcessors() {
        return new CellProcessor[]{
                new NotNull(), // file
                new Optional(), // superTypeFileCount
                new Optional(), // subTypeFileCount
                new Optional(), // clientFileCount
        };
    }

    private static CellProcessor[] getModifierProcessors() {
        return new CellProcessor[]{
                new NotNull(), // start_modifier
                new NotNull(), // end_modifier
                new NotNull(), // count
        };
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

}
