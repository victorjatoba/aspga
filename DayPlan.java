/*
  Copyright 2013 by Victor Jatoba
  Licensed under the Academic Free License version 3.0
  See the file "LICENSE" for more information
*/


package ec.app.aspga;
import  java.util.ArrayList;
/*
 * DayPlan.java
 *
 * Modified: Thu Nov 26 02:29 2013
 * By: Victor Jatoba
 */

public class DayPlan {

	public ArrayList<SubjectWorkload> morning;
	public ArrayList<SubjectWorkload> afternoon;
	public ArrayList<SubjectWorkload> night;

	public ArrayList<SubjectWorkload> getMorning() {
		return this.morning;
	}

	public void setMorning(ArrayList<SubjectWorkload> morning) {
		this.morning = morning;
	}

	public ArrayList<SubjectWorkload> getAfternoon() {
		return this.afternoon;
	}

	public void setAfternoon(ArrayList<SubjectWorkload> afternoon) {
		this.afternoon = afternoon;
	}

	public ArrayList<SubjectWorkload> getNight() {
		return this.night;
	}

	public void setNight(ArrayList<SubjectWorkload> night) {
		this.night = night;
	}

}


