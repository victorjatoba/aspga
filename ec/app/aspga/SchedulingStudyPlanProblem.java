/*
  Copyright 2014 by Victor Jatoba
  Licensed under the Academic Free License version 3.0
*/

package ec.app.aspga;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import ec.EvolutionState;
import ec.Individual;
import ec.Problem;
import ec.app.aspga.bean.AspgaContext;
import ec.app.aspga.bean.Period;
import ec.app.aspga.bean.PeriodAvailable;
import ec.app.aspga.bean.Student;
import ec.app.aspga.bean.Subject;
import ec.app.aspga.bean.SubjectWorkload;
import ec.simple.SimpleFitness;
import ec.simple.SimpleProblemForm;
import ec.util.Parameter;

//Data packages
//Java Packages

/**
 * The main evolutionary class. This, is implemented
 * all the evolution logic (The fitness function)
 * to scoring the individuals of the
 * actual population.
 *
 * @author Victor Jatoba
 * @version Fri Mar 14 03:26 2014
 */
public class SchedulingStudyPlanProblem extends Problem implements SimpleProblemForm
{
    private static final long serialVersionUID = 1;

    private static final boolean LOCAL_DEBUG = false;

    public static final char BIG =      'B';
    public static final char SMALL =    'S';
    public static final char NOTHING =  'N';

    public static final char GOOD =     'G';
    public static final char MEDIUM =   'M';
    public static final char BAD =      'B';

    public static final char HARD =     'H';
    public static final char EASY =     'E';

    public static final char NONE =     'N';

    Subject[] subjects;
    Student student;
    PeriodAvailable dayPeriodAvailable;
    PeriodAvailable intelectualAvailable;

    /**
     * Responsible to read the params and fill them attributes.
     *
     * @param state [description]
     * @param base  [description]
     *
     * @see EvolutionState
     * @see Parameter
     */
    public void setup (  final EvolutionState state, final Parameter base) {
        if( LOCAL_DEBUG ){
            System.out.println("SchedulingStudyPlanProblem, setup");
        }

        AspgaContext context = AspgaContext.getInstance();

        this.subjects               = context.getSubjects();
        this.student                = context.getStudent();
        this.dayPeriodAvailable     = context.getDayPeriodAvailable();
        this.intelectualAvailable   = context.getIntelectualAvailable();
    }

    /**
     * Implement the logic of the evaluete process.
     *
     * @param state         [description]
     * @param ind           [description]
     * @param subpopulation [description]
     * @param threadnum     [description]
     *
     * @see EvolutionState
     * @see Individual
     * @see DayPlanGeneVectorIndividual
     * @see SimpleFitness
     */
    public void evaluate(   final EvolutionState state,
                            final Individual ind,
                            final int subpopulation,
                            final int threadnum) {

        if( LOCAL_DEBUG ){
            System.out.println("SchedulingStudyPlanProblem, evaluate");
        }

        if (ind.evaluated) return;

        if (!(ind instanceof DayPlanGeneVectorIndividual)) {
            state.output.fatal("Whoa!  It's not a DayPlanGeneVectorIndividual!!!",null);
        }

        DayPlanGeneVectorIndividual individual = (DayPlanGeneVectorIndividual)ind;

        if (!(individual.fitness instanceof SimpleFitness)) {
            state.output.fatal("Whoa!  It's not a SimpleFitness!!!",null);
        }

        float fitnessValue = calculateFitnessValue(individual);

        ((SimpleFitness)individual.fitness).setFitness(state,
            /// ...the fitness...
            fitnessValue,
            ///... is the individual ideal?  Indicate here...
            (fitnessValue == 200.0f));

        individual.evaluated = true;
    }

    /**
     * Responsible for to calculate the fitness value of the individual
     * in related of the contraints below. <br/>
     *
     * <br/>Constraints classification: <br/>
     * <br/>FIXED: <br/>
     *     {@link SchedulingStudyPlanProblem#allocateAllSubjects} <br/>
     *     {@link SchedulingStudyPlanProblem#subjectInInappropriatePeriod} <br/>
     *
     * <br/>HARD: <br/>
     *     {@link SchedulingStudyPlanProblem#fillPeriodsAvailable} <br/>
     *     {@link SchedulingStudyPlanProblem#hardSubjectInEasyPeriod} <br/>
     *     {@link SchedulingStudyPlanProblem#subjectMoreDificultyNeedMoreTime} <br/>
     *     {@link SchedulingStudyPlanProblem#haveDifferentDayPlans} <br/>
     *
     * <br/>SOFT: <br/>
     *     {@link SchedulingStudyPlanProblem#hoursToLeisure} <br/>
     *     {@link SchedulingStudyPlanProblem#notWasteAllTimeInTheSameSubject} <br/>
     *     {@link SchedulingStudyPlanProblem#toStudyGradually} <br/>
     *
     * @param  individual   the curent individual to be calculated.
     *
     * @return <code>float</code>   the fitness value.
     *
     * @see DayPlanGeneVectorIndividual
     */
    public float calculateFitnessValue(DayPlanGeneVectorIndividual individual) {
        //float maxSix = maxSixHoursPerPeriod(individual);
        if( LOCAL_DEBUG ){
            System.out.println("SchedulingStudyPlanProblem, calculateFitnessValue");
        }

        float allocateAll = allocateAllSubjects(individual);
        float inappropriatePeriod = subjectInInappropriatePeriod(individual);

        float fillPeriods = fillPeriodsAvailable(individual);
        float hardSubject = hardSubjectInEasyPeriod(individual);
        float needMoreTime = subjectMoreDificultyNeedMoreTime(individual);
        float differentPlans = haveDifferentDayPlans(individual);

        float leisure = hoursToLeisure(individual);
        float maxHour = notWasteAllTimeInTheSameSubject(individual);
//        float gradually = toStudyGradually(individual);

        float fixed = (allocateAll + inappropriatePeriod) / 2;
        float hard = (fillPeriods + needMoreTime ) / 2;
        float soft = (leisure + maxHour + differentPlans + hardSubject) / 4;

        float fitness = fixed + (hard * 0.6f) + (soft * 0.3f);

        return fitness;
    }

    /**
     * I really need to study all subjects that I put in my plan. <br/>
     *
     * <br/>Classification: Fixed <br/>
     *
     * @param individual    the current individual to be verified.
     *
     * @return <code>float</code>   the acumulative value.
     *
     * @see ec.app.aspga.bean.SubjectWorkload
     * @see Subject
     * @see DayPlanGene
     * @see DayPlanGeneVectorIndividual
     */
    public float allocateAllSubjects(DayPlanGeneVectorIndividual individual) {
        if( LOCAL_DEBUG ){
            System.out.println("SchedulingStudyPlanProblem, alocateAllSubjects");
        }

        float acumulativeValue = 0;

        if( individual.size() > 0 ){
            Subject[] subjectsAlreadyCounted = new Subject[0];

            for (int i = 0; i < individual.size(); i++) {
                for( SubjectWorkload sw : ( (DayPlanGene) individual.genome[i] ).getMorning() ){
                    subjectsAlreadyCounted = addElement( subjectsAlreadyCounted, sw.getSubject() );
                }
                for( SubjectWorkload sw : ( (DayPlanGene) individual.genome[i] ).getAfternoon() ){
                    subjectsAlreadyCounted = addElement( subjectsAlreadyCounted, sw.getSubject() );
                }
                for( SubjectWorkload sw : ( (DayPlanGene) individual.genome[i] ).getNight() ){
                    subjectsAlreadyCounted = addElement( subjectsAlreadyCounted, sw.getSubject() );
                }
            }

            int qttSubjects  = subjectsAlreadyCounted.length;
            acumulativeValue = getAcumulativeValueByAlocateAll(qttSubjects);
        }

        return acumulativeValue;
    }

