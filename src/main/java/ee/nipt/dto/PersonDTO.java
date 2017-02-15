package ee.nipt.dto;

import ee.nipt.constraint.NationalIdentificationNumber;
import ee.nipt.domain.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Email;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PersonDTO {

    @NationalIdentificationNumber
    private String identificationCode;

    @NotNull
    @Size(max = 50)
    private String firstName;

    @NotNull
    @Size(max = 50)
    private String lastName;

    @NotNull
    @Email
    private String email;

    private Boolean canViewReport = false;

    private Boolean canCreateReport = false;

    private Boolean canCreateExamination = false;

    private Boolean canAddUsers = false;

    public List<Role> getRoleList() {
        List<Role> roles = new ArrayList<>();

        if (canViewReport)
            roles.add(Role.VIEW_REPORT);

        if (canCreateReport)
            roles.add(Role.CREATE_REPORT);

        if (canCreateExamination)
            roles.add(Role.CREATE_EXAMINATION);

        if (canAddUsers)
            roles.add(Role.ADD_USERS);

        return roles;
    }
}