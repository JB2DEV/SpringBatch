package springbatch.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.xml.bind.annotation.*;

@Data @AllArgsConstructor@NoArgsConstructor
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType( propOrder = { "personId", "firstName", "lastName", "email", "age" })
@XmlRootElement(name = "Person")
public class PersonXML {

    @XmlElement(name = "ID", required = true)
    private Integer personId;
    @XmlElement(name = "FirstName", required = true)
    private String firstName;
    @XmlElement(name = "LastName", required = true)
    private String lastName;
    @XmlElement(name = "Email", required = true)
    private String email;
    @XmlElement(name = "AGE", required = true)
    private Integer age;

}
