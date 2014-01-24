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

    public static final String P_COURSEINFORMATION = "courseinformation";
    public static final String P_INTELECTUALAVAILABLE = "intelectualavailable";
    public static final String P_DAYPERIODAVAILABLE = "dayperiodavailable";
    public static final String P_STUDENTINFORMATION = "studentinformation";

    public EvolutionState state;
    public Parameter base;

    ArrayList<Subject> subjects;
    Student student;
    PeriodAvailable dayPeriodAvailable;
    PeriodAvailable intelectualAvailable;

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
    *   Fill the PeriodAvailable information with the input file
    */
    public PeriodAvailable fillPeriodAvailable(Vector<String> periodAvailables) {
        PeriodAvailable periodAvailable = new PeriodAvailable();
        Period[] periods = getPeriodsStandarized(periodAvailables);

        periodAvailable.setMonday(periods[0]);
        periodAvailable.setTuesday(periods[1]);
        periodAvailable.setWednesday(periods[2]);
        periodAvailable.setThursday(periods[3]);
        periodAvailable.setFriday(periods[4]);
        periodAvailable.setSaturday(periods[5]);
        periodAvailable.setSunday(periods[6]);

        return periodAvailable;
    }

    /**
    *   Change a vector of the string in a array of the Periods
    */
    public Period[] getPeriodsStandarized(Vector<String> periodAvailables) {
        Period[] periods = new Period[7];

        if (periodAvailables != null) {
            int i = 0;
            for (String line: periodAvailables) {
                String[] periodAvailablesInfo = line.split(" ");

                Period period = new Period();
                period.setMorning(periodAvailablesInfo[0].charAt(0));
                period.setAfternoon(periodAvailablesInfo[1].charAt(0));
                period.setNight(periodAvailablesInfo[2].charAt(0));

                periods[i++] = period;
            }

        } else {
            state.output.error("SSPP| Error: The periodAvailables information are null!");
        }

        return periods;
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

        System.out.println("\n----------------"+ type +"--------------------!");

        for (String lineIt: lines) {
            state.output.message("courseInformationInput = " + lineIt);
            System.out.println();
        }

        System.out.println("\n----------------Done!-------------------");

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
        GeneVectorIndividual indGeneVector = (GeneVectorIndividual)ind;

        if (!(indGeneVector.fitness instanceof SimpleFitness)) {
            state.output.fatal("Whoa!  It's not a SimpleFitness!!!",null);
        }

        float fitnessValue = calculateFitnessValue(indGeneVector);

        ((SimpleFitness)indGeneVector.fitness).setFitness(state,
            /// ...the fitness...
            fitnessValue,
            ///... is the individual ideal?  Indicate here...
            (fitnessValue == (float)200.0));

        indGeneVector.evaluated = true;
    }

    public float calculateFitnessValue(GeneVectorIndividual indGeneVector) {
/*        ((acepts*100)/qtdFixedConstraints) + ((acepts*100)/qtdFixedConstraints)
        (float)(((double)sum)/ind2.genome.length)
*/
        return 0.0f;
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
    *   Check if have subjects in the period of the day if the
    *   user don't have disponibility.
    */
    public void SubjectInInappropriatePeriod() {
    }

    /**
    *   Check if the hours to leisure foi atendida.
    */
    public void hoursToLeisure() {
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

        System.out.println("-----------------------\nStudent:");
        System.out.println("Name: " + this.student.getName());
        System.out.println("hoursToLeisure: " + this.student.getHoursToLeisure());

        System.out.println("-----------------------\nDayPeriodAvailable: ");
        Period period = new Period();
        period = this.dayPeriodAvailable.getMonday();
        System.out.println("Monday: " +     period.getMorning() + " " +
                                            period.getAfternoon() + " " +
                                            period.getNight());
        period = this.dayPeriodAvailable.getTuesday();
        System.out.println("Tuesday: " +    period.getMorning() + " " +
                                            period.getAfternoon() + " " +
                                            period.getNight());
        period = this.dayPeriodAvailable.getWednesday();
        System.out.println("Wednesday: " +     period.getMorning() + " " +
                                            period.getAfternoon() + " " +
                                            period.getNight());

    }
}
