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
        DayPlanGene geneI;
        int point;

	if (genome.length != i.genome.length) {
		state.output.fatal("Genome lengths are not the same for fixed-length vector crossover");
	}

	ArrayList<SubjectWorkload> periodTmp;
	switch(s.crossoverType) {
		case VectorSpecies.C_ONE_POINT:
			point = state.random[thread].nextInt((genome.length / s.chunksize)+1);
			//for(int x=0; x < point*s.chunksize; x++) {
			int x = state.random[thread].nextInt(genome.length);
			int y = state.random[thread].nextInt(i.genome.length);
				geneI = (DayPlanGene)i.genome[y];

				int periodOfTheDay = state.random[thread].nextInt(3);
				int periodOfTheDay2 = state.random[thread].nextInt(3);
				ArrayList<SubjectWorkload> periodI;
	            if(periodOfTheDay == 0) {
					String name = (geneI.getMorning().isEmpty() != true) ? geneI.getMorning().get(0).getSubject().getName() : "empty";
					System.out.println("i: " +  name);
	                periodTmp = geneI.getMorning();

					if(periodOfTheDay2 == 0) {
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
	            } else if (periodOfTheDay == 1) {
	            	String name = (geneI.getAfternoon().isEmpty() != true) ? geneI.getAfternoon().get(0).getSubject().getName() : "empty";
	            	System.out.println("i: " + name);
	                periodTmp = geneI.getAfternoon();
					//------------------------------
					if(periodOfTheDay2 == 0) {
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
	            } else {
	            	String name = (geneI.getNight().isEmpty() != true) ? geneI.getNight().get(0).getSubject().getName() : "empty";
	            	System.out.println("i: " + name);
	                periodTmp = geneI.getNight();

	                //------------------------------
					if(periodOfTheDay2 == 0) {
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
	            }

	            String name = (periodTmp.isEmpty() != true) ? periodTmp.get(0).getSubject().getName() : "empty";
				System.out.println("tmp: " + name);
			//}
			break;
		}
    }

}
