package springbatch.configuration;

import springbatch.listener.JobCompletionNotificationListener;
import springbatch.model.Person;
import springbatch.processor.PersonItemProcessor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.database.builder.JdbcCursorItemReaderBuilder;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.builder.FlatFileItemWriterBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import javax.sql.DataSource;

@Configuration
@EnableBatchProcessing
public class BatchConfig {


    public static final String QUERY1 = "SELECT person_id,first_name,last_name,email,age FROM person";
    public static final String FILE1 = "persons.csv";

    @Autowired
    private JobBuilderFactory jobBuilderFactory;
    @Autowired
    private StepBuilderFactory stepBuilderFactory;
    @Autowired
    private DataSource dataSource;


    @Bean
    public JdbcCursorItemReader<Person> reader(){
        return new JdbcCursorItemReaderBuilder<Person>()
                .name("personItemReader")
                .dataSource(dataSource)
                .sql(QUERY1)
                .rowMapper((rs, rowNum) -> new Person(rs.getInt(1),rs.getString(2), rs.getString(3),rs.getString(4),rs.getInt(5) ))
                .build();

    }

    @Bean
    public PersonItemProcessor processor(){
        return  new PersonItemProcessor();
    }

    @Bean
    public FlatFileItemWriter<Person> writer(){
        return new FlatFileItemWriterBuilder<Person>()
                .name("personItemWriter")
                .resource(new ClassPathResource(FILE1))
                .delimited().delimiter(",")
                .names(new String[]{"personId","firstName", "lastName", "email", "age" })
                .build();
    }

    @Bean
    public Step step1(){
        return stepBuilderFactory.get("step1")
                .<Person,Person>chunk(100)
                .reader(reader())
                .processor(processor())
                .writer(writer())
                .build();
    }


    @Bean
    public Job exportPersonJob(JobCompletionNotificationListener listener, Step step1){
        return jobBuilderFactory.get("exportPersonToCSVJob")
                .incrementer(new RunIdIncrementer())
                .listener(listener)
                .flow(step1)
                .end()
                .build();
    }


}
