package org.spiderland.Psh;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import ec.util.MersenneTwisterFast;

public class InterpreterTest {

	Interpreter interpreter = null;
	
	@Before
	public void prepare() throws RuntimeException, Exception {
		interpreter = new Interpreter();
		interpreter.Initialize(new MersenneTwisterFast());
		interpreter.SetInstructions(new Program("( registered.float input.makeinputs1 )"));
//		System.out.println(interpreter.GetRegisteredInstructionsString());
	}

//	@Test
	public void interpreter_test_print_stack_instructions() {
		interpreter.printStackInstructions();
	}
	
	@Test
	public void interpreter_test_push_stacks() throws Exception {
		interpreter.ClearStacks();
		
//		System.out.println(interpreter);
		
		Program p1 = new Program ("( 1 2 integer.dup 1.0 2.0 float.dup )");
		
		intStack expectedIntStack = new intStack();
		expectedIntStack.push(1);
		expectedIntStack.push(2);
		expectedIntStack.push(2);
		
		floatStack expectedFloatStack = new floatStack();
		expectedFloatStack.push(1);
		expectedFloatStack.push(2);
		expectedFloatStack.push(2);
		
		interpreter.Execute(p1);
			
		assertEquals(interpreter.intStack(), expectedIntStack);
		assertEquals(interpreter.floatStack(), expectedFloatStack);
		
		interpreter.PushStacks();
		
		interpreter.Execute(p1);
		assertEquals(interpreter.intStack(), expectedIntStack);
		assertEquals(interpreter.floatStack(), expectedFloatStack);
				
	}
	
	
}
