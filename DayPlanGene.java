/*
  Copyright 2013 by Victor Jatoba
  Licensed under the Academic Free License version 3.0
*/

package ec.app.aspga;

import ec.vector.Gene;
import ec.EvolutionState;
import ec.util.*;

import java.util.ArrayList;
import java.io.File;
import java.util.Vector;
import java.io.FileInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * The Gene that represent the individual genome. <br/>
 * In this class is implemented the mutation logic, 
 * the creation of the initial population and the method
 * used to compare two individuals.
 * 
 * @author Victor Jatoba
 * @version Wed Mar 12 02:29 2014
 */

@SuppressWarnings("serial")
public class DayPlanGene extends Gene {
    public static final String P_COURSEINFORMATION = "courseInformation";
	public static final int PERIOD_HOURS_LIMIT = 10;

	//Our allele is represented by the tree period of the day below.
	public ArrayList<SubjectWorkload> morning;
	public ArrayList<SubjectWorkload> afternoon;
	public ArrayList<SubjectWorkload> night;

	public ArrayList<Subject> subjects;
    public EvolutionState state;
    public int thread;
    public Parameter base;

    @Override
    public void setup(final EvolutionState state, final Parameter base) {
    	super.setup(state, base);

        File courseInformationInput = null;

        courseInformationInput = state.parameters.getFile(base.push(P_COURSEINFORMATION),null);

        if (courseInformationInput == null) {
            state.output.error("CourseInformation File doesn't exist", base.push(P_COURSEINFORMATION));
        }

        Vector<String> courseInformationVector = convertFileToVectorString(courseInformationInput);

        this.subjects = fillSubjects(courseInformationVector, state);

        this.morning = new ArrayList<SubjectWorkload>();
        this.afternoon = new ArrayList<SubjectWorkload>();
        this.night = new ArrayList<SubjectWorkload>();
    }

    @Override
    public void reset(EvolutionState state, int thread) {

        this.morning = new ArrayList<SubjectWorkload>();
        this.afternoon = new ArrayList<SubjectWorkload>();
        this.night = new ArrayList<SubjectWorkload>();

        //I can study max 32 subjects/day. But I need breaks too! So reduce for 30
        int maxSubjectsQuantity = state.random[thread].nextInt(30);
        //System.out.println("maxSubjectsQuantity: " + maxSubjectsQuantity);

        for(int i = 0; i < maxSubjectsQuantity; i++) {
            if(!insertSubjectWorkload(state, thread)) {
              //insertSubjectWorkload(state, thread);
            }
        }

        //System.out.println(printGeneToStringForHumans());
    }

    /**
     * Verify if the array of subjectWorkloads contains the subject passed by param.
     *
     * @param  periodOfTheDay   period of the day that to be searched.
     * @param  sw               the subjectWorkload to be found.
     *
     * @return
     *         <code>true</code>   if found.
     *         <code>false</code>  otherwise.
     */
    public Boolean containsAndMoreThanFive(ArrayList<SubjectWorkload> periodOfTheDay, SubjectWorkload sw) {

        Boolean constraint = Boolean.FALSE;

        if (this.contains(periodOfTheDay, sw.getSubject()) || this.workloadExceeds(periodOfTheDay, sw.getWorkload())) {
            constraint = Boolean.TRUE;
        }

        return constraint;
    }

