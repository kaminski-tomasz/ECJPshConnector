package org.ecj.psh;

import ec.EvolutionState;
import ec.gp.GPNodeGatherer;
import ec.gp.koza.GPKozaDefaults;
import ec.gp.koza.KozaNodeSelector;
import ec.util.Parameter;

/**
 * Node selector which picks Push program nodes randomly 
 * 
 * @author Tomasz Kamiñski
 *
 */
public class UnbiasedNodeSelector implements PshNodeSelector {

	public static final String P_NODESELECTOR = "ns";

	@Override
	public Parameter defaultBase() {
		return PshDefaults.base().push(P_NODESELECTOR);
	}

	@Override
	public Object clone() {
		try {
			UnbiasedNodeSelector s = (UnbiasedNodeSelector) (super.clone());
			s.reset();
			return s;
		} catch (CloneNotSupportedException e) {
			throw new InternalError(); // never happens
		}
	}

	@Override
	public void setup(EvolutionState state, Parameter base) {
		// there is nothing to do
	}

	@Override
	public int pickNode(EvolutionState state, int subpopulation, int thread,
			PshIndividual ind) {
		// TODO pick random node from PushGP program code
		return 0;
	}

	@Override
	public void reset() {
		// there is nothing to do
	}

}
