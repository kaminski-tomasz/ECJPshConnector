package org.ecj.psh;

import org.ecj.psh.util.Simplifier;
import org.spiderland.Psh.Interpreter;
import org.spiderland.Psh.Program;

import ec.EvolutionState;
import ec.Individual;
import ec.Problem;
import ec.simple.SimpleProblemForm;
import ec.util.Parameter;

public abstract class PshProblem extends Problem implements SimpleProblemForm {

	public static final String P_PSHPROBLEM = "problem";

	public static final String P_MAXRANDCODESIZE = "max-random-code-size";
	public static final String P_EXECUTIONLIMIT = "execution-limit";
	public static final String P_MAXPOINTSINPROG = "max-points-in-program";
	
	public static final String P_USEFRAMES = "push-frame-mode";
	
	public static final String P_MAXRANDINT = "max-random-integer";
	public static final String P_MINRANDINT = "min-random-integer";
	public static final String P_RANDINTRES = "random-integer-res";
	
	public static final String P_MAXRANDFLOAT = "max-random-float";
	public static final String P_MINRANDFLOAT = "min-random-float";
	public static final String P_RANDFLOATRES = "random-float-res";
	
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
	
	public int maxRandomCodeSize;
	public int executionLimit;
	public int maxPointsInProgram;
	
	public boolean useFrames;

	public int maxRandomInt;
	public int minRandomInt;
	public int randomIntResolution;

	public float maxRandomFloat;
	public float minRandomFloat;
	public float randomFloatResolution;

	/**
	 * PshProblem defines a default base so your subclass doesn't absolutely
	 * have to.
	 */
	@Override
	public Parameter defaultBase() {
		return PshDefaults.base().push(P_PSHPROBLEM);
	}
	
	/**
	 * Sets up the instruction list
	 * TODO should be loaded from file!
	 */
	public abstract void setupInstructionList();
	
	/**
	 * Set up prototype for PshProblem.
	 */
	@Override
	public void setup(final EvolutionState state, final Parameter base) {
		
		Parameter defInterpreter = defaultBase().push("interpreter");
		Parameter baseInterpreter = base.push("interpreter");

		// max. random code size, default 30
		maxRandomCodeSize = state.parameters.getIntWithDefault(
				baseInterpreter.push(P_MAXRANDCODESIZE),
				defInterpreter.push(P_MAXRANDCODESIZE), 30);
		// execution limit for Push programs
		executionLimit = state.parameters.getIntWithDefault(
				baseInterpreter.push(P_EXECUTIONLIMIT),
				defInterpreter.push(P_EXECUTIONLIMIT), 100);
		// max number of points in program
		maxPointsInProgram = state.parameters.getIntWithDefault(
				baseInterpreter.push(P_MAXPOINTSINPROG),
				defInterpreter.push(P_MAXPOINTSINPROG), 100);
		
		// maximum random integer
		maxRandomInt = state.parameters.getIntWithDefault(
				baseInterpreter.push(P_MAXRANDINT),
				defInterpreter.push(P_MAXRANDINT), 10);
		// minimum random integer
		minRandomInt = state.parameters.getIntWithDefault(
				baseInterpreter.push(P_MINRANDINT),
				defInterpreter.push(P_MINRANDINT), -10);
		// random integer resolution
		randomIntResolution = state.parameters.getIntWithDefault(
				baseInterpreter.push(P_RANDINTRES),
				defInterpreter.push(P_RANDINTRES), 1);
		
		// maximum random float
		maxRandomFloat = state.parameters.getFloatWithDefault(
				baseInterpreter.push(P_MAXRANDFLOAT),
				defInterpreter.push(P_MAXRANDFLOAT), 10.0);
		// minimum random float
		minRandomFloat = state.parameters.getFloatWithDefault(
				baseInterpreter.push(P_MINRANDFLOAT),
				defInterpreter.push(P_MINRANDFLOAT), -10.0);
		// random integer float
		randomFloatResolution = state.parameters.getFloatWithDefault(
				baseInterpreter.push(P_RANDFLOATRES),
				defInterpreter.push(P_RANDFLOATRES), 0.01);
		
		// should we use push frame mode
		useFrames = state.parameters.getBoolean(
				baseInterpreter.push(P_USEFRAMES),
				defInterpreter.push(P_USEFRAMES), false);
		
		this.setupInstructionList();
		
		// construct interpreter with instruction list
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

	@Override
	public void describe(final EvolutionState state, final Individual ind,
			final int subpopulation, final int threadnum, final int log) {
		
		PshProblem problem = (PshProblem)state.evaluator.p_problem.clone();
		
		state.output.println("After simplifications: ", log);
		//Simplifier.autoSimplify(state, ind, problem, 1000, subpopulation, threadnum, 0.2);
		Simplifier.autoSimplify(state, (PshIndividual)ind, problem, 1000, subpopulation, threadnum, 0.2f);
		state.output.println("\t"+ind, log);
	}
}
