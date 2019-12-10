package ca.rooper.RunExperiment.controller;

import java.io.BufferedReader;
import java.io.File;

import java.io.FileWriter;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.FilenameUtils;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;

import ca.rooper.CommonExperiment.model.Experiment;
import ca.rooper.CommonExperiment.model.ExperimentError;
import ca.rooper.CommonExperiment.repository.Experiments;
import ca.rooper.RunExperiment.model.FlatModel;
import ca.rooper.RunExperiment.model.LoggerDef;
import ca.rooper.RunExperiment.model.ModelDef;
import ca.rooper.RunExperiment.model.PortDef;
import ca.rooper.RunExperiment.model.SubModel;
import ca.rooper.CommonExperiment.model.ResponseMessage;

@RestController
public class RunCadmium
{
//	private static final Logger logger = LoggerFactory.getLogger(RunCadmium.class);

	@Value("${experiments.basedir}")
	private String sBaseDir;

	@Value("${experiments.compile}")
	private String sCompileCommand;

	@Value("${experiments.run}")
	private String sRunCommand;

	@Value("${cadmium.maxtimeout}")
	private int iMaxTimeout;

	@Autowired
	private Environment myEnvVars;	
	
	@Autowired
	private Experiments myExperiments;
	
    @Autowired
    private SpringTemplateEngine myTemplates;
    
	private ResponseMessage toRtn;
	private PrintWriter myLogFiles;
	
	@CrossOrigin
	@PostMapping("/RunCadmium")
    public ResponseMessage RunExperimentList(@RequestParam("iId") int tId,@RequestParam("sTop") String sTop,@RequestParam("sConfig") String sConfig)
    {
		boolean bStatus=false;
		
		Optional<Experiment> tExperiment = null;
		Experiment myExperiment;
		ModelDef myTopModel=null;
		toRtn=new ResponseMessage();
		
		String sBaseExpDir=sBaseDir+tId+"/";
		
		try
		{
			XMLSimParser.getInstance().SetXSD(myEnvVars);
			tExperiment = myExperiments.findById(tId);
			myExperiment = tExperiment.get();
			
			File oTOPFile=new File(sBaseExpDir+sTop);
			switch(FilenameUtils.getExtension(oTOPFile.toString()).toUpperCase())
			{
				case "XML":
					myTopModel=XMLSimParser.getInstance().ParseXML(oTOPFile);
					break;
				default:
					bStatus=false;
					toRtn.setData("No valid parser for: "+oTOPFile.getName());
			}
			myLogFiles = new PrintWriter(new FileWriter(sBaseExpDir+"logs.list",false));
			File oConfigFile=new File(sBaseExpDir+sConfig);
			switch(FilenameUtils.getExtension(oConfigFile.toString()).toUpperCase())
			{
				case "XML":
					myTopModel=XMLSimParser.getInstance().ParseConfigXML(myTopModel,oConfigFile,sBaseExpDir,myLogFiles);
					bStatus=true;
					break;
				default:
					bStatus=false;
					toRtn.setData("No valid parser for: "+oConfigFile.getName());
			}
			myLogFiles.flush();
			myLogFiles.close();
			if(bStatus)
			{
				switch(myTopModel.getSimulator())
				{
					case "Cadmium":
						myExperiment.setComment("Files Validated");
						myExperiments.save(myExperiment);				
						BuildSim(myTopModel, sBaseExpDir);
						myExperiment.setComment("Sim Built");
						myExperiments.save(myExperiment);
						CompileSim(tId);
						myExperiment.setComment("Sim Compiled");
						myExperiments.save(myExperiment);
						RunSim(tId);
						myExperiment.setComment("Sim Complete");
						myExperiments.save(myExperiment);
						toRtn.setData("Sim Executed Successfully.");
						break;
					default:
						bStatus=false;
						toRtn.setData("Simulator not found: "+myTopModel.getSimulator());
				}
			}
		}catch(Exception errConvert)
		{
			bStatus=false;
			toRtn.setData(errConvert.toString());
		}		
		
		toRtn.setStatus(bStatus ? "Success" : "Error");
		return toRtn;
    }

