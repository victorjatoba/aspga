/*
  Copyright 2014 by Victor Jatoba
  Licensed under the Academic Free License version 3.0
  See the file "LICENSE" for more information
*/


package ec.app.aspga;
import java.util.ArrayList;
/**
 * DayPeriodAvailable.java
 *
 * Modified: Mon Jan 20 22:42 2014
 * By: Victor Jatoba
 */
public class DayPeriodAvailable {

	private Period monday;
	private Period tuesday;
	private Period wednesday;
	private Period thursday;
	private Period friday;
	private Period saturday;
	private Period sunday;

	public Period getMonday() {
		return this.monday;
	}

	public void setMonday(Period monday) {
		this.monday = monday;
	}

	public Period getTuesday() {
		return this.tuesday;
	}

	public void setTuesday(Period tuesday) {
		this.tuesday = tuesday;
	}

	public Period getWednesday() {
		return this.wednesday;
	}

	public void setWednesday(Period wednesday) {
		this.wednesday = wednesday;
	}

	public Period getThursday() {
		return this.thursday;
	}

	public void setThursday(Period thursday) {
		this.thursday = thursday;
	}

	public Period getFriday() {
		return this.friday;
	}

	public void setFriday(Period friday) {
		this.friday = friday;
	}

	public Period getSaturday() {
		return this.saturday;
	}

	public void setSaturday(Period saturday) {
		this.saturday = saturday;
	}

	public Period getSunday() {
		return this.sunday;
	}

	public void setSunday(Period sunday) {
		this.sunday = sunday;
	}
}
