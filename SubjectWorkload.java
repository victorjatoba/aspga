/*
  Copyright 2013 by Victor Jatoba
  Licensed under the Academic Free License version 3.0
*/

package ec.app.aspga;

/**
 * SubjectWorkloadGene.java
 *
 * @author Victor Jatoba
 * @version Mon Nov 26 02:29 2013
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
