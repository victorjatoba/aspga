/*
  Copyright 2013 by Victor Jatoba
  Licensed under the Academic Free License version 3.0
  See the file "LICENSE" for more information
*/


package ec.app.aspga;
import ec.app.aspga.util.ValidationUtil;

/**
 * Subject.java
 *
 * Modified: Mon Nov 26 02:29 2013
 * By: Victor Jatoba
 */
public class Subject
{
	int id;
	String name;
	int dificulty; //from 1 to 100
//	private int importance;

	public String getName()
	{
		return this.name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public int getDificulty()
	{
		return this.dificulty;
	}

	public void setDificulty(int dificulty)
	{
		this.dificulty = dificulty;
	}

	public int getId()
	{
		return this.id;
	}

	public void setId(int id)
	{
		this.id = id;
	}
}