    private Subject[] addElement(Subject[] array, Subject element){
        boolean isAdded = false;
        for(Subject subject : array){
            if( subject.getId() == element.getId() ){
                isAdded = true;
                break;
            }
        }

        if( isAdded == false ){
            array  = Arrays.copyOf(array, array.length + 1);
            array[array.length - 1] = element;
        }

        return array;
    }

    /**
     * Get the correct acumulativeValue in relation of
     * the quantity of subjects was alocated in the plan.
     *
     * @param  qttSubjectsFound [description]
     *
     * @return <code>float</code>   the acumulative value.
     */
    public float getAcumulativeValueByAlocateAll(int qttSubjectsFound) {
        if( LOCAL_DEBUG ){
            System.out.println("SchedulingStudyPlanProblem, getAcumulativeValueByAlocateAll");
        }

//        int qttSubjects = subjects.size();
//        float percent = (qttSubjectsFound*100) / subjects.size();
        return (qttSubjectsFound*100) / subjects.length;
    }

//    /**
//     * Verify if the array of the subjects contains
//     * the subject passed by param.
//     *
//     * @param  subjects [description]
//     * @param  subject  [description]
//     *
//     * @return          [description]
//     */
//    public Boolean contains(ArrayList<Subject> subjects, Subject subject) {
//        System.out.println("SchedulingStudyPlanProblem, contains");
//
//        for (Subject s: subjects) {
//            if (s.getId() == subject.getId()) {
//                return Boolean.TRUE;
//            }
//        }
//
//        return Boolean.FALSE;
//    }

    /**
     * Subjects that contains more dificulty needs more
     * time to study. <br/>
     *
     * <br/>Classification: Hard Constraint. <br/>
     *
     * @param individual    the current individual to be verified.
     *
     * @return <code>float</code>   the acumulative value.
     *
     * @see Subject
     */
    public float subjectMoreDificultyNeedMoreTime(DayPlanGeneVectorIndividual individual) {
        if (LOCAL_DEBUG) {
            System.out.println("SchedulingStudyPlanProblem, subjectMoreDificultyNeedMoreTime");
        }

        float acumulativeValue = 0;

        int sumAllDificulties = getSumAllDifficulty();
        int qttHoursAvailable = getQttHoursAvailable(individual);
        for (Subject sub : subjects) {
            int dificultyPercent = getDificultyPercent(sub.getDifficulty(), sumAllDificulties);
            float hoursIdeal = getQttIdealOfHours(qttHoursAvailable, dificultyPercent);
            float hoursReal = getQttRealOfHours(individual, sub);
            acumulativeValue += getAcumulativeValueByWasteHours(hoursIdeal, hoursReal);
        }

        return acumulativeValue / subjects.length;
    }

    /**
     * Get the percent of the real hours of the subject in relation
     * of the ideal hours that this subject should be.
     *
     * @param  hoursIdeal    the quantity of ideal hours to be used by the subject analysed.
     * @param  hoursReal     the quantity of real hours used by the subject analysed.
     *
     * @return <code>float</code>   the percent of proximity between the params.
     */
    public float getAcumulativeValueByWasteHours(float hoursIdeal, float hoursReal) {
        if( LOCAL_DEBUG ){
            System.out.println("SchedulingStudyPlanProblem, getAcumulativeValueByWasteHours");
        }

        float percentOfWasteHours = 0;
        if (hoursIdeal != 0) {
            float hoursIdealWithMinuts = hoursIdeal*2;
            percentOfWasteHours = (hoursReal*100) / hoursIdealWithMinuts;
            if (hoursReal > hoursIdealWithMinuts) {
                float cent = percentOfWasteHours - 100;
                percentOfWasteHours = (cent < 100) ? (100 - cent) : 0;
            }
        }

        //System.out.println("real: " + hoursReal + " ideal: " + hoursIdeal*2 + " perc: " + percentOfWasteHours);
        return percentOfWasteHours;
    }

    /**
     * Get the sum of the all workload of the subject in the
     * all study plan.
     *
     * @param  individual   the study plan.
     * @param  subject      the subject to be found.
     *
     * @return            [description]
     */
    public int getQttRealOfHours(DayPlanGeneVectorIndividual individual, Subject subject) {
        if( LOCAL_DEBUG ){
            System.out.println("SchedulingStudyPlanProblem, getQttRealOfHours");
        }

//        int individualLength = (int)individual.size();
        int qttHoursTotal = 0;
//        int subjectId = subject.getId();

        for (int i = 0; i < individual.size(); i++) {
//            DayPlanGene gene = (DayPlanGene) individual.genome[i];

            qttHoursTotal += getWorkloadOfTheSubject(((DayPlanGene) individual.genome[i]).getMorning(), subject.getId());
            qttHoursTotal += getWorkloadOfTheSubject(((DayPlanGene) individual.genome[i]).getAfternoon(), subject.getId());
            qttHoursTotal += getWorkloadOfTheSubject(((DayPlanGene) individual.genome[i]).getNight(), subject.getId());
        }

        return qttHoursTotal;
    }

    /**
     * Get the workload of the subject passed by param.
     *
     * @param  subjectWorkloads     the set of subjectWorkloads.
     * @param  subjectId            the id of the subject to be found.
     *
     * @return the workload or zero otherwise.
     */
    public int getWorkloadOfTheSubject(SubjectWorkload[] subjectWorkloads, int subjectId) {
        if( LOCAL_DEBUG ){
            System.out.println("SchedulingStudyPlanProblem, getWorkloadOfTheSubject");
        }

        for (SubjectWorkload sw : subjectWorkloads) {
            if (sw.getSubject().getId() == subjectId) {
                //is not permited one period has duplicated subjects. So just return if found.
                return sw.getWorkload();
            }
        }

        return 0;
    }

    /**
     *
     * @return [description]
     */
    /**
     * Get the total of ideal hours to study a subject
     * with its dificulty percent.
     *
     * @param  qttHoursAvailable    the total of hours available to study.
     * @param  percent              the percent of the subject dificulty.
     *
     * @return <code>float</code>   the percent of proximity between the params.
     */
    public int getQttIdealOfHours(int qttHoursAvailable, int percent) {
        if( LOCAL_DEBUG ){
            System.out.println("SchedulingStudyPlanProblem, getQttIdealOfHours");
        }

        int qttIdealOfHous = (qttHoursAvailable * percent) / 100;
        //System.out.println("qttHA: " + qttHoursAvailable + " percent: " + percent);

        return qttIdealOfHous;
    }

