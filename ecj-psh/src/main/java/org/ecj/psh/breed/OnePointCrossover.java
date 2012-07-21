package org.ecj.psh.breed;

import org.ecj.psh.PshEvolutionState;
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
	protected void crossover(EvolutionState state, int thread, boolean breedSecondParent) {
		int maxPointsInProgram = ((PshEvolutionState) state).interpreter[thread]
				.getMaxPointsInProgram();

		Program p1 = parents[0].program;
		Program p2 = parents[1].program;
		
		int p1Length = p1.size();
		int p2Length = p2.size();

		// check if we can crossover
		if (p1Length <= 0 || p2Length <= 0)
			return;

		// choose cutpoints
		int p1cutpoint = 0, p2cutpoint = 0;
		if (!homologous) {
			int remainingTries = this.tries;
			int p1Size = p1.programsize();
			int p2Size = p2.programsize();
			boolean isSizeCorrect = true;
			do {
				p1cutpoint = state.random[thread].nextInt(p1Length);
				p2cutpoint = state.random[thread].nextInt(p2Length);
				int p1ReplacedSize = p1.programsize(p1cutpoint);
				int p2ReplacedSize = p2.programsize(p2cutpoint);
				isSizeCorrect = (p1Size - p1ReplacedSize + p2ReplacedSize <= maxPointsInProgram)
						&& (!breedSecondParent || (p2Size - p2ReplacedSize
								+ p1ReplacedSize <= maxPointsInProgram));
			} while (--remainingTries != 0 && !isSizeCorrect);
			if (remainingTries == 0 && !isSizeCorrect) {
				// giving up...
				return;
			}
		} else {
			p1cutpoint = p2cutpoint = state.random[thread].nextInt(Math.min(
					p1Length, p2Length));
		}
		
		Program o1 = null, o2 = null;
		
		o1 = p1.Copy(0, p1cutpoint);
		p2.CopyTo(o1, p2cutpoint);
		
		if (breedSecondParent) {
			o2 = p2.Copy(0, p2cutpoint);
			p1.CopyTo(o2, p1cutpoint);
		}

		parents[0].program = o1;
		parents[0].evaluated = false;

		if (breedSecondParent) {
			parents[1].program = o2;
			parents[1].evaluated = false;
		}
	}
}
