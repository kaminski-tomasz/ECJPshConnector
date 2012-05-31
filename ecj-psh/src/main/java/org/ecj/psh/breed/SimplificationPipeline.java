package org.ecj.psh.breed;

import ec.EvolutionState;
import ec.Individual;
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

	/** Probability of chosing simplification by flattening */
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
		// TODO implement reproduction by simplification based on Psh code
		return 0;
	}

}
