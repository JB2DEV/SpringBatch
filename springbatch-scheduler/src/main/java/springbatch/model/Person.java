package springbatch.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Person {
    private Integer personId;
    private String firstName;
    private String lastName;
    private String email;
    private Integer age;

}