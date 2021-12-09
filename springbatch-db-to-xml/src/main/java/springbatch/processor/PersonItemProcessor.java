package springbatch.processor;

import springbatch.model.Person;
import org.springframework.batch.item.ItemProcessor;
import springbatch.model.PersonXML;

public class PersonItemProcessor implements ItemProcessor<Person, PersonXML> {

    @Override
    public PersonXML process(Person person) throws Exception {
        return new PersonXML(person.getPersonId(), person.getFirstName(), person.getLastName(), person.getEmail(), person.getAge());
    }
}
