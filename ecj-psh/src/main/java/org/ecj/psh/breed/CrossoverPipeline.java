package org.ecj.psh.breed;

import org.ecj.psh.PshNodeSelector;

import ec.EvolutionState;
import ec.Individual;
import ec.gp.GPNodeSelector;
import ec.util.Parameter;

/**
 * Crossover pipeline reproduction based on Psh implementation
 * 
 * @author Tomasz Kamiñski
 * 
 */
public class CrossoverPipeline extends PshBreedingPipeline {

	public static final String P_CROSSOVER = "xover";
	public static final String P_TOSS = "toss";
	public static final int NUM_SOURCES = 2;

	/** Should the pipeline discard the second parent after crossing over? */
	public boolean tossSecondParent;

	/** How the pipeline selects a node from individual 1 */
	public PshNodeSelector nodeSelector1;

	/** How the pipeline selects a node from individual 2 */
	public PshNodeSelector nodeSelector2;

	@Override
	public Parameter defaultBase() {
		return PshBreedDefaults.base().push(P_CROSSOVER);
	}

	@Override
	public int numSources() {
		return NUM_SOURCES;
	}

	@Override
	public Object clone() {
		CrossoverPipeline c = (CrossoverPipeline) (super.clone());
		// deep-cloned stuff
		c.nodeSelector1 = (PshNodeSelector) (nodeSelector1.clone());
		c.nodeSelector2 = (PshNodeSelector) (nodeSelector2.clone());
		return c;
	}

	public void setup(final EvolutionState state, final Parameter base) {
		super.setup(state, base);

		Parameter def = defaultBase();
		Parameter p = base.push(P_NODESELECTOR).push("0");
		Parameter d = def.push(P_NODESELECTOR).push("0");

		nodeSelector1 = (PshNodeSelector) (state.parameters
				.getInstanceForParameter(p, d, PshNodeSelector.class));
		nodeSelector1.setup(state, p);

		p = base.push(P_NODESELECTOR).push("1");
		d = def.push(P_NODESELECTOR).push("1");

		if (state.parameters.exists(p, d)
				&& state.parameters.getString(p, d).equals(V_SAME))
			// can't just copy it this time; the selectors
			// can use internal caches. So we have to clone it no matter what
			nodeSelector2 = (PshNodeSelector) (nodeSelector1.clone());
		else {
			nodeSelector2 = (PshNodeSelector) (state.parameters
					.getInstanceForParameter(p, d, PshNodeSelector.class));
			nodeSelector2.setup(state, p);
		}

		// should we toss second parent?
		tossSecondParent = state.parameters.getBoolean(base.push(P_TOSS),
				def.push(P_TOSS), false);
	}

	@Override
	public int produce(int min, int max, int start, int subpopulation,
			Individual[] inds, EvolutionState state, int thread) {
		// TODO implement crossover operator based on Psh code
		return 0;
	}

}
