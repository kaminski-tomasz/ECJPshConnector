package org.ecj.psh.breed;

import org.ecj.psh.PshEvolutionState;

import ec.EvolutionState;
import ec.util.Parameter;

public class PshCrossover extends CrossoverPipeline {

	public static final String P_PSHXOVER = "psh-xover";

	@Override
	public Parameter defaultBase() {
		return PshBreedDefaults.base().push(P_PSHXOVER);
	}

	@Override
	protected void crossover(EvolutionState state, int thread, boolean breedSecondParent) {
		int maxPointsInProgram = ((PshEvolutionState) state).interpreter[thread]
				.getMaxPointsInProgram();

		int parent1size = parents[0].program.programsize();
		int parent2size = parents[1].program.programsize();

		if (parent1size < 1 || parent2size < 1)
			return;
		
		// crossover by swap subtrees
		int index1 = state.random[thread].nextInt(parent1size);
		int index2 = state.random[thread].nextInt(parent2size);

		Object subtree1 = parents[0].program.Subtree(index1);
		Object subtree2 = parents[1].program.Subtree(index2);

		int subtree1size = parents[0].program.SubtreeSize(index1);
		int subtree2size = parents[1].program.SubtreeSize(index2);
		
		// crossover first parent
		if (parent1size - subtree1size + subtree2size <= maxPointsInProgram) {
			parents[0].program.ReplaceSubtree(index1, subtree2);
			parents[0].evaluated = false;
		}

		if (breedSecondParent) {
			// crossover second parent
			if (parent2size - subtree2size + subtree1size <= maxPointsInProgram) {
				parents[1].program.ReplaceSubtree(index2, subtree1);
				parents[1].evaluated = false;
			}
		}
	}

}
