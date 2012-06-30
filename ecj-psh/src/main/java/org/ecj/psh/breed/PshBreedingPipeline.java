package org.ecj.psh.breed;

import org.ecj.psh.PshSpecies;

import ec.BreedingPipeline;
import ec.EvolutionState;
import ec.Individual;
import ec.Population;
import ec.gp.GPSpecies;
import ec.util.Parameter;

public abstract class PshBreedingPipeline extends BreedingPipeline {

	/**
	 * Standard parameter for node-selectors associated with a
	 * PshBreedingPipeline
	 */
	public static final String P_NODESELECTOR = "ns";

	/** Returns true if species is a PshSpecies. */
	public boolean produces(final EvolutionState state,
			final Population newpop, final int subpopulation, final int thread) {
		if (!super.produces(state, newpop, subpopulation, thread))
			return false;

		// we produce individuals which are owned by subclasses of PshSpecies
		if (newpop.subpops[subpopulation].species instanceof PshSpecies)
			return true;
		return false;
	}

}
