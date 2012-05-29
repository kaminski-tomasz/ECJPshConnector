package org.ecj.psh;

import ec.EvolutionState;
import ec.Prototype;

/**
 * Interface implemented by classes responsible for selecting nodes (described
 * by index) from PushGP individual in order to perform crossover, mutation etc.
 * 
 * @author Tomasz Kamiñski
 * 
 */
public interface PshNodeSelector extends Prototype {

	/**
	 * Picks node from PushGP individual
	 * 
	 * @return index of node (instruction,literal) in Push program code
	 */
	public abstract int pickNode(final EvolutionState state,
			final int subpopulation, final int thread, final PshIndividual ind);

	/**
	 * Resets the Node Selector before a new series of pickNode() if need be.
	 */
	public abstract void reset();
}
