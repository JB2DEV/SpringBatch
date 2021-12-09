package springbatch.configuration;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.database.builder.JdbcCursorItemReaderBuilder;
import org.springframework.batch.item.json.JacksonJsonObjectMarshaller;
import org.springframework.batch.item.json.JsonFileItemWriter;
import org.springframework.batch.item.json.builder.JsonFileItemWriterBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import springbatch.listener.JobCompletionNotificationListener;
import springbatch.model.Person;
import springbatch.processor.PersonItemProcessor;

import javax.sql.DataSource;


@Configuration
@EnableBatchProcessing
public class BatchConfig {


    public static final String QUERY = "SELECT person_id,first_name,last_name,email,age FROM person";
    public static final String FILE = "persons.json";

    @Autowired
    private JobBuilderFactory jobBuilderFactory;
    @Autowired
    private StepBuilderFactory stepBuilderFactory;
    @Autowired
    private DataSource dataSource;


    @Bean
    public JdbcCursorItemReader<Person> reader() {
        return new JdbcCursorItemReaderBuilder<Person>()
                .name("personItemReader")
                .dataSource(dataSource)
                .sql(QUERY)
                .rowMapper((rs, rowNum) -> new Person(rs.getInt(1), rs.getString(2), rs.getString(3), rs.getString(4), rs.getInt(5)))
                .build();

    }

    @Bean
    public PersonItemProcessor processor() {
        return new PersonItemProcessor();
    }

    @Bean
    public JsonFileItemWriter<Person> writer() {
        return new JsonFileItemWriterBuilder<Person>()
                .name("tradeJsonFileItemWriter")
                .resource(new ClassPathResource(FILE))
                .jsonObjectMarshaller(new JacksonJsonObjectMarshaller<>())
                .build();
    }

    @Bean
    public Step step1() {
        return stepBuilderFactory.get("step1")
                .<Person, Person>chunk(100)
                .reader(reader())
                .processor(processor())
                .writer(writer())
                .build();
    }


    @Bean
    public Job exportPersonJob(JobCompletionNotificationListener listener, Step step1) {
        return jobBuilderFactory.get("exportPersonToXMLJob")
                .incrementer(new RunIdIncrementer())
                .listener(listener)
                .flow(step1)
                .end()
                .build();
    }


}
