/*
  Copyright 2013 by Victor Jatoba
  Licensed under the Academic Free License version 3.0
  See the file "LICENSE" for more information
*/


package ec.app.aspga;

import ec.vector.*;
import ec.EvolutionState;
import ec.util.*;
import java.util.ArrayList;

/**
 * DayPlanGeneVectorIndividual.java
 *
 * Modified: Thu Nov 26 02:29 2013
 * @author Victor Jatoba
 * @version 1.0.0
 */

@SuppressWarnings("serial")
public class DayPlanGeneVectorIndividual extends GeneVectorIndividual {

	@Override
	public void defaultCrossover(EvolutionState state, int thread, VectorIndividual ind) {
		GeneVectorSpecies s = (GeneVectorSpecies) species;
        GeneVectorIndividual i = (GeneVectorIndividual) ind;

		if (genome.length != i.genome.length) {
			state.output.fatal("Genome lengths are not the same for fixed-length vector crossover");
		}

		switch(s.crossoverType) {
			case VectorSpecies.C_ONE_POINT:
	            perDayCrossover(state, thread, i);
	            break;

	        case VectorSpecies.C_TWO_POINT:
				perPeriodCrossover(state, thread, i);
				break;

			case VectorSpecies.C_ANY_POINT:
				int crossoverType = state.random[thread].nextInt(2);
				if(crossoverType == 0) {
					perPeriodCrossover(state, thread, i);
				} else {
                	perDayCrossover(state, thread, i);
				}
				break;
		}

    }

    public void perDayCrossover(EvolutionState state, int thread, GeneVectorIndividual ind) {
    	GeneVectorSpecies s = (GeneVectorSpecies) species;
        GeneVectorIndividual i = ind;
        Gene tmp;
        int point;

    	point = state.random[thread].nextInt((genome.length / s.chunksize)+1);
        for(int x=0; x < point*s.chunksize; x++) {
			tmp = i.genome[x];
			i.genome[x] = genome[x];
			genome[x] = tmp;
		}
    }

    public void perPeriodCrossover(EvolutionState state, int thread, GeneVectorIndividual ind) {
		GeneVectorSpecies s = (GeneVectorSpecies) species;
        GeneVectorIndividual i = ind;

    	int y = state.random[thread].nextInt(i.genome.length);
		DayPlanGene geneI = (DayPlanGene)i.genome[y];

		int periodOfTheDay = state.random[thread].nextInt(3);

    	int point = state.random[thread].nextInt((genome.length / s.chunksize)+1);
        for(int x=0; x < point*s.chunksize; x++) {
	        if(periodOfTheDay == 0) {
	            switchGeneIMorning(geneI, state, thread);
	        } else if (periodOfTheDay == 1) {
	            switchGeneIAfternoon(geneI, state, thread);
	        } else {
	            switchGeneINight(geneI, state, thread);
	        }
	    }
    }

    public void switchGeneIMorning(DayPlanGene geneI, EvolutionState state, int thread) {
		ArrayList<SubjectWorkload> periodTmp = geneI.getMorning();
		int x = state.random[thread].nextInt(genome.length);
		int periodOfTheDay = state.random[thread].nextInt(3);

		if(periodOfTheDay == 0) {
			geneI.setMorning(((DayPlanGene)genome[x]).getMorning());
			((DayPlanGene)genome[x]).setMorning(periodTmp);
        } else if (periodOfTheDay == 1) {
			geneI.setMorning(((DayPlanGene)genome[x]).getAfternoon());
			((DayPlanGene)genome[x]).setAfternoon(periodTmp);
        } else {
			geneI.setMorning(((DayPlanGene)genome[x]).getNight());
			((DayPlanGene)genome[x]).setNight(periodTmp);
        }
    }

    public void switchGeneIAfternoon(DayPlanGene geneI, EvolutionState state, int thread) {
		ArrayList<SubjectWorkload> periodTmp = geneI.getAfternoon();
		int x = state.random[thread].nextInt(genome.length);
		int periodOfTheDay = state.random[thread].nextInt(3);

		if(periodOfTheDay == 0) {
			geneI.setAfternoon(((DayPlanGene)genome[x]).getMorning());
			((DayPlanGene)genome[x]).setMorning(periodTmp);
        } else if (periodOfTheDay == 1) {
			geneI.setAfternoon(((DayPlanGene)genome[x]).getAfternoon());
			((DayPlanGene)genome[x]).setAfternoon(periodTmp);
        } else {
			geneI.setAfternoon(((DayPlanGene)genome[x]).getNight());
			((DayPlanGene)genome[x]).setNight(periodTmp);
        }
    }
    public void switchGeneINight(DayPlanGene geneI, EvolutionState state, int thread) {
		ArrayList<SubjectWorkload> periodTmp = geneI.getNight();
		int x = state.random[thread].nextInt(genome.length);
		int periodOfTheDay = state.random[thread].nextInt(3);

		if(periodOfTheDay == 0) {
			geneI.setNight(((DayPlanGene)genome[x]).getMorning());
			((DayPlanGene)genome[x]).setMorning(periodTmp);
        } else if (periodOfTheDay == 1) {
			geneI.setNight(((DayPlanGene)genome[x]).getAfternoon());
			((DayPlanGene)genome[x]).setAfternoon(periodTmp);
        } else {
			geneI.setNight(((DayPlanGene)genome[x]).getNight());
			((DayPlanGene)genome[x]).setNight(periodTmp);
        }
    }

