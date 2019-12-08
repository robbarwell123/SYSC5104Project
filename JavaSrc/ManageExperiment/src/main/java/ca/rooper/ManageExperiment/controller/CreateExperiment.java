package ca.rooper.ManageExperiment.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import ca.rooper.CommonExperiment.model.Experiment;
import ca.rooper.CommonExperiment.model.ResponseMessage;
import ca.rooper.CommonExperiment.repository.Experiments;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;

@RestController
public class CreateExperiment
{
//	private static final Logger logger = LoggerFactory.getLogger(CreateExperiment.class);

	@Value("${experiments.basedir}")
	private String sBaseDir;
	
	private String sExpDir;
	
	@Autowired
	Experiments myExperiments;
	
	@CrossOrigin
	@PostMapping("/CreateExperiment")
    public ResponseMessage UploadXMLs(@RequestParam("sExperimentName") String sExperimentName, @RequestParam("XMLModels") MultipartFile[] XMLModels)
    {
		ResponseMessage toRtn=new ResponseMessage();
		Experiment myExperiment = null;
		
		try
		{
			myExperiment = new Experiment(sExperimentName);
			myExperiments.save(myExperiment);
			sExpDir=sBaseDir+myExperiment.getId()+"/";
			new File(sExpDir).mkdir();

	        for(MultipartFile fModel : XMLModels)
	        {	        	
	        	Path oNewFileLoc=Paths.get(sExpDir+fModel.getOriginalFilename());
	        	Files.copy(fModel.getInputStream(), oNewFileLoc, StandardCopyOption.REPLACE_EXISTING);
	        }	        
	        myExperiment.setComment("Files Uploaded");
			myExperiments.save(myExperiment);	        
	        toRtn.setStatus("Success");
	        toRtn.setData(XMLModels.length+" Files Uploaded");
		}catch(Exception ex)
		{
			toRtn.setStatus("Error");
			toRtn.setData(ex.toString());
			if(myExperiment!=null)
			{
				myExperiments.delete(myExperiment);
			}
		}

    	return toRtn;
    }
}