package GAPDetector.json.util;

import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.io.CsvBeanWriter;
import org.supercsv.io.ICsvBeanWriter;
import org.supercsv.prefs.CsvPreference;

import java.io.*;

public class CSVUtil {
    public static StringWriter writeCSVAnalyzerContext(String[] header, CellProcessor[] processors, Object object) throws IOException {
        StringWriter writer = new StringWriter();
        ICsvBeanWriter beanWriter = new CsvBeanWriter(writer, CsvPreference.STANDARD_PREFERENCE);
        beanWriter.writeHeader(header);
        beanWriter.write(object, header, processors);
        beanWriter.close();
        return writer;
    }

    public static void outputCSVFile(StringWriter writer, File file) {
        try {
            //如果文件不存在则创建
            if (!file.exists()) {
                file.createNewFile();
            }
            FileWriter fileWritter = new FileWriter(file);
            BufferedWriter bufferWritter = new BufferedWriter(fileWritter);
            bufferWritter.write(writer.toString());
            bufferWritter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