    /**
     * get the quantity of hours available in all study plan.
     *
     * @param  individual   the study plan to be get the qtt of hours available.
     *
     * @return the value of the hours available.
     *
     * @see DayPlanGeneVectorIndividual
     */
    public int getQttHoursAvailable(DayPlanGeneVectorIndividual individual) {
        if( LOCAL_DEBUG ){
            System.out.println("SchedulingStudyPlanProblem, getQttHoursAvailable");
        }

        int individualLength = (int)individual.size();
        Period[] studyCycle = dayPeriodAvailable.getStudyCycle();
        int studyCycleSize = studyCycle.length;
        int cycleIt = 0;
        int qttNothingPeriods = 0;
        int qttSmallPeriods = 0;
        for (int i = 0; i < individualLength; i++) {
            Period period = studyCycle[cycleIt];
            if (period.getMorning() == NOTHING) {
                qttNothingPeriods++;
            } else if (period.getMorning() == SMALL) {
                qttSmallPeriods++;
            }

            if (period.getAfternoon() == NOTHING) {
                qttNothingPeriods++;
            } else if (period.getMorning() == SMALL) {
                qttSmallPeriods++;
            }

            if (period.getNight() == NOTHING) {
                qttNothingPeriods++;
            } else if (period.getMorning() == SMALL) {
                qttSmallPeriods++;
            }

            cycleIt++;
            if(cycleIt == studyCycleSize) {
                cycleIt = 0;
            }
        }

        int qttHoursAvailable = (individualLength * 3) - (qttNothingPeriods + (qttSmallPeriods/2));
        qttHoursAvailable *= 5; //qtt hours per period.
        //System.out.println("avai: " + qttHoursAvailable + " noth: " + qttNothingPeriods);
        return qttHoursAvailable;
    }

    /**
     * Get dificulty percent in relation of the sum of
     * all others dificulties.
     *
     * @param  dificulty        dificulty to get the sum.
     * @param  dificultySum     sum of all dificulty.
     *
     * @return the dificulty percent in relation of the params.
     */
    public int getDificultyPercent(int dificulty, int dificultySum) {
        if( LOCAL_DEBUG ){
            System.out.println("SchedulingStudyPlanProblem, getDificultyPercent");
        }

        int percent = (dificulty*100) / dificultySum;

        return percent;
    }

    /**
     * Get the sum of all dificulty of the subjects.
     *
     * @return the sum.
     */
    public int getSumAllDifficulty() {
        int dificultySum = 0;

        for (Subject sub: subjects) {
            dificultySum += sub.getDifficulty();
        }

        return dificultySum;
    }

    /**
    * Check if the study plan have grow-up learn. To do this,
    * verify the quantity of the medium difficulty subjects exist
    * in the begin of the plan. After verify the hard and finally
    * the easy difficulty subjects.Then placing one percent that
    * depends the quantity it was found. <br/>
    *
    * <br/>Classification: Hard Constraint. <br/>
    *
    * @param individual    the current individual to be verified.
    *
    * @return <code>float</code>   the acumulative value.
    *
    * @see SubjectWorkload
    * @see DayPlanGene
    * @see DayPlanGeneVectorIndividual
    */
    public float toStudyGradually(DayPlanGeneVectorIndividual individual) {
        if( LOCAL_DEBUG ){
            System.out.println("SchedulingStudyPlanProblem, toStudyGradually");
        }

        int acumulativeValue = 0;
        int qtdPerids = (int)individual.size();

        List<SubjectWorkload[]> allPeriods = new ArrayList<>();
        SubjectWorkload[] subjectWorkloads;

        Vector<Boolean> periodsEmpty = new Vector<>();

        for (int i = 0; i <  qtdPerids*3; i++) {
            periodsEmpty.add(false);
        }

        for (int i = 0; i < qtdPerids; i++) {
            DayPlanGene gene = (DayPlanGene) individual.genome[i];

            subjectWorkloads = gene.getMorning();
            if (subjectWorkloads.length > 0) {
                allPeriods.add(subjectWorkloads);

            } else {
                periodsEmpty.set(i*3, true);
            }

            subjectWorkloads = gene.getAfternoon();
            if (subjectWorkloads.length > 0) {
                allPeriods.add(subjectWorkloads);
            } else {
                periodsEmpty.set((i*3)+1, true);
            }

            subjectWorkloads = gene.getNight();
            if (subjectWorkloads.length > 0) {
                allPeriods.add(subjectWorkloads);
            } else {
                periodsEmpty.set((i*3)+2, true);
            }
        }

        if (allPeriods.size() != 0) {
            int amountSWByPeriod = 0;

            int countInit   = 0;
            int countFinal  = qtdPerids - countEmptyByPeriod(MEDIUM, periodsEmpty);
            int countMedium = countSubjectsDifficultyBetween(allPeriods, countInit, countFinal, MEDIUM);
            amountSWByPeriod = countSWByPeriod(allPeriods, countInit, countFinal);
            acumulativeValue += countAcumulativeValueByDifficulty(countMedium, amountSWByPeriod);

            countInit   = countFinal;
            countFinal  += qtdPerids - countEmptyByPeriod(HARD, periodsEmpty);
            int countHard   = countSubjectsDifficultyBetween(allPeriods, countInit, countFinal, HARD);
            amountSWByPeriod = countSWByPeriod(allPeriods, countInit, countFinal);
            acumulativeValue += countAcumulativeValueByDifficulty(countHard, amountSWByPeriod);

            countInit   = countFinal;
            countFinal  += qtdPerids - countEmptyByPeriod(EASY, periodsEmpty);
            int countEasy   = countSubjectsDifficultyBetween(allPeriods, countInit, countFinal, EASY);
            amountSWByPeriod = countSWByPeriod(allPeriods, countInit, countFinal);
            acumulativeValue += countAcumulativeValueByDifficulty(countEasy,  amountSWByPeriod);
        }

        //System.out.println(((float)acumulativeValue/3f));
        return acumulativeValue/3f;
    }

    /**
     * Count a number of SubjectWorkloads (SW) that exist
     * in the Array of the periods (Array of SW) by init and final indices.
     *
     * @param  allPeriods       the Array of periods to be counted.
     * @param  init             the first position.
     * @param  end              the last position.
     *
     * @return the number of SW found.
     *
     * @see SubjectWorkload
     * @see ArrayList
     */
    public int countSWByPeriod(List<SubjectWorkload[]> allPeriods, int init, int end) {
        if( LOCAL_DEBUG ){
            System.out.println("SchedulingStudyPlanProblem, countSWByPeriod");
        }

        int amountSWByPeriod = 0;
        for (int i = init; i < end; i++) {
            amountSWByPeriod += allPeriods.get(i).length;
        }

        return amountSWByPeriod;
    }