	public void CompileSim(int tId) throws ExperimentError
	{
		String sErrMsg;
		
		try
		{
			Process myCompile = Runtime.getRuntime().exec(sCompileCommand+" "+tId);
			if(!myCompile.waitFor(iMaxTimeout,TimeUnit.SECONDS))
			{
	            BufferedReader stdError = new BufferedReader(new InputStreamReader(myCompile.getErrorStream()));				
	            while ((sErrMsg = stdError.readLine()) != null)
	            {
	            	toRtn.setData(sErrMsg);
	            }
            	throw new ExperimentError("Compile Error");
			}
		}catch(Exception errCompile)
		{
			throw new ExperimentError("Compile Error");
		}
	}
	
	public void RunSim(int tId) throws ExperimentError
	{
		String sErrMsg;
		
		try
		{
			Process myRun = Runtime.getRuntime().exec(sRunCommand+" "+tId);
			if(!myRun.waitFor(iMaxTimeout,TimeUnit.SECONDS))
			{
	            BufferedReader stdError = new BufferedReader(new InputStreamReader(myRun.getErrorStream()));				
	            while ((sErrMsg = stdError.readLine()) != null)
	            {
	            	toRtn.setData(sErrMsg);
	            }
            	throw new ExperimentError("Run Error");
			}
		}catch(Exception errCompile)
		{
			throw new ExperimentError("Run Error");
		}
	}
	
	private void BuildSim(ModelDef myTopModel, String sBaseExpDir) throws ExperimentError
	{
		String myTopModelOutput;
		String myMakeFileOutput;
		
		FlatModel myModel = new FlatModel();
		myModel.setMyTopModel(myTopModel);
		myModel.setMyLoggers(BuildSimLoggers(myTopModel));

		FlattenModel(myTopModel,myModel);
		
		Context myContext = new Context();
		myContext.setVariable("myEnvVars", myEnvVars);
		myContext.setVariable("myModel", myModel);
		
		myTopModelOutput=myTemplates.process("coupledmodel", myContext);
		myMakeFileOutput=myTemplates.process("makefile", myContext);
		
		try
		{
			File myFileOut=new File(sBaseExpDir+"sim.cpp");
			FileWriter outFile = new FileWriter(myFileOut,false);
			outFile.write(myTopModelOutput);
			outFile.close();
			
			myFileOut=new File(sBaseExpDir+"makefile");
			outFile = new FileWriter(myFileOut,false);
			outFile.write(myMakeFileOutput);
			outFile.close();
		}catch(Exception errSaveFile)
		{
			throw new ExperimentError("Error Saving Experiment File");			
		}
	}
	
	private String BuildSimLoggers(ModelDef myModel)
	{
		String toRtn="";
		
		for(LoggerDef myLogger : myModel.getLoggers())
		{
			toRtn+=myLogger.getName()+",";
		}
		
		return toRtn.replaceAll(",$","");
	}
	
	private void FlattenModel(ModelDef myCurrModel, FlatModel myModel) throws ExperimentError
	{
		for(PortDef myPort : myCurrModel.getPorts())
		{
			if(!myModel.getMyPorts().containsKey(myPort.getName()))
			{
				myModel.addPort(myPort.getType()+myPort.getName(), myPort);				
			}
		}
		
		for(SubModel mySubModel : myCurrModel.getModels())
		{
			if(mySubModel.getXmlImplementation().equalsIgnoreCase("iestream"))
			{
				if(mySubModel.getModelParams().size()!=2)
				{
					throw new ExperimentError("Input Reader "+mySubModel.getName()+" does not have enough attributes.");
				}
				myModel.addInputReader(mySubModel);
				myModel.addAtomicModel(mySubModel);
			}else if(mySubModel.getSubmodel()==null)
			{
				myModel.addAtomicModel(mySubModel);
			}else
			{
				FlattenModel(mySubModel.getSubmodel(),myModel);
			}
		}
		
		myCurrModel.Flatten();
		myModel.addCoupledModel(myCurrModel);
	}
	
}