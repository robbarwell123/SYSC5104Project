package ca.rooper.ViewExperiment;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EntityScan("ca.rooper.CommonExperiment.model")
@EnableJpaRepositories("ca.rooper.CommonExperiment.repository")
public class ViewExperimentApplication
{
	public static void main(String[] args)
	{
		SpringApplication.run(ViewExperimentApplication.class, args);
	}
}