    public void switchGeneIMorningDebug(DayPlanGene geneI, EvolutionState state, int thread) {
		System.out.println("[M]");
		ArrayList<SubjectWorkload> periodTmp = geneI.getMorning();
		int x = state.random[thread].nextInt(genome.length);
		int periodOfTheDay = state.random[thread].nextInt(3);

    	String name = (geneI.getMorning().isEmpty() != true) ? geneI.getMorning().get(0).getSubject().getName() : "empty";
		System.out.println("i: " +  name);

		if(periodOfTheDay == 0) {
			geneI.setMorning(((DayPlanGene)genome[x]).getMorning());
			name = (geneI.getMorning().isEmpty() != true) ? geneI.getMorning().get(0).getSubject().getName() : "empty";
			System.out.println("i: " +  name);
            //System.out.println("i2: " + name);

			name = (((DayPlanGene)genome[x]).getMorning().isEmpty() != true) ? ((DayPlanGene)genome[x]).getMorning().get(0).getSubject().getName() : "empty";
			System.out.println("g: " + name);
			((DayPlanGene)genome[x]).setMorning(periodTmp);
			name = (((DayPlanGene)genome[x]).getMorning().isEmpty() != true) ? ((DayPlanGene)genome[x]).getMorning().get(0).getSubject().getName() : "empty";
			System.out.println("g2: " + name);
        } else if (periodOfTheDay == 1) {
			geneI.setMorning(((DayPlanGene)genome[x]).getAfternoon());
			name = (geneI.getMorning().isEmpty() != true) ? geneI.getMorning().get(0).getSubject().getName() : "empty";
			System.out.println("i: " +  name);

			name = (((DayPlanGene)genome[x]).getAfternoon().isEmpty() != true) ? ((DayPlanGene)genome[x]).getAfternoon().get(0).getSubject().getName() : "empty";
			System.out.println("g: " + name);
			((DayPlanGene)genome[x]).setAfternoon(periodTmp);
			name = (((DayPlanGene)genome[x]).getAfternoon().isEmpty() != true) ? ((DayPlanGene)genome[x]).getAfternoon().get(0).getSubject().getName() : "empty";
			System.out.println("g3: " + name);
        } else {
			geneI.setMorning(((DayPlanGene)genome[x]).getNight());
			name = (geneI.getMorning().isEmpty() != true) ? geneI.getMorning().get(0).getSubject().getName() : "empty";
			System.out.println("i: " +  name);

			name = (((DayPlanGene)genome[x]).getNight().isEmpty() != true) ? ((DayPlanGene)genome[x]).getNight().get(0).getSubject().getName() : "empty";
			System.out.println("g: " + name);
			((DayPlanGene)genome[x]).setNight(periodTmp);
			name = (((DayPlanGene)genome[x]).getNight().isEmpty() != true) ? ((DayPlanGene)genome[x]).getNight().get(0).getSubject().getName() : "empty";
			System.out.println("g4: " + name);
        }

        name = (periodTmp.isEmpty() != true) ? periodTmp.get(0).getSubject().getName() : "empty";
		System.out.println("tmp: " + name);
    }

