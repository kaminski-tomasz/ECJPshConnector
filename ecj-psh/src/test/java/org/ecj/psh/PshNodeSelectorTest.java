package org.ecj.psh;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

import org.junit.Before;
import org.junit.Test;
import org.spiderland.Psh.Program;

import ec.EvolutionState;
import ec.util.MersenneTwisterFast;

public class PshNodeSelectorTest {

	protected EvolutionState state = null;

	@Before
	public void prepare() {
		state = mock(EvolutionState.class);
		state.random = new MersenneTwisterFast[1];
		state.random[0] = new MersenneTwisterFast();
	}

	@Test
	public void pickNode_empty_program() throws Exception {
		PshIndividual ind = mock(PshIndividual.class);
		PshNodeSelector nodeSelector = new UnbiasedNodeSelector();
		ind.program = new Program();
		assertEquals(ind.program.programsize(), 0);
		assertEquals(nodeSelector.pickNode(state, 0, 0, ind), 0);	
	}
	
	@Test
	public void pickNode_program_size_eq_1() throws Exception {
		PshIndividual ind = mock(PshIndividual.class);
		PshNodeSelector nodeSelector = new UnbiasedNodeSelector();
		ind.program = new Program("( integer.* )");
		assertEquals(ind.program.programsize(), 1);
		assertEquals(nodeSelector.pickNode(state, 0, 0, ind), 0);
	}
	
	@Test
	public void pickNode_program_size_gt_1() throws Exception {
		PshIndividual ind = mock(PshIndividual.class);
		PshNodeSelector nodeSelector = new UnbiasedNodeSelector();
		ind.program = new Program("( integer.* integer.+  12.3 float.- )");
		assertEquals(ind.program.programsize(), 4);
		int node = nodeSelector.pickNode(state, 0, 0, ind);
		assertTrue(node >= 0 && node < ind.program.size());
	}

}
