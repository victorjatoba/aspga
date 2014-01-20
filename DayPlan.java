/*
  Copyright 2013 by Victor Jatoba
  Licensed under the Academic Free License version 3.0
  See the file "LICENSE" for more information
*/


package ec.app.aspga;

/*
 * DayPlan.java
 *
 * Modified: Thu Nov 26 02:29 2013
 * By: Victor Jatoba
 */

public class DayPlan
{
	public Array<SubjectWorkload> morning;
	public Array<SubjectWorkload> afternoon;
	public Array<SubjectWorkload> night;

	public void addMorningInformation(SubjectWorkload subjectWorkload)
	{
		this.morning.add(subjectWorkload);
	}

	public void addAfternoonInformation(SubjectWorkload subjectWorkload)
	{
		this.afternoon.add(subjectWorkload);
	}

	public void addNightInformation(SubjectWorkload subjectWorkload)
	{
		this.night.add(subjectWorkload);
	}

	public void getMorningInformation()
	{
		return this.morning;
	}

	public void getAfternoonInformation()
	{
		return this.afternoon;
	}

	public void getNightInformation()
	{
		return this.night;
	}
}


