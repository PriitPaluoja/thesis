package ee.nipt.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SettingsDTO {

    @NotNull
    private String newPasswordRepeat;

    @NotNull
    private String oldPassword;

    @NotNull
    private String newPassword;
}
