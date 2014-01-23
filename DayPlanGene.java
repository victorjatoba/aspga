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
import  java.util.ArrayList;
import java.io.File;
import java.util.Vector;
import java.io.FileInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Random;

/*
 * DayPlanIndividual.java
 *
 * Modified: Thu Nov 26 02:29 2013
 * By: Victor Jatoba
 */

@SuppressWarnings("serial")
public class DayPlanGene extends Gene {
	public static final String P_COURSEINFORMATION = "courseinformation";

	public ArrayList<SubjectWorkload> morning;
	public ArrayList<SubjectWorkload> afternoon;
	public ArrayList<SubjectWorkload> night;

	ArrayList<Subject> subjects;
    EvolutionState state;
    Parameter base;

    @Override
    public void setup(final EvolutionState state, final Parameter base) {
    	super.setup(state, base);

        File courseInformationInput = null;

        courseInformationInput = state.parameters.getFile(base.push(P_COURSEINFORMATION),null);

        if (courseInformationInput == null) {
            state.output.error("CourseInformation File doesn't exist", base.push(P_COURSEINFORMATION));
        }

        Vector<String> courseInformationVector = convertFileToVectorString(courseInformationInput);

        this.subjects = fillSubjects(courseInformationVector);
    }

    public void mutate(EvolutionState state) {
        while(!doMutate(state));
    }

    /**
    * Make mutation. Case success return true and false otherwise.
    */
    public Boolean doMutate(EvolutionState state) {

        Boolean mutated = Boolean.TRUE;
        Random random = new Random();

        int mutationType = random.nextInt(5);
        mutationType = 4;

        int vectorsLength = morning.size() + afternoon.size() + night.size();
        //If I don't have any SubjectWorkload, just add one.
        if(mutationType == 0 || vectorsLength == 0) {
            System.out.println("---0---");
            insertSubjectWorkload(state);
        } else if(mutationType == 1) {
            System.out.println("---1---");
            mutated = removeSubjectWorkload();
        } else if(mutationType == 2) {
            System.out.println("---2---");
            mutated = changeWorkload();
        } else if(mutationType == 3) {
            System.out.println("---3---");
            mutated = changeSubject();
        } else {
            System.out.println("---4---");
            Boolean mutated2 = changeWorkload();
            mutated = changeSubject();
            mutated = (mutated || mutated2);
        }

        return mutated;
    }


    public void insertSubjectWorkload(EvolutionState state) {
        Random random = new Random();

        int workload = (random.nextInt(12) + 1); //random from 1 to 12. Min and max per period of the day.
        SubjectWorkload subjectWorkload = new SubjectWorkload();
        subjectWorkload.setWorkload(workload);

/*        Subject subjectNew = new Subject();
        subjectNew.setName(new String(subjectRandom.getName()));
        subjectNew.setId(subjectRandom.getId());
        subjectNew.setDificulty(subjectRandom.getDificulty());
*/
        Subject subjectRandom = subjects.get(state.random[0].nextInt(subjects.size()));
        Subject subjectNew = getNewSubjectInstance(subjectRandom);
        subjectWorkload.setSubject(subjectNew);

        int periodOfTheDay = random.nextInt(3); //random from 0 to 2
        if(periodOfTheDay == 0) {
            this.morning.add(subjectWorkload);
        } else if (periodOfTheDay == 1) {
            this.afternoon.add(subjectWorkload);
        } else {
            this.night.add(subjectWorkload);
        }
    }

    public Boolean removeSubjectWorkload() {
        Random random = new Random();

        int periodOfTheDay = random.nextInt(3); //random from 0 to 2
        if(periodOfTheDay == 0 && !this.morning.isEmpty()) {
            this.morning.remove(random.nextInt(morning.size()));
        } else if (periodOfTheDay == 1 && !this.afternoon.isEmpty()) {
            this.afternoon.remove(random.nextInt(afternoon.size()));
        } else if (!this.night.isEmpty()) {
            this.night.remove(random.nextInt(night.size()));
        } else {
            return Boolean.FALSE;
        }

        return Boolean.TRUE;
    }

