package ca.rooper.ViewExperiment.controller;

import java.io.FileInputStream;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FilenameUtils;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ca.rooper.CommonExperiment.repository.Experiments;


@RestController
public class DownloadLogs
{
//	private static final Logger logger = LoggerFactory.getLogger(DownloadLogs.class);

	@Value("${experiments.basedir}")
	private String sBaseDir;
	
	@Autowired
	Experiments myExperiments;

	@CrossOrigin
	@GetMapping("/DownloadLogs")
    public ResponseEntity<?> GetExperimentList(@RequestParam("iId") int tId)
    {		
		ResponseEntity<?> toRtn;
		List<String> myLogFiles=Collections.emptyList();
		
		try
		{
			myLogFiles=Files.readAllLines(Paths.get(sBaseDir+tId+"/logs.list"));
			
	        Map<String, String> env = new HashMap<>(); 
	        env.put("create", "true");
	        Path oZipFile = Paths.get(sBaseDir+tId+"/LOGS.zip");
			FileSystem myZipFile = FileSystems.newFileSystem(oZipFile, env);			
			for(String myLogFile : myLogFiles)
			{
	            Path oLogFile = Paths.get(sBaseDir+tId+"/"+myLogFile);
	            Path inZipFile = myZipFile.getPath(myLogFile);          
	            Files.copy(oLogFile,inZipFile,StandardCopyOption.REPLACE_EXISTING );
			}
			myZipFile.close();

		    InputStreamResource rtnZipFile = new InputStreamResource(new FileInputStream(oZipFile.toString()));

	        HttpHeaders myHeaders = new HttpHeaders();
	        myHeaders.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=Experiment"+tId+".zip");
	        
		    toRtn = ResponseEntity.ok()
		    		.headers(myHeaders)
		            .contentLength(oZipFile.toFile().length())
		            .contentType(MediaType.parseMediaType("application/octet-stream"))
		            .body(rtnZipFile);			
		}catch(Exception errDelete)
		{
			toRtn = ResponseEntity.ok()
				.body("Error: "+errDelete.toString());			
		}
		
		return toRtn;
    }

}
