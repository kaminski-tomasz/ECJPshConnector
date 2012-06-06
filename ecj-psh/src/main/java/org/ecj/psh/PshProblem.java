package org.ecj.psh;

import org.ecj.psh.util.Simplifier;
import org.spiderland.Psh.Interpreter;
import org.spiderland.Psh.Program;

import ec.EvolutionState;
import ec.Individual;
import ec.Problem;
import ec.simple.SimpleProblemForm;
import ec.util.Parameter;

/**
 * Base class for problems solved with PushGP
 * 
 * @author Tomasz Kamiński
 * 
 */
public abstract class PshProblem extends Problem implements SimpleProblemForm {

	public static final String P_PSHPROBLEM = "problem";

	/**
	 * PshProblem defines a default base so your subclass doesn't absolutely
	 * have to.
	 */
	@Override
	public Parameter defaultBase() {
		return PshDefaults.base().push(P_PSHPROBLEM);
	}

	/**
	 * Set up prototype for PshProblem.
	 */
	@Override
	public void setup(final EvolutionState state, final Parameter base) {
		super.setup(state, base);
		Parameter def = defaultBase();
	}

	@Override
	public Object clone() {
		PshProblem newProblem = (PshProblem) super.clone();
		return newProblem;
	}

	@Override
	public void describe(final EvolutionState state, final Individual ind,
			final int subpopulation, final int threadnum, final int log) {

		state.output.println("After simplifications: ", log);

		Simplifier.autoSimplify(state, (PshIndividual) ind, this, 1000,
				subpopulation, threadnum, 0.2f);
		state.output.println("\t" + ind, log);
	}
}
