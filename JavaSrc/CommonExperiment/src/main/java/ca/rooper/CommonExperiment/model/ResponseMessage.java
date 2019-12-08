package ca.rooper.CommonExperiment.model;

import java.util.ArrayList;
import java.util.List;

public class ResponseMessage
{
	private String status;
	private List<String> data;
	
	public ResponseMessage()
	{
		data = new ArrayList<String>();
	}
	
	public String getStatus()
	{
		return status;
	}
	
	public List<String> getData()
	{
		return data;
	}
	
	public void setStatus(String tStatus)
	{
		status=tStatus;
	}
	
	public void setData(String tData)
	{
		data.add(tData);
	}
}
