package org.ecj.psh.select;

import org.ecj.psh.PshDefaults;

import ec.DefaultsForm;
import ec.util.Parameter;

public class PshSelectDefaults implements DefaultsForm {
	public static final String P_SELECT = "select";

	/** Returns the default base, which is built off of the PshDefaults base. */
	public static final Parameter base() {
		return PshDefaults.base().push(P_SELECT);
	}

}
