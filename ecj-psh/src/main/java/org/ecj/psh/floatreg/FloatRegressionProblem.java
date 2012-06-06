package org.ecj.psh.floatreg;

import org.ecj.psh.PshEvaluator;
import org.ecj.psh.PshIndividual;
import org.ecj.psh.PshProblem;
import org.spiderland.Psh.Interpreter;
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
			{ -5.0f, 18.0f }, { -4.0f, 9.0f }, { -3.0f, 2.0f }, 
			{ -2.0f, -3.0f }, { -1.0f, -6.0f }, { 0.0f, -7.0f }, 
			{ 1.0f, -6.0f }, { 2.0f, -3.0f }, { 3.0f, 2.0f }, 
			{ 4.0f, 9.0f }, { 5.0f, 18.0f } 
	};
	
	public void setupInstructionList() {
		// Allowed instruction list
		// FIXME it doesn't work anymore
		try {
//			this.instructionList = new Program("(float.* float.+ float.- "
//					+ "float./ float.dup float.flush float.stackdepth "
//					+ "float.swap float.erc input.makeinputs1)");
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
		
		Interpreter interpreter = ((PshEvaluator) state.evaluator).interpreter[threadnum];
		Program program = ((PshIndividual) ind).program;
		
		float fitness = 0.0f;
		int hits = 0;
		
		for (float[] testCase : testCases) {
			float input = testCase[0];
			float output = testCase[1];
			interpreter.ClearStacks();
			
			// setting input value to input stack
			interpreter.inputStack().push((Float)input);

			// executing the program
			interpreter.Execute(state, threadnum, program,
					interpreter.getExecutionLimit());

			// Penalize individual if there is no result on the stack.
			if (interpreter.floatStack().size() == 0) {
				fitness += 1000.0f;
				continue;
			}

			// update result with absolute difference
			float result = Math.abs(interpreter.floatStack().top() - output);
			if (result < 0.01)
				hits++;
			fitness += result;

		}
		if (Float.isInfinite(fitness)) {
			fitness = Float.MAX_VALUE;
		} else {
			// compute mean absolute error
			fitness = fitness / (float) testCases.length;
		}

		KozaFitness f = (KozaFitness) ind.fitness; 
		f.setStandardizedFitness(state, fitness);
		f.hits = hits;
		ind.evaluated = true;
		
	}

}
