package org.ecj.psh.breed;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;

import org.ecj.psh.PshEvaluator;
import org.ecj.psh.PshIndividual;
import org.junit.Before;
import org.spiderland.Psh.Interpreter;
import org.spiderland.Psh.Program;

import ec.EvolutionState;
import ec.util.MersenneTwisterFast;
import ec.util.Output;

public abstract class CrossoverTest {

	protected CrossoverPipeline crossover;
	
	protected EvolutionState state;
	protected Output stateOutput;
	protected MersenneTwisterFast[] stateRandom;
	protected PshEvaluator stateEvaluator;
	protected Interpreter interpreter;
	protected int thread;
    
	
	@Before
	public void prepare() {
	
        // mock the evolution state because it is too expensive
        // to provide it properly configured
        state = mock(EvolutionState.class);
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
                
        stateEvaluator.interpreter = new Interpreter[1];
        stateEvaluator.interpreter[0] = interpreter;
        
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
