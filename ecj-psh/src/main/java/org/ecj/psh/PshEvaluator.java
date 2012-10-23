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
import ec.gp.koza.KozaFitness;
import ec.simple.SimpleEvaluator;
import ec.util.Parameter;

/**
 * Evaluator for PushGP problems. Holds interpreters for each thread in run.
 * 
 * @author Tomasz Kamiński
 * 
 */
public class PshEvaluator extends SimpleEvaluator {

	public final static String P_IDEAL_THRESHOLD = "ideal-threshold";
	
	/** Threshold of standardized fitness (mean absolute error) 
	 * to which individuals are treated as ideal */
	public float idealThreshold;
	
	/**
	 * Sets up the interpreters for each thread.
	 */
	public void setup(final EvolutionState state, final Parameter base) {
		super.setup(state, base);
		Parameter def = PshDefaults.base();
		idealThreshold = state.parameters.getFloatWithDefault(
				base.push(P_IDEAL_THRESHOLD), def.push(P_IDEAL_THRESHOLD), 0.0f);
	}
	
	/**
	 * The SimpleEvaluator determines that a run is complete by asking each
	 * individual in each population if he's optimal; if he finds an individual
	 * somewhere that's optimal, he signals that the run is complete.
	 */
	public boolean runComplete(final EvolutionState state) {
		for (int x = 0; x < state.population.subpops.length; x++)
			for (int y = 0; y < state.population.subpops[x].individuals.length; y++) {
				KozaFitness fitness = (KozaFitness) state.population.subpops[x].individuals[y].fitness;
				if (fitness.isIdealFitness()
						|| fitness.standardizedFitness() <= idealThreshold)
					return true;
			}

		return false;
	}

}