    /**
     * Count the number of 1 exist in the especific period
     * of the dificulty. <br/>
     *
     * <br/>Obs.: 1 signific that exist one period of the day empty.
     * In the other words, this period not contains SubjectWorkloads.
     *
     * @param  period           {MEDIUM, HARD or EASY}
     * @param  periodsEmpty     the vector of empty periods.
     *
     * @return              the number of empty periods.
     */
    public int countEmptyByPeriod(char period, Vector<Boolean> periodsEmpty) {
        if( LOCAL_DEBUG ){
            System.out.println("SchedulingStudyPlanProblem, countEmptyByPeriod");
        }

        int posInit, posFinal;
        int qtdPerids = periodsEmpty.size()/3;

        if (period == MEDIUM) {
            posInit = 0;
            posFinal = qtdPerids;
        } else if (period == HARD) {
            posInit = qtdPerids;
            posFinal = qtdPerids*2;
        } else {
            posInit = qtdPerids*2;
            posFinal = qtdPerids*3;
        }

        int countEmpty = 0;
        for (int i = posInit; i < posFinal; i++) {
            if (periodsEmpty.get(i)) {
                countEmpty++;
            }
        }

        //System.out.println("period " + period + " countEmpty: " + countEmpty + " i " + posInit + " f " + posFinal);
        return countEmpty;
    }

    /**
     * Search if the amountFound is the same of the total or
     * is the majority.
     *
     * @param  amountFound  the amount to be compair.
     * @param  total        the total to be compair.
     *
     * @return <code>float</code> the percent of proximity between the params.
     */
    public float countAcumulativeValueByDifficulty(int amountFound, int total) {
        if( LOCAL_DEBUG ){
            System.out.println("SchedulingStudyPlanProblem, countAcumulativeValueByDifficulty");
        }

        float acumulativeValue = (total != 0) ? (amountFound*100)/total : 0;
        //System.out.println("am: " + amountFound + " tl: " + total + " ac: " + acumulativeValue);
        return acumulativeValue;
    }

    /**
     * Responsible to count the number of difficulty type
     * that the array of the subjects contains from a especific
     * period of index (from init position to end position).
     *
     * @param  allPeriods       the periods to be counted.
     * @param  init             the position of the first subjectWorkload.
     * @param  end              the position of the last subjectWorkload.
     * @param  difficultyType   the type to be compared.
     *
     * @return the number of difficulty type that allPeriods contain.
     *
     * @see SubjectWorkload
     */
    public int countSubjectsDifficultyBetween(List<SubjectWorkload[]> allPeriods, int init, int end, char difficultyType) {
        if( LOCAL_DEBUG ){
            System.out.println("SchedulingStudyPlanProblem, countSubjectsDifficultyBetween");
        }

        int countDifficulty = 0;

        int a, b;
        if (difficultyType == MEDIUM) {
            a = 20;
            b = 80;
        } else if (difficultyType == HARD) {
            a = 80;
            b = 101;
        } else {
            a = 0;
            b = 20;
        }

        for (int i = init; i < end; i++) {
            for (SubjectWorkload sw: allPeriods.get(i)) {
                if(isBetween(sw.getSubject().getDifficulty(), a, b)) {
                    //System.out.println("name: " + sw.getSubject().getName() + " dif: " + sw.getSubject().getDificulty());
                    countDifficulty++;
                }
            }
        }

        //System.out.println("found: " + countDifficulty + "  i: " + init + " f: " + end);
        return countDifficulty;
    }

    /**
     * verify if the first number is between the
     * second (included) and third (exclude) number.
     *
     *
     * @param  verify  the number to be verified.
     * @param  a       from (included).
     * @param  b       to (excluded).
     *
     * @return  <code>true</code>   if is between.
     *          <code>false</code>  otherwise.
     */
    public boolean isBetween(int verify, int a, int b) {
        if( LOCAL_DEBUG ){
            System.out.println("SchedulingStudyPlanProblem, isBetween");
        }
        return (verify >= a && verify < b);
    }

    /**
    * Hard Subjects should be alocated in the period of the day that the user
    * have more intelectual facility for to learn.
    *
    * <br/>Classification: Hard Constraint. <br/>
    *
    * @param individual    the current individual to be verified.
    *
    * @return <code>float</code>   the acumulative value.
    *
    * @see SubjectWorkload
    * @see DayPlanGene
    * @see DayPlanGeneVectorIndividual
    * @see Period
    */
    public float hardSubjectInEasyPeriod(DayPlanGeneVectorIndividual individual) {
        if( LOCAL_DEBUG ){
            System.out.println("SchedulingStudyPlanProblem, hardSubjectInEasyPeriod");
        }

        long individualLength = individual.size();
        int cycleIt = 0;
        Period[] studyCycle = this.intelectualAvailable.getStudyCycle();
        int studyCycleSize = studyCycle.length;
        int acumulativeValue = 0;
        int qtdPeriodsAvailable = 0;

        char periodAvailable;

        SubjectWorkload[] subjectWorkloads;
        for (int i = 0; i < individualLength; i++) {
            DayPlanGene gene = (DayPlanGene) individual.genome[i];
            Period period = studyCycle[cycleIt];

            //Morning
            subjectWorkloads = gene.getMorning();
            periodAvailable = period.getMorning();
            if (subjectWorkloads.length > 0) {
                qtdPeriodsAvailable++;
                acumulativeValue += getPeriodAcumulativeValue(periodAvailable, subjectWorkloads);
            }

            //Afternoon
            subjectWorkloads = gene.getAfternoon();
            periodAvailable = period.getAfternoon();
            if (subjectWorkloads.length > 0) {
                qtdPeriodsAvailable++;
                acumulativeValue += getPeriodAcumulativeValue(periodAvailable, subjectWorkloads);
            }

            //Night
            subjectWorkloads = gene.getNight();
            periodAvailable = period.getNight();
            if (subjectWorkloads.length > 0) {
                qtdPeriodsAvailable++;
                acumulativeValue += getPeriodAcumulativeValue(periodAvailable, subjectWorkloads);
            }

            cycleIt++;
            if(cycleIt == studyCycleSize) {
                cycleIt = 0;
            }
        }

        float total = 0.0f; //exist one individual that don't have genes in their genome.
        if (qtdPeriodsAvailable != 0) {
            total = (float)acumulativeValue / (float)qtdPeriodsAvailable;
        }
        //System.out.println("acumulativeValue: " + acumulativeValue + " qtdPeriodsAvailable: " + qtdPeriodsAvailable + " Total: " + total);

        return total;
    }

    /**
     * Get the acumulativeValue of the period passed by param.
     *
     * @param  periodAvailable      the period (M, A, N).
     * @param  subjectWorkloads     the set of subjectWorkloads.
     *
     * @return  <code>int</code>    the acumulativeValue.
     */
    public int getPeriodAcumulativeValue(char periodAvailable, SubjectWorkload[] subjectWorkloads) {
        if( LOCAL_DEBUG ){
            System.out.println("SchedulingStudyPlanProblem, hardSubjectInEasyPeriod");
        }

        int dificultySum = 0;
        for (SubjectWorkload sw: subjectWorkloads) {
            //System.out.println(sw.getSubject().getName());
            dificultySum += sw.getSubject().getDifficulty();
        }
        float dificultyAverage = dificultySum / subjectWorkloads.length;

        int acumulativeValue = getAcumulativeValueByDificulty(periodAvailable, dificultyAverage, subjectWorkloads);

        return acumulativeValue;
    }

