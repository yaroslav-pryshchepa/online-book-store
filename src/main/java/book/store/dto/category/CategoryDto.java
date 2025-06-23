package book.store.dto.category;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CategoryDto {
    private Long id;
    @NotBlank
    private String name;
    private String description;
}
