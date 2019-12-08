package ca.rooper.CommonExperiment.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;

@Entity
@NamedQuery(name = "Experiment.getAllExperiments", query = "SELECT e FROM Experiment e ORDER BY e.id DESC")
@Table(name="experiments")
public class Experiment
{
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	private Timestamp date;
	private String name;
	private String comment;
	
	public Experiment() {}
	
	public Experiment(int tId)
	{
		this.id=tId;
	}
	
	public Experiment(String tName)
	{
		this.date=new Timestamp(System.currentTimeMillis());
		this.name=tName;
		this.comment="N/A";
	}

	public int getId()
	{
		return id;
	}
	
	public String getDate()
	{
		return new SimpleDateFormat("dd MMM yyyy HH:mm").format(date);
	}
	
	public String getName()
	{
		return name;
	}
	
	public String getComment()
	{
		return comment;
	}
	
	public void setComment(String sComment)
	{
		this.comment=sComment;
	}
	
	@Override
	public String toString()
	{
		return "Experiment: " + name + " (" + id + ") created on " + date;
	}
}
