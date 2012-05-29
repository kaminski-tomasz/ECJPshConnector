package org.ecj.psh;

import ec.DefaultsForm;
import ec.util.Parameter;

/**
 * A static class that returns the base for "default values" which PushGP
 * operators use, rather than making the user specify them all on a per- species
 * basis.
 * 
 * @author Tomasz Kamiński
 * @version 1.0
 */
public final class PshDefaults implements DefaultsForm {
	public static final String P_PSH = "psh";

	/** Returns the default base. */
	public static final Parameter base() {
		return new Parameter(P_PSH);
	}

}
