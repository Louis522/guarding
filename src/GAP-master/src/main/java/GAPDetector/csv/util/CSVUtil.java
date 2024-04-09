package GAPDetector.csv.util;

import GAPDetector.csv.util.dto.AllEntitiesDTO;
import GAPDetector.csv.util.dto.MeasureResultClassDTO;
import GAPDetector.csv.util.dto.MeasureResultDTO;
import GAPDetector.csv.util.dto.OwnerShipDTO;
import com.opencsv.CSVReader;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import org.supercsv.cellprocessor.Optional;
import org.supercsv.cellprocessor.ParseDouble;
import org.supercsv.cellprocessor.ParseInt;
import org.supercsv.cellprocessor.constraint.NotNull;
import org.supercsv.cellprocessor.ift.CellProcessor;

import java.io.FileReader;
import java.util.List;

public class CSVUtil {

    public static <T> List<T> readCSV(String filePath, Class<T> beanClass) throws Exception {
        try (CSVReader reader = new CSVReader(new FileReader(filePath))) {
            CsvToBean<T> csvToBean = new CsvToBeanBuilder<T>(reader)
                    .withType(beanClass)
                    .withIgnoreLeadingWhiteSpace(true)
                    .build();
            return csvToBean.parse();
        }
    }


    private static <T> CellProcessor[] getCellProcessors(Class<T> beanClass) {
        // 根据beanClass类型创建不同的CellProcessor对象
        if (beanClass == OwnerShipDTO.class) {
            return new CellProcessor[]{
                    new NotNull(new ParseInt()), // id (must be an integer),
                    new NotNull(new ParseInt()), // not_aosp (must be an integer),
                    new NotNull(new ParseInt()), // old_aosp (must be an integer),
                    new NotNull(new ParseInt()), // isIntrusive (must be an integer),
                    new NotNull(), // category (must be an integer),
                    new NotNull(), // qualifiedName (must be an integer),
                    new Optional(), // file_path (must be an integer),
                    new NotNull(new ParseInt()), // mapping (must be an integer),
            };
        } else if (beanClass == MeasureResultClassDTO.class) {
            return new CellProcessor[]{
                    new Optional(),
                    new Optional(new ParseDouble()),
                    new Optional(),
            };
        } else if (beanClass == MeasureResultDTO.class) {
            return new CellProcessor[]{
                    new Optional(),
                    new Optional(),
                    new Optional(),
                    new Optional(new ParseDouble()),
                    new Optional(new ParseInt()),
            };
        } else if (beanClass == AllEntitiesDTO.class) {


            return new CellProcessor[]{
                    new Optional(),
                    new Optional(),
                    new Optional(),
                    new Optional(),
                    new Optional(),
                    new Optional(),
                    new Optional(),
                    new Optional(),
                    new Optional(),
            };
        } else {
            throw new IllegalArgumentException("Unsupported bean class: " + beanClass.getName());
        }


    }

    public static void main(String[] args) throws Exception {
        String filePath = "C:\\Users\\admin\\Desktop\\all_entities.csv";
        List<AllEntitiesDTO> allEntitiesDTOS = CSVUtil.readCSV(filePath, AllEntitiesDTO.class);
        System.out.println();
    }
}




