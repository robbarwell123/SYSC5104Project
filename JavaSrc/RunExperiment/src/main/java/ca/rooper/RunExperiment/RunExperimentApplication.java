package ca.rooper.RunExperiment;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EntityScan("ca.rooper.CommonExperiment.model")
@EnableJpaRepositories("ca.rooper.CommonExperiment.repository")
public class RunExperimentApplication {

	public static void main(String[] args) {
		SpringApplication.run(RunExperimentApplication.class, args);
	}

}
