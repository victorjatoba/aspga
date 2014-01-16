/*
  Copyright 2013 by Victor Jatoba
  Licensed under the Academic Free License version 3.0
  See the file "LICENSE" for more information
*/


package ec.app.pea;
import ec.util.*;
import ec.*;
import ec.gp.*;
import ec.gp.koza.*;
import ec.simple.*;

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
    public static final String P_INTELECTUALAVAIBLE = "intelectualavaible";
    public static final String P_DAYPERIODAVAIBLE = "dayperiodavaible";
    public static final String P_STUDENTINFORMATION = "studentinformation";

    public void

    public void setup(  final EvolutionState state,
                        final Parameter base)
    {

        File courseInformationInput  = null;
        File intelectuAlavaibleInput = null;
        File dayPeriodAvaibleInput   = null;
        File studentInformationInput = null;

        courseInformationInput  = state.parameters.getFile(base.push(P_COURSEINFORMATION),null);
        intelectuAlavaibleInput = state.parameters.getFile(base.push(P_INTELECTUALAVAIBLE),null);
        dayPeriodAvaibleInput   = state.parameters.getFile(base.push(P_DAYPERIODAVAIBLE),null);
        studentInformationInput = state.parameters.getFile(base.push(P_STUDENTINFORMATION),null);

        verifyInputExistence(courseInformationInput, intelectuAlavaibleInput, dayPeriodAvaibleInput, studentInformationInput);

        convertFileToString(courseinformation);
        convertFileToString(intelectualavaible);
        convertFileToString(dayperiodavaible);
        convertFileToString(studentinformation);

    }

    /**
    *   Verify if the inputs are not null.
    */
    public void verifyInputExistence(   File courseInformationInput,
                                        File intelectuAlavaibleInput,
                                        File dayPeriodAvaibleInput,
                                        File studentInformationInput)
    {

        if (courseInformationInput == null) {
            state.output.error("CourseInformation File doesn't exist", base.push(P_COURSEINFORMATION));
        }
        if (intelectuAlavaibleInput == null) {
            state.output.error("IntelectuAlavaible File doesn't exist", base.push(P_INTELECTUALAVAIBLE));
        }
        if (dayPeriodAvaibleInput == null) {
            state.output.error("DayPeriodAvaible File doesn't exist", base.push(P_DAYPERIODAVAIBLE));
        }
        if (studentInformationInput == null) {
            state.output.error("StudentInformation File doesn't exist", base.push(P_STUDENTINFORMATION));
        }

        state.output.exitIfErrors();
    }

    public void convertFileToString(File file) {
        try {

            inputStream = new FileInputStream(file);

            br = new BufferedReader(new InputStreamReader(inputStream));

            StringBuilder sb = new StringBuilder();

            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            System.out.println(sb.toString());
            System.out.println("\nDone!");

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

        ((SimpleFitness)ind2.fitness).setFitness(state,
            /// ...the fitness...
            (float)(((double)sum)/ind2.genome.length),
            ///... is the individual ideal?  Indicate here...
            sum == ind2.genome.length);

        ind2.evaluated = true;

    }
}
