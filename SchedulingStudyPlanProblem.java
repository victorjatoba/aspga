/*
  Copyright 2013 by Victor Jatoba
  Licensed under the Academic Free License version 3.0
  See the file "LICENSE" for more information
*/


package ec.app.aspga;
import ec.util.*;
import ec.*;
import ec.simple.*;
import ec.vector.GeneVectorIndividual;
import ec.util.*; //Parameter

//Data packages
import ec.app.aspga.Student;
import ec.app.aspga.Subject;

//Java Packages
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.Vector;
import java.util.ArrayList;
//import java.util.Array;
import java.lang.Character;

/**
 * SchedulingStudyPlanProblem.java
 *
 * Modified: Mon Nov 28 02:36 2013
 * By: Victor Jatoba
 */

/**
 * @author victorjatoba
 * @version 1.0
 */

public class SchedulingStudyPlanProblem extends Problem implements SimpleProblemForm
{
    private static final long serialVersionUID = 1;

    public static final String P_COURSEINFORMATION = "courseInformation";
    public static final String P_INTELECTUALAVAILABLE = "intelectualAvailable";
    public static final String P_DAYPERIODAVAILABLE = "dayperiodAvailable";
    public static final String P_STUDENTINFORMATION = "studentInformation";

    public static final char BIG =      'B';
    public static final char SMAL =     'S';
    public static final char NOTHING =  'N';

    public static final char GOOD =     'G';
    public static final char MEDIUM =   'M';
    public static final char BAD =      'B';

    public static final char HARD =     'H';
    public static final char EASY =     'E';

    public static final char NONE =     'N';

    ArrayList<Subject> subjects;
    Student student;
    PeriodAvailable dayPeriodAvailable;
    PeriodAvailable intelectualAvailable;

    //GeneVectorIndividual individual;

    public void setup (  final EvolutionState state, final Parameter base) {

        File courseInformationInput     = null;
        File intelectualAvailableInput  = null;
        File dayPeriodAvailableInput    = null;
        File studentInformationInput    = null;

        courseInformationInput      = state.parameters.getFile(base.push(P_COURSEINFORMATION),null);
        intelectualAvailableInput   = state.parameters.getFile(base.push(P_INTELECTUALAVAILABLE),null);
        dayPeriodAvailableInput     = state.parameters.getFile(base.push(P_DAYPERIODAVAILABLE),null);
        studentInformationInput     = state.parameters.getFile(base.push(P_STUDENTINFORMATION),null);

        verifyInputExistence(state, base, courseInformationInput, intelectualAvailableInput, dayPeriodAvailableInput, studentInformationInput);

        Vector<String> courseInformationVector = convertFileToVectorString(courseInformationInput);
        Vector<String> intelectualAvailableVector = convertFileToVectorString(intelectualAvailableInput);
        Vector<String> dayPeriodAvailableVector = convertFileToVectorString(dayPeriodAvailableInput);
        Vector<String> studentInformationVector = convertFileToVectorString(studentInformationInput);

        //printInputLines(courseInformationVector, "CourseInformation");

        this.subjects = fillSubjects(courseInformationVector, state);
        this.student = fillStudent(studentInformationVector, state);
        this.dayPeriodAvailable = fillPeriodAvailable(dayPeriodAvailableVector, state);
        this.intelectualAvailable = fillPeriodAvailable(intelectualAvailableVector, state);
    }

    /**
    *   Implement the ftness function
    */
    public void evaluate(   final EvolutionState state,
                            final Individual ind,
                            final int subpopulation,
                            final int threadnum)
    {
        if (ind.evaluated) return;

        if (!(ind instanceof GeneVectorIndividual)) {
            state.output.fatal("Whoa!  It's not a GeneVectorIndividual!!!",null);
        }

        GeneVectorIndividual individual = (GeneVectorIndividual)ind;

        if (!(individual.fitness instanceof SimpleFitness)) {
            state.output.fatal("Whoa!  It's not a SimpleFitness!!!",null);
        }

        float fitnessValue = calculateFitnessValue(individual);

        ((SimpleFitness)individual.fitness).setFitness(state,
            /// ...the fitness...
            fitnessValue,
            ///... is the individual ideal?  Indicate here...
            (fitnessValue == 100.0f));

        individual.evaluated = true;
    }

