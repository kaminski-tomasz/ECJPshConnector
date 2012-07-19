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