    /**
     * Verify if the period contains the specific subject.
     *
     * @param  periodOfTheDay   period of the day to be verified.
     * @param  subject          subject to be found.
     *
     * @return
     *         <code>true</code>   if period contains the subject.
     *         <code>false</code>  otherwise.
     *
     */
    public Boolean contains(ArrayList<SubjectWorkload> periodOfTheDay, Subject subject) {
        Boolean contains = Boolean.FALSE;
        int subjectIdToBeFound = subject.getId();

        for (SubjectWorkload swActual : periodOfTheDay) {
            if(swActual.getSubject().getId() == subjectIdToBeFound) {
                contains = Boolean.TRUE;
            }
        }

        return contains;
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

    @Override
    public void mutate(final EvolutionState state, final int thread) {
        int count = 0;
        while(!doMutate(state, thread) && count <5) {
            count++;
        }
        //doMutate(state, thread);
    }

   /**
    * To make mutation in a random gene.
    *
    * @param  state  [description]
    * @param  thread [description]
    *
    * @return
    *         <code>true</code>   Case the mutation was done.<br/>
    *         <code>false</code>  otherwise.
    */
    public Boolean doMutate(EvolutionState state, final int thread) {

        Boolean mutated = Boolean.TRUE;

        int mutationType = state.random[thread].nextInt(3);
        //mutationType = 4;

        int vectorsLength = this.morning.size() + this.afternoon.size() + this.night.size();
        if(mutationType == 1 && vectorsLength != 0) {
//            System.out.println("---1---");
            mutated = changeWorkload(state, thread);
        } else if(mutationType == 2 && vectorsLength != 0) {
//            System.out.println("---2---");
            mutated = changeSubject(state, thread);
        }
        //If I don't have any SubjectWorkload, just add one.
/*      else if(mutationType == 0) {
//            System.out.println("---0---");
            mutated = insertSubjectWorkload(state, thread);
      } else if(mutationType == 3) {
//            System.out.println("---2---");
            mutated = removeSubjectWorkload(state, thread);
        }
        else {
            System.out.println("---3---");
            Boolean changeW = changeWorkload(state, thread);
            Boolean changeS = changeSubject(state, thread);
            mutated = (changeW || changeS);
        }
*/
        return mutated;
    }

    /**
     * Add a new subjectWorkload in a random period of the day.
     *
     * @param state
     * @param thread
     *
	 * @return
     *         <code>true</code>	if the subjectWorkload was added successfully. <br/>
     *         <code>false</code>  otherwise.
     *
     * @see EvolutionState
     * @see SubjectWorkload
     */
    public Boolean insertSubjectWorkload(EvolutionState state, final int thread) {

        Boolean addedSuccessfuly = Boolean.TRUE;

        SubjectWorkload subjectWorkload = getNewSubjectWorkloadInstance(state, thread);
        int periodOfTheDay = state.random[thread].nextInt(3); //random from 0 to 2
        //Don't be permitted insert duplicated subjects
        if(periodOfTheDay == 0 && !containsAndMoreThanFive(this.morning, subjectWorkload)) {
            //System.out.println(subjectWorkload.getSubject().getName() + " inserted 1");
            this.morning.add(subjectWorkload);
        } else if (periodOfTheDay == 1 && !containsAndMoreThanFive(this.afternoon, subjectWorkload)) {
            //System.out.println(subjectWorkload.getSubject().getName() + " inserted 2");
            this.afternoon.add(subjectWorkload);
        } else if(!containsAndMoreThanFive(this.night, subjectWorkload)) {
            //System.out.println(subjectWorkload.getSubject().getName() + " inserted 3");
            this.night.add(subjectWorkload);
        } else {
            addedSuccessfuly = Boolean.FALSE;
        }

        return addedSuccessfuly;
    }

    /**
     * Remove randomly one subjectWorkload from one period of the day.
     *
     * @return
     *         <code>true</code>	if the subjectWorkload was removed successfully.<br/>
     *         <code>false</code>   otherwise.
     */
    public Boolean removeSubjectWorkload(EvolutionState state, int thread) {

        int periodOfTheDay = state.random[thread].nextInt(3); //random from 0 to 2
        if(periodOfTheDay == 0 && !this.morning.isEmpty()) {
            this.morning.remove(state.random[thread].nextInt(morning.size()));
        } else if (periodOfTheDay == 1 && !this.afternoon.isEmpty()) {
            this.afternoon.remove(state.random[thread].nextInt(afternoon.size()));
        } else if (!this.night.isEmpty()) {
            this.night.remove(state.random[thread].nextInt(night.size()));
        } else {
            return Boolean.FALSE;
        }

        return Boolean.TRUE;
    }

	/**
     * Responsible for change a workload from a randomly subjectWorkload.
     *
     * @return
     *         <code>true</code>	if the workload was change successfully.<br/>
     *         <code>false</code> 	otherwise.
     */
    public Boolean changeWorkload(EvolutionState state, int thread) {
        ArrayList<SubjectWorkload> period = null;
        Boolean mutated = Boolean.TRUE;

        int periodOfTheDay = state.random[thread].nextInt(3); //random from 0 to 2
        if(periodOfTheDay == 0 && !this.morning.isEmpty()) {
            period = this.morning;
        } else if (periodOfTheDay == 1 && !this.afternoon.isEmpty()) {
            period = this.afternoon;
        } else if (!this.night.isEmpty()) {
            period = this.night;
        } else {
            mutated = Boolean.FALSE;
        }

        if (period != null) {
            SubjectWorkload randomSubjectWorkload = period.get(state.random[thread].nextInt(period.size()));
            int workloadToBeChanged = getWorkloadDifferentOf(randomSubjectWorkload.getWorkload(), state, thread);

            if (!workloadExceeds(period, workloadToBeChanged)) {
                randomSubjectWorkload.setWorkload(workloadToBeChanged);
                //String s = randomSubjectWorkload.getSubject().getName(); s += "[M]"; randomSubjectWorkload.getSubject().setName(s);
            } else {
                mutated = Boolean.FALSE;
            }
        }

        return mutated;
    }

    /**
     * Verify if the sum of the workload and the all workloads of the Subjects passed
     * by param don't exceeds more than five hours.
     *
     * @param  periodOfTheDay       The subjects and their workloads to be verified.
     * @param  workload             the workload to be verified.
     *
     * @return
     *         <code>true</code>   if the sum don't exceeds five hours. <br/>
     *         <code>false</code>  otherwise.
     */
    public Boolean workloadExceeds(ArrayList<SubjectWorkload> periodOfTheDay, int workload) {

        int workloadSum = 0;
        Boolean exceed = Boolean.FALSE;

        for (SubjectWorkload swActual : periodOfTheDay) {
            workloadSum += swActual.getWorkload();
        }

        if ((workloadSum + workload) > PERIOD_HOURS_LIMIT) {
            exceed = Boolean.TRUE;
        }

        return exceed;
    }

    /**
     * Get a workload different of the workload passed by param.
     *
     * @param  workload
     * @return <code>int</code> the workload value. From 1 to 10
     */
    public int getWorkloadDifferentOf(int workload, EvolutionState state, int thread) {
        int workloadRandom = (state.random[thread].nextInt(PERIOD_HOURS_LIMIT) + 1);

        while(workloadRandom == workload) {
//            System.out.println(workload + " equals. Generating other..");
            workloadRandom = (state.random[thread].nextInt(PERIOD_HOURS_LIMIT) + 1);
        }

        return workloadRandom;
    }

    /**
     * Modify a random subject in a random period of the day.
     *
     * @return
     *         <code>true</code>	if the subject was change successfully.<br/>
     *         <code>false</code> 	otherwise.
     */
    public Boolean changeSubject(EvolutionState state, int thread) {

        ArrayList<SubjectWorkload> period = null;
        Subject subjectOld = null;
        Boolean mutated = Boolean.TRUE;

        int periodOfTheDay = state.random[thread].nextInt(3); //random from 0 to 2
        if(periodOfTheDay == 0 && !this.morning.isEmpty()) {
            subjectOld = this.morning.get(state.random[thread].nextInt(morning.size())).getSubject();
            period = this.morning;
        } else if (periodOfTheDay == 1 && !this.afternoon.isEmpty()) {
            subjectOld = this.afternoon.get(state.random[thread].nextInt(afternoon.size())).getSubject();
            period = this.afternoon;
        } else if (!this.night.isEmpty()) {
            subjectOld = this.night.get(state.random[thread].nextInt(night.size())).getSubject();
            period = this.night;
        } else {
            mutated = Boolean.FALSE;
        }

        if (subjectOld != null) {
            //System.out.println("Old: " + subjectOld.getName() + " "+ subjectOld.getId());
            Subject subjectRandom = getSubjectDifferentOf(subjectOld, state, thread);
            if (!contains(period, subjectRandom)) {
                subjectOld.setName(new String(subjectRandom.getName()));
                //String s = subjectOld.getName(); s += "[-]"; subjectOld.setName(s);
                subjectOld.setId(subjectRandom.getId());
                subjectOld.setDificulty(subjectRandom.getDificulty());
                //System.out.println("OldNew: " + subjectOld.getName() + " "+ subjectOld.getId());
            } else {
                mutated = Boolean.FALSE;
            }
        }

        return mutated;
    }

    /**
     * Get a Subject different of the subject passed by param.
     *
     * @param  subject
     * @param  state
     * @param  thread
     *
     * @return <code>Subject</code> the new instance.
     *
     * @see Subject
     */
    public Subject getSubjectDifferentOf(Subject subject, EvolutionState state, int thread) {
        Subject subjectNew  = subjects.get(state.random[thread].nextInt(subjects.size()));
        int idRandom        = subjectNew.getId();
        int idOld           = subject.getId();

        if (subjects.size() > 1) {
            while(idRandom == idOld) {
                subjectNew  = subjects.get(state.random[thread].nextInt(subjects.size()));
                idRandom    = subjectNew.getId();
            }
        }
        //System.out.println("" + subject.getName() + " " + subjectNew.getName());
        return getNewSubjectInstance(subjectNew);
    }

    /**
     * Get a new instance from a subject passed by param.
     *
     * @param  subject
     * @return <code>Subject</code> the new instance from subject.
     *
     * @see Subject
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
     * @see SubjectWorkload
     * @see ec.EvolutionState
     */
    public SubjectWorkload getNewSubjectWorkloadInstance(EvolutionState state, int thread) {
        Subject subject = subjects.get(state.random[thread].nextInt(subjects.size()));

        Subject subjectNew = getNewSubjectInstance(subject);
        //System.out.println("id1 " + subjectNew.getId() + " id2 " + subject.getId());

        int workload = (state.random[thread].nextInt(PERIOD_HOURS_LIMIT) + 1); //random from 1 to 10

        SubjectWorkload subjectWorkload = new SubjectWorkload();
        subjectWorkload.setSubject(subjectNew);
        subjectWorkload.setWorkload(workload);
        return subjectWorkload;
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
        subjectWorkloads.addAll(morning);
        subjectWorkloads.addAll(afternoon);
        subjectWorkloads.addAll(night);

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

        return output;
    }

    @Override
    public String toString() {
    	return printGeneToStringForHumans();
    }

   	/**
    * Fill the Subjects with the input file.
    *
    * @param  subjectsIn [description]
    * @param  state      [description]
    *
    * @return            the list of subjects standardized.
    */
    public ArrayList<Subject> fillSubjects(Vector<String> subjectsIn, EvolutionState state) {
        ArrayList<Subject> subjects = new ArrayList<Subject>();

        if (subjectsIn != null) {
            int i = 0;
            for (String line: subjectsIn) {
                String[] subjectDificulty = line.split(" ");

                Subject subject = new Subject();
                subject.setName(subjectDificulty[0]);
                String dificultyStr = subjectDificulty[1];
                int dificulty = Integer.parseInt(dificultyStr);

                //int dificulty = Character.getNumericValue(dificultyChar);
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
    * @see java.util.Vector
    */
    public Vector<String> convertFileToVectorString(File file) {

        FileInputStream inputStream = null;
        BufferedReader br           = null;
        Vector<String> lines 		= new Vector<String>();

        try {

            inputStream = new FileInputStream(file);

            br = new BufferedReader(new InputStreamReader(inputStream));

            //StringBuilder sb = new StringBuilder();

            String line;

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

  	public ArrayList<SubjectWorkload> getMorning() {
  		  return this.morning;
  	}

  	public ArrayList<SubjectWorkload> getAfternoon() {
  		  return this.afternoon;
  	}

  	public ArrayList<SubjectWorkload> getNight() {
  		  return this.night;
  	}

    public void setMorning(ArrayList<SubjectWorkload> morning) {
        this.morning = morning;
    }

    public void setAfternoon(ArrayList<SubjectWorkload> afternoon) {
        this.afternoon = afternoon;
    }

    public void setNight(ArrayList<SubjectWorkload> night) {
        this.night = night;
    }

    /**
    *   Print in the console the lines of the input.
    */
    public void printInputLines(Vector<String> lines, String type) {

        System.out.println("\n------------"+ type +"---------------");

        for (String lineIt: lines) {
            System.out.println(lineIt);
            System.out.println();
        }

        System.out.println("----------------Done!-------------------");

    }

}
