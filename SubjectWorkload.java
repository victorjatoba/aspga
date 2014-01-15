/*
  Copyright 2013 by Victor Jatoba
  Licensed under the Academic Free License version 3.0
  See the file "LICENSE" for more information
*/


package ec.app.pea;

/**
 * SubjectWorkload.java
 *
 * Modified: Mon Nov 26 02:29 2013
 * By: Victor Jatoba
 */
public class SubjectWorkload
{
	private Subject subject;
	private Double workload; //per week

	public Subject getSubject()
	{
		return this.subject;
	}

	public void setSubject(Subject subject)
	{
		this.subject = subject;
	}

	public Double getWorkload()
	{
		return this.workload;
	}

	public void setWorkload(Double workload)
	{
		this.workload = workload;
	}
}
