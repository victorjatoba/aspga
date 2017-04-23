/*
  Copyright 2013 by Victor Jatoba
  Licensed under the Academic Free License version 3.0
*/

package ec.app.aspga.bean;

import ec.app.aspga.bean.Subject;

/**
 * SubjectWorkloadGene.java
 *
 * @author Victor Jatoba
 * @version Mon Nov 26 02:29 2013
 */
public class SubjectWorkload {

    private static final byte MIN_WORKLOAD_VALUE = 1;
    private static final byte MAX_WORKLOAD_VALUE = 10;

	private Subject subject;
	private byte workload; //from 1 to 10

	public Subject getSubject() {
		return this.subject;
	}

	public void setSubject(Subject subject) {
		this.subject = subject;
	}

	public byte getWorkload() {
		return this.workload;
	}

	public void setWorkload(byte workload) {
		if( workload < 1 ){
            workload = 1;
        }
        else if( workload > 10 ){
            workload = 10;
        }
        this.workload = workload;
	}

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SubjectWorkload)) return false;

        SubjectWorkload that = (SubjectWorkload) o;

        if (workload != that.workload) return false;
        if (!subject.equals(that.subject)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = subject.hashCode();
        result = 31 * result + workload;
        return result;
    }
}
