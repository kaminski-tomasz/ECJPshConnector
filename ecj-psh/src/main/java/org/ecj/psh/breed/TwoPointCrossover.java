package org.ecj.psh.breed;

import org.ecj.psh.PshEvolutionState;
import org.spiderland.Psh.Program;

import ec.EvolutionState;
import ec.util.Parameter;

public class TwoPointCrossover extends CrossoverPipeline {

	public static final String P_TWOPOINT = "two-point-xover";
	public static final String P_REPLACEMENTLENGTH = "replacement-length";
	
	/** Length of the replaced part */ 
	public int replacementLength;
	
	@Override
	public Parameter defaultBase() {
		return PshBreedDefaults.base().push(P_TWOPOINT);
	}

	@Override
	public void setup(EvolutionState state, Parameter base) {
		super.setup(state, base);
		Parameter def = defaultBase();
		// length of the replaced part. 0 means that it can be any value
		replacementLength = state.parameters.getInt(
				base.push(P_REPLACEMENTLENGTH), def.push(P_REPLACEMENTLENGTH),
				1);
	}

	/**
	 * Two-point [homologous] crossover. Assumes that parents holds current
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

		int p1Length = p1.size(), p2Length = p2.size();

		// check if we can crossover
		if (p1Length <= 0 || p2Length <= 0 || p1Length < replacementLength
				|| p2Length < replacementLength)
			return;

		// choose cutpoints
		int[] p1cutpoint = new int[2], p2cutpoint = new int[2];

		if (!homologous) {
			int p1Size = p1.programsize();
			int p2Size = p2.programsize();
			int remainingTries = tries;
			boolean isSizeCorrect;
			do {
				findCuttingPoint(p1Length, p1cutpoint, state, thread);
				findCuttingPoint(p2Length, p2cutpoint, state, thread);
				int p1ReplacedSize = p1.programsize(p1cutpoint[0], p1cutpoint[1]);
				int p2ReplacedSize = p2.programsize(p2cutpoint[0], p2cutpoint[1]);
				isSizeCorrect = (p1Size - p1ReplacedSize + p2ReplacedSize <= maxPointsInProgram)
						&& (!breedSecondParent || (p2Size - p2ReplacedSize
								+ p1ReplacedSize <= maxPointsInProgram));
			} while (--remainingTries != 0 && !isSizeCorrect);
			if (remainingTries == 0 && !isSizeCorrect) {
				// giving up...
				return;
			}
		} else {
			findCuttingPoint(Math.min(p1Length, p2Length), 
					p1cutpoint, state, thread);
			p2cutpoint = p1cutpoint;
		}
		
		Program o1 = null, o2 = null;		
		o1 = p1.Copy(0, p1cutpoint[0]);					// prefix part
		p2.CopyTo(o1, p2cutpoint[0], p2cutpoint[1]);	// replaced part
		p1.CopyTo(o1, p1cutpoint[0] + p1cutpoint[1]);	// suffix part
		
		if (breedSecondParent) {
			o2 = p2.Copy(0, p2cutpoint[0]);					// prefix part
			p1.CopyTo(o2, p1cutpoint[0], p1cutpoint[1]);	// replaced part
			p2.CopyTo(o2, p2cutpoint[0] + p2cutpoint[1]);	// suffix part	
		}
		parents[0].program = o1;
		parents[0].evaluated = false;
		if (breedSecondParent) {
			parents[1].program = o2;
			parents[1].evaluated = false;
		}
	}
	
	/**
	 * Generate cutting point for 2PX.
	 * 
	 * @param parentLength
	 *            length of the parent program root stack
	 * @param cutpoint
	 *            array with two numbers: starting point and length of the
	 *            replacement
	 */
	public void findCuttingPoint(int parentLength, int[] cutpoint,
			EvolutionState state, int thread) {
		if (replacementLength > 0) {
			cutpoint[0] = state.random[thread].nextInt(parentLength
					- (replacementLength - 1));
			cutpoint[1] = replacementLength;
		} else {
			cutpoint[0] = state.random[thread].nextInt(parentLength);
			cutpoint[1] = state.random[thread].nextInt(parentLength
					- cutpoint[0]) + 1;
		}
	}

}
