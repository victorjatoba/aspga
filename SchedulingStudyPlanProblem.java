/*
  Copyright 2013 by Victor Jatoba
  Licensed under the Academic Free License version 3.0
  See the file "LICENSE" for more information
*/


package ec.app.aspga;
import ec.util.*;
import ec.*;
import ec.simple.*;
import ec.vector.BitVectorIndividual;
import ec.util.*; //Parameter


//Packages to read
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
        Vector<String> intelectuAlavailableVector = convertFileToVectorString(intelectualAvailableInput);
        Vector<String> dayPeriodAvailableVector = convertFileToVectorString(dayPeriodAvailableInput);
        Vector<String> studentInformationVector = convertFileToVectorString(studentInformationInput);

        printInput(courseInformationVector, "CourseInformation");

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
    public void printInput(Vector<String> lines, String type) {

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

        if (!(ind instanceof BitVectorIndividual)) {
            state.output.fatal("Whoa!  It's not a BitVectorIndividual!!!",null);
        }

        int sum=0;
        BitVectorIndividual ind2 = (BitVectorIndividual)ind;

        if (!(ind2.fitness instanceof SimpleFitness)) {
            state.output.fatal("Whoa!  It's not a SimpleFitness!!!",null);
        }

        float fitnessValue = calculateFitnessValue();

        ((SimpleFitness)ind2.fitness).setFitness(state,
            /// ...the fitness...
            fitnessValue,
            ///... is the individual ideal?  Indicate here...
            sum == ind2.genome.length);

        ind2.evaluated = true;
    }

    public float calculateFitnessValue() {
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
}
