package org.ecj.psh.breed;

import org.ecj.psh.PshIndividual;

import ec.BreedingPipeline;
import ec.EvolutionState;
import ec.Individual;
import ec.util.Parameter;

/**
 * Abstract crossover pipeline for Push programs.
 * 
 * @author Tomasz Kamiński
 * 
 */
public abstract class CrossoverPipeline extends PshBreedingPipeline {

	public static final String P_TOSS = "toss";
	public static final String P_HOMOLOGOUS = "homologous";
	public static final String P_TRIES = "tries";
	public static final int NUM_SOURCES = 2;
		
	/** Should the pipeline discard the second parent after crossing over? */
	public boolean tossSecondParent;

	/** Temporary holding place for parents */
	protected PshIndividual parents[];
	
	/** Is it a homologous operator */
	public boolean homologous;

	/** Number of tries while finding the cutpoints before giving up */
	public int tries;
	
	public CrossoverPipeline() {
		parents = new PshIndividual[2];
	}

	@Override
	public int numSources() {
		return NUM_SOURCES;
	}

	@Override
	public Object clone() {
		CrossoverPipeline c = (CrossoverPipeline) (super.clone());
		c.parents = (PshIndividual[]) parents.clone();
		return c;
	}

	@Override
	public void setup(final EvolutionState state, final Parameter base) {
		super.setup(state, base);
		Parameter def = defaultBase();

		// should we toss second parent?
		tossSecondParent = state.parameters.getBoolean(base.push(P_TOSS),
				def.push(P_TOSS), true);
		
		// is it a homologous operator
		homologous = state.parameters.getBoolean(base.push(P_HOMOLOGOUS),
				def.push(P_HOMOLOGOUS), false);
		
		// tries = 0 means that we're trying to find the cutpoints in the infinity loop
		tries = state.parameters.getIntWithDefault(base.push(P_TRIES),
				def.push(P_TRIES), 100);
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

			crossover(state, thread, breedSecondParent);
			
			// add 'em to the population
			inds[q++] = parents[0];
			if (q < n + start && !tossSecondParent) {
				inds[q++] = parents[1];
			}
		}
		return n;
	}

	/**
	 * Crossover parents
	 * @param state
	 * @param thread
	 * @param breedSecondParent should we breed second parent
	 */
	abstract protected void crossover(EvolutionState state, int thread, boolean breedSecondParent);

	public PshIndividual[] getParents() {
		return parents;
	}
	
	public void setParents(PshIndividual[] parents) {
		this.parents = parents;
	}
}
