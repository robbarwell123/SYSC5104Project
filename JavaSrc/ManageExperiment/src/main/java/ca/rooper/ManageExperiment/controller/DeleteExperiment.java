package ca.rooper.ManageExperiment.controller;

import java.io.File;
import java.util.Optional;

import org.apache.tomcat.util.http.fileupload.FileUtils;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ca.rooper.CommonExperiment.model.Experiment;
import ca.rooper.CommonExperiment.model.ResponseMessage;
import ca.rooper.CommonExperiment.repository.Experiments;

@RestController
public class DeleteExperiment
{
//	private static final Logger logger = LoggerFactory.getLogger(DeleteExperiment.class);

	@Value("${experiments.basedir}")
	private String sBaseDir;
	
	@Autowired
	Experiments myExperiments;

	@CrossOrigin
	@GetMapping("/DeleteExperiment")
    public ResponseMessage GetExperimentList(@RequestParam("iId") int tId)
    {
		ResponseMessage toRtn=new ResponseMessage();
		Optional<Experiment> tExperiment = null;
		Experiment myExperiment;
		File myExperimentDir;
		
		try
		{
			myExperimentDir=new File(sBaseDir+tId+"/");
			tExperiment = myExperiments.findById(tId);
			myExperiment = tExperiment.get();
			FileUtils.deleteDirectory(myExperimentDir);
			myExperiments.delete(myExperiment);
			toRtn.setStatus("Success");
			toRtn.setData("Experiment "+tId+" Deleted");
		}catch(Exception errDelete)
		{
			toRtn.setStatus("Error");
			toRtn.setData(errDelete.toString());
		}
		
		return toRtn;
    }

}
