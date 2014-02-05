/*
  Copyright 2013 by Victor Jatoba
  Licensed under the Academic Free License version 3.0
  See the file "LICENSE" for more information
*/

package ec.app.aspga;

import ec.vector.Gene;
import ec.EvolutionState;
import ec.util.*;

import java.lang.*;
import java.io.DataOutput;

/**
 * SubjectWorkloadGene.java
 *
 * Modified: Mon Nov 26 02:29 2013
 * By: Victor Jatoba
 */
public class SubjectWorkload {

	Subject subject;
	int workload; //from 1 to 10

	public Subject getSubject() {
		return this.subject;
	}

	public void setSubject(Subject subject) {
		this.subject = subject;
	}

	public int getWorkload() {
		return this.workload;
	}

	public void setWorkload(int workload) {
		this.workload = workload;
	}

}