    public Boolean changeWorkload() {
        Random random = new Random();
        SubjectWorkload subjectWorkload = new SubjectWorkload();

        int periodOfTheDay = random.nextInt(3); //random from 0 to 2
        if(periodOfTheDay == 0 && !this.morning.isEmpty()) {
            subjectWorkload = this.morning.get(random.nextInt(morning.size()));
        } else if (periodOfTheDay == 1 && !this.afternoon.isEmpty()) {
            subjectWorkload = this.afternoon.get(random.nextInt(afternoon.size()));
        } else if (!this.night.isEmpty()) {
            subjectWorkload = this.night.get(random.nextInt(night.size()));
        } else {
            System.out.println("Not mutation workload");
            return Boolean.FALSE;
        }
        subjectWorkload.setWorkload(getWorkloadDifferentOf(subjectWorkload.getWorkload()));
        System.out.println(((float)subjectWorkload.getWorkload()/(float)2)+"[M]");

        return Boolean.TRUE;
    }

    public int getWorkloadDifferentOf(int workload) {
        Random random = new Random();
        int workloadRandom = (random.nextInt(12) + 1);

        while(workloadRandom == workload) {
            System.out.println(workload + " equals. Generating other..");
            workloadRandom = (random.nextInt(12) + 1);
        }

        return workloadRandom;
    }

    public Boolean changeSubject() {
        Random random = new Random();
        //int workload = (random.nextInt(12) + 1);

        Subject subjectOld;

        int periodOfTheDay = random.nextInt(3); //random from 0 to 2
        if(periodOfTheDay == 0 && !this.morning.isEmpty()) {
            subjectOld = this.morning.get(random.nextInt(morning.size())).getSubject();
        } else if (periodOfTheDay == 1 && !this.afternoon.isEmpty()) {
            subjectOld = this.afternoon.get(random.nextInt(afternoon.size())).getSubject();
        } else if (!this.night.isEmpty()) {
            subjectOld = this.night.get(random.nextInt(night.size())).getSubject();
        } else {
            System.out.println("this gene don't have mutation");
            return Boolean.FALSE ;
        }

        Subject subjectRandom = getSubjectDifferentOf(subjectOld);
        subjectOld.setName(new String(subjectRandom.getName()));
        subjectOld.setId(subjectRandom.getId());
        subjectOld.setDificulty(subjectRandom.getDificulty());

//        subjectOld = getSubjectDifferentOf(subjectOld);
        String s = subjectOld.getName(); s += "[M]"; subjectOld.setName(s);
        System.out.println(""+subjectOld.getName());

        return Boolean.TRUE;
    }

    public Subject getSubjectDifferentOf(Subject subject) {
        Random random = new Random();
        Subject subjectNew = subjects.get(random.nextInt(subjects.size()));
        int idRandom = subjectNew.getId();
        int idOld = subject.getId();

        while(idRandom == idOld) {
            subjectNew = subjects.get(random.nextInt(subjects.size()));
            idRandom = subjectNew.getId();
        }

        return getNewSubjectInstance(subjectNew);
    }

    public Subject getNewSubjectInstance(Subject subject) {
        Subject subjectRandom = new Subject();
        subjectRandom.setName(new String(subject.getName()));
        subjectRandom.setId(subject.getId());
        subjectRandom.setDificulty(subject.getDificulty());
        return subjectRandom;
    }

    /*
     * used to initialize the Individual, in turn calls this method on your Gene subclass.
     */
    public void reset(EvolutionState state, int thread) {
        Random random = new Random();
        //I can study max 32 subjects/day. But I need breaks too!
        //int maxSubjectsQuantity = random.nextInt(32);
        int maxSubjectsQuantity = random.nextInt(32);

        for(int i = 0; i < maxSubjectsQuantity; i++) {

            SubjectWorkload subjectWorkload = getNewSubjectWorkloadInstance(state, thread);

            int periodOfTheDay = random.nextInt(3); //random from 0 to 2
            if(periodOfTheDay == 0) {
                this.morning.add(subjectWorkload);
            } else if (periodOfTheDay == 1) {
                this.afternoon.add(subjectWorkload);
            } else {
                this.night.add(subjectWorkload);
            }
        }
    }

