package springbatch.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.xml.bind.annotation.*;

@Data
@NoArgsConstructor
@AllArgsConstructor

@XmlRootElement(name = "person")
public class Person {

    private Integer personId;
    private String firstName;
    private String lastName;
    private String email;
    private Integer age;


}
