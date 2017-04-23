/*
  Copyright 2013 by Victor Jatoba
  Licensed under the Academic Free License version 3.0
*/

package ec.app.aspga;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

import ec.EvolutionState;
import ec.app.aspga.bean.AspgaContext;
import ec.app.aspga.bean.Subject;
import ec.app.aspga.bean.SubjectWorkload;
import ec.util.Parameter;
import ec.vector.Gene;

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
	public static final int PERIOD_HOURS_LIMIT = 10;

	//Our allele is represented by the tree period of the day below.
    private SubjectWorkload[] morning;
    private SubjectWorkload[] afternoon;
    private SubjectWorkload[] night;

	private Subject[] subjects;

    @Override
    public void setup(final EvolutionState state, final Parameter base) {
    	super.setup(state, base);

        this.subjects = AspgaContext.getInstance().getSubjects();

        this.morning   = new SubjectWorkload[0];
        this.afternoon = new SubjectWorkload[0];
        this.night     = new SubjectWorkload[0];
    }

    @Override
    public void reset(EvolutionState state, int thread) {

        this.morning   = new SubjectWorkload[0];
        this.afternoon = new SubjectWorkload[0];
        this.night     = new SubjectWorkload[0];

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
    //TODO containsAndMore... or containsOrMore.. ?
    public boolean containsAndMoreThanFive(SubjectWorkload[] periodOfTheDay, SubjectWorkload sw) {
        return ( this.contains(periodOfTheDay, sw.getSubject()) ||
                 this.workloadExceeds(periodOfTheDay, sw.getWorkload()) );
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
    public boolean contains(SubjectWorkload[] periodOfTheDay, Subject subject) {
        for (SubjectWorkload swActual : periodOfTheDay) {
            if(swActual.getSubject().getId() == subject.getId()) {
                return true;
            }
        }

        return false;
    }


    @Override
    public boolean equals(Object other) {

        if( other == null ) return false;
        if(! ( other instanceof DayPlanGene ) ) return false;

        if( ((DayPlanGene)other).morning.length   != this.morning.length)   return false;
        if( ((DayPlanGene)other).afternoon.length != this.afternoon.length) return false;
        if( ((DayPlanGene)other).night.length     != this.night.length)     return false;

        for( int i = 0; i < this.morning.length; i++ ){
            if( this.morning[i].getSubject().getId() != ((DayPlanGene)other).morning[i].getSubject().getId() ) return false;
            if( this.morning[i].getWorkload() != ((DayPlanGene)other).morning[i].getWorkload() ) return false;
        }

        for( int i = 0; i < this.afternoon.length; i++ ){
            if( this.afternoon[i].getSubject().getId() != ((DayPlanGene)other).afternoon[i].getSubject().getId() ) return false;
            if( this.afternoon[i].getWorkload() != ((DayPlanGene)other).afternoon[i].getWorkload() ) return false;
        }

        for( int i = 0; i < this.night.length; i++ ){
            if( this.night[i].getSubject().getId() != ((DayPlanGene)other).night[i].getSubject().getId() ) return false;
            if( this.night[i].getWorkload() != ((DayPlanGene)other).night[i].getWorkload() ) return false;
        }

        return true;
    }

    @Override
    public void mutate(final EvolutionState state, final int thread) {
        int count = 0;
        while( !doMutate(state, thread) && ( count < 5 ) ) {
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

        int vectorsLength = this.morning.length + this.afternoon.length + this.night.length;
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
    public boolean insertSubjectWorkload(EvolutionState state, final int thread) {
        SubjectWorkload subjectWorkload = getNewSubjectWorkloadInstance(state, thread);
        int periodOfTheDay = state.random[thread].nextInt(3); //random from 0 to 2

        //Don't be permitted insert duplicated subjects
        if(periodOfTheDay == 0 && !containsAndMoreThanFive(this.morning, subjectWorkload)) {
            //System.out.println(subjectWorkload.getSubject().getName() + " inserted 1");
            this.morning = addElement( this.morning, subjectWorkload );
        }
        else if (periodOfTheDay == 1 && !containsAndMoreThanFive(this.afternoon, subjectWorkload)) {
            //System.out.println(subjectWorkload.getSubject().getName() + " inserted 2");
            this.afternoon = addElement( this.afternoon, subjectWorkload );
        }
        else if(!containsAndMoreThanFive(this.night, subjectWorkload)) {
            //System.out.println(subjectWorkload.getSubject().getName() + " inserted 3");
            this.night = addElement( this.night, subjectWorkload );
        }
        else {
            return false;
        }

        return true;
    }

    private SubjectWorkload[] addElement(SubjectWorkload[] array, SubjectWorkload element){
        array  = Arrays.copyOf(array, array.length + 1);
        array[array.length - 1] = element;
        return array;
    }

    /**
     * Remove randomly one subjectWorkload from one period of the day.
     *
     * @return
     *         <code>true</code>	if the subjectWorkload was removed successfully.<br/>
     *         <code>false</code>   otherwise.
     */
    public boolean removeSubjectWorkload(EvolutionState state, int thread) {
        int periodOfTheDay = state.random[thread].nextInt(3); //random from 0 to 2
        if(periodOfTheDay == 0 && !(this.morning.length == 0) ) {
            this.morning = removeElement( this.morning, state.random[thread].nextInt(morning.length));
        } else if (periodOfTheDay == 1 && !(this.afternoon.length == 0)) {
            this.afternoon = removeElement( this.afternoon, state.random[thread].nextInt(afternoon.length));
        } else if (!(this.night.length == 0)) {
            this.night = removeElement( this.night, state.random[thread].nextInt(night.length));
        } else {
            return false;
        }

        return true;
    }

    public static SubjectWorkload[] removeElement(SubjectWorkload[] input, int position) {
        List result = new LinkedList();

        for( int i = 0; i < input.length; i++ ){
            if( position != i ){
                result.add( input[i] );
            }
        }

        return (SubjectWorkload[])result.toArray(input);
    }

	/**
     * Responsible for change a workload from a randomly subjectWorkload.
     *
     * @return
     *         <code>true</code>	if the workload was change successfully.<br/>
     *         <code>false</code> 	otherwise.
     */
    public Boolean changeWorkload(EvolutionState state, int thread) {
        SubjectWorkload[] period = null;

        int periodOfTheDay = state.random[thread].nextInt(3); //random from 0 to 2
        if(periodOfTheDay == 0 && ( this.morning.length > 0 ) ) {
            period = this.morning;
        } else if (periodOfTheDay == 1 && ( this.afternoon.length > 0 )) {
            period = this.afternoon;
        } else if (this.night.length > 0) {
            period = this.night;
        } else {
            return false;
        }

        SubjectWorkload randomSubjectWorkload = period[(state.random[thread].nextInt(period.length))];
        byte workloadToBeChanged = (byte)getWorkloadDifferentOf(randomSubjectWorkload.getWorkload(), state, thread);

        if (!workloadExceeds(period, workloadToBeChanged)) {
            randomSubjectWorkload.setWorkload(workloadToBeChanged);
            //String s = randomSubjectWorkload.getSubject().getName(); s += "[M]"; randomSubjectWorkload.getSubject().setName(s);
        } else {
            return false;
        }

        return true;
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
    public boolean workloadExceeds(SubjectWorkload[] periodOfTheDay, int workload) {
        int workloadSum = 0;

        for (SubjectWorkload swActual : periodOfTheDay) {
            workloadSum += swActual.getWorkload();
        }

        if ((workloadSum + workload) > PERIOD_HOURS_LIMIT) {
            return true;
        }

        return false;
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

        SubjectWorkload[] period = null;
        Subject subjectOld = null;
        Boolean mutated = Boolean.TRUE;

        int periodOfTheDay = state.random[thread].nextInt(3); //random from 0 to 2
        if(periodOfTheDay == 0 && ( this.morning.length > 0 ) ) {
            subjectOld = this.morning[state.random[thread].nextInt(morning.length)].getSubject();
            period = this.morning;
        } else if (periodOfTheDay == 1 && ( this.afternoon.length > 0 ) ) {
            subjectOld = this.afternoon[state.random[thread].nextInt(afternoon.length)].getSubject();
            period = this.afternoon;
        } else if (this.night.length > 0) {
            subjectOld = this.night[state.random[thread].nextInt(night.length)].getSubject();
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
                subjectOld.setDifficulty(subjectRandom.getDifficulty());
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
        Subject subjectNew  = subjects[state.random[thread].nextInt(subjects.length)];
        int idRandom        = subjectNew.getId();
        int idOld           = subject.getId();

        if (subjects.length > 1) {
            while(idRandom == idOld) {
                subjectNew  = subjects[state.random[thread].nextInt(subjects.length)];
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
        subjectRandom.setDifficulty(subject.getDifficulty());

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
        Subject subject = subjects[state.random[thread].nextInt(subjects.length)];

        Subject subjectNew = getNewSubjectInstance(subject);
        //System.out.println("id1 " + subjectNew.getId() + " id2 " + subject.getId());

        byte workload = (byte)(state.random[thread].nextInt(PERIOD_HOURS_LIMIT) + 1); //random from 1 to 10

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
        int hash = ((Object) this).getClass().hashCode();

        hash = ( hash << 1 | hash >>> 31 );
        for(int x = 0; x < morning.length; x++) {
        	SubjectWorkload sw = morning[x];
            hash = ( hash << 1 | hash >>> 31 ) ^ (sw.getSubject().getId() + sw.getWorkload());
        }

        for(int x = 0; x < afternoon.length; x++) {
            SubjectWorkload sw = afternoon[x];
            hash = ( hash << 1 | hash >>> 31 ) ^ (sw.getSubject().getId() + sw.getWorkload());
        }

        for(int x = 0; x < night.length; x++) {
            SubjectWorkload sw = night[x];
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
        int size = morning.length + afternoon.length + night.length;

        String output = new String("");
        output += "" + ( "--------------------------\n" +
                        "number of subjectWorkloads: " + size +
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
    * Print all the classes filled
    *
    * @param dayPlanGene
    */
    public void printDayPlan(DayPlanGene dayPlanGene) {

        int size = morning.length + afternoon.length + night.length;

        System.out.println("-----------------------\nnumber of subjectWorkloads: " + size);

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

  	public SubjectWorkload[] getMorning() {
  		  return this.morning;
  	}

  	public SubjectWorkload[] getAfternoon() {
  		  return this.afternoon;
  	}

  	public SubjectWorkload[] getNight() {
  		  return this.night;
  	}

    public void setMorning(SubjectWorkload[] morning) {
        this.morning = morning;
    }

    public void setAfternoon(SubjectWorkload[] afternoon) {
        this.afternoon = afternoon;
    }

    public void setNight(SubjectWorkload[] night) {
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
