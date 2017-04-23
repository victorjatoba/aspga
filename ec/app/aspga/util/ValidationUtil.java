/*
  Copyright 2013 by Victor Jatoba
  Licensed under the Academic Free License version 3.0
  See the file "LICENSE" for more information
*/


package ec.app.aspga.util;

/**
 * ValidationUtil.java
 *
 * Created: Mon Nov 26 02:29 2013
 * By: Victor Jatoba
 */

public class ValidationUtil
{

	public static Boolean isBetweenOneAndFive(int val)
        {
		if(val >= 1 && val <= 5)
		{
			return true;
		}
		else
		{
			return false;
		}
	}
}


