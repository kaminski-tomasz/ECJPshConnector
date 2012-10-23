/*
* Copyright 2009-2010 Tomasz Kamiński
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/

package org.ecj.psh;

import ec.EvolutionState;
import ec.Individual;
import ec.gp.koza.KozaFitness;
import ec.simple.SimpleProblemForm;
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
	
	 /** Logs the best individual of the run. */
	public void finalStatistics(final EvolutionState state, final int result) {
//		for (int x = 0; x < state.population.subpops.length; x++) {
//			PshIndividual ind = (PshIndividual) best_of_run[x];
//			KozaFitness fitness = (KozaFitness) ind.fitness;
//			state.output.println(
//					"" + fitness.isIdealFitness() + " " + ind.size() + " "
//							+ fitness.standardizedFitness() + " "
//							+ fitness.adjustedFitness(), statisticslog);
//		}
	}

}
