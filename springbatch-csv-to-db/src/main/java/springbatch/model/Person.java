package springbatch.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data @NoArgsConstructor @AllArgsConstructor
public class Person implements Serializable {
    private static final long serialVersionUID = -6402068923614583448L;
    private String firstName;
    private String lastName;
    private String email;
    private Integer age;

}