    /**
     * Responsible for calculate the fitness value.
     *
     * @param  individual the individual to be calculated.
     *
     * @return <code>float</code>   the fitness value.
     */
    public float calculateFitnessValue(GeneVectorIndividual individual) {
        //float fitness = subjectInInappropriatePeriod();
        //float inappropriatePeriod = subjectInInappropriatePeriod(individual);
        //float hard = hardSubjectInEasyPeriod(individual);
        //float fitness = inappropriatePeriod + (hard-30);
        //float fitness = maxSixHoursPerPeriod(individual);
        float gradually = toStudyGradually(individual);
        float fitness = gradually;
        //System.out.println("fits: " + inappropriatePeriod + " " + hard);
        return fitness;
    }

    /**
    * Check if the study plan have grow-up learn. To do this,
    * verify the quantity of the medium difficulty subjects exist
    * in the begin of the plan. After verify the hard and finally
    * the easy difficulty subjects.Then placing one percent that
    * depends the quantity it was found.
    *
    * Classification: Fixed/Hard Constraint.
    *
    * @return  <code>true</code>   if the constraint was satisfied.
    *          <code>false</code>  otherwise.
    */
    public float toStudyGradually(GeneVectorIndividual individual) {
        int acumulativeValue = 0;
        int qtdPerids = (int)individual.size();

        ArrayList<SubjectWorkload> allSubjWorkloads = new ArrayList<SubjectWorkload>();
        ArrayList<ArrayList<SubjectWorkload> > allPeriods = new ArrayList<ArrayList<SubjectWorkload> >();
        ArrayList<SubjectWorkload> subjectWorkloads;
        //ArrayList<SubjectWorkload> emptyPeriod;

        Vector<Integer> periodsEmpty = new Vector<Integer>();

        for (int i = 0; i <  qtdPerids*3; i++) {
            periodsEmpty.add(0);
        }


        for (int i = 0; i < qtdPerids; i++) {
            DayPlanGene gene = (DayPlanGene) individual.genome[i];

            subjectWorkloads = gene.getMorning();
            if (!subjectWorkloads.isEmpty()) {
                allPeriods.add(subjectWorkloads);
                allSubjWorkloads.addAll(subjectWorkloads);

            } else {
                int pos = i*3;
                periodsEmpty.set(pos, 1);
            }

            subjectWorkloads = gene.getAfternoon();
            if (!subjectWorkloads.isEmpty()) {
                allPeriods.add(subjectWorkloads);
                allSubjWorkloads.addAll(subjectWorkloads);
            } else {
                int pos = (i*3)+1;
                periodsEmpty.set(pos, 1);
            }

            subjectWorkloads = gene.getNight();
            if (!subjectWorkloads.isEmpty()) {
                allPeriods.add(subjectWorkloads);
                allSubjWorkloads.addAll(subjectWorkloads);
            } else {
                int pos = (i*3)+2;
                periodsEmpty.set(pos, 1);
            }
        }

        //System.out.println(qtdPerids +" "+allPeriods.size()/3);
        //qtdPerids = allPeriods.size()/3;
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
        return acumulativeValue/3;
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
     */
    public int countSWByPeriod(ArrayList<ArrayList<SubjectWorkload> > allPeriods, int init, int end) {
        int amountSWByPeriod = 0;
        for (int i = init; i < end; i++) {
            amountSWByPeriod += allPeriods.get(i).size();
        }

        return amountSWByPeriod;
    }

    /**
     * Count the number of 1 exist in the especific period
     * of the dificulty.
     *
     * Obs.: 1 signific that exist one period of the day empty.
     * In the other words, this period not contains SubjectWorkloads.
     *
     * @param  period           {MEDIUM, HARD or EASY}
     * @param  periodsEmpty     the vector of empty periods.
     *
     * @return              the number of empty periods.
     */
    public int countEmptyByPeriod(char period, Vector<Integer> periodsEmpty) {
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
            if (periodsEmpty.get(i) == 1) {
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
     * @return  100     if is the same of the total.
     *          75      if is greater than the third of the total.
     *          50      if is greater than the half of the total.
     *          25      if is greater than the quarter of the total.
     *          0       otherwise.
     */
    public int countAcumulativeValueByDifficulty(int amountFound, int total) {
        int acumulativeValue = 0;

        if (total != 0) {
            float third = total * 0.75f;
            float half = total * 0.5f;
            float quarter = total * 0.25f;

            if (amountFound == total) {
                acumulativeValue = 100;
            } else if (amountFound > third) {
                acumulativeValue = 75;
            } else if (amountFound >= half) {
                acumulativeValue = 50;
            } else if (amountFound > quarter) {
                acumulativeValue = 25;
            }
        }

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
     */
    public int countSubjectsDifficultyBetween(ArrayList<ArrayList<SubjectWorkload> > allPeriods, int init, int end, char difficultyType) {
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
                if(isBetween(sw.getSubject().getDificulty(), a, b)) {
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
    public Boolean isBetween(int verify, int a, int b) {
        if (verify >= a && verify < b) {
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }

    /**
    * Hard Subjects should be alocated in the period of the day that the user
    * have more intelectual facility for to learn.
    *
    * Classification: Hard Constraint.
    *
    * @param individual
    *
    * @return  <code>float</code>   the fitness value for this constraint.
    */
    public float hardSubjectInEasyPeriod(GeneVectorIndividual individual) {
        long individualLength = individual.size();
        int cycleIt = 0;
        ArrayList<Period> studyCycle = this.intelectualAvailable.getStudyCycle();
        int studyCycleSize = studyCycle.size();
        int acumulativeValue = 0;
        int qtdPeriodsAvailable = 0;

        char periodAvailable;

        ArrayList<SubjectWorkload> subjectWorkloads;
        for (int i = 0; i < individualLength; i++) {
            DayPlanGene gene = (DayPlanGene) individual.genome[i];
            Period period = studyCycle.get(cycleIt);

            //Morning
            subjectWorkloads = gene.getMorning();
            periodAvailable = period.getMorning();
            if (!subjectWorkloads.isEmpty()) {
                qtdPeriodsAvailable++;
                acumulativeValue += getPeriodAcumulativeValue(periodAvailable, subjectWorkloads);
            }

            //Afternoon
            subjectWorkloads = gene.getAfternoon();
            periodAvailable = period.getAfternoon();
            if (!subjectWorkloads.isEmpty()) {
                qtdPeriodsAvailable++;
                acumulativeValue += getPeriodAcumulativeValue(periodAvailable, subjectWorkloads);
            }

            //Night
            subjectWorkloads = gene.getNight();
            periodAvailable = period.getNight();
            if (!subjectWorkloads.isEmpty()) {
                qtdPeriodsAvailable++;
                acumulativeValue += getPeriodAcumulativeValue(periodAvailable, subjectWorkloads);
            }

            cycleIt++;
            if(cycleIt == studyCycleSize) {
                cycleIt = 0;
            }
        }

        float total = 0; //exist one individual that don't have genes in their genome.
        if (qtdPeriodsAvailable != 0) {
            total = acumulativeValue / qtdPeriodsAvailable;
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
    public int getPeriodAcumulativeValue(char periodAvailable, ArrayList<SubjectWorkload> subjectWorkloads) {
        int dificultySum = 0;
        for (SubjectWorkload sw: subjectWorkloads) {
            //System.out.println(sw.getSubject().getName());
            dificultySum += sw.getSubject().getDificulty();
        }
        float dificultyAverage = dificultySum / subjectWorkloads.size();

        int acumulativeValue = getAcumulativeValueByDificulty(periodAvailable, dificultyAverage, subjectWorkloads);

        return acumulativeValue;
    }

    /**
     * Return the acumulativeValue from the period using the
     * table classification below.
     *
     * The subject classification by difficulty:
     *      00 <= n < 20      Easy
     *      20 <= n < 40      Easy/Medium
     *      40 <= n < 60      Medium
     *      60 <= n < 80      Hard/Medium
     *      80 <= n < 100     Hard
     *
     * The difficulty classification as regard as period of the day.
     * If period of the day is...
     *     GOOD:
     *          00 <= n < 20      0 (the acumulative value)
     *          20 <= n < 40      25
     *          40 <= n < 60      50
     *          60 <= n < 80      75
     *          80 <= n < 100     100
     *
     *     MEDIUM:
     *          00 <= n < 20      25
     *          20 <= n < 40      75
     *          40 <= n < 60      100
     *          60 <= n < 80      75
     *          80 <= n < 100     25
     *
     *     EASY:
     *          00 <= n < 20      100
     *          20 <= n < 40      75
     *          40 <= n < 60      50
     *          60 <= n < 80      25
     *          80 <= n < 100     0
     *
     * @param  periodOfDificult     if is GOOD, MEDIUM or EASY.
     * @param  dificultyAverage     the difficulty average of the all subjects.
     *
     * @return  the acumulative value as regard as table classification above.
     *
     * @see {@link Period}
     */
    public int getAcumulativeValueByDificulty(char periodAvailable, float dificultyAverage, ArrayList<SubjectWorkload> subjectWorkloads) {
        int acumulativeValue = 0;

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
     * have distinct dificulty. In other words,
     * if exist subjets hard and easy in the same
     * period.
     *
     * @param  subjectWorkloads     the period.
     *
     * @return  <code>true</code>   if exist.
     *          <code>false</code>  otherwise.
     */
    public Boolean isFakeMedium(ArrayList<SubjectWorkload> subjectWorkloads) {
        Boolean isFake = Boolean.FALSE;

        for (SubjectWorkload sw: subjectWorkloads) {
            int dificulty = sw.getSubject().getDificulty();
            if (dificulty < 20 || dificulty >= 80) {
                isFake = Boolean.TRUE;
                break;
            }
        }

        return isFake;
    }

/*
    public float hardSubjectInEasyPeriod(GeneVectorIndividual individual) {
        long individualLength = individual.size();
        int cycleIt = 0;
        ArrayList<Period> studyCycle = this.intelectualAvailable.getStudyCycle();
        int acumulativeValue = 0;
        int qtdPeriodsAvailable = 0;
        char maxChar;
        ArrayList<SubjectWorkload> genePeriod;
        //dayPeriodAvailable;

        for (int i = 0; i < individualLength; i++) {
            DayPlanGene gene = (DayPlanGene) individual.genome[i];
            Period period = studyCycle.get(cycleIt);

            //Morning
            genePeriod = gene.getMorning();
            if (!genePeriod.isEmpty()) {
                qtdPeriodsAvailable++;

                maxChar = getMaxDificulty(genePeriod);
                acumulativeValue += getAcumulativeValueByDificulty(period.getMorning(), maxChar);
            }

            //Afternoon
            genePeriod = gene.getAfternoon();
            if (!genePeriod.isEmpty()) {
                qtdPeriodsAvailable++;

                maxChar = getMaxDificulty(genePeriod);
                acumulativeValue += getAcumulativeValueByDificulty(period.getMorning(), maxChar);
            }

            //Night
            genePeriod = gene.getNight();
            if (!genePeriod.isEmpty()) {
                qtdPeriodsAvailable++;

                maxChar = getMaxDificulty(genePeriod);
                acumulativeValue += getAcumulativeValueByDificulty(period.getMorning(), maxChar);
            }

        }

        float total = acumulativeValue / qtdPeriodsAvailable;
        //System.out.println("acumulativeValue: " + acumulativeValue + " qtdPeriodsAvailable: " + qtdPeriodsAvailable + " Total: " + total);

        return total;
    }

    public char getMaxDificulty(ArrayList<SubjectWorkload> subjects) {
        char maxChar;
        int maxDif;
        int hardSum = 0;
        int mediumSum = 0;
        int easySum = 0;

        for (SubjectWorkload sw: subjects) {
            char dificulty = sw.getSubject().getDificulty();
            if (dificulty == HARD) {
                hardSum++;
            } else if (dificulty == MEDIUM) {
                mediumSum++;
            } else {
                easySum++;
            }
        }
        maxDif = Math.max(Math.max(hardSum,mediumSum),easySum);

        maxChar = NONE;
        if (maxDif == hardSum) {
            maxChar = HARD;
        } else if (maxDif == mediumSum) {
            maxChar = MEDIUM;
        } else {
            maxChar = EASY;
        }

        return maxChar;
    }

    public int getAcumulativeValueByDificulty(char periodAvailable, char maxDificulty) {
        int acumulativeValue = 0;

        if (maxDificulty == HARD) {
            //Hard subjects
            if (periodAvailable == GOOD) {
                acumulativeValue = 100;
            } else if (periodAvailable == MEDIUM) {
                acumulativeValue = 50;
            }
        } else if (maxDificulty > MEDIUM) {
            //Median subjects
            if (periodAvailable == GOOD) {
                acumulativeValue = 50;
            } else if (periodAvailable == MEDIUM) {
                acumulativeValue = 100;
            } else {
                acumulativeValue = 50;
            }
        } else if (maxDificulty == EASY){
            // Easy subjects
            if (periodAvailable == MEDIUM) {
                acumulativeValue = 50;
            } else if (periodAvailable == BAD) {
                acumulativeValue = 100;
            }
        }

        return acumulativeValue;
    }
*/

    /**
    * Check if the subjects are studies in one time only.
    *
    * Classification: Fixed/HARD Constraint.
    *
    * @return  <code>true</code>   if the constraint was satisfied.
    *          <code>false</code>  otherwise.
    */
    public void notWasteAllTimeInTheSameSubject() {

    }

    /**
     * Try to fill the period of the day that the user
     * have disponibility to study.
     *
     * Classification: Hard
     *
     */
    public void fillPeriodsAvailable() {
    }

   /**
    * Check if have subjects in the period of the day who the
    * user don't have disponibility.
    *
    * Classification: Fixed Constraint.
 * @param individual
    *
    * @return  <code>true</code>   if the constraint was satisfied.
    *          <code>false</code>  otherwise.
    */
    public float subjectInInappropriatePeriod(GeneVectorIndividual individual) {
        long individualLength = individual.size();
        int cycleIt = 0;
        ArrayList<Period> studyCycle = this.dayPeriodAvailable.getStudyCycle();
        int studyCycleSize = studyCycle.size();
        int acumulativeValue = 0;
        int qtdNothingPeriods = 0;
        //dayPeriodAvailable;

        for (int i = 0; i < individualLength; i++) {
            DayPlanGene gene = (DayPlanGene) individual.genome[i];

            if (studyCycle.get(cycleIt).getMorning() == NOTHING) {
                qtdNothingPeriods++;
                if(gene.getMorning().isEmpty()) {
                    acumulativeValue += 100;
                }
            }

            if (studyCycle.get(cycleIt).getAfternoon() == NOTHING) {
                qtdNothingPeriods++;
                if (gene.getAfternoon().isEmpty()) {
                    acumulativeValue += 100;
                }
            }

            if (studyCycle.get(cycleIt).getNight() == NOTHING) {
                qtdNothingPeriods++;
                if (gene.getNight().isEmpty()) {
                    acumulativeValue += 100;
                }
            }

            cycleIt++;
            if(cycleIt == studyCycleSize) {
                cycleIt = 0;
            }
        }

        //System.out.println( "qtdNothingPeriods: " + qtdNothingPeriods + " acumulativeValue: " + acumulativeValue);

        float total = acumulativeValue / qtdNothingPeriods;

        return total;
    }

    /**
    * Verify if the student hours to leisure was attended.
    *
    * WHEN ONE PERSON CHOISE "NOTHING" IN A PERIOD OF THE DAY, THEY
    * WAS PLANING
    *
    * Classification: Soft Constraint.
    *
    * @return  <code>true</code>   if the constraint was attended.
    *          <code>false</code>  otherwise.
    */
    public void hoursToLeisure() {

    }

    /**
    * Check if the Student hours to leisure was satisfield.
    *
    * Classification: Fixed Constraint.
     * @param individual
    *
    * @return  <code>true</code>   if the constraint was attended.
    *          <code>false</code>  otherwise.
    */
    public float maxSixHoursPerPeriod(GeneVectorIndividual individual) {
        long individualLength = individual.size();

        int qtdPeriodsAvailable = 0;
        int acumulativeValue = 0;

        for (int i = 0; i < individualLength; i++) {
            DayPlanGene gene = (DayPlanGene) individual.genome[i];

            //Morning
            if (!gene.getMorning().isEmpty()) {
                qtdPeriodsAvailable++;
                acumulativeValue += verifySubjectsSum(gene.getMorning());
            }

            //Afternoon
            if (!gene.getAfternoon().isEmpty()) {
                qtdPeriodsAvailable++;
                acumulativeValue += verifySubjectsSum(gene.getAfternoon());
            }

            //Night
            if (!gene.getNight().isEmpty()) {
                qtdPeriodsAvailable++;
                acumulativeValue += verifySubjectsSum(gene.getNight());
            }
        }

        float total = acumulativeValue / qtdPeriodsAvailable;
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
    public int verifySubjectsSum(ArrayList<SubjectWorkload> periodWithSubjects) {
        int workloadSum = 0;
        int acumulativeValue = 0;
        for (SubjectWorkload sw: periodWithSubjects) {
            workloadSum += sw.getWorkload();
        }
        if(workloadSum < 12) {
            acumulativeValue += 100;
        }

        return acumulativeValue;
    }

    /**
    *   Fill the PeriodAvailable information with the input file
    */
    public PeriodAvailable fillPeriodAvailable(Vector<String> periodAvailables, EvolutionState state) {
        ArrayList<Period> studyCycle = new ArrayList<Period>();

        if (periodAvailables != null) {
            for (String line: periodAvailables) {
                String[] periodAvailablesInfo = line.split(" ");

                Period period = new Period();
                period.setMorning(periodAvailablesInfo[0].charAt(0));
                period.setAfternoon(periodAvailablesInfo[1].charAt(0));
                period.setNight(periodAvailablesInfo[2].charAt(0));

                studyCycle.add(period);
            }
        } else {
            state.output.error("SSPP| Error: The periodAvailables information are null!");
        }

        PeriodAvailable periodAvailable = new PeriodAvailable();
        periodAvailable.setStudyCycle(studyCycle);

        return periodAvailable;
    }

    /**
    *   Fill the Student information with the input file
    */
    public Student fillStudent(Vector<String> studentIn, EvolutionState state) {
        Student student = new Student();

        if (studentIn != null) {
            for (String line: studentIn) {
                String[] studentInfo = line.split(" ");

                student.setName(studentInfo[0]);
                char hoursToLeisureChar = studentInfo[1].charAt(0);
                int hoursToLeisure = Character.getNumericValue(hoursToLeisureChar);
                student.setHoursToLeisure(hoursToLeisure);
            }
        } else {
            state.output.error("SSPP| Error: The Student information are null!");
        }

        return student;
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
    *   Verify if the inputs are not null.
    */
    public void verifyInputExistence(   EvolutionState state,
                                        final Parameter base,
                                        File courseInformationInput,
                                        File intelectualAvailableInput,
                                        File dayPeriodAvailableInput,
                                        File studentInformationInput)
    {

        if (courseInformationInput == null) {
            state.output.error("CourseInformation File doesn't exist", base.push(P_COURSEINFORMATION));
        }
        if (intelectualAvailableInput == null) {
            state.output.error("intelectualAvailable File doesn't exist", base.push(P_INTELECTUALAVAILABLE));
        }
        if (dayPeriodAvailableInput == null) {
            state.output.error("DayPeriodAvailable File doesn't exist", base.push(P_DAYPERIODAVAILABLE));
        }
        if (studentInformationInput == null) {
            state.output.error("StudentInformation File doesn't exist", base.push(P_STUDENTINFORMATION));
        }

        state.output.exitIfErrors();
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
        System.out.println("-----------------------\nnumber of subjects: " + subjects.size());
        for (Subject subject : this.subjects) {
            System.out.println("Name: " + subject.getName());
            System.out.println("Dificulty: " + subject.getDificulty());
        }

        System.out.println("\n-------------Student----------");
        System.out.println("Name: " + this.student.getName());
        System.out.println("hoursToLeisure: " + this.student.getHoursToLeisure());

        System.out.println("\n-------------DayPeriodAvailable----");
        System.out.println("[WeekDay]----[Disponibility]");
        ArrayList<Period> studyCycle = dayPeriodAvailable.getStudyCycle();

        int cycleIt = 0;
        int studyCycleSize = studyCycle.size();
        for (Period period: studyCycle) {

            System.out.println( "" + cycleIt+1 + " " +
                                period.getMorning() + " " +
                                period.getAfternoon() + " " +
                                period.getNight());

            cycleIt = (cycleIt == studyCycleSize-1) ? 0 : ++cycleIt;
        }
    }
}
