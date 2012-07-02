package org.ecj.psh.breed;

import org.ecj.psh.PshEvaluator;
import org.ecj.psh.PshIndividual;
import org.spiderland.Psh.Interpreter;

import ec.BreedingPipeline;
import ec.EvolutionState;
import ec.Individual;
import ec.util.Parameter;

/**
 * 
 * Mutation pipeline based on Psh implementation
 * 
 * @author Tomasz Kamiñski
 * 
 */
public class MutationPipeline extends PshBreedingPipeline {

	public static final String P_MUTATION = "mutate";
	public static final String P_FAIR = "use-fair";
	public static final String P_FAIRRANGE = "fair-mutation-range";
	public static final int NUM_SOURCES = 1;

	/** should we use fair mutation */
	public boolean useFairMutation;

	/** range for fair mutation */
	public float fairMutationRange;

	@Override
	public Parameter defaultBase() {
		return PshBreedDefaults.base().push(P_MUTATION);
	}

	@Override
	public int numSources() {
		return NUM_SOURCES;
	}

	@Override
	public void setup(final EvolutionState state, final Parameter base) {
		super.setup(state, base);
		Parameter def = defaultBase();
		// should we use fair mutation mode?
		useFairMutation = state.parameters.getBoolean(base.push(P_FAIR),
				def.push(P_FAIR), true);

		// fair mutation range
		fairMutationRange = state.parameters.getFloatWithDefault(
				base.push(P_FAIRRANGE), def.push(P_FAIRRANGE), 0.3);
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
			mutate(ind, state, thread);
		}
		return n;
	}

	protected void mutate(PshIndividual ind, EvolutionState state, int thread) {
		
		Interpreter interpreter = ((PshEvaluator) state.evaluator).interpreter[thread]; 
		int maxPointsInProgram = interpreter.getMaxPointsInProgram();
		int maxRandomCodeSize = interpreter.getMaxRandomCodeSize();
		
		int totalsize = ind.program.programsize();
		int which = 0;
		if (totalsize > 0)
			which = state.random[thread].nextInt(totalsize);

		int oldsize = ind.program.SubtreeSize(which);
		int newsize = 0;

		if (useFairMutation) {
			int range = (int) Math.max(1, fairMutationRange * oldsize);
			newsize = Math.max(1,
					oldsize + state.random[thread].nextInt(2 * range)
							- range);
		} else {
			newsize = state.random[thread].nextInt(maxRandomCodeSize);
		}

		Object newtree;

		if (newsize == 1)
			newtree = interpreter.RandomAtom();
		else
			newtree = interpreter.RandomCode(newsize);

		if (newsize + totalsize - oldsize <= maxPointsInProgram) {
			ind.program.ReplaceSubtree(which, newtree);
			ind.evaluated = false;
		}
	}

}
