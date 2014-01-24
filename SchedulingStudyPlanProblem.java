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
    public static final char MEDIAN =   'M';
    public static final char BAD =      'B';

    public EvolutionState state;
    public Parameter base;

    ArrayList<Subject> subjects;
    Student student;
    PeriodAvailable dayPeriodAvailable;
    PeriodAvailable intelectualAvailable;

    GeneVectorIndividual individual;

    public void setup (  final EvolutionState state, final Parameter base) {
        this.state = state;
        this.base = base;

        File courseInformationInput     = null;
        File intelectualAvailableInput  = null;
        File dayPeriodAvailableInput    = null;
        File studentInformationInput    = null;

        courseInformationInput      = state.parameters.getFile(base.push(P_COURSEINFORMATION),null);
        intelectualAvailableInput   = state.parameters.getFile(base.push(P_INTELECTUALAVAILABLE),null);
        dayPeriodAvailableInput     = state.parameters.getFile(base.push(P_DAYPERIODAVAILABLE),null);
        studentInformationInput     = state.parameters.getFile(base.push(P_STUDENTINFORMATION),null);

        verifyInputExistence(courseInformationInput, intelectualAvailableInput, dayPeriodAvailableInput, studentInformationInput);

        Vector<String> courseInformationVector = convertFileToVectorString(courseInformationInput);
        Vector<String> intelectualAvailableVector = convertFileToVectorString(intelectualAvailableInput);
        Vector<String> dayPeriodAvailableVector = convertFileToVectorString(dayPeriodAvailableInput);
        Vector<String> studentInformationVector = convertFileToVectorString(studentInformationInput);

        printInputLines(courseInformationVector, "CourseInformation");

        this.subjects = fillSubjects(courseInformationVector);
        this.student = fillStudent(studentInformationVector);
        this.dayPeriodAvailable = fillPeriodAvailable(dayPeriodAvailableVector);
        this.intelectualAvailable = fillPeriodAvailable(intelectualAvailableVector);
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

        int sum=0;
        individual = (GeneVectorIndividual)ind;

        if (!(individual.fitness instanceof SimpleFitness)) {
            state.output.fatal("Whoa!  It's not a SimpleFitness!!!",null);
        }

        float fitnessValue = calculateFitnessValue();

        ((SimpleFitness)individual.fitness).setFitness(state,
            /// ...the fitness...
            fitnessValue,
            ///... is the individual ideal?  Indicate here...
            (fitnessValue == 200.0f));

        individual.evaluated = true;
    }

    public float calculateFitnessValue() {

/*        ((acepts*100)/qtdFixedConstraints) + ((acepts*100)/qtdFixedConstraints)
        (float)(((double)sum)/ind2.genome.length)
*/
        float fitness = subjectInInappropriatePeriod() ? 200.0f : 0.0f;
        return fitness;
    }

    /**
    *   Hard Subjects should be alocated in the period of the day that the user
    *   have more intelectual facility for to learn.
    */
    public void hardSubjectInEasyPeriod() {
    }

    /**
    *   Check if the subjects are studies in one time only.
    */
    public void notCostAllTimeInTheSameSubject() {
    }

    /**
    *   Check if the study plan have grow-up learn.
    */
    public void toStudyGradually() {
    }

   /**
    * Check if have subjects in the period of the day who the
    * user don't have disponibility.
    *
    * Classification: Fixed Constraint.
    *
    * @return
    */
    public Boolean subjectInInappropriatePeriod() {
        long individualLength = individual.size();
        int cycleIt = 0;
        ArrayList<Period> studyCycle = this.dayPeriodAvailable.getStudyCycle();
        int studyCycleSize = studyCycle.size();
        //dayPeriodAvailable;

        for (int i = 0; i < individualLength; i++) {
            DayPlanGene gene = (DayPlanGene) individual.genome[i];

            if (studyCycle.get(cycleIt).getMorning() == NOTHING && gene.getMorning().size() > 0) {
                return Boolean.FALSE;
            } else if (studyCycle.get(cycleIt).getAfternoon() == NOTHING && gene.getAfternoon().size() > 0) {
                return Boolean.FALSE;
            } else if (studyCycle.get(cycleIt).getNight() == NOTHING && gene.getNight().size() > 0) {
                return Boolean.FALSE;
            }

            cycleIt++;
            if(cycleIt == studyCycleSize){
                cycleIt = 0;
            }
        }

        return Boolean.TRUE;
    }

    /**
    *   Check if the hours to leisure foi atendida.
    */
    public void hoursToLeisure() {
    }

    /**
    *   Fill the PeriodAvailable information with the input file
    */
    public PeriodAvailable fillPeriodAvailable(Vector<String> periodAvailables) {
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
    public Student fillStudent(Vector<String> studentIn) {
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
    *   Verify if the inputs are not null.
    */
    public void verifyInputExistence(   File courseInformationInput,
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
    public void printInputLines(Vector<String> lines, String type) {

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
