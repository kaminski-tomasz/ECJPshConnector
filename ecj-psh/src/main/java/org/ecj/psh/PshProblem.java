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

import org.ecj.psh.util.Simplifier;
import org.spiderland.Psh.Interpreter;
import org.spiderland.Psh.Program;

import ec.EvolutionState;
import ec.Individual;
import ec.Problem;
import ec.simple.SimpleProblemForm;
import ec.util.Parameter;

/**
 * Base class for problems solved with PushGP
 * 
 * @author Tomasz Kamiński
 * 
 */
public abstract class PshProblem extends Problem implements SimpleProblemForm {

	public static final String P_PSHPROBLEM = "problem";

	/**
	 * PshProblem defines a default base so your subclass doesn't absolutely
	 * have to.
	 */
	@Override
	public Parameter defaultBase() {
		return PshDefaults.base().push(P_PSHPROBLEM);
	}

	/**
	 * Set up prototype for PshProblem.
	 */
	@Override
	public void setup(final EvolutionState state, final Parameter base) {
		super.setup(state, base);
		Parameter def = defaultBase();
	}

	@Override
	public Object clone() {
		PshProblem newProblem = (PshProblem) super.clone();
		return newProblem;
	}

	@Override
	public void describe(final EvolutionState state, final Individual ind,
			final int subpopulation, final int threadnum, final int log) {

		state.output.println("After simplifications: ", log);

		Simplifier.autoSimplify(state, (PshIndividual) ind, this, 100000,
				subpopulation, threadnum, 0.2f);
		state.output.println("\t" + ind, log);
	}
}