    public SubjectWorkload getNewSubjectWorkloadInstance(EvolutionState state, int thread) {
        Random random = new Random();
        Subject subject = subjects.get(state.random[thread].nextInt(subjects.size()));

        Subject subjectNew = getNewSubjectInstance(subject);

        int workload = (random.nextInt(12) + 1); //random from 1 to 12

        SubjectWorkload subjectWorkload = new SubjectWorkload();
        subjectWorkload.setSubject(subjectNew);
        subjectWorkload.setWorkload(workload);
        return subjectWorkload;
    }

    /*
     * Verify if one gene is equals to other.
     */
    public boolean equals(Object other) {
        Boolean isEquals = Boolean.TRUE;

        if (other != null && other instanceof DayPlanGene) {
            ArrayList<SubjectWorkload> morningOther = ((DayPlanGene)other).morning;
            ArrayList<SubjectWorkload> afternoonOther = ((DayPlanGene)other).afternoon;
            ArrayList<SubjectWorkload> nightOther = ((DayPlanGene)other).night;

            //the periods of the day have the same length?
            if (morningOther.size() == this.morning.size() &&
                afternoonOther.size() == this.afternoon.size() &&
                nightOther.size() == this.night.size() ) {

/*                System.out.println("size:");
                System.out.println(morningOther.size() +" == "+ this.morning.size());
                System.out.println(afternoonOther.size() +" == "+ this.afternoon.size());
                System.out.println(nightOther.size() +" == "+ this.night.size());
*/
                ArrayList<SubjectWorkload> subjectWorkloads = new ArrayList<SubjectWorkload>();
                subjectWorkloads.addAll(this.morning);
                subjectWorkloads.addAll(this.afternoon);
                subjectWorkloads.addAll(this.night);

                ArrayList<SubjectWorkload> subjectWorkloadsOther = new ArrayList<SubjectWorkload>();
                subjectWorkloadsOther.addAll(morningOther);
                subjectWorkloadsOther.addAll(afternoonOther);
                subjectWorkloadsOther.addAll(nightOther);

                int sizeSW = subjectWorkloads.size();
                if(sizeSW == subjectWorkloadsOther.size()) {
/*                    System.out.println("size together:");
                    System.out.println(sizeSW +" == "+ subjectWorkloadsOther.size());
*/                    for (int i = 0; i < sizeSW; i++) {
                        SubjectWorkload subjectWorkload = subjectWorkloads.get(i);
                        SubjectWorkload subjectWorkloadOther = subjectWorkloadsOther.get(i);

/*                        System.out.println(subjectWorkload.getWorkload() +" == "+ subjectWorkloadOther.getWorkload());
                        System.out.println(subjectWorkload.getSubject().getId() +" == "+ subjectWorkloadOther.getSubject().getId());
*/
                        //Verify the workloads and ids
                        if ( subjectWorkload.getWorkload() != subjectWorkloadOther.getWorkload() ||
                             subjectWorkload.getSubject().getId() != subjectWorkloadOther.getSubject().getId()) {

                            isEquals = Boolean.FALSE;
                            break;
                        }
                    }
                } else {
                    state.output.error("DPG| Error: Differents SubjectWorkloas sizes is impossible!");
                }
            } else {
                isEquals = Boolean.FALSE;
            }

        } else {
            isEquals = Boolean.FALSE;
        }

        return isEquals;
    }

    /*
     * @see ec.vector.GeneVectorIndividual#hashCode()
     */
    @Override
    public int hashCode() {
    	return 0;
    }

    @Override
    public Object clone() {
    	return null;
    }

    @Override
    public String toString() {
		return null;
    }

	/**
	* In reality Gene is not going to be very useful unless you at
	* least provide a way to describe the gene when it’s printed to a log.
	* The easiest way to do this is to override this method.
	*/
	public String printGeneToStringForHumans() {
		ArrayList<SubjectWorkload> subjectWorkloads = new ArrayList<SubjectWorkload>();
		String output;
        output = "" + ("-----------------------\nnumber of subjectWorkloads: " + subjectWorkloads.size());

        output += "" + ("\nmorning: ");
        for (SubjectWorkload subjectWorkload : morning) {
            float workload = ((float)subjectWorkload.getWorkload())/(float)2;
            output += "" + (subjectWorkload.getSubject().getName() + " " +
                                workload);
        }
        System.out.println("\nafternoon: ");
        for (SubjectWorkload subjectWorkload : afternoon) {
            float workload = ((float)subjectWorkload.getWorkload())/(float)2;
            output += "" + (subjectWorkload.getSubject().getName() + " " +
                                workload);
        }
        System.out.println("\nnight: ");
        for (SubjectWorkload subjectWorkload : night) {
            float workload = ((float)subjectWorkload.getWorkload())/(float)2;
            output += "" + (subjectWorkload.getSubject().getName() + " " +
                                workload);
        }
		return output;
	}

