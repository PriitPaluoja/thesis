package ee.nipt.domain;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Person {

    @Id
    private String identificationCode;

    private String firstName;

    private String lastName;

    private String email;

    private String pswdHash;

    private String language = "EE";

    private String creator;

    @Enumerated(EnumType.STRING)
    @ElementCollection(fetch = FetchType.EAGER)
    private List<Role> roleList;
}