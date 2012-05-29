package org.ecj.psh;

import java.io.IOException;
import java.io.LineNumberReader;

import org.spiderland.Psh.Program;

import ec.EvolutionState;
import ec.Individual;
import ec.util.Parameter;

/**
 * Subclass of ec.Individual which contains Push program
 * 
 * @author Tomek
 * 
 */
public class PshIndividual extends Individual {

	public static final String P_PROGRAM = "program";

	/**
	 * Push program (an inherited object stack)
	 */
	public Program program;

	@Override
	public Parameter defaultBase() {
		return PshDefaults.base().push(P_INDIVIDUAL);
	}

	/**
	 * Sets up a prototypical PshIndividual with those features which it shares
	 * with other PshIndividuals in its species, and nothing more.
	 */
	@Override
	public void setup(final EvolutionState state, final Parameter base) {
		super.setup(state, base);
		Parameter def = defaultBase();
		evaluated = false;
		this.program = (Program) (state.parameters.getInstanceForParameterEq(
				base.push(P_PROGRAM), def.push(P_PROGRAM), Program.class));
	}

	@Override
	public boolean equals(Object ind) {
		if (!(this.getClass().equals(ind.getClass())))
			return false; // PshIndividuals are special.
		PshIndividual i = (PshIndividual) ind;
		return this.program.equals(i.program);
	}

	@Override
	public int hashCode() {
		// FIXME Program isn't hashed properly
		return this.program.hashCode();
	}

	/**
	 * Used in printIndividual methods
	 */
	@Override
	public String toString() {
		return this.program.toString();
	}

	/**
	 * Used in readIndividual method
	 */
	@Override
	protected void parseGenotype(EvolutionState state, LineNumberReader reader)
			throws IOException {
		int linenumber = reader.getLineNumber();
		String s = reader.readLine();
		if (s == null) {
			state.output.fatal("Reading Line " + linenumber + ": "
					+ "No Push code found.");
		}
		try {
			this.program.Parse(s);
		} catch (Exception e) {
			throw new IOException(e.getMessage());
		}
	}

}
