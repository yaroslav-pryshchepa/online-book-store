package book.store.dto.order;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class UpdateStatusRequestDto {
    @NotNull
    @Pattern(regexp = "PENDING|PROCESSING|COMPLETED|DELIVERED|CANCELLED")
    private String status;
}
