package book.store.dto.user;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UserLoginResponseDto {
    @NotBlank
    private String token;

    public UserLoginResponseDto(String token) {
        this.token = token;
    }
}
