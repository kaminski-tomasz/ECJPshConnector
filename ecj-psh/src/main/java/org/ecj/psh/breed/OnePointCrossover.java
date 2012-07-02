package org.ecj.psh.breed;

import org.ecj.psh.PshEvaluator;
import org.spiderland.Psh.Program;

import ec.EvolutionState;
import ec.util.Parameter;

public class OnePointCrossover extends CrossoverPipeline {

	public static final String P_ONEPOINT = "one-point-xover";
	
	@Override
	public Parameter defaultBase() {
		return PshBreedDefaults.base().push(P_ONEPOINT);
	}

	/**
	 * One-point [homologous] crossover. Assumes that parents holds current
	 * parents for crossover operation.
	 * 
	 * @param breedSecondParent
	 *            do we breed second parent?
	 */
	@Override
	void crossover(EvolutionState state, int thread, boolean breedSecondParent) {
		int maxPointsInProgram = ((PshEvaluator) state.evaluator).interpreter[thread]
				.getMaxPointsInProgram();

		int parent1size = parents[0].program.size();
		int parent2size = parents[1].program.size();

		// check if we can crossover
		if (parent1size <= 1 || parent2size <= 1)
			return;

		// choose cutpoints
		int cutpoint1 = 0, cutpoint2 = 0, child1size = 0, child2size = 0;
		if (!homologous) {
			int numOfTries = this.tries;
			do {
				cutpoint1 = state.random[thread].nextInt(parent1size - 1) + 1;
				do {
					numOfTries--;
					cutpoint2 = state.random[thread].nextInt(parent2size - 1) + 1;
					child1size = cutpoint1 + parent2size - cutpoint2;
				} while (numOfTries != 0
						&& child1size > maxPointsInProgram);
				child2size = cutpoint2 + parent1size - cutpoint1;
			} while (numOfTries != 0
					&& (breedSecondParent && child2size > maxPointsInProgram));
			if (numOfTries == 0
					&& (child1size > maxPointsInProgram || 
							(breedSecondParent && child2size > maxPointsInProgram))) {
				// giving up...
				return;
			}
		} else {
			cutpoint1 = cutpoint2 = state.random[thread].nextInt(Math.min(
					parent1size, parent2size) - 1) + 1;
		}
		Program child1 = new Program(), child2 = new Program();
		// breed first parent
		for (int index1 = 0; index1 < cutpoint1; index1++) {
			child1.push(parents[0].program.peek(index1));
		}
		for (int index2 = cutpoint2; index2 < parent2size; index2++) {
			child1.push(parents[1].program.peek(index2));
		}

		// breed second parent
		if (breedSecondParent) {
			for (int index2 = 0; index2 < cutpoint2; index2++) {
				child2.push(parents[1].program.peek(index2));
			}
			for (int index1 = cutpoint1; index1 < parent1size; index1++) {
				child2.push(parents[0].program.peek(index1));
			}
		}

		parents[0].program = child1;
		parents[0].evaluated = false;

		if (breedSecondParent) {
			parents[1].program = child2;
			parents[1].evaluated = false;
		}

	}

}
