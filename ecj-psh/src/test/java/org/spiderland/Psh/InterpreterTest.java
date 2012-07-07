package org.spiderland.Psh;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;

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
	
	@Test
	public void some_test() throws Exception {
		
		Program p1 = new Program("( 1 2 integer.+ 3 integer.* )");
		ObjectStack p2 = new Program("( 3.0 2.0 float./ )");
		
		System.out.println(p1);
				
		Interpreter interp = new Interpreter();
		interp.Initialize(new MersenneTwisterFast(1234));
		interp.SetInstructions(new Program("( integer.+ integer.* integer.- float.* float./ )"));
		interp._maxPointsInProgram = 50;
		interp._maxRandomCodeSize = 30;
		interp._maxRandomInt = 10;
		interp._minRandomInt = -10;
		interp._maxRandomFloat = 10;
		interp._minRandomFloat = -10;
		
		for (int i = 0; i < 3; i++)		
		interp.ExecuteInstruction(p1.peek(i));
		
		System.out.println(interp);
		
		
		intStack semantic = interp.intStack().clone();
		System.out.println(semantic);
		
		interp._intStack = semantic.clone();
		assertNotSame(semantic, interp._floatStack);
		interp.ExecuteInstruction((Integer)123);
		interp.ExecuteInstruction(new IntegerAdd());
		System.out.println(interp);
		

		interp._intStack = semantic.clone();
		interp._codeStack.push(p1);
		interp.ExecuteInstruction((Integer)123);
		interp.ExecuteInstruction(new IntegerAdd());
		System.out.println(interp);
		
		for (int i = 0; i< interp._codeStack.size(); i++) {
			System.out.print(" "+interp._codeStack.peek(i));
		}
		System.out.println(interp._codeStack.top());

//		interp._intStack = semantic.clone();
//		Program wstawka = interp.RandomCode(15);
//		interp.ExecuteInstruction(wstawka);
//		System.out.println(interp);
	}
	
}
