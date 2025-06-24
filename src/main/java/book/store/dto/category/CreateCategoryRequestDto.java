package book.store.dto.category;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class CreateCategoryRequestDto {
    @NotEmpty
    private String name;
    private String description;
}
