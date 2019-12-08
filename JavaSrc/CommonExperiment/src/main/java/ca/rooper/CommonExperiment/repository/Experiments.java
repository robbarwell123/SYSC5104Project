package ca.rooper.CommonExperiment.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ca.rooper.CommonExperiment.model.Experiment;

@Repository
public interface Experiments extends JpaRepository<Experiment, Integer>
{
	public List<Experiment> getAllExperiments();
}