    public void switchGeneIAfternoonDebug(DayPlanGene geneI, EvolutionState state, int thread) {
		System.out.println("[A]");
		ArrayList<SubjectWorkload> periodTmp = geneI.getAfternoon();
		int x = state.random[thread].nextInt(genome.length);
		int periodOfTheDay = state.random[thread].nextInt(3);

		String name = (geneI.getAfternoon().isEmpty() != true) ? geneI.getAfternoon().get(0).getSubject().getName() : "empty";
    	System.out.println("i: " + name);
		//------------------------------
		if(periodOfTheDay == 0) {
			geneI.setAfternoon(((DayPlanGene)genome[x]).getMorning());
            name = (geneI.getAfternoon().isEmpty() != true) ? geneI.getAfternoon().get(0).getSubject().getName() : "empty";
    		System.out.println("i: " + name);

			name = (((DayPlanGene)genome[x]).getMorning().isEmpty() != true) ? ((DayPlanGene)genome[x]).getMorning().get(0).getSubject().getName() : "empty";
			System.out.println("g: " + name);
			((DayPlanGene)genome[x]).setMorning(periodTmp);
			name = (((DayPlanGene)genome[x]).getMorning().isEmpty() != true) ? ((DayPlanGene)genome[x]).getMorning().get(0).getSubject().getName() : "empty";
			System.out.println("g2: " + name);
        } else if (periodOfTheDay == 1) {
			geneI.setAfternoon(((DayPlanGene)genome[x]).getAfternoon());
			name = (geneI.getAfternoon().isEmpty() != true) ? geneI.getAfternoon().get(0).getSubject().getName() : "empty";
    		System.out.println("i: " + name);

			name = (((DayPlanGene)genome[x]).getAfternoon().isEmpty() != true) ? ((DayPlanGene)genome[x]).getAfternoon().get(0).getSubject().getName() : "empty";
			System.out.println("g: " + name);
			((DayPlanGene)genome[x]).setAfternoon(periodTmp);
			name = (((DayPlanGene)genome[x]).getAfternoon().isEmpty() != true) ? ((DayPlanGene)genome[x]).getAfternoon().get(0).getSubject().getName() : "empty";
			System.out.println("g3: " + name);
        } else {
			geneI.setAfternoon(((DayPlanGene)genome[x]).getNight());
			name = (geneI.getAfternoon().isEmpty() != true) ? geneI.getAfternoon().get(0).getSubject().getName() : "empty";
    		System.out.println("i: " + name);

			name = (((DayPlanGene)genome[x]).getNight().isEmpty() != true) ? ((DayPlanGene)genome[x]).getNight().get(0).getSubject().getName() : "empty";
			System.out.println("g: " + name);
			((DayPlanGene)genome[x]).setNight(periodTmp);
			name = (((DayPlanGene)genome[x]).getNight().isEmpty() != true) ? ((DayPlanGene)genome[x]).getNight().get(0).getSubject().getName() : "empty";
			System.out.println("g4: " + name);
        }

        name = (periodTmp.isEmpty() != true) ? periodTmp.get(0).getSubject().getName() : "empty";
		System.out.println("tmp: " + name);
    }

    public void switchGeneINightDebug(DayPlanGene geneI, EvolutionState state, int thread) {
		System.out.println("[N]");
		ArrayList<SubjectWorkload> periodTmp = geneI.getNight();
		int x = state.random[thread].nextInt(genome.length);
		int periodOfTheDay = state.random[thread].nextInt(3);

		String name = (geneI.getNight().isEmpty() != true) ? geneI.getNight().get(0).getSubject().getName() : "empty";
    	System.out.println("i: " + name);

        //------------------------------
		if(periodOfTheDay == 0) {
			geneI.setNight(((DayPlanGene)genome[x]).getMorning());
            name = (geneI.getNight().isEmpty() != true) ? geneI.getNight().get(0).getSubject().getName() : "empty";
    		System.out.println("i: " + name);

			name = (((DayPlanGene)genome[x]).getMorning().isEmpty() != true) ? ((DayPlanGene)genome[x]).getMorning().get(0).getSubject().getName() : "empty";
			System.out.println("g: " + name);
			((DayPlanGene)genome[x]).setMorning(periodTmp);
			name = (((DayPlanGene)genome[x]).getMorning().isEmpty() != true) ? ((DayPlanGene)genome[x]).getMorning().get(0).getSubject().getName() : "empty";
			System.out.println("g2: " + name);
        } else if (periodOfTheDay == 1) {
			geneI.setNight(((DayPlanGene)genome[x]).getAfternoon());
			name = (geneI.getNight().isEmpty() != true) ? geneI.getNight().get(0).getSubject().getName() : "empty";
    		System.out.println("i: " + name);

			name = (((DayPlanGene)genome[x]).getAfternoon().isEmpty() != true) ? ((DayPlanGene)genome[x]).getAfternoon().get(0).getSubject().getName() : "empty";
			System.out.println("g: " + name);
			((DayPlanGene)genome[x]).setAfternoon(periodTmp);
			name = (((DayPlanGene)genome[x]).getAfternoon().isEmpty() != true) ? ((DayPlanGene)genome[x]).getAfternoon().get(0).getSubject().getName() : "empty";
			System.out.println("g3: " + name);
        } else {
			geneI.setNight(((DayPlanGene)genome[x]).getNight());
			name = (geneI.getNight().isEmpty() != true) ? geneI.getNight().get(0).getSubject().getName() : "empty";
    		System.out.println("i: " + name);

			name = (((DayPlanGene)genome[x]).getNight().isEmpty() != true) ? ((DayPlanGene)genome[x]).getNight().get(0).getSubject().getName() : "empty";
			System.out.println("g: " + name);
			((DayPlanGene)genome[x]).setNight(periodTmp);
			name = (((DayPlanGene)genome[x]).getNight().isEmpty() != true) ? ((DayPlanGene)genome[x]).getNight().get(0).getSubject().getName() : "empty";
			System.out.println("g4: " + name);
        }
        name = (periodTmp.isEmpty() != true) ? periodTmp.get(0).getSubject().getName() : "empty";
		System.out.println("tmp: " + name);
    }

}
