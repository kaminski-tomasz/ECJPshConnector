package org.ecj.psh.breed;

import org.ecj.psh.PshEvaluator;
import org.ecj.psh.breed.PshBreedDefaults;
import org.spiderland.Psh.Program;

import ec.EvolutionState;
import ec.util.Parameter;

public class TwoPointCrossover extends CrossoverPipeline {

	public static final String P_TWOPOINT = "two-point-xover";
	
	@Override
	public Parameter defaultBase() {
		return PshBreedDefaults.base().push(P_TWOPOINT);
	}

	/**
	 * Two-point [homologous] crossover. Assumes that parents holds current
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
		int[] cutpoints1 = new int[2];
		int[] cutpoints2 = new int[2];
		int parent1slice = 0, parent2slice = 0, child1size = 0, child2size = 0;
		if (!homologous) {
			int numOfTries = this.tries;
			do {
				parent1slice = findCuttingPoints(parent1size, cutpoints1,
						state, thread);
				do {
					numOfTries--;
					parent2slice = findCuttingPoints(parent2size, cutpoints2,
							state, thread);
					child1size = parent1size - parent1slice + parent2slice;
				} while (numOfTries != 0 && 
						child1size > maxPointsInProgram);
				child2size = parent2size - parent2slice + parent1slice;
			} while (numOfTries != 0 && 
					(breedSecondParent && child2size > maxPointsInProgram));
			if (numOfTries == 0
					&& (child1size > maxPointsInProgram || 
							(breedSecondParent && child2size > maxPointsInProgram))) {
				// giving up...
				return;
			}
		} else {
			parent1slice = parent2slice = findCuttingPoints(Math.min(parent1size, parent2size), 
					cutpoints1, state, thread);
			cutpoints2 = cutpoints1;
		}
		
		Program child1 = new Program(), child2 = new Program();
		// breed first parent
		for (int index1 = 0; index1 < cutpoints1[0]; index1++) {
			child1.push(parents[0].program.peek(index1));
		}
		for (int index2 = cutpoints2[0]; index2 <= cutpoints2[1]; index2++) {
			child1.push(parents[1].program.peek(index2));
		}
		for (int index1 = cutpoints1[1]+1; index1 < parent1size; index1++) {
			child1.push(parents[0].program.peek(index1));
		}

		// breed second parent
		if (breedSecondParent) {
			for (int index2 = 0; index2 < cutpoints2[0]; index2++) {
				child2.push(parents[1].program.peek(index2));
			}
			for (int index1 = cutpoints1[0]; index1 <= cutpoints1[1]; index1++) {
				child2.push(parents[0].program.peek(index1));
			}
			for (int index2 = cutpoints2[1]+1; index2 < parent2size; index2++) {
				child2.push(parents[1].program.peek(index2));
			}
		}

		parents[0].program = child1;
		parents[0].evaluated = false;

		if (breedSecondParent) {
			parents[1].program = child2;
			parents[1].evaluated = false;
		}
	}
	
	/**
	 * Generate cutting points for 2PX.
	 * @param parentSize length of a parent program
	 * @param cutpoints array with two cutting points being generated
	 * @param state
	 * @param thread
	 * @return the length of the slice 
	 */
	public int findCuttingPoints(int parentSize, int[] cutpoints,
			EvolutionState state, int thread) {
		cutpoints[0] = state.random[thread].nextInt(parentSize - 1);
		cutpoints[1] = state.random[thread].nextInt(parentSize - 1);
		if (cutpoints[0] > cutpoints[1]) {
			int temp = cutpoints[0];
			cutpoints[0] = cutpoints[1];
			cutpoints[1] = temp;
		}
		return cutpoints[1] - cutpoints[0] + 1;
	}

}
