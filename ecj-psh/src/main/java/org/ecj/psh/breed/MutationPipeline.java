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
