/*
* Copyright 2012 Tomasz Kamiński
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/

package org.ecj.psh.problem;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Scanner;

import org.ecj.psh.PshEvolutionState;
import org.ecj.psh.PshIndividual;
import org.ecj.psh.PshProblem;
import org.spiderland.Psh.Interpreter;
import org.spiderland.Psh.Program;

import ec.EvolutionState;
import ec.Individual;
import ec.gp.koza.KozaFitness;
import ec.util.Parameter;

/**
 * Simple symbolic regression problem for integer numbers
 * 
 * @author Tomasz Kamiński
 * 
 */
public class IntRegressionProblem extends PshProblem {
	
	public static final String P_TESTCASES = "test-cases";
	public static final String P_REPEATINTSTACK = "repeat-int-stack"; 

	/** How many times should input number be duplicated in int stack */
	public int repeatIntStack;

	// symbolic regression test cases
	public ArrayList<Integer[]> testCases;
		
	@Override
	public void setup(final EvolutionState state, final Parameter base) {
		super.setup(state, base);
		Parameter def = this.defaultBase();

		state.output.message(base.push(P_TESTCASES).toString());
		state.output.message(def.push(P_TESTCASES).toString());
		File testCasesFile = state.parameters.getFile(base.push(P_TESTCASES),
				def.push(P_TESTCASES));
		state.output.message(testCasesFile.toString());
		testCases = new ArrayList<Integer[]>();
		FileReader reader = null;
		try {
			reader = new FileReader(testCasesFile);
			Scanner scanner = new Scanner(reader);
			scanner.useLocale(Locale.US);
			while (scanner.hasNextFloat()) {
				int input = scanner.nextInt();
				int output = scanner.nextInt();
				testCases.add(new Integer[]{input,output});
			}
		} catch (IOException e) {
			state.output.fatal("Couldn't read test cases for integer symbolic regression.");
		} finally {
			if (reader != null)	try {
					reader.close();
				} catch (IOException e) { 
			}
		}
		state.output.message("Test cases: ");
		for (Integer[] testCase : testCases) {
			state.output.message("input = " + testCase[0] + ", output = "
					+ testCase[1]);
		}
		
		repeatIntStack = state.parameters.getIntWithDefault(
				base.push(P_REPEATINTSTACK), def.push(P_REPEATINTSTACK), 1);
		
	}
	
	private int evaluateTestCase(Interpreter interpreter, Program program, int input, int output) {
		
		interpreter.ClearStacks();

		// pushing input value to int stack
		for (int i = 0; i < repeatIntStack; i++) {
			interpreter.intStack().push(input);
		}
		
		// setting input value to input stack
		interpreter.inputStack().push((Integer)input);

		// executing the program
		interpreter.Execute(program,
				interpreter.getExecutionLimit());

		// Penalize individual if there is no result on the stack.
		if (interpreter.intStack().size() == 0) {
			return 1000;
		}

		// compute result as absolute difference
		int error = Math.abs(interpreter.intStack().top() - output);
		
		if (error == Integer.MIN_VALUE)
			error = Integer.MAX_VALUE;
		
		return error;
	}
	
	@Override
	public void evaluate(EvolutionState state, Individual ind,
			int subpopulation, int threadnum) {

		if (ind.evaluated)
			return;

		if (!(ind instanceof PshIndividual)) {
			state.output.fatal("This is not PshIndividual instance!");
		}
		
		Interpreter interpreter = ((PshEvolutionState) state).interpreter[threadnum];
		Program program = ((PshIndividual) ind).program;
		
		double meanError = 0.0f;
		int hits = 0;
		
		for (Integer[] testCase : testCases) {
			int input = testCase[0];
			int output = testCase[1];

			int error = evaluateTestCase(interpreter, program, input, output);
			if (error == 0)
				hits++;
			
			meanError += (double) error;
		}
				
		if (Double.isInfinite(meanError)) {
			meanError = Float.MAX_VALUE;
		} else {
			// compute mean absolute error
			meanError = meanError / (float) testCases.size();
		}
		
		KozaFitness f = (KozaFitness) ind.fitness; 
		f.setStandardizedFitness(state, (float)meanError);
		f.hits = hits;
		ind.evaluated = true;
	}

}
