package ca.rooper.ManageExperiment.controller;

import java.util.List;

//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import ca.rooper.CommonExperiment.model.Experiment;
import ca.rooper.CommonExperiment.repository.Experiments;

@RestController
public class GetSimList
{
//	private static final Logger logger = LoggerFactory.getLogger(GetSimList.class);

	@Autowired
	Experiments myExperiments;

	@CrossOrigin
	@GetMapping("/GetSimList")
    public List<Experiment> GetExperimentList()
    {
		return myExperiments.getAllExperiments();
    }
}