    /**
     * Return the acumulativeValue from the period using the
     * table classification below.
     *
     * The subject classification by difficulty:
     *      00 >= n < 20      Easy
     *      20 >= n < 40      Easy/Medium
     *      40 >= n < 60      Medium
     *      60 >= n < 80      Hard/Medium
     *      80 >= n <= 100     Hard
     *
     * The difficulty classification as regard as period of the day.
     * If period of the day is...
     *     GOOD:
     *          00 >= n < 20      0 (the acumulative value)
     *          20 >= n < 40      25
     *          40 >= n < 60      50
     *          60 >= n < 80      75
     *          80 >= n <= 100     100
     *
     *     MEDIUM:
     *          00 >= n < 20      25
     *          20 >= n < 40      75
     *          40 >= n < 60      100
     *          60 >= n < 80      75
     *          80 >= n <= 100     25
     *
     *     EASY:
     *          00 >= n < 20      100
     *          20 >= n < 40      75
     *          40 >= n < 60      50
     *          60 >= n < 80      25
     *          80 >= n <= 100     0
     *
     * @param  periodAvailable     if is GOOD, MEDIUM or EASY.
     * @param  dificultyAverage     the difficulty average of the all subjects.
     *
     * @return  the acumulative value as regard as table classification above.
     *
     * @see Period
     */
    public int getAcumulativeValueByDificulty(char periodAvailable, float dificultyAverage, SubjectWorkload[] subjectWorkloads) {
        if( LOCAL_DEBUG ){
            System.out.println("SchedulingStudyPlanProblem, getAcumulativeValueByDificulty");
        }

        int acumulativeValue = 0;

        //TODO to make these verifications more easy, simple verifying the percent using "Regra de 3"
        if (periodAvailable == GOOD) {
            if (dificultyAverage >= 80f) {
                acumulativeValue = 100;
            } else if (dificultyAverage >= 60f) {
                acumulativeValue = 75;
            } else if (dificultyAverage >= 40f) {
                acumulativeValue = 50;
            } else if (dificultyAverage >= 20f) {
                acumulativeValue = 25;
            }
        } else if (periodAvailable == MEDIUM) {
            if (!isFakeMedium(subjectWorkloads)) {
                if (dificultyAverage >= 80f) {
                    acumulativeValue = 25;
                } else if (dificultyAverage >= 60f) {
                    acumulativeValue = 75;
                } else if (dificultyAverage >= 40f) {
                    acumulativeValue = 100;
                } else if (dificultyAverage >= 20f) {
                    acumulativeValue = 75;
                } else {
                    acumulativeValue = 25;
                }
            }
        } else {
            acumulativeValue = 100;
            if (dificultyAverage >= 80f) {
                acumulativeValue = 0;
            } else if (dificultyAverage >= 60f) {
                acumulativeValue = 25;
            } else if (dificultyAverage >= 40f) {
                acumulativeValue = 50;
            } else if (dificultyAverage >= 20f) {
                acumulativeValue = 75;
            }
        }

        //System.out.println("" + periodAvailable + " " + dificultyAverage + " " + acumulativeValue);
        return acumulativeValue;
    }

    /**
     * Search if the period contains subjects that
     * have distinct difficulty. In other words,
     * if exist subjects hard and easy in the same
     * period.
     *
     * @param  subjectWorkloads     the period.
     *
     * @return
     *         <code>true</code>   if exist. <br/>
     *         <code>false</code>  otherwise.
     */
    public Boolean isFakeMedium(SubjectWorkload[] subjectWorkloads) {
        if( LOCAL_DEBUG ){
            System.out.println("SchedulingStudyPlanProblem, isFakeMedium");
        }

        Boolean isFake = Boolean.FALSE;

        for (SubjectWorkload sw: subjectWorkloads) {
            int dificulty = sw.getSubject().getDifficulty();
            if (dificulty < 20 || dificulty >= 80) {
                isFake = Boolean.TRUE;
                break;
            }
        }

        return isFake;
    }

    /**
     * Generate individuals that contains the max
     * number of distinct genes. <br/>
     *
     * <br/>Classification: Fixed Constraint<br/>
     *
     * @param individual    the current individual to be verified.
     *
     * @return <code>float</code>   the acumulative value.
     *
     * @see SubjectWorkload
     * @see DayPlanGene
     * @see DayPlanGeneVectorIndividual
     */
    public float haveDifferentDayPlans(DayPlanGeneVectorIndividual individual) {
        if( LOCAL_DEBUG ){
            System.out.println("SchedulingStudyPlanProblem, haveDifferentDayPlans");
        }

        if( individual.size() > 0 ){
            Set<SubjectWorkload[]> list = new HashSet<>();
            int total = 0;
            for (int i = 0; i < individual.size(); i++) {
                list.add( ((DayPlanGene) individual.genome[i]).getMorning() );
                list.add( ((DayPlanGene) individual.genome[i]).getAfternoon() );
                list.add(((DayPlanGene) individual.genome[i]).getNight());
                total = total + 3;
            }

            if (list.size() > 0) {
                return getAcumulativeValueByDistinctPlans(total - list.size(), list.size());
            }
        }

        return 0;
    }

    /**
     * The cumulative value is then given by subtracting 100 by the percentage of
     * similarity between the quantity of equal periods and total of the periods
     * not empty. This means that if the plan has repeated 10 times and 100
     * non-peak periods, the percentage of closeness is 10%, then the cumulative
     * value is 90, which is the subtraction 100 - 10.
     *
     * @param  qttRepetitivePeriods     quantity of equal periods.
     * @param  totalPeriods             total of the periods not empty.
     *
     * @return <code>int</code> the acumulative value from this constraint.
     */
    public int getAcumulativeValueByDistinctPlans(int qttRepetitivePeriods, int totalPeriods) {
        if( LOCAL_DEBUG ){
            System.out.println("SchedulingStudyPlanProblem, getAcumulativeValueByDistinctPlans");
        }

        int acumulativeValue = 0;
        int percentOfDistinctPeriods = (qttRepetitivePeriods * 100) / totalPeriods;

        acumulativeValue = 100 - percentOfDistinctPeriods;

        return acumulativeValue;
    }

