package GAPDetector.json.outputDTO.analyzers;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

@AllArgsConstructor
@Data
public class FileCounter {
    private String file;
    private Integer superTypeFileCount;
    private Integer subTypeFileCount;
    private Integer clientTypeFileCount;

    public FileCounter(String file) {
        this.file = file;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        FileCounter other = (FileCounter) o;
        EqualsBuilder builder = new EqualsBuilder();
        builder.append(file, other.file);
        return builder.isEquals();
    }

    @Override
    public int hashCode() {
        HashCodeBuilder builder = new HashCodeBuilder(17, 37);
        builder.append(file);
        return builder.toHashCode();
    }


}
