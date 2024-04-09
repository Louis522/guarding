package GAPDetector.csv.util.dto;

import com.opencsv.bean.CsvBindByName;
import lombok.Data;
@Data
public class AllEntitiesDTO {
    @CsvBindByName(column = "Entity")
    private String entity;
    @CsvBindByName(column = "category")
    private String category;
    @CsvBindByName(column = "id")
    private Integer id;
    @CsvBindByName(column = "param_names")
    private String param_names;
    @CsvBindByName(column = "file path")
    private String file_path;
    @CsvBindByName(column = "commits")
    private String commits;
    @CsvBindByName(column = "base commits")
    private String base_commits;
    @CsvBindByName(column = "old base commits")
    private String old_base_commits;
    @CsvBindByName(column = "accompany commits")
    private String accompany_commits;
}