/*
  Copyright 2014 by Victor Jatoba
  Licensed under the Academic Free License version 3.0
  See the file "LICENSE" for more information
*/


package ec.app.aspga;

/**
 * Period.java
 *
 * Modified: Mon Jan 20 22:42 2014
 * By: Victor Jatoba
 */
public class Period {
	private char morning;
	private char afternoon;
	private char night;

	public char getMorning() {
		return this.morning;
	}

	public void setMorning(char morning) {
		this.morning = morning;
	}

	public char getAfternoon() {
		return this.afternoon;
	}

	public void setAfternoon(char afternoon) {
		this.afternoon = afternoon;
	}

	public char getNight() {
		return this.night;
	}

	public void setNight(char night) {
		this.night = night;
	}
}
