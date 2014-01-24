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
import java.util.ArrayList;
import java.io.File;
import java.util.Vector;
import java.io.FileInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Random;

/**
 * DayPlanIndividual.java
 *
 * Modified: Thu Nov 26 02:29 2013
 * @author Victor Jatoba
 * @version 1.0.0
 */

@SuppressWarnings("serial")
public class DayPlanGene extends Gene {
	public static final String P_COURSEINFORMATION = "courseinformation";

	//Our allele is represented by the tree period of the day below.
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

    @Override
    public void mutate(final EvolutionState state, final int thread) {
        while(!doMutate(state, thread));
    }

    /**
    * Make mutation. Case success return true and false otherwise.
    */
    public Boolean doMutate(EvolutionState state, final int thread) {

        Boolean mutated = Boolean.TRUE;
        Random random = new Random();

        int mutationType = random.nextInt(5);
        mutationType = 4;

        int vectorsLength = morning.size() + afternoon.size() + night.size();
        //If I don't have any SubjectWorkload, just add one.
        if(mutationType == 0 || vectorsLength == 0) {
//            System.out.println("---0---");
            mutated = insertSubjectWorkload(state, thread);
        } else if(mutationType == 1) {
//            System.out.println("---1---");
            mutated = removeSubjectWorkload();
        } else if(mutationType == 2) {
//            System.out.println("---2---");
            mutated = changeWorkload();
        } else if(mutationType == 3) {
//            System.out.println("---3---");
            mutated = changeSubject();
        } else {
//            System.out.println("---4---");
            Boolean mutated2 = changeWorkload();
            mutated = changeSubject();
            mutated = (mutated || mutated2);
        }

        return mutated;
    }

    /**
     * Add a new subjectWorkload in a random period of the day.
     *
     * @param state
     * @param thread
     *
	 * @return 	<code>true</code>	if the subjectWorkload was added successfully.
     * 			<code>false</code>  otherwise.
     *
     * @see {@link State}
     * @see {@link SubjectWorkload}
     */
    public Boolean insertSubjectWorkload(EvolutionState state, final int thread) {
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
        if(periodOfTheDay == 0 && !contens(morning, subjectWorkload)) {
            this.morning.add(subjectWorkload);
        } else if (periodOfTheDay == 1 && !contens(afternoon, subjectWorkload)) {
            this.afternoon.add(subjectWorkload);
        } else if(!contens(night, subjectWorkload)) {
            this.night.add(subjectWorkload);
        } else {
            return Boolean.FALSE;
        }

        return Boolean.TRUE;
    }

    /**
     * Remove randomly one subjectWorkload from one period of the day.
     *
     * @return 	<code>true</code>	if the subjectWorkload was removed successfully.
     * 			<code>false</code> otherwise.
     */
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

	/**
     * Responsible for change a workload from a randomly subjectWorkload.
     *
     * @return 	<code>true</code>	if the workload was change successfully.
     * 			<code>false</code> 	otherwise.
     */
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
//            System.out.println("Not mutation workload");
            return Boolean.FALSE;
        }
        subjectWorkload.setWorkload(getWorkloadDifferentOf(subjectWorkload.getWorkload()));
//        System.out.println(((float)subjectWorkload.getWorkload()/(float)2)+"[M]");

        return Boolean.TRUE;
    }

    /**
     * Get a workload different of the workload passed by param.
     *
     * @param  workload
     * @return <code>int</code> the workload value. From 1 to 12
     */
    public int getWorkloadDifferentOf(int workload) {
        Random random = new Random();
        int workloadRandom = (random.nextInt(12) + 1);

        while(workloadRandom == workload) {
//            System.out.println(workload + " equals. Generating other..");
            workloadRandom = (random.nextInt(12) + 1);
        }

        return workloadRandom;
    }

    /**
     * Modify a random subject in a random period of the day.
     *
     * @return 	<code>true</code>	if the subject was change successfully.
     * 			<code>false</code> 	otherwise.
     */
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
 //           System.out.println("this gene don't have mutation");
            return Boolean.FALSE ;
        }

        Subject subjectRandom = getSubjectDifferentOf(subjectOld);
        subjectOld.setName(new String(subjectRandom.getName()));
        subjectOld.setId(subjectRandom.getId());
        subjectOld.setDificulty(subjectRandom.getDificulty());

//        subjectOld = getSubjectDifferentOf(subjectOld);
        String s = subjectOld.getName(); s += "[M]"; subjectOld.setName(s);
