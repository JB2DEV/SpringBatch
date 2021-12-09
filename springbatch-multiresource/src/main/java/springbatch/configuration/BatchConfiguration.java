package springbatch.configuration;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.MultiResourceItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.builder.FlatFileItemWriterBuilder;
import org.springframework.batch.item.file.builder.MultiResourceItemReaderBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.BeanWrapperFieldExtractor;
import org.springframework.batch.item.file.transform.DelimitedLineAggregator;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import springbatch.model.Person;
import springbatch.processor.PersonItemProcessor;


@Configuration
@EnableBatchProcessing
public class BatchConfiguration {

    @Autowired
    public JobBuilderFactory jobBuilderFactory;

    @Autowired
    public StepBuilderFactory stepBuilderFactory;

    @Value(value = "input/persons_*.csv")
    private Resource[] resources;

    @Bean
    public FlatFileItemReader<Person> reader() {

        return new FlatFileItemReaderBuilder<Person>()
                .name("personItemReader")
                .lineTokenizer(new DelimitedLineTokenizer() {
                    {
                        setNames("firstName", "lastName", "email", "age");
                    }
                })
                .fieldSetMapper(new BeanWrapperFieldSetMapper<Person>() {{
                    setTargetType(Person.class);
                }})

                .build();
    }

    @Bean
    public MultiResourceItemReader<Person> multiResourceItemReader() {
        return new MultiResourceItemReaderBuilder<Person>()
                .name("multiResourcesReader")
                .resources(resources)
                .delegate(reader())
                .build();
    }

    @Bean
    public PersonItemProcessor processor() {
        return new PersonItemProcessor();
    }

    @Bean
    public FlatFileItemWriter<Person> writer() {

        return new FlatFileItemWriterBuilder<Person>()
                .name("itemWriter")
                .resource(new ClassPathResource("output/persons_output.csv"))
                .delimited().delimiter(",")
                .names(new String[]{"firstName", "lastName", "email", "age"})
                .build();
    }

    @Bean
    public Job importMultiResourceToCSV() {
        return jobBuilderFactory.get("importMultiResourceToCSV")
                .incrementer(new RunIdIncrementer())
                .flow(step1())
                .end()
                .build();
    }

    @Bean
    public Step step1() {
        return stepBuilderFactory.get("step1")
                .<Person, Person>chunk(10)
                .reader(multiResourceItemReader())
                .processor(processor())
                .writer(writer())
                .build();
    }
}