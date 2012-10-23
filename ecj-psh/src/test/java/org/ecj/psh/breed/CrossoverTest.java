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

package org.ecj.psh.breed;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;

import org.ecj.psh.PshEvaluator;
import org.ecj.psh.PshEvolutionState;
import org.ecj.psh.PshIndividual;
import org.junit.Before;
import org.spiderland.Psh.Interpreter;
import org.spiderland.Psh.Program;

import ec.util.MersenneTwisterFast;
import ec.util.Output;

public abstract class CrossoverTest {

	protected CrossoverPipeline crossover;
	
	protected PshEvolutionState state;
	protected Output stateOutput;
	protected MersenneTwisterFast[] stateRandom;
	protected PshEvaluator stateEvaluator;
	protected Interpreter interpreter;
	protected int thread;
    
	
	@Before
	public void prepare() {
	
        // mock the evolution state because it is too expensive
        // to provide it properly configured
        state = mock(PshEvolutionState.class);
        stateOutput = mock(Output.class);
        stateRandom = new MersenneTwisterFast[1];
        stateRandom[0] = mock(MersenneTwisterFast.class);
        stateEvaluator = mock(PshEvaluator.class);
        interpreter = mock(Interpreter.class);
        thread = 0;
        
        // make state output fatal throw a new exception
        doThrow(IllegalStateException.class).when(stateOutput).fatal(anyString());

        // mocking the evolution state
        state.output = stateOutput;
        state.random = stateRandom;
        state.evaluator = stateEvaluator;
                
        state.interpreter = new Interpreter[1];
        state.interpreter[0] = interpreter;
        
	}
	
	protected void do_crossover_test(String[] parents, String[]expectedChildren, boolean breedSecondParent) throws Exception {
		PshIndividual parentA = crossover.parents[0]; 
		PshIndividual parentB = crossover.parents[1];
		Program childA, childB;
		
		// parents
		parentA.program = new Program(parents[0]);
		parentB.program = new Program(parents[1]);
		System.out.println("Parents: " + parentA + ", " + parentB);
		// children
		childA = new Program(expectedChildren[0]);
		childB = new Program(expectedChildren[1]);
		
		// crossover & check answer
		crossover.crossover(state, thread, breedSecondParent);
		System.out.println("Children: " + parentA + ", " + parentB);
		System.out.println("Expected: " + childA + ", " + childB);
		System.out.println();
		assertEquals(childA, parentA.program);
		assertEquals(childB, parentB.program);
	}
	
}
