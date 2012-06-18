package org.ecj.psh;

import org.spiderland.Psh.Interpreter;

import ec.EvolutionState;
import ec.Initializer;
import ec.gp.koza.KozaFitness;
import ec.simple.SimpleEvaluator;
import ec.util.Parameter;

/**
 * Evaluator for PushGP problems. Holds interpreters for each thread in run.
 * 
 * @author Tomek
 * 
 */
public class PshEvaluator extends SimpleEvaluator {

	public final static String P_INTERPRETER = "interpreter";
	public final static String P_IDEAL_THRESHOLD = "ideal-threshold";
	
	/** Interpreter used in evaluating individuals */
	public Interpreter[] interpreter;

	/** Threshold of standardized fitness (mean absolute error) 
	 * to which individuals are treated as ideal */
	public float idealThreshold;
	
	/**
	 * Sets up the interpreters for each thread.
	 */
	public void setup(final EvolutionState state, final Parameter base) {
		super.setup(state, base);
		Parameter def = PshDefaults.base();
		int numOfInterpreters = state.evalthreads > state.breedthreads ? state.evalthreads
				: state.breedthreads;
		Parameter p = base.push(P_INTERPRETER);
		interpreter = new Interpreter[numOfInterpreters];
		for (int i = 0; i < numOfInterpreters; i++) {
			interpreter[i] = (Interpreter) (state.parameters
					.getInstanceForParameterEq(p, def.push(P_INTERPRETER), Interpreter.class));
			interpreter[i].Initialize(state.random[i]);
			interpreter[i].setup(state, p);
		}
		
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
