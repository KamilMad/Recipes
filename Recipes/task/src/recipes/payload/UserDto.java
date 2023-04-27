package recipes.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.validation.constraints.*;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserDto {

    @Email
    @NotEmpty
    @NotNull
    private String email;

    @Column
    @NotBlank
    @Size(min = 8)
    private String password;
}
