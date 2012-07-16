package org.ecj.psh.breed;

import org.ecj.psh.PshIndividual;
import org.ecj.psh.PshProblem;
import org.ecj.psh.util.Simplifier;

import ec.EvolutionState;
import ec.Individual;
import ec.util.Parameter;

/**
 * Simplification pipeline reproduction based on Psh implementation
 * 
 * @author Tomasz Kamiñski
 * 
 */
public class PshSimplification extends MutationPipeline {
	public static final String P_SIMPLIFICATION = "psh-simplify";
	public static final String P_STEPS = "steps";
	public static final String P_FLATTENPROB = "flatten-prob";

	/** How many simplifications should be applied */
	public int simplificationSteps;

	/** Probability of choosing simplification by flattening */
	public float simplifyByFlattenProb;

	/** Temporary object holding Push problem instance */
	private PshProblem problem;

	@Override
	public Parameter defaultBase() {
		return PshBreedDefaults.base().push(P_SIMPLIFICATION);
	}

	@Override
	public void setup(final EvolutionState state, final Parameter base) {
		super.setup(state, base);
		Parameter def = this.defaultBase();
		// number of simplifications used in reproduction
		simplificationSteps = state.parameters.getIntWithDefault(
				base.push(P_STEPS), def.push(P_STEPS), 20);

		// probability of simplification by flattening
		simplifyByFlattenProb = state.parameters.getFloatWithDefault(
				base.push(P_FLATTENPROB), def.push(P_FLATTENPROB), 0.2);
	}

	@Override
	public int produce(int min, int max, int start, int subpopulation,
			Individual[] inds, EvolutionState state, int thread) {

		// we need Problem to evaluate individual
		problem = ((PshProblem) (state.evaluator.p_problem).clone());

		return super.produce(min, max, start, subpopulation, inds, state,
				thread);
	}

	@Override
	protected void mutate(PshIndividual ind, EvolutionState state, int thread,
			int subpopulation) {
		Simplifier.autoSimplify(state, ind, problem, simplificationSteps,
				subpopulation, thread, simplifyByFlattenProb);
		ind.evaluated = false;
	}

}
