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

package org.ecj.psh.breed;

import org.ecj.psh.PshIndividual;

import ec.BreedingPipeline;
import ec.EvolutionState;
import ec.Individual;

/**
 * 
 * Abstract mutation pipeline. The code is based on ECJ documentation
 * 
 * @author Tomasz Kamiñski
 * 
 */
abstract public class MutationPipeline extends PshBreedingPipeline {

	public static final int NUM_SOURCES = 1;

	@Override
	public int numSources() {
		return NUM_SOURCES;
	}

	@Override
	public int produce(int min, int max, int start, int subpopulation,
			Individual[] inds, EvolutionState state, int thread) {

		int n = sources[0].produce(min, max, start, subpopulation, inds, state,
				thread);

		// should we bother mutating?
		if (!state.random[thread].nextBoolean(likelihood)) {
			return reproduce(n, start, subpopulation, inds, state, thread,
					false);
		}

		// clone individuals
		if (!(sources[0] instanceof BreedingPipeline)) {
			for (int i = start; i < n + start; i++) {
				inds[i] = (Individual) (inds[i].clone());
			}
		}

		// mutate 'em
		for (int i = start; i < n + start; i++) {
			PshIndividual ind = (PshIndividual) inds[i];
			mutate(ind, state, thread, subpopulation);
		}
		return n;
	}

	abstract protected void mutate(PshIndividual ind, EvolutionState state,
			int thread, int subpopulation);

}
