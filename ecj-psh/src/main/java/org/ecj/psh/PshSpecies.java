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

package org.ecj.psh;

import org.spiderland.Psh.Interpreter;

import ec.EvolutionState;
import ec.Fitness;
import ec.Individual;
import ec.Species;
import ec.util.Parameter;

/**
 * Species subclass for PushGP individuals
 * 
 * @author Tomasz Kamiñski
 * 
 */
public class PshSpecies extends Species {
	public static final String P_PSHSPECIES = "species";

	@Override
	public Parameter defaultBase() {
		return PshDefaults.base().push(P_PSHSPECIES);
	}

	@Override
	public void setup(final EvolutionState state, final Parameter base) {
		super.setup(state, base);
		// check to make sure that our individual prototype is a PshIndividual
		if (!(i_prototype instanceof PshIndividual))
			state.output.fatal("The Individual class " + "for the Species "
					+ getClass().getName() + " must be a subclass of "
					+ "org.ecj.psh.PshIndividual.", base);
	}

	@Override
	public Individual newIndividual(EvolutionState state, int thread) {
		PshIndividual newind = ((PshIndividual) (i_prototype)).clone();

		// interpreter
		Interpreter interpreter = ((PshEvolutionState) state).interpreter[thread];

		// Generate random program
		int randomCodeSize = state.random[thread]
				.nextInt(interpreter.getMaxRandomCodeSize()) + 2;
		
		newind.program = interpreter.RandomCode(randomCodeSize);

		// Set the fitness
		newind.fitness = (Fitness) (f_prototype.clone());
		newind.evaluated = false;

		// Set the species to me
		newind.species = this;

		// ...and we're ready!
		return newind;
	}
}
