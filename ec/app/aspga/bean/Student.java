/*
  Copyright 2014 by Victor Jatoba
  Licensed under the Academic Free License version 3.0
  See the file "LICENSE" for more information
*/

package ec.app.aspga.bean;

/**
 * The Class that have the student features. <br/>
 * The Student contains:<br/><br/>
 * 
 * <code>{@link String}</code> NAME <br/>
 * <code>float</code> HOURSTOLEISURE <br/>
 *
 * @author Victor Jatoba
 * @version Mon Jan 20 23:34 2014
 */
public class Student {

	private String name;
	private float hoursToLeisure; //on all study plan.

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public float getHoursToLeisure() {
		return this.hoursToLeisure;
	}

	public void setHoursToLeisure(float hoursToLeisure) {
		this.hoursToLeisure = hoursToLeisure;
	}
}


