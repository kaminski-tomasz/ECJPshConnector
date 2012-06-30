package org.ecj.psh.breed;

import org.ecj.psh.PshDefaults;
import org.ecj.psh.PshEvaluator;
import org.ecj.psh.PshIndividual;
import org.spiderland.Psh.Interpreter;
import org.spiderland.Psh.Program;

import ec.BreedingPipeline;
import ec.EvolutionState;
import ec.Individual;
import ec.util.Parameter;

/**
 * Crossover operator for flat programs. Default is one-point.
 * 
 * @author Tomasz Kamiński
 * 
 */
public class FlatCrossoverPipeline extends PshBreedingPipeline {

	public static final String P_FLATCROSSOVER = "flat-xover";
	public static final String P_CROSSOVERTYPE = "type";
	public static final String V_ONE_POINT = "one-point";
	public static final String P_TOSS = "toss";
	public static final int NUM_SOURCES = 2;

	public static final int C_ONE_POINT = 1;

	/** What kind of cross-over do we have? */
	public int crossoverType;

	/** Should the pipeline discard the second parent after crossing over? */
	public boolean tossSecondParent;

	/** Temporary holding place for parents */
	PshIndividual parents[];

	public FlatCrossoverPipeline() {
		parents = new PshIndividual[2];
	}

	@Override
	public Parameter defaultBase() {
		return PshBreedDefaults.base().push(P_FLATCROSSOVER);
	}

	@Override
	public int numSources() {
		return NUM_SOURCES;
	}

	@Override
	public Object clone() {
		FlatCrossoverPipeline c = (FlatCrossoverPipeline) (super.clone());
		c.parents = (PshIndividual[]) parents.clone();
		return c;
	}

	@Override
	public void setup(final EvolutionState state, final Parameter base) {
		super.setup(state, base);
		Parameter def = defaultBase();

		// crossover type
		String ctype = state.parameters.getStringWithDefault(
				base.push(P_CROSSOVERTYPE), def.push(P_CROSSOVERTYPE), null);
		crossoverType = C_ONE_POINT;
		if (ctype == null)
			state.output.warning(
					"No crossover type given for FlatCrossoverPipeline,"
							+ " assuming one-point crossover",
					base.push(P_CROSSOVERTYPE), def.push(P_CROSSOVERTYPE));
		else if (ctype.equalsIgnoreCase(V_ONE_POINT))
			crossoverType = C_ONE_POINT; // redundant

		// should we toss second parent?
		tossSecondParent = state.parameters.getBoolean(base.push(P_TOSS),
				def.push(P_TOSS), true);
	}

	/**
	 * Returns 2 * minimum number of typical individuals produced by any
	 * sources, else 1* minimum number if tossSecondParent is true.
	 */
	public int typicalIndsProduced() {
		return (tossSecondParent ? minChildProduction()
				: minChildProduction() * 2);
	}

	@Override
	public int produce(int min, int max, int start, int subpopulation,
			Individual[] inds, EvolutionState state, int thread) {

		// how many individuals should we make?
		int n = typicalIndsProduced();
		if (n < min)
			n = min;
		if (n > max)
			n = max;

		// should we bother?
		if (!state.random[thread].nextBoolean(likelihood))
			return reproduce(n, start, subpopulation, inds, state, thread, true);

		// keep on going until we're filled up
		for (int q = start; q < n + start; /* no increment */) {
			// grab two individuals from our sources
			if (sources[0] == sources[1]) {
				// grab from the same source
				sources[0].produce(2, 2, 0, subpopulation, parents, state,
						thread);
				if (!(sources[0] instanceof BreedingPipeline)) {
					parents[0] = (PshIndividual) (parents[0].clone());
					parents[1] = (PshIndividual) (parents[1].clone());
				}
			} else {
				// grab from different sources
				sources[0].produce(1, 1, 0, subpopulation, parents, state,
						thread);
				sources[1].produce(1, 1, 1, subpopulation, parents, state,
						thread);
				if (!(sources[0] instanceof BreedingPipeline)) {
					parents[0] = (PshIndividual) (parents[0].clone());
				}
				if (!(sources[1] instanceof BreedingPipeline)) {
					parents[1] = (PshIndividual) (parents[1].clone());
				}
			}

			boolean breedSecondParent = n - (q - start) >= 2
					&& !tossSecondParent;

			// cross them over
			if (crossoverType == C_ONE_POINT) {
				// one-point crossover
				onePointCrossover(breedSecondParent, state, thread);
			}

			// add 'em to the population
			inds[q++] = parents[0];
			if (q < n + start && !tossSecondParent) {
				inds[q++] = parents[1];
			}
			state.output.flush();
		}
		return n;
	}

	/**
	 * One-point flat crossover. Assumes that parents holds current parents for
	 * crossover operation.
	 * 
	 * @param breedSecondParent
	 *            do we breed second parent?
	 * @param maxPointsInProgram
	 *            maximum length of breeded programs
	 */
	protected void onePointCrossover(boolean breedSecondParent,
			EvolutionState state, int thread) {

		int maxPointsInProgram = ((PshEvaluator) state.evaluator).interpreter[thread]
				.getMaxPointsInProgram();

		int parent1size = parents[0].program.size();
		int parent2size = parents[1].program.size();

		// check if we can crossover
		if (parent1size <= 1 || parent2size <= 1)
			return;

		Program child1 = null, child2 = null;

		int cutpoint1, cutpoint2;
		do {
			cutpoint1 = state.random[thread].nextInt(parent1size - 1) + 1;
			do {
				cutpoint2 = state.random[thread].nextInt(parent2size - 1) + 1;
			} while (cutpoint1 + parent2size - cutpoint2 > maxPointsInProgram);
		} while (breedSecondParent
				&& (cutpoint2 + parent1size - cutpoint1 > maxPointsInProgram));

		// breed first parent
		{
			child1 = new Program();
			int index1 = 0;
			for (; index1 < cutpoint1; index1++) {
				child1.push(parents[0].program.peek(index1));
			}
			int index2 = cutpoint2;
			for (; index2 < parent2size; index2++) {
				child1.push(parents[1].program.peek(index2));
			}
		}

		// breed second parent
		if (breedSecondParent) {
			child2 = new Program();
			int index2 = 0;
			for (; index2 < cutpoint2; index2++) {
				child2.push(parents[1].program.peek(index2));
			}
			int index1 = cutpoint1;
			for (; index1 < parent1size; index1++) {
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
