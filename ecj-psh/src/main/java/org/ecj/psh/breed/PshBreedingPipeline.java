package org.ecj.psh.breed;

import org.ecj.psh.PshSpecies;

import ec.BreedingPipeline;
import ec.EvolutionState;
import ec.Population;

public abstract class PshBreedingPipeline extends BreedingPipeline {

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
