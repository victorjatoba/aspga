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

public class SchedulingStudyPlanProblem extends Problem implements SimpleProblemForm
{
    private static final long serialVersionUID = 1;

    public void setup(  final EvolutionState state,
                        final Parameter base)
    {

    }

    /**
    * implementa a ftness function
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
