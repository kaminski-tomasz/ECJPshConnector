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
import ec.Prototype;
import ec.simple.SimpleEvolutionState;
import ec.util.Parameter;

public class PshEvolutionState extends SimpleEvolutionState {

	public final static String P_INTERPRETER = "interpreter";

	/** Interpreter used in evaluating individuals */
	public Interpreter[] interpreter;
	
	/**
	 * Unlike for other setup() methods, ignore the base; it will always be
	 * null.
	 * 
	 * @see Prototype#setup(EvolutionState,Parameter)
	 */
	public void setup(final EvolutionState state, final Parameter base) {
				
		int numOfInterpreters = random.length;
		Parameter p = PshDefaults.base().push(P_INTERPRETER);

		interpreter = new Interpreter[numOfInterpreters];
		for (int i = 0; i < numOfInterpreters; i++) {
			interpreter[i] = (Interpreter) (parameters
					.getInstanceForParameterEq(p, null, Interpreter.class));
			interpreter[i].Initialize(random[i]);
			interpreter[i].setup(this, p);
		}
		super.setup(this, base);
	}

}
