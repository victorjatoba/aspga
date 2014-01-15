/*
  Copyright 2013 by Victor Jatoba
  Licensed under the Academic Free License version 3.0
  See the file "LICENSE" for more information
*/


package ec.app.pea;

import ec.app.pea.ValidationUtil;

/**
 * Subject.java
 *
 * Modified: Mon Nov 26 02:29 2013
 * By: Victor Jatoba
 */
public class Subject
{
	private String name;
	private int dificulty;
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
		if(ValidationUtil.isBetweenOneAndFive(dificulty))
		{
			this.dificulty = dificulty;
		}
		else
		{
			state.output.fatal("The value of dificulty could be between 1 and 5",null);
		}
	}
/*
	public int getImportance()
	{
		return this.importance;
	}

	public void setImportance(int importance)
	{
		if(ValidationUtil.isBetweenOneAndFive(importance))
		{
			this.importance = importance;
		}
		else
		{
			state.output.fatal("The value of importance could be between 1 and 5",null);
		}
	}
*/
}


