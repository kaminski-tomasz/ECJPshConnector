package org.ecj.psh.floatreg;

import org.ecj.psh.PshIndividual;
import org.ecj.psh.PshProblem;
import org.spiderland.Psh.Program;

import ec.EvolutionState;
import ec.Individual;
import ec.gp.koza.KozaFitness;
import ec.util.Parameter;

/**
 * Simple symbolic regression problem
 * 
 * @author Tomasz Kamiński
 * 
 */
public class FloatRegressionProblem extends PshProblem {
	
	// some test cases
	float testCases[][] = { 
			{ -5, 18 }, { -4, 9 }, { -3, 2 }, 
			{ -2, -3 }, { -1, -6 }, { 0, -7 }, 
			{ 1, -6 }, { 2, -3 }, { 3, 2 }, 
			{ 4, 9 }, { 5, 18 } 
	};
	
	@Override
	public void setupInstructionList() {
		// Allowed instruction list
		// TODO these should be loaded from file!
		try {
			this.instructionList = new Program("(float.* float.+ float.- "
					+ "float./ float.dup float.flush float.stackdepth "
					+ "float.swap float.erc input.makeinputs1)");
		} catch (Exception e) {
			throw new InternalError();
		}
	}
	
	@Override
	public void setup(final EvolutionState state, final Parameter base) {
		super.setup(state, base);
		// maybe some init here?
	}
	
	@Override
	public void evaluate(EvolutionState state, Individual ind,
			int subpopulation, int threadnum) {

		if (ind.evaluated)
			return;

		if (!(ind instanceof PshIndividual)) {
			state.output.fatal("This is not PshIndividual instance!");
		}
		
		Program program = ((PshIndividual) ind).program;
		float fitness = 0.0f;
		
		for (float[] testCase : testCases) {
			float input = testCase[0];
			float output = testCase[1];
			interpreter.ClearStacks();
			
			// setting input value to input stack
			interpreter.inputStack().push((Float)input);
			
			// executing the program
			interpreter.Execute(state, threadnum, program, executionLimit);
			
			// Penalize individual if there is no result on the stack.
			if(interpreter.inputStack().size() == 0){
				fitness += 1000.0f;
				continue;
			}
			
			// update result with absolute difference
			float result = interpreter.floatStack().top();
			fitness += Math.abs(result - output);
		}
		if (Float.isInfinite(fitness)) {
			fitness = Float.MAX_VALUE;
		} else {
			// compute mean absolute error
			fitness /= testCases.length;
		}

		((KozaFitness)ind.fitness).setStandardizedFitness(state, fitness);
		ind.evaluated = true;
	}

}
