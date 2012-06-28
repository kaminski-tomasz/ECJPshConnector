package org.ecj.psh;

import ec.EvolutionState;
import ec.Individual;
import ec.gp.koza.KozaFitness;
import ec.simple.SimpleShortStatistics;

/**
 * Subclass of SimpleShortStatics. The only difference is that we write standardized fitnesses to stat file.
 * 
 * @author Tomasz Kamiński
 *
 */
public class PshShortStatistics extends SimpleShortStatistics {

	/**
	 * Prints out the statistics, but does not end with a println -- this lets
	 * overriding methods print additional statistics on the same line
	 */
	protected void _postEvaluationStatistics(final EvolutionState state) {
		// gather timings
		super._postEvaluationStatistics(state);
		
		// TODO check somehow whether Individuals have KozaFitness
		
		Individual[] best_i = new Individual[state.population.subpops.length];
		for (int x = 0; x < state.population.subpops.length; x++) {
			
			// standardized fitness information
			double meanStandardizedFitness = 0.0;

			for (int y = 0; y < state.population.subpops[x].individuals.length; y++) {
				// best individual
				if (best_i[x] == null
						|| state.population.subpops[x].individuals[y].fitness
								.betterThan(best_i[x].fitness))
					best_i[x] = state.population.subpops[x].individuals[y];

				// mean fitness for population
				meanStandardizedFitness += ((KozaFitness)state.population.subpops[x].individuals[y].fitness)
						.standardizedFitness();
			}

			// compute fitness stats
			meanStandardizedFitness /= state.population.subpops[x].individuals.length;
			state.output
					.print(""
							+ meanStandardizedFitness
							+ " "
							+ ((KozaFitness) best_i[x].fitness)
									.standardizedFitness()
							+ " "
							+ ((KozaFitness) best_of_run[x].fitness)
									.standardizedFitness() + " ", statisticslog);
			

		}
		// we're done!
	}

}
