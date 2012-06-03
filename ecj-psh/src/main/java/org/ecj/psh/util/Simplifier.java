package org.ecj.psh.util;

import org.ecj.psh.PshIndividual;
import org.ecj.psh.PshProblem;
import org.spiderland.Psh.Program;

import ec.EvolutionState;
import ec.Individual;

/**
 * Simplify individual by removing some nodes or flattening them.
 * Based on Psh implementation by J.Klein
 * 
 * @author Tomek
 *
 */
public class Simplifier {

	public static void autoSimplify(EvolutionState state, Individual ind,
			PshProblem problem, int steps, int subpopulation, int threadnum,
			float simplifyByFlattenProb) {

		PshIndividual simplest = (PshIndividual) ind.clone();

		simplest.evaluated = false;
		problem.evaluate(state, simplest, subpopulation, threadnum);

		boolean madeSimpler = false;
		for (int i = 0; i < steps; i++) {

			PshIndividual trial = (PshIndividual) simplest.clone();

			if (trial.program.programsize() <= 0)
				break;

			if (state.random[threadnum].nextBoolean(simplifyByFlattenProb)) {
				// Flatten random thing
				int pointIndex = state.random[threadnum].nextInt(trial.program
						.programsize());
				Object point = trial.program.Subtree(pointIndex);
				if (point instanceof Program) {
					trial.program.Flatten(pointIndex);
					madeSimpler = true;
				}
			} else {
				// Remove small number of random things
				int numberToRemove = state.random[threadnum].nextInt(3) + 1;

				for (int j = 0; j < numberToRemove; j++) {
					int trialSize = trial.program.programsize();

					if (trialSize > 0) {
						int pointIndex = state.random[threadnum]
								.nextInt(trialSize);
						trial.program.ReplaceSubtree(pointIndex, new Program());
						trial.program.Flatten(pointIndex);
						madeSimpler = true;
					}
				}
			}
			if (madeSimpler) {
				trial.evaluated = false;
				problem.evaluate(state, trial, subpopulation, threadnum);

				if (trial.fitness.betterThan(simplest.fitness)
						|| trial.fitness.equivalentTo(simplest.fitness)) {
					simplest = trial.clone();
				}
			}
			madeSimpler = false;
		}

		((PshIndividual) ind).program = new Program(simplest.program);
	}

}