	public void readGeneFromString(String string, EvolutionState state) {
	/*	string = string.trim().substring(0); // get rid of the ">"
		DecodeReturn dr = new DecodeReturn(string);
		Code.decode(dr); x = dr.d; // no error checking
		Code.decode(dr); y = dr.d;
	*/
	}

	/**
	* If you’re writing Individuals with the intent that they be
	* read back in again later, you’ll probably want to override
	* this method:
	*/
/*
	public String printGeneToString() {
		return "> " + Code.Encode(this.subject) + " " + Code.Encode(this.workload);
	}

	public void writeGene(EvolutionState state, DataOutput out) throws IOException {
		out.writeObject(this.subject); //See ObjectOutput
		out.writeDouble(this.workload);
	}
	public void readGene(EvolutionState state, DataOutput in) throws IOException {
		x = in.readDouble(); y = in.readDouble();
	}
*/

	/**
    *   Fill the Subjects with the input file
    */
    public ArrayList<Subject> fillSubjects(Vector<String> subjectsIn) {
        ArrayList<Subject> subjects = new ArrayList<Subject>();

        if (subjectsIn != null) {
        	int i = 0;
            for (String line: subjectsIn) {
                String[] subjectDificulty = line.split(" ");

                Subject subject = new Subject();
                subject.setName(subjectDificulty[0]);
                char dificultyChar = subjectDificulty[1].charAt(0);
                int dificulty = Character.getNumericValue(dificultyChar);
                //int dificulty = dificultyChar - '0';
                subject.setDificulty(dificulty);
                subject.setId(i++);
                subjects.add(subject);
            }
        } else {
            state.output.error("SSPP| Error: The vector of Subjects are null!");
        }

        return subjects;
    }

    /**
    *   Convert a file to a vector of string.
    */
    public Vector<String> convertFileToVectorString(File file) {

        FileInputStream inputStream = null;
        BufferedReader br           = null;
        Vector<String> lines        = null;

        try {

            inputStream = new FileInputStream(file);

            br = new BufferedReader(new InputStreamReader(inputStream));

            //StringBuilder sb = new StringBuilder();

            String line;
            lines = new Vector<String>();
            while ((line = br.readLine()) != null) {
                //sb.append(line);
                lines.add(line);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return lines;
    }

	/**
    * Print all the classes filled
    */
    public void printDayPlan(DayPlanGene dayPlanGene) {
        ArrayList<SubjectWorkload> subjectWorkloads = new ArrayList<SubjectWorkload>();
        subjectWorkloads.addAll(dayPlanGene.morning);
        subjectWorkloads.addAll(dayPlanGene.afternoon);
        subjectWorkloads.addAll(dayPlanGene.night);

        System.out.println("-----------------------\nnumber of subjectWorkloads: " + subjectWorkloads.size());

        System.out.println("\nmorning: ");
        for (SubjectWorkload subjectWorkload : dayPlanGene.morning) {
            float workload = ((float)subjectWorkload.getWorkload())/(float)2;
            System.out.println(subjectWorkload.getSubject().getName() + " " +
                                workload);
        }
        System.out.println("\nafternoon: ");
        for (SubjectWorkload subjectWorkload : dayPlanGene.afternoon) {
            float workload = ((float)subjectWorkload.getWorkload())/(float)2;
            System.out.println(subjectWorkload.getSubject().getName() + " " +
                                workload);
        }
        System.out.println("\nnight: ");
        for (SubjectWorkload subjectWorkload : dayPlanGene.night) {
            float workload = ((float)subjectWorkload.getWorkload())/(float)2;
            System.out.println(subjectWorkload.getSubject().getName() + " " +
                                workload);
        }

    }

}


