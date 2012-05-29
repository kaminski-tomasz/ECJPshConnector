package org.ecj.psh.breed;

import org.ecj.psh.PshDefaults;

import ec.DefaultsForm;
import ec.util.Parameter;

public class PshBreedDefaults implements DefaultsForm {
	public static final String P_BREED = "breed";

	/** Returns the default base, which is built off of the PshDefaults base. */
	public static final Parameter base() {
		return PshDefaults.base().push(P_BREED);
	}

}
