package org.ecj.psh.breed;

import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.when;

import org.ecj.psh.PshIndividual;
import org.junit.Test;

public class OnePointCrossoverTest extends CrossoverTest {
	
	@Override
	public void prepare() {
		super.prepare();
		
		// setting up operator
		crossover = new OnePointCrossover();
		crossover.parents = new PshIndividual[2];
		crossover.parents[0] = new PshIndividual();
		crossover.parents[1] = new PshIndividual();
		// ten tries before giving up (but it'll never be reached)
		crossover.tries = 10;
		// safe limit for program length
		when(interpreter.getMaxPointsInProgram()).thenReturn(50);
	}
		
	@Test
	public void _1PX_test_too_short_programs() throws Exception {
		crossover.homologous = false;		

		System.out.println("*** 1PX too short programs:");
		do_crossover_test(
			new String[]{ "( )", "( B1 B2 )" },
			new String[]{ "( )", "( B1 B2 )" },
			true
		);
		do_crossover_test(
			new String[]{ "( A1 A2 )", "( )" },
			new String[]{ "( A1 A2)", "( )" },
			true
		);
	}
	
	@Test
	public void _1PX_test_cutpoints_borderline_cases() throws Exception {
		crossover.homologous = false;		

		System.out.println("*** 1PX cutpoints borderline cases:");
		
		// cutpoints: 0, 1
		when(stateRandom[0].nextInt(anyInt())).thenReturn( 0, 1 );
		do_crossover_test(
			new String[]{ "( A1 )", "( B1 B2 )" },
			new String[]{ "( B2 )", "( B1 A1 )" },
			true
		);
		// cutpoints: 0, 0
		when(stateRandom[0].nextInt(anyInt())).thenReturn( 0, 0 );
		do_crossover_test(
			new String[]{ "( A1 A2 )", "( B1 )" },
			new String[]{ "( B1 )", "( A1 A2 )" },
			true
		);
		
		
		// cutpoints: 1, 1
		when(stateRandom[0].nextInt(anyInt())).thenReturn( 1, 1 );
		do_crossover_test(
			new String[]{ "( A1 A2 A3 A4 )", "( B1 B2 B3 B4 )" },
			new String[]{ "( A1 B2 B3 B4 )", "( B1 A2 A3 A4 )" },
			true
		);
		// cutpoints: 3, 3
		when(stateRandom[0].nextInt(anyInt())).thenReturn( 3, 3 );
		do_crossover_test(
			new String[]{ "( A1 A2 A3 (A4 A5) )", "( B1 B2 B3 B4 )" },
			new String[]{ "( A1 A2 A3 B4 )", "( B1 B2 B3 (A4 A5) )" },
			true
		);
		// cutpoints: 1, 3
		when(stateRandom[0].nextInt(anyInt())).thenReturn( 1, 3 );
		do_crossover_test(
			new String[]{ "( A1 A2 A3 (A4 A5))", "( B1 B2 B3 B4 )" },
			new String[]{ "( A1 B4 )", "( B1 B2 B3 A2 A3 (A4 A5) )" },
			true
		);
	}
	
	@Test
	public void _1PX_some_normal_cases() throws Exception {
		crossover.homologous = false;		

		System.out.println("*** 1PX some normal cases:");

		// cutpoints: 3, 2
		when(stateRandom[0].nextInt(anyInt())).thenReturn( 3, 2 );
		do_crossover_test(
			new String[]{ "( A1 A2 A3 (A4 A5 (1.2 234)) A6 )", "( (B1 B2) B3 123 B4 B5 )" },
			new String[]{ "( A1 A2 A3 123 B4 B5 )", "( (B1 B2) B3 (A4 A5 (1.2 234)) A6 )" },
			true
		);
	}

	@Test
	public void _1PXh_test_too_short_programs() throws Exception {
		crossover.homologous = true;		

		System.out.println("*** 1PXh too short programs:");
		do_crossover_test(
			new String[]{ "( )", "( B1 B2 )" },
			new String[]{ "(  )", "( B1 B2 )" },
			true
		);
		do_crossover_test(
			new String[]{ "( A1 A2 )", "(  )" },
			new String[]{ "( A1 A2)", "(  )" },
			true
		);
	}
	
