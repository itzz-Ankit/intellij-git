package services;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
@ComponentScan(basePackages = {
        "controller",
        "config",
        "Filter",
        "entity",
        "repository",
        "services",
        "Scheduler",
        "cache",
        "Exception",
        "Mapper",
        "enums",
        "api",
        "dto_request",
        "dto_response"
})
@EnableMongoRepositories(basePackages = "repository")
@EnableScheduling
public class JournalApplication {

    public static void main(String[] args) {
        SpringApplication.run(JournalApplication.class, args);
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
