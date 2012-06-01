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
 * @author Tomasz Kamiński
 * 
 */
public class PshIndividual extends Individual {

	/**
	 * Push program (an inherited object stack)
	 */
	public Program program;

	@Override
	public Parameter defaultBase() {
		return PshDefaults.base().push(P_INDIVIDUAL);
	}

	@Override
	public PshIndividual clone() {
		PshIndividual ind = (PshIndividual) super.clone();
		// deep-clone involves copying entire program code
		ind.program = new Program(this.program);
		return ind;
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
		// create empty program (empty object list)
		// TODO maybe Program class should be parametrized, think about it.
		//		So far we don't need it
		this.program = new Program();
	}

	@Override
	public boolean equals(Object ind) {
		if (!(this.getClass().equals(ind.getClass())))
			return false; // PshIndividuals are special.
		PshIndividual i = (PshIndividual) ind;
		return i.program != null && this.program.equals(i.program);
	}

	@Override
	public int hashCode() {
		// FIXME Program isn't hashed properly. But I wonder whether we need this
		// as long as we don't bother with identical individuals chosen randomly
		// during reproduction step?
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