	@Test
	public void _1PXh_test_cutpoint_borderline_cases() throws Exception {
		crossover.homologous = true;		

		System.out.println("*** 1PXh cutpoint borderline cases:");
		// cutpoint: 0
		when(stateRandom[0].nextInt(anyInt())).thenReturn( 0 );
		do_crossover_test(
			new String[]{ "( A1 )", "( B1 B2 )" },
			new String[]{ "( B1 B2 )", "( A1 )" },
			true
		);
		// cutpoint: 1
		when(stateRandom[0].nextInt(anyInt())).thenReturn( 1 );
		do_crossover_test(
			new String[]{ "( A1 A2 )", "( B1 )" },
			new String[]{ "( A1 )", "( B1 A2 )" },
			true
		);
		
		// cutpoint: 1
		when(stateRandom[0].nextInt(anyInt())).thenReturn( 1 );
		do_crossover_test(
			new String[]{ "( A1 A2 A3 A4 )", "( B1 B2 B3 B4 )" },
			new String[]{ "( A1 B2 B3 B4 )", "( B1 A2 A3 A4 )" },
			true
		);
		// cutpoint: 3
		when(stateRandom[0].nextInt(anyInt())).thenReturn( 3 );
		do_crossover_test(
			new String[]{ "( A1 A2 A3 (A4 A5) )", "( B1 B2 B3 B4 )" },
			new String[]{ "( A1 A2 A3 B4 )", "( B1 B2 B3 (A4 A5) )" },
			true
		);
	}
	
	@Test
	public void _1PXh_some_normal_cases() throws Exception {
		crossover.homologous = true;		

		System.out.println("*** 1PXh some normal cases:");

		// cutpoint: 2
		when(stateRandom[0].nextInt(anyInt())).thenReturn( 2 );
		do_crossover_test(
			new String[]{ "( A1 A2 A3 (A4 A5 (1.2 234)) A6 )", "( (B1 B2) B3 123 B4 B5 )" },
			new String[]{ "( A1 A2 123 B4 B5 )", "( (B1 B2) B3 A3 (A4 A5 (1.2 234)) A6  )" },
			true
		);
		
		// cutpoint: 2
		when(stateRandom[0].nextInt(anyInt())).thenReturn( 2 );
		do_crossover_test(
			new String[]{ "( A1 A2 A3 (A4 A5 (1.2 234)) A6 )", "( (B1 B2) B3 123 B4 B5 )" },
			new String[]{ "( A1 A2 123 B4 B5 )", "( (B1 B2) B3 123 B4 B5 )" },
			false
		);
	}
	
	@Test
	public void _1PX_limited_tries() throws Exception {
		crossover.homologous = false;		

		crossover.tries = 1;
		// safe limit for program length
		when(interpreter.getMaxPointsInProgram()).thenReturn(4);
		
		System.out.println("*** 1PX limited tries cases:");

		// cutpoints: 3, 1
		when(stateRandom[0].nextInt(anyInt())).thenReturn( 3, 1 );
		do_crossover_test(
			new String[]{ "( A1 A2 A3 A4 )", "( B1 B2 B3 B4 )" },
			new String[]{ "( A1 A2 A3 A4 )", "( B1 B2 B3 B4 )" },
			true
		);
		
		crossover.tries = 2;
		// cutpoints: 3, 1,  3, 3
		when(stateRandom[0].nextInt(anyInt())).thenReturn( 3, 1, 3 );
		do_crossover_test(
			new String[]{ "( A1 A2 A3 A4 )", "( B1 B2 B3 B4 )" },
			new String[]{ "( A1 A2 A3 B4 )", "( B1 B2 B3 A4 )" },
			true
		);
		
		crossover.tries = 1;
		// cutpoints: 1, 3
		when(stateRandom[0].nextInt(anyInt())).thenReturn( 1, 3 );
		do_crossover_test(
			new String[]{ "( A1 A2 A3 A4 )", "( B1 B2 B3 B4 )" },
			new String[]{ "( A1 A2 A3 A4 )", "( B1 B2 B3 B4 )" },
			true
		);
		
		crossover.tries = 1;
		// cutpoints: 1, 3
		when(stateRandom[0].nextInt(anyInt())).thenReturn( 1, 3 );
		do_crossover_test(
			new String[]{ "( A1 A2 A3 A4 )", "( B1 B2 B3 B4 )" },
			new String[]{ "( A1 B4 )", "( B1 B2 B3 B4 )" },
			false		// ignore second parent
		);
	}

}
