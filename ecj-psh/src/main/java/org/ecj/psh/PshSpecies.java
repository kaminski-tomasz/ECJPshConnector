package org.ecj.psh;

import ec.Species;
import ec.gp.GPDefaults;
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

}
