/*
* Copyright 2009-2010 Tomasz Kamiński
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/

package org.ecj.psh.select;

import ec.EvolutionState;
import ec.Individual;
import ec.select.TournamentSelection;
import ec.util.Parameter;

/**
 * Trivial geography tournament selection based on Psh implementation.
 * Center position is chosen randomly.
 * 
 * @author Tomasz Kamiński
 * 
 */
public class TrivialGeographySelection extends TournamentSelection {
	public static final String P_TRIVIALGEOGRAPHY = "trivial-geography";
	public static final String P_RADIUS = "radius";

	/** Radius for tournament selection */
	public int trivialGeographyRadius;

	@Override
	public Parameter defaultBase() {
		return PshSelectDefaults.base().push(P_TRIVIALGEOGRAPHY);
	}

	@Override
	public void setup(final EvolutionState state, final Parameter base) {
		super.setup(state, base);
		Parameter def = defaultBase();
		trivialGeographyRadius = state.parameters.getIntWithDefault(
				base.push(P_RADIUS), def.push(P_RADIUS), 0);
	}

	@Override
	public int produce(final int min, final int max, final int start,
			final int subpopulation, final Individual[] inds,
			final EvolutionState state, final int thread) {
		// radius should be grater that zero to perform selection with trivial geography
		if (trivialGeographyRadius > 0) {
			int n = INDS_PRODUCED;
			if (n < min)
				n = min;
			if (n > max)
				n = max;

			for (int q = 0; q < n; q++) {
				Individual[] oldinds = state.population.subpops[subpopulation].individuals;
				// centerPosition must be drawn, there is no way in ECJ to give current location
				// in subpopulation array being fulfilled . So I choose it randomly
				int centerPosition = state.random[thread]
						.nextInt(oldinds.length);
				int index = produceWithinRadius(centerPosition++, subpopulation,
						state, thread);
				inds[start + q] = oldinds[index];
			}
			return n;
		} else {
			return super.produce(min, max, start, subpopulation, inds, state,
					thread);
		}
	}

	/**
	 * Tournament selection with trivial geography
	 * @param centerPosition center position in old population
	 * @return
	 */
	public int produceWithinRadius(int centerPosition, final int subpopulation,
			final EvolutionState state, final int thread) {
		// pick size random individuals, then pick the best.
		Individual[] oldinds = state.population.subpops[subpopulation].individuals;
		int best = selectIndividualWithinRadius(centerPosition, oldinds,
				subpopulation, state, thread);
		int s = getTournamentSizeToUse(state.random[thread]);

		if (pickWorst)
			for (int x = 1; x < s; x++) {
				int j = selectIndividualWithinRadius(centerPosition, oldinds,
						subpopulation, state, thread);
				if (!betterThan(oldinds[j], oldinds[best], subpopulation,
						state, thread)) // j is at least as bad as best
					best = j;
			}
		else
			for (int x = 1; x < s; x++) {
				int j = selectIndividualWithinRadius(centerPosition, oldinds,
						subpopulation, state, thread);
				if (betterThan(oldinds[j], oldinds[best], subpopulation, state,
						thread)) // j is better than best
					best = j;
			}
		return best;
	}

	/**
	 * Selects individual's index within radius around center position - the index
	 * from old population. It works as if neighbourhood was held in a cyclic buffer.
	 * 
	 * @param centerPosition
	 *            center position
	 * @param oldinds
	 *            old population
	 * @return
	 */
	public int selectIndividualWithinRadius(int centerPosition,
			Individual[] oldinds, final int subpopulation,
			final EvolutionState state, final int thread) {
		int j = state.random[thread].nextInt(trivialGeographyRadius * 2)
				- trivialGeographyRadius + centerPosition;
		if (j < 0)
			j += oldinds.length;
		return j % oldinds.length;
	}

}
