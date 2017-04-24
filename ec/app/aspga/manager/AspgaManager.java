package ec.app.aspga.manager;

import java.io.IOException;
import java.io.InputStream;

import ec.EvolutionState;
import ec.Evolve;
import ec.Individual;
import ec.app.aspga.DayPlanGene;
import ec.app.aspga.DayPlanGeneVectorIndividual;
import ec.app.aspga.bean.StudyPlanGenerated;
import ec.simple.SimpleStatistics;
import ec.util.Output;
import ec.util.ParameterDatabase;
import ec.vector.Gene;

/**
 * Esta classe é responsável por intermediar a comunicação do aspga com o ecj.
 */
public class AspgaManager {

	/**
	 * Este método é responsável por chamar a biblioteca ASPGA, que gerará o plano de estudo ideal a partir dos parâmetros recebidos.
	 *
	 * @param aspgaContext
	 *            contém as informações necessárias para criação do plano de estudo, como nome do estudante, as horas disponíveis para lazer, matérias a serem
	 *            estudadas, disponibilidade por período e facilidade de aprendizado por período
	 * @return o <code>StudyPlan</code> "ideal", ou <code>null</code>, caso tenha ocorrido algum erro na geração
	 */
	public StudyPlanGenerated createPlanWithECJ(InputStream configStreamParams) {

		ParameterDatabase parameterDatabase = null;
		EvolutionState evolutionState = null;

		try {
			parameterDatabase = new ParameterDatabase(configStreamParams);

			Output output = silentECJ();

			evolutionState = Evolve.initialize(parameterDatabase, 0, output);
			evolutionState.run(EvolutionState.C_STARTED_FRESH);

			// Pega o melhor Individual da run
			Individual[] inds = ((SimpleStatistics) evolutionState.statistics).getBestSoFar();

			StudyPlanGenerated studyPlanGenerated = null;

			for (Individual ind : inds) {
				// Aqui imprime o melhor
				// ind.printIndividualForHumans(evolutionState,0);
				// Transforma o Individual em DayPlanGeneVectorIndividual
				DayPlanGeneVectorIndividual dp = (DayPlanGeneVectorIndividual) ind;

				// Pega os genes do DayPlanGeneVectorIndividual
				Gene[] genes = ((Gene[]) dp.getGenome());

				DayPlanGene[] dpgs = new DayPlanGene[genes.length];
				// Transforma os genes em DayPlanGene
				for (int i = 0; i < genes.length; i++) {
					dpgs[i] = (DayPlanGene) genes[i];
				}

				// Aqui deve ser montado o objeto do StudyPlan propriamente dito, com as matérias e duração (workload/2)
				studyPlanGenerated = StudyPlanGenerated.newInstance(dpgs);
			}

			return studyPlanGenerated;

		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * Responsible to quiet stdout and stderr from ECJ
	 * 
	 * @return
	 */
	private Output silentECJ() {
		Output output = Evolve.buildOutput();
		output.getLog(0).silent = true; // stdout
		output.getLog(1).silent = true; // stderr
		return output;
	}

}
