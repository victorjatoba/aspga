/*
  Copyright 2014 by Victor Jatoba
  Licensed under the Academic Free License version 3.0
  See the file "LICENSE" for more information
*/


package ec.app.aspga;

/**
 * Student.java
 *
 * Modified: Tue Jan 14 20:30 2014
 * By: Victor Jatoba
 */
public class Student
{
	private String name;
	private float hoursToLeisure; //per week

	public String getName()
	{
		return this.name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public float getHoursToLeisure()
	{
		return this.hoursToLeisure;
	}

	public void setHoursToLeisure(float hoursToLeisure)
	{
		this.hoursToLeisure = hoursToLeisure;
	}
}


