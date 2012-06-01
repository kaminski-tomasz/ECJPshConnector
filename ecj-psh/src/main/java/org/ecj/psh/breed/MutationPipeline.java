package org.ecj.psh.breed;

import org.ecj.psh.PshIndividual;
import org.ecj.psh.PshNodeSelector;
import org.ecj.psh.PshProblem;
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

	/** How the pipeline chooses a subtree to mutate */
	public PshNodeSelector nodeSelector;

	public boolean useFairMutation;

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
	public Object clone() {
		MutationPipeline c = (MutationPipeline) (super.clone());
		// deep-cloned stuff
		c.nodeSelector = (PshNodeSelector) (nodeSelector.clone());
		return c;
	}

	@Override
	public void setup(final EvolutionState state, final Parameter base) {
		super.setup(state, base);

		Parameter def = defaultBase();
		Parameter p = base.push(P_NODESELECTOR).push("" + 0);
		Parameter d = def.push(P_NODESELECTOR).push("" + 0);

		nodeSelector = (PshNodeSelector) (state.parameters
				.getInstanceForParameter(p, d, PshNodeSelector.class));
		nodeSelector.setup(state, p);

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

		// we hold Interpreter in Problem object
		PshProblem problem = ((PshProblem) (state.evaluator.p_problem));

		// mutate 'em
		for (int i = start; i < n + start; i++) {

			PshIndividual ind = (PshIndividual) inds[i];

			int totalsize = ind.program.programsize();
			int which = this.nodeSelector.pickNode(state, subpopulation,
					thread, ind);

			int oldsize = ind.program.SubtreeSize(which);
			int newsize = 0;

			if (useFairMutation) {
				int range = (int) Math.max(1, fairMutationRange * oldsize);
				newsize = Math.max(1,
						oldsize + state.random[thread].nextInt(2 * range)
								- range);
			} else {
				newsize = state.random[thread].nextInt(problem.maxRandomCodeSize);
			}

			Object newtree;

			if (newsize == 1)
				newtree = problem.interpreter.RandomAtom(state, thread);
			else
				newtree = problem.interpreter.RandomCode(state, thread, newsize);

			if (newsize + totalsize - oldsize <= problem.maxPointsInProgram)
				ind.program.ReplaceSubtree(which, newtree);
		}

		return n;
	}

}
