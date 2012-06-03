package org.ecj.psh;

import org.spiderland.Psh.Program;

import ec.EvolutionState;
import ec.Fitness;
import ec.Individual;
import ec.Species;
import ec.util.Parameter;

/**
 * Species subclass for PushGP individuals
 * 
 * @author Tomasz Kamiñski
 * 
 */
public class PshSpecies extends Species {
	public static final String P_PSHSPECIES = "species";

	@Override
	public Parameter defaultBase() {
		return PshDefaults.base().push(P_PSHSPECIES);
	}

	@Override
	public void setup(final EvolutionState state, final Parameter base) {
		super.setup(state, base);
		// check to make sure that our individual prototype is a PshIndividual
		if (!(i_prototype instanceof PshIndividual))
			state.output.fatal("The Individual class " + "for the Species "
					+ getClass().getName() + " must be a subclass of "
					+ "org.ecj.psh.PshIndividual.", base);
	}

	@Override
	public Individual newIndividual(EvolutionState state, int thread) {
		PshIndividual newind = ((PshIndividual) (i_prototype)).clone();

		// problem
		PshProblem problem = (PshProblem)state.evaluator.p_problem;
		
		// Generate random program
		int randomCodeSize = state.random[thread]
				.nextInt(problem.maxRandomCodeSize) + 2;
		
		newind.program = problem.interpreter.RandomCode(state, thread,
				randomCodeSize);

		// Set the fitness
		newind.fitness = (Fitness) (f_prototype.clone());
		newind.evaluated = false;

		// Set the species to me
		newind.species = this;

		// ...and we're ready!
		return newind;
	}
}
