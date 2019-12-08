package ca.rooper.ViewExperiment.controller;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

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
			
			ByteArrayOutputStream myOutput = new ByteArrayOutputStream();
			ZipOutputStream myZip = new ZipOutputStream(myOutput);
			
			ZipEntry myAddFile;
			
			for(String myLogFile : myLogFiles)
			{
				myAddFile=new ZipEntry(myLogFile);
				myZip.putNextEntry(myAddFile);
				myZip.write(Files.readAllBytes(Paths.get(sBaseDir+tId+"/"+myLogFile)));
				myZip.closeEntry();
			}
			myZip.flush();
			myZip.close();
			
			InputStream targetStream = new ByteArrayInputStream(myOutput.toByteArray());
			
		    InputStreamResource rtnZipFile = new InputStreamResource(targetStream);

	        HttpHeaders myHeaders = new HttpHeaders();
	        myHeaders.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=Experiment"+tId+".zip");
	        
		    toRtn = ResponseEntity.ok()
		    		.headers(myHeaders)
		            .contentLength(myOutput.toByteArray().length)
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
