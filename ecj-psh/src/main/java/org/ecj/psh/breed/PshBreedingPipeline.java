/*
* Copyright 2012 Tomasz Kamiński
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
