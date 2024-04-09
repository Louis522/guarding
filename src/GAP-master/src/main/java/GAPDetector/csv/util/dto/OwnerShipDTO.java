package GAPDetector.csv.util.dto;

import com.opencsv.bean.CsvBindByName;
import lombok.Data;

@Data
public class OwnerShipDTO {
    @CsvBindByName(column = "id")
    private Integer id;
    @CsvBindByName(column = "not_aosp")
    private Integer not_aosp;
    @CsvBindByName(column = "old_aosp")
    private Integer old_aosp;
    @CsvBindByName(column = "isIntrusive")
    private Integer isIntrusive;
    @CsvBindByName(column = "category")
    private String category;
    @CsvBindByName(column = "qualifiedName")
    private String qualifiedName;
    @CsvBindByName(column = "file_path")
    private String file_path;
    @CsvBindByName(column = "mapping")
    private int mapping;
}