/*
  Copyright 2013 by Victor Jatoba
  Licensed under the Academic Free License version 3.0
  See the file "LICENSE" for more information
*/


package ec.app.pea;
import ec.gp.*;

/**
 * StudyPlanGene.java
 *
 * Modified: Mon Nov 26 02:29 2013
 * By: Victor Jatoba
 */

public class StudyPlanGene extends Gene {
	public ArrayList<DayPlan> week;
	/*
	public DayPlan monday;
	public DayPlan tuesday;
	public DayPlan wednesday;
	public DayPlan thursday;
	public DayPlan friday;
	public DayPlan saturday;
	public DayPlan sunday;

	public StudyPlanGene ()	{
		this.week = fillWeek();
	}
	*/

	public ArrayList<DayPlan> fillWeek()
	{
		ArrayList<DayPlan> week = new ArrayList<DayPlan>();

		DayPlan monday = new DayPlan();
		DayPlan tuesday = new DayPlan();
		DayPlan wednesday = new DayPlan();
		DayPlan thursday = new DayPlan();
		DayPlan friday = new DayPlan();
		DayPlan saturday = new DayPlan();
		DayPlan sunday = new DayPlan();

		week.add(monday);
		week.add(tuesday);
		week.add(wednesday);
		week.add(thursday);
		week.add(friday);
		week.add(saturday);
		week.add(sunday);

		return week;
	}

	public ArrayList<DayPlan> getWeek() {
		return this.week;
	}

	public void setWeek(ArrayList<DayPlan> week) {
		this.week = week;
	}
}


