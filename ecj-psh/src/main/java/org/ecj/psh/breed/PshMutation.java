package org.ecj.psh.breed;

import org.ecj.psh.PshEvolutionState;
import org.ecj.psh.PshIndividual;
import org.spiderland.Psh.Interpreter;

import ec.EvolutionState;
import ec.util.Parameter;

/**
 * 
 * Mutation pipeline based on Psh implementation by J.Klein
 * 
 * @author Tomasz Kamiñski
 * 
 */
public class PshMutation extends MutationPipeline {

	public static final String P_MUTATION = "psh-mutate";
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
	protected void mutate(PshIndividual ind, EvolutionState state, int thread,
			int subpopulation) {

		Interpreter interpreter = ((PshEvolutionState) state).interpreter[thread];
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
					oldsize + state.random[thread].nextInt(2 * range) - range);
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
