package org.ecj.psh.breed;

import org.ecj.psh.PshIndividual;
import org.ecj.psh.PshProblem;
import org.ecj.psh.util.Simplifier;
import org.spiderland.Psh.Program;

import ec.BreedingPipeline;
import ec.EvolutionState;
import ec.Individual;
import ec.gp.koza.KozaFitness;
import ec.util.Parameter;

/**
 * Simplification pipeline reproduction based on Psh implementation
 * 
 * @author Tomasz Kamiñski
 * 
 */
public class SimplificationPipeline extends PshBreedingPipeline {
	public static final String P_SIMPLIFICATION = "simplify";
	public static final String P_STEPS = "steps";
	public static final String P_FLATTENPROB = "flatten-prob";
	public static final int NUM_SOURCES = 1;

	/** How many simplifications should be applied */
	public int simplificationSteps;

	/** Probability of choosing simplification by flattening */
	public float simplifyByFlattenProb;

	@Override
	public Parameter defaultBase() {
		return PshBreedDefaults.base().push(P_SIMPLIFICATION);
	}

	@Override
	public int numSources() {
		return NUM_SOURCES;
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

		// we need Problem to evaluate individual
		PshProblem problem = ((PshProblem) (state.evaluator.p_problem).clone());

		// simplify 'em
		for (int i = start; i < n + start; i++) {
			PshIndividual ind = (PshIndividual) inds[i];
			Simplifier.autoSimplify(state, ind, problem, this.simplificationSteps,
					subpopulation, thread, simplifyByFlattenProb);
			ind.evaluated = false;
		}
			
		return 0;
	}

}
