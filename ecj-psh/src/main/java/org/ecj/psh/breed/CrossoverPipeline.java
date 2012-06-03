package org.ecj.psh.breed;

import org.ecj.psh.PshIndividual;
import org.ecj.psh.PshNodeSelector;
import org.ecj.psh.PshProblem;
import org.spiderland.Psh.Interpreter;

import ec.BreedingPipeline;
import ec.EvolutionState;
import ec.Individual;
import ec.gp.GPIndividual;
import ec.gp.GPNodeSelector;
import ec.util.Parameter;
import ec.vector.VectorIndividual;

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

	/** Temporary holding place for parents */
	PshIndividual parents[];

	public CrossoverPipeline() {
		parents = new PshIndividual[2];
	}

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
		c.parents = (PshIndividual[]) parents.clone();
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

		int maxPointsInProgram = ((PshProblem) state.evaluator.p_problem).maxPointsInProgram;

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
			
			// prepare the nodeselectors
			nodeSelector1.reset();
			nodeSelector2.reset();

			// crossover by swap subtrees
			int index0 = nodeSelector1.pickNode(state, subpopulation, thread,
					parents[0]);
			int index1 = nodeSelector2.pickNode(state, subpopulation, thread,
					parents[1]);

			Object subtree0 = parents[0].program.Subtree(index0);
			Object subtree1 = parents[1].program.Subtree(index1);

			int subtreeDiff = parents[0].program.SubtreeSize(index0)
					- parents[1].program.SubtreeSize(index1);

			// crossover first parent
			if (parents[0].program.programsize() - subtreeDiff <= maxPointsInProgram) {
				parents[0].program.ReplaceSubtree(index0, subtree1);
				parents[0].evaluated = false;
			}

			if (n - (q - start) >= 2 && !tossSecondParent) {
				// crossover second parent
				if (parents[1].program.programsize() + subtreeDiff <= maxPointsInProgram) {
					parents[1].program.ReplaceSubtree(index1, subtree0);
					parents[1].evaluated = false;
				}
			}

			// add 'em to the population
			inds[q] = parents[0];
			q++;
			if (q < n + start && !tossSecondParent) {
				inds[q] = parents[1];
				q++;
			}
		}

		return n;
	}

}