    public boolean contains(ArrayList<SubjectWorkload[]> periodsCompaired, SubjectWorkload[] periodToBeCompaired) {
        if( LOCAL_DEBUG ){
            System.out.println("SchedulingStudyPlanProblem, contains");
        }

        for (SubjectWorkload[] period: periodsCompaired) {
            if (equalPeriods(period, periodToBeCompaired)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Verify if the periods are equals.
     *
     * @param  periodA  period of the day A
     * @param  periodB  period of the day B
     *
     * @return
     *         <code>true</code>   if the periods are equals. <br/>
     *         <code>false</code>  otherwise.
     *
     */
    public boolean equalPeriods(SubjectWorkload[] periodA, SubjectWorkload[] periodB) {
        if( LOCAL_DEBUG ){
            System.out.println("SchedulingStudyPlanProblem, equalPeriods");
        }

        if( periodA.length != periodB.length ) return false;

        for (int i = 0; i < periodA.length; i++) {
            if( periodA[i].getSubject().getId() != periodB[i].getSubject().getId() ) return false;
            if ( periodA[i].getWorkload() != periodB[i].getWorkload() ) return false;
        }

        return true;
    }

    /**
     * Try to fill the period of the day that the user
     * have disponibility to study. <br/>
     *
     * <br/>Classification: Fixed <br/>
     *
     * @param individual    the current individual to be verified.
     *
     * @return <code>float</code>   the acumulative value.
     *
     * @see DayPlanGene
     * @see DayPlanGeneVectorIndividual
     */
    public float fillPeriodsAvailable(DayPlanGeneVectorIndividual individual) {
        if( LOCAL_DEBUG ){
            System.out.println("SchedulingStudyPlanProblem, fillPeriodsAvailable");
        }

        long individualLength = individual.size();
        int cycleIt = 0;
        Period[] studyCycle = this.dayPeriodAvailable.getStudyCycle();
        int studyCycleSize = studyCycle.length;
        int acumulativeValue = 0;

        DayPlanGene gene;
        Period period;

        for (int i = 0; i < individualLength; i++) {
            gene = (DayPlanGene) individual.genome[i];
            period = studyCycle[cycleIt];

            acumulativeValue += getAcumulativeValueFillPeriods(gene.getMorning(), period.getMorning());
            acumulativeValue += getAcumulativeValueFillPeriods(gene.getAfternoon(), period.getAfternoon());
            acumulativeValue += getAcumulativeValueFillPeriods(gene.getNight(), period.getNight());

            cycleIt++;
            if(cycleIt == studyCycleSize) {
                cycleIt = 0;
            }
        }

        return acumulativeValue / (individualLength*3f);
    }

    /**
     * Get the correct acumulativeValue from a list of subjectWorkloads
     * and the period of the day available. <br/>
     *
     * <br/>Classification table: <br/>
     *
     * <br/>If period of the day is... <br/>
     *     NOTHING: <br/>
     *          n = 0               100 (the acumulative value) <br/>
     *          0 > n < 1           75 <br/>
     *          1 >= n < 2          50 <br/>
     *          2 >= n < 3          25 <br/>
     *          n >= 3              25 <br/>
     *<br/>
     *     SMALL: <br/>
     *          1 <= n >= 9         0 <br/>
     *          7 > n <= 9          25 <br/>
     *          6 > n <= 7          75 <br/>
     *          4 > n <= 6          100 <br/>
     *          3 > n <= 4          75 <br/>
     *          1 > n <= 3          25 <br/>
     *<br/>
     *     BIG: <br/>
     *          0 >= n < 2          0 <br/>
     *          2 >= n < 4          25 <br/>
     *          4 >= n < 6          50 <br/>
     *          6 >= n < 8          75 <br/>
     *          8 >= n <= 10        100 <br/>
     *
     * @param  workloadSum   [description]
     * @param  dayPeriodAvailable [description]
     *
     * @return                    [description]
     */
    public int getAcumulativeValueByWorkload(int workloadSum, char dayPeriodAvailable) {
        if( LOCAL_DEBUG ){
            System.out.println("SchedulingStudyPlanProblem, getAcumulativeValueByWorkload");
        }

        int acumulativeValue = 0;

        if (dayPeriodAvailable == NOTHING) {
            if (workloadSum == 0) {
                acumulativeValue = 100;
            } else if (workloadSum < 1) {
                acumulativeValue = 75;
            } else if (workloadSum < 2) {
                acumulativeValue = 50;
            } else if (workloadSum < 3) {
                acumulativeValue = 25;
            }
        } else if (dayPeriodAvailable == SMALL) {
            if (workloadSum <= 1 || workloadSum >= 9) {
                acumulativeValue = 0;
            } else if (workloadSum > 7) {
                acumulativeValue = 25;
            } else if (workloadSum > 6) {
                acumulativeValue = 75;
            } else if (workloadSum > 4) {
                acumulativeValue = 100;
            } else if (workloadSum > 3) {
                acumulativeValue = 75;
            } else if (workloadSum > 1) {
                acumulativeValue = 25;
            }
        } else {
            if (workloadSum > 8) {
                acumulativeValue = 100;
            } else if (workloadSum > 6) {
                acumulativeValue = 75;
            } else if (workloadSum > 4) {
                acumulativeValue = 50;
            } else if (workloadSum > 2) {
                acumulativeValue = 25;
            }
        }

        return acumulativeValue;
    }

    public int getAcumulativeValueByQttSubjects(int qtt, char dayPeriodAvailable) {
        if( LOCAL_DEBUG ){
            System.out.println("SchedulingStudyPlanProblem, getAcumulativeValueByQttSubjects");
        }

        int acumulativeValue = 0;

        if (dayPeriodAvailable == NOTHING) {
            if (qtt == 0) {
                acumulativeValue = 100;
            } else if (qtt <= 1) {
                acumulativeValue = 25;
            } else if (qtt <= 2) {
                acumulativeValue = 10;
            }
        } else if (dayPeriodAvailable == SMALL) {
            if (qtt == 0 || qtt >= 9) {
                acumulativeValue = 0;
            } else if (qtt >= 6) {
                acumulativeValue = 25;
            } else if (qtt > 3) {
                acumulativeValue = 75;
            } else if (qtt <= 3) {
                acumulativeValue = 100;
            }
        } else {
            if (qtt >= 5) {
                acumulativeValue = 100;
            } else if (qtt >= 4) {
                acumulativeValue = 75;
            } else if (qtt >= 3) {
                acumulativeValue = 50;
            } else if (qtt >= 2) {
                acumulativeValue = 25;
            }
        }

        return acumulativeValue;
    }

    /**
     * The ideal is fill all time period avaible
     * with a more subjects quantity.
     *
     * @param  subjectWorkloads   [description]
     * @param  dayPeriodAvailable [description]
     *
     * @return                    [description]
     */
    public int getAcumulativeValueFillPeriods(SubjectWorkload[] subjectWorkloads, char dayPeriodAvailable) {
        if( LOCAL_DEBUG ){
            System.out.println("SchedulingStudyPlanProblem, getAcumulativeValueFillPeriods");
        }

        int workloadSum = 0;
        for (SubjectWorkload sw : subjectWorkloads) {
            workloadSum += sw.getWorkload();
        }

        int acumulativeValue = getAcumulativeValueByWorkload(workloadSum, dayPeriodAvailable);
        //acumulativeValue += getAcumulativeValueByQttSubjects(subjectWorkloads.size(), dayPeriodAvailable);

        return acumulativeValue;
    }

   /**
    * Check if have subjects in the period of the day who the
    * user don't have disponibility.<br/>
    *
    * <br/>Classification: Fixed Constraint.<br/>
    *
    * <br/><b>NOTE</b>: Maybe should be better verify in relation of quantity of
    * hours and not in relation of quantity of subjects.
    *
    * @param individual     the study plan.
    *
    * @return
    *         <code>true</code>   if the constraint was attended. <br/>
    *         <code>false</code>  otherwise.
    *
    * @see Period
    * @see DayPlanGene
    * @see DayPlanGeneVectorIndividual
    */
    public float subjectInInappropriatePeriod(DayPlanGeneVectorIndividual individual) {
        if( LOCAL_DEBUG ){
            System.out.println("SchedulingStudyPlanProblem, subjectInInappropriatePeriod");
        }

        long individualLength = individual.size();
        int cycleIt = 0;
        Period[] studyCycle = this.dayPeriodAvailable.getStudyCycle();
        int studyCycleSize = studyCycle.length;
        int acumulativeValue = 0;
        DayPlanGene gene;
        Period period;

        for (int i = 0; i < individualLength; i++) {
            gene = (DayPlanGene) individual.genome[i];
            period = studyCycle[cycleIt];

            acumulativeValue += getAcumulativeValueByQttSubjects(gene.getMorning().length, period.getMorning());
            acumulativeValue += getAcumulativeValueByQttSubjects(gene.getAfternoon().length, period.getAfternoon());
            acumulativeValue += getAcumulativeValueByQttSubjects(gene.getNight().length, period.getNight());

            cycleIt++;
            if(cycleIt == studyCycleSize) {
                cycleIt = 0;
            }
        }

        float total = acumulativeValue / (individualLength*3f);

        return total;
    }

    /**
     * Generate a value for nearest empty period.
     *
     * @param  period [description]
     *
     * @return        [description]
     */
    public int getAcumulativeValueByNothingPeriod(SubjectWorkload[] period) {
        if( LOCAL_DEBUG ){
            System.out.println("SchedulingStudyPlanProblem, getAcumulativeValueByNothingPeriod");
        }

        int acumulativeValue = 0;

        int total = period.length;

        if(total == 0) {
            acumulativeValue = 100;
        } else if (total == 1) {
            acumulativeValue = 75;
        } else if (total == 2) {
            acumulativeValue = 50;
        } else if (total == 3) {
            acumulativeValue = 25;
        }

        return acumulativeValue;
    }

    /**
    * Verify if the student hours to leisure was attended. <br/>
    *
    * <br/> WHEN A PERSON CHOISE "NOTHING" IN A PERIOD OF THE DAY, THEY
    * WAS PLANING <br/>
    *
    * <br/> Classification: Soft Constraint. <br/>
    *
    * @param individual    the current individual to be verified.
    *
    * @return <code>float</code>   the acumulative value.
    *
    * @see SubjectWorkload
    * @see Period
    * @see DayPlanGene
    * @see DayPlanGeneVectorIndividual
    */
    public float hoursToLeisure(DayPlanGeneVectorIndividual individual) {
        if( LOCAL_DEBUG ){
            System.out.println("SchedulingStudyPlanProblem, hoursToLeisure");
        }

        long individualLength = individual.size();
        int cycleIt = 0;
//        ArrayList<SubjectWorkload> subjectWorkloads = new ArrayList<>();
        Period[] studyCycle = this.dayPeriodAvailable.getStudyCycle();
        int studyCycleSize = studyCycle.length;
        int qtdNothingPeriods = 0;

        float hoursSum = 0.0f;
        DayPlanGene gene = null;
        for (int i = 0; i < individualLength; i++) {
            gene = (DayPlanGene) individual.genome[i];
//            subjectWorkloads.addAll(gene.getMorning());
//            subjectWorkloads.addAll(gene.getAfternoon());
//            subjectWorkloads.addAll(gene.getNight());


            for (SubjectWorkload sw: gene.getMorning()) {
                hoursSum += sw.getWorkload();
            }
            for (SubjectWorkload sw: gene.getAfternoon()) {
                hoursSum += sw.getWorkload();
            }
            for (SubjectWorkload sw: gene.getNight()) {
                hoursSum += sw.getWorkload();
            }

            if (studyCycle[cycleIt].getMorning() == NOTHING) {
                qtdNothingPeriods++;
            }

            if (studyCycle[cycleIt].getAfternoon() == NOTHING) {
                qtdNothingPeriods++;
            }

            if (studyCycle[cycleIt].getNight() == NOTHING) {
                qtdNothingPeriods++;
            }

            cycleIt++;
            if(cycleIt == studyCycleSize) {
                cycleIt = 0;
            }
        }

        int qtdPeriodsAlocated = ((int)individualLength*3) - qtdNothingPeriods;
        //System.out.println("qtdPeriodsAlocated: " + qtdPeriodsAlocated + " qtdNothingPeriods: " + qtdNothingPeriods);
        float qtdTotalHoursAvailable = qtdPeriodsAlocated * 5;
        //System.out.println("qtdTotalHoursAvailable: " + qtdTotalHoursAvailable +" sum: " + hoursSum);
        int acumulativeValue = getAcumulativeValueByLeisure(qtdTotalHoursAvailable, hoursSum, this.student.getHoursToLeisure());

        return (float)acumulativeValue;
    }

    /**
     * Get a acumulativeValue from a individual respecting
     * the hours to leisure from user.
     *
     * @param  qtdTotalHoursAvailable    the total of hours available.
     * @param  hoursSum                  sum of the all subjects in the individual.
     * @param  hoursToLeisure            hours reserved from user to not study.
     *
     * @return the acumulative value by hours to leisure respected.
     */
    public int getAcumulativeValueByLeisure(float qtdTotalHoursAvailable, float hoursSum, float hoursToLeisure) {
        if( LOCAL_DEBUG ){
            System.out.println("SchedulingStudyPlanProblem, getAcumulativeValueByLeisure");
        }

        int acumulativeValue = 0;

        float qtdHoursAvailable = qtdTotalHoursAvailable - hoursToLeisure;
        if (qtdHoursAvailable >= hoursSum) {
            acumulativeValue = 100;
        } else if (hoursSum <= qtdHoursAvailable + 3) {
                acumulativeValue = 75;
        } else if (hoursSum <= qtdHoursAvailable + 6) {
                acumulativeValue = 50;
        } else if (hoursSum <= qtdHoursAvailable + 9) {
                acumulativeValue = 25;
        }

        //System.out.println("acumulativeValue: " + acumulativeValue + " qtdHoursAvailable: " + qtdHoursAvailable);
        return acumulativeValue;
    }

    /**
    * Check if the subjects are studies in one time only. <br/>
    *
    * <br/>Classification: Soft Constraint. <br/>
    *
    * @param individual    the current individual to be verified.
    *
    * @return <code>float</code>   the acumulative value.
    *
    * @see SubjectWorkload
    * @see DayPlanGene
    * @see DayPlanGeneVectorIndividual
    * @see Period
    */
    public float notWasteAllTimeInTheSameSubject(DayPlanGeneVectorIndividual individual) {
        if( LOCAL_DEBUG ){
            System.out.println("SchedulingStudyPlanProblem, notWasteAllTimeInTheSameSubject");
        }

        long individualLength = individual.size();
        int acumulativeValue = 0;
        int qtdPeriodsAvailable = 0;

        SubjectWorkload[] period;
        for (int i = 0; i < individualLength; i++) {
            DayPlanGene gene = (DayPlanGene) individual.genome[i];

            period = gene.getMorning();
            if (period.length > 0) {
                qtdPeriodsAvailable++;
                acumulativeValue += getAcumulativeValueByMoreThanOneSubject(period.length);
            }

            period = gene.getAfternoon();
            if (period.length > 0) {
                qtdPeriodsAvailable++;
                acumulativeValue += getAcumulativeValueByMoreThanOneSubject(period.length);
            }

            period = gene.getNight();
            if (period.length > 0) {
                qtdPeriodsAvailable++;
                acumulativeValue += getAcumulativeValueByMoreThanOneSubject(period.length);
            }
        }

        float total = (qtdPeriodsAvailable != 0) ? (float)acumulativeValue / (float)qtdPeriodsAvailable : 0;
        return total;
    }

    /**
     * Return the acumulative value in relation of
     * the quantity of subjects by period. If the period
     * have more than one alocated subject 100 is returned,
     * 0 otherwise. <br/>
     *
     * @param  qttSubjectsByPeriod   the quantity of subjects by period.
     *
     * @return
     *         100 if have more than 1; <br/>
     *         0 otherwise.
     */
    public int getAcumulativeValueByMoreThanOneSubject(int qttSubjectsByPeriod) {
        if( LOCAL_DEBUG ){
            System.out.println("SchedulingStudyPlanProblem, getAcumulativeValueByMoreThanOneSubject");
        }

        int acumulativeValue = 0;
        if (qttSubjectsByPeriod > 1) {
            acumulativeValue = 100;
        }

        return acumulativeValue;
    }

    /**
     * Return the acumulative value in relation of
     * the classification below: <br/>
     *
     * <br/>if the difference between max and the second max is: <br/>
     *     0 then: 100 - (0*25) is equals 100. <br/>
     *     1 then: 100 - (1*25) is equals 75. <br/>
     *     and so on.. <br/>
     *
     * @param  period   the current period to be verified.
     *
     * @return the classification.
     */
    public int getAcumulativeValueByMaxHour(SubjectWorkload[] period) {
        if( LOCAL_DEBUG ){
            System.out.println("SchedulingStudyPlanProblem, getAcumulativeValueByMaxHour");
        }

        ArrayList<Byte> hours = new ArrayList<>();
        hours.clear();
        for (SubjectWorkload sw: period) {
            hours.add(sw.getWorkload());
        }
        Collections.sort(hours);

        int hoursLength = hours.size();
        int max = hours.get(hoursLength - 1);

        int acumulativeValue = 0;
        int dif = -1;
        if ((hoursLength - 2) >= 0) {
            int secondMax = hours.get(hoursLength - 2);
            dif = (max/2) - (secondMax/2);

            if (dif <= 4) {
                acumulativeValue = 100 - (dif*25);
            }
        }

        return acumulativeValue;
    }

    /**
    * Check if exist periods that the sum of workload is greather
    * than 6 hours.
    *
    * Classification: Fixed Constraint.
     * @param individual
    *
    * @return  <code>true</code>   if the constraint was attended.
    *          <code>false</code>  otherwise.
    */
    public float maxSixHoursPerPeriod(DayPlanGeneVectorIndividual individual) {
        if( LOCAL_DEBUG ){
            System.out.println("SchedulingStudyPlanProblem, maxSixHoursPerPeriod");
        }

        long individualLength = individual.size();

        int qtdPeriodsAvailable = 0;
        int acumulativeValue = 0;

        for (int i = 0; i < individualLength; i++) {
            DayPlanGene gene = (DayPlanGene) individual.genome[i];

            //Morning
            if (gene.getMorning().length > 0) {
                qtdPeriodsAvailable++;
                acumulativeValue += verifySubjectsSum(gene.getMorning());
            }

            //Afternoon
            if (gene.getAfternoon().length > 0) {
                qtdPeriodsAvailable++;
                acumulativeValue += verifySubjectsSum(gene.getAfternoon());
            }

            //Night
            if (gene.getNight().length > 0) {
                qtdPeriodsAvailable++;
                acumulativeValue += verifySubjectsSum(gene.getNight());
            }
        }

        float total = (float)acumulativeValue / (float)qtdPeriodsAvailable;
        //System.out.println("acumulativeValue: " + acumulativeValue + " qtdPeriodsAvailable: " + qtdPeriodsAvailable + " Total: " + total);

        return total;
    }

    /**
     * Verify if all subjectsWorkload into the period of the day passed by param
     * exceeds six hours.
     *
     * @param  periodWithSubjects   the subjects of the period of the day.
     *
     * @return      100     if the subjects workload don't exceeds six hours.
     *              0       if exceeds.
     */
    public int verifySubjectsSum(SubjectWorkload[] periodWithSubjects) {
        if( LOCAL_DEBUG ){
            System.out.println("SchedulingStudyPlanProblem, verifySubjectsSum");
        }

        int workloadSum = 0;
        int acumulativeValue = 0;
        for (SubjectWorkload sw: periodWithSubjects) {
            workloadSum += sw.getWorkload();
        }
        if(workloadSum < 12) {
            acumulativeValue = 100;
        }

        return acumulativeValue;
    }

    /**
    *   Print in the console the lines of the input.
    */
    public void printInputLines(Vector<String> lines, String type, EvolutionState state) {

        System.out.println("\n------------"+ type +"---------------");

        for (String lineIt: lines) {
            state.output.message(lineIt);
            System.out.println();
        }

        System.out.println("----------------Done!-------------------");

    }

    /**
    *   Print all the classes filled
    */
    public void printInputConverted() {
        System.out.println("-----------------------\nnumber of subjects: " + subjects.length);
        for (Subject subject : this.subjects) {
            System.out.println("Name: " + subject.getName());
            System.out.println("Dificulty: " + subject.getDifficulty());
        }

        System.out.println("\n-------------Student----------");
        System.out.println("Name: " + this.student.getName());
        System.out.println("hoursToLeisure: " + this.student.getHoursToLeisure());

        System.out.println("\n-------------DayPeriodAvailable----");
        System.out.println("[WeekDay]----[Disponibility]");
        Period[] studyCycle = dayPeriodAvailable.getStudyCycle();

        int cycleIt = 0;
        int studyCycleSize = studyCycle.length;
        for (Period period: studyCycle) {

            System.out.println( "" + cycleIt+1 + " " +
                                period.getMorning() + " " +
                                period.getAfternoon() + " " +
                                period.getNight());

            cycleIt = (cycleIt == studyCycleSize-1) ? 0 : ++cycleIt;
        }
    }
}
