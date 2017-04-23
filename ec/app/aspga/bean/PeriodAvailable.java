/*
  Copyright 2014 by Victor Jatoba
  Licensed under the Academic Free License version 3.0
  See the file "LICENSE" for more information
*/


package ec.app.aspga.bean;
import java.util.ArrayList;

import ec.app.aspga.bean.Period;

/**
 * Is the class that contains the study cycle by 
 * difficulty and facility of to learn. <br/>
 * Here is the data structured used for storing
 * the Day periods available and the facility
 * level for to learn by user.
 *
 * @author Victor Jatoba
 * @version Mon Jan 20 22:42 2014
 */
public class PeriodAvailable {

	private Period[] studyCycle;

	public Period[] getStudyCycle() {
		return this.studyCycle;
	}

	public void setStudyCycle(Period[] studyCycle) {
		this.studyCycle = studyCycle;
	}
}