//        System.out.println(""+subjectOld.getName());

        return Boolean.TRUE;
    }

    /**
     * Get a Subject different of the subject passed by param.
     *
     * @param  workload
     * @return <code>int</code> the workload value. From 1 to 12
     *
     * @see {@link Subject}
     */
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

    /**
     * Get a new instance from a subject passed by param.
     *
     * @param  subject
     * @return <code>Subject</code> the new instance from subject.
     *
     * @see {@link Subject}
     */
    public Subject getNewSubjectInstance(Subject subject) {
        Subject subjectRandom = new Subject();
        subjectRandom.setName(new String(subject.getName()));
        subjectRandom.setId(subject.getId());
        subjectRandom.setDificulty(subject.getDificulty());
        return subjectRandom;
    }

    /**
     * Get a new instance from a arbitrary subjectWorkload.
     *
     * @param state
     * @param thread
     *
     * @return <code>SubjectWorkload</code> the new instance from subjectWorkload.
     *
     * @see {@link SubjectWorkload}
     * @see {@link ec.EvolutionState}
     */
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

    @Override
    public void reset(EvolutionState state, int thread) {
        Random random = new Random();
        //I can study max 32 subjects/day. But I need breaks too!
        //int maxSubjectsQuantity = random.nextInt(32);
        int maxSubjectsQuantity = random.nextInt(32);

        for(int i = 0; i < maxSubjectsQuantity; i++) {

            SubjectWorkload subjectWorkload = getNewSubjectWorkloadInstance(state, thread);

            int periodOfTheDay = random.nextInt(3); //random from 0 to 2
            if(periodOfTheDay == 0 && !contens(morning, subjectWorkload)) {
                this.morning.add(subjectWorkload);
            } else if (periodOfTheDay == 1 && !contens(afternoon, subjectWorkload)) {
                this.afternoon.add(subjectWorkload);
            } else if(!contens(night, subjectWorkload)) {
                this.night.add(subjectWorkload);
            }
        }
    }

    /**
     * Verify if the array of subjectWorkloads contens the subjectWorkload passed by param.
     *
     * @param  periodOfTheDay 	period of the day that to be searched.
     * @param  sw 				the subjectWorkload to be found.
     *
     * @return 	<code>true</code>	if found.
     * 			<code>false</code> 	otherwise.
     */
    public Boolean contens(ArrayList<SubjectWorkload> periodOfTheDay, SubjectWorkload sw) {

    	SubjectWorkload swToBeFind = sw;
        for (SubjectWorkload swActual : periodOfTheDay) {
            if(swActual.getSubject().getId() == swToBeFind.getSubject().getId()){
                return Boolean.TRUE;
            }
        }
        return Boolean.FALSE;
    }

    @Override
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
    	// stolen from GPIndividual.  It's a decent algorithm.
        int hash = this.getClass().hashCode();

		ArrayList<SubjectWorkload> subjectWorkloads = new ArrayList<SubjectWorkload>();
        subjectWorkloads.addAll(this.morning);
        subjectWorkloads.addAll(this.afternoon);
        subjectWorkloads.addAll(this.night);

        hash = ( hash << 1 | hash >>> 31 );
        for(int x = 0; x < subjectWorkloads.size(); x++) {
        	SubjectWorkload sw = subjectWorkloads.get(x);
            hash = ( hash << 1 | hash >>> 31 ) ^ (sw.getSubject().getId() + sw.getWorkload());
        }

        return hash;
    }

	/**
	* In reality Gene is not going to be very useful unless you at
	* least provide a way to describe the gene when it’s printed to a log.
	* The easiest way to do this is to override this method.
	*/
	@Override
	public String printGeneToStringForHumans() {
        ArrayList<SubjectWorkload> subjectWorkloads = new ArrayList<SubjectWorkload>();
        subjectWorkloads.addAll(this.morning);
        subjectWorkloads.addAll(this.afternoon);
        subjectWorkloads.addAll(this.night);

        String output = new String("");
        output += "" + ( "--------------------------\n" +
                        "number of subjectWorkloads: " + subjectWorkloads.size() +
                        "\n--------------------------\n");

        output += "" + ("\n[Morning] \n");
        for (SubjectWorkload subjectWorkload : morning) {
            float workload = ((float)subjectWorkload.getWorkload())/(float)2;
            output += "" + (subjectWorkload.getSubject().getName() + " " +
                                workload + "\n");
        }
        output += "" + ("\n[Afternoon] \n");
        for (SubjectWorkload subjectWorkload : afternoon) {
            float workload = ((float)subjectWorkload.getWorkload())/(float)2;
            output += "" + (subjectWorkload.getSubject().getName() + " " +
                                workload+ "\n");
        }
        output += "" + ("\n[Night] \n");
        for (SubjectWorkload subjectWorkload : night) {
            float workload = ((float)subjectWorkload.getWorkload())/(float)2;
            output += "" + (subjectWorkload.getSubject().getName() + " " +
                                workload+ "\n");
        }
        output += "--------------------------\n";

        return output;
    }

   	/**
    * Fill the Subjects with the input file
    *
    * @param  subjectsIn
    * @return <code>ArrayList<Subject></code> all input subjects.
    *
    * @see {@link java.util.ArrayList}
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
    * Convert a file to a vector of string.
    *
    * @param  file
    * @return <code>Vector<String></code> the file converted.
    *
    * @see {@link java.util.Vector}
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
    *
    * @param dayPlanGene
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


