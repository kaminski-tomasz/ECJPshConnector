package org.ecj.psh;

import org.spiderland.Psh.Interpreter;
import org.spiderland.Psh.Program;

import ec.EvolutionState;
import ec.Individual;
import ec.Problem;
import ec.simple.SimpleProblemForm;
import ec.util.Parameter;

public abstract class PshProblem extends Problem implements SimpleProblemForm {

	public static final String P_PSHPROBLEM = "problem";

	public static final String P_USEFRAMES = "use-frames";
	public static final String P_MAXRANDINT = "max-rand-int";
	public static final String P_MINRANDINT = "min-rand-int";
	public static final String P_RANDINTRES = "rand-int-res";
	public static final String P_MAXRANDFLOAT = "max-rand-float";
	public static final String P_MINRANDFLOAT = "min-rand-float";
	public static final String P_RANDFLOATRES = "rand-float-res";
	public static final String P_MAXRANDCODESIZE = "max-rand-code-size";
	public static final String P_MAXPOINTS = "max-points-int-prog";

	/**
	 * List of allowed instructions
	 */
	public Program instructionList;

	/**
	 * Interpreter of Push programs
	 */
	public Interpreter interpreter;

	/*
	 * Settings for interpreters. These are loaded from params file
	 */
	public boolean useFrames;

	public int maxRandomInt;
	public int minRandomInt;
	public int randomIntResolution;

	public float maxRandomFloat;
	public float minRandomFloat;
	public float randomFloatResolution;

	public int maxRandomCodeSize;
	public int maxPointsInProgram;

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
		// TODO set up all parameters, initialize instruction set

		// construct interpreter with instruction set
		interpreter = new Interpreter();
		interpreter.SetInstructions(instructionList);
		interpreter.SetRandomParameters(minRandomInt, maxRandomInt,
				randomIntResolution, minRandomFloat, maxRandomFloat,
				randomFloatResolution, maxRandomCodeSize, maxPointsInProgram);
	}

	@Override
	public Object clone() {
		PshProblem newProblem = (PshProblem) super.clone();
		// at present interpreter is created from scratch ;-(
		// and so its instruction list (which are shared among interpreter and
		// its clones)
		newProblem.interpreter = new Interpreter();
		newProblem.interpreter.SetInstructions(instructionList);
		newProblem.interpreter.SetRandomParameters(minRandomInt, maxRandomInt,
				randomIntResolution, minRandomFloat, maxRandomFloat,
				randomFloatResolution, maxRandomCodeSize, maxPointsInProgram);
		// instruction set stays the same
		return newProblem;
	}

}
