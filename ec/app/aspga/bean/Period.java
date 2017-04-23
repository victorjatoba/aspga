/*
  Copyright 2014 by Victor Jatoba
  Licensed under the Academic Free License version 3.0
  See the file "LICENSE" for more information
*/

package ec.app.aspga.bean;

/**
 * The Class that contains the Period features. <br/>
 * The period contains three character to define the information of it. <br/>
 * A period is formed by: <br/><br/>
 *
 * <code>char</code> MONING <br/>
 * <code>char</code> AFTERNOON <br/>
 * <code>char</code> NIGHT <br/>
 *
 * @author Victor Jatoba
 * @version Mon Jan 20 22:42 2014
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
