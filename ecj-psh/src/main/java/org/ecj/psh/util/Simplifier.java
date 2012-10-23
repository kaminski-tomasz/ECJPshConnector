/*
* Copyright 2009-2010 Tomasz Kamiński
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

package org.ecj.psh.util;

import org.ecj.psh.PshIndividual;
import org.ecj.psh.PshProblem;
import org.spiderland.Psh.Program;

import ec.EvolutionState;
import ec.Individual;

/**
 * Simplify individual by removing some nodes or flattening them.
 * Based on Psh implementation by J.Klein
 * 
 * @author Tomasz Kamiński
 *
 */
public class Simplifier {

	public static void autoSimplify(EvolutionState state, Individual ind,
			PshProblem problem, int steps, int subpopulation, int threadnum,
			float simplifyByFlattenProb) {

		PshIndividual simplest = (PshIndividual) ind.clone();

		simplest.evaluated = false;
		problem.evaluate(state, simplest, subpopulation, threadnum);

		boolean madeSimpler = false;
		for (int i = 0; i < steps; i++) {

			PshIndividual trial = (PshIndividual) simplest.clone();

			if (trial.program.programsize() <= 0)
				break;

			if (state.random[threadnum].nextBoolean(simplifyByFlattenProb)) {
				// Flatten random thing
				int pointIndex = state.random[threadnum].nextInt(trial.program
						.programsize());
				Object point = trial.program.Subtree(pointIndex);
				if (point instanceof Program) {
					trial.program.Flatten(pointIndex);
					madeSimpler = true;
				}
			} else {
				// Remove small number of random things
				int numberToRemove = state.random[threadnum].nextInt(3) + 1;

				for (int j = 0; j < numberToRemove; j++) {
					int trialSize = trial.program.programsize();

					if (trialSize > 0) {
						int pointIndex = state.random[threadnum]
								.nextInt(trialSize);
						trial.program.ReplaceSubtree(pointIndex, new Program());
						trial.program.Flatten(pointIndex);
						madeSimpler = true;
					}
				}
			}
			if (madeSimpler) {
				trial.evaluated = false;
				problem.evaluate(state, trial, subpopulation, threadnum);

				if (trial.fitness.betterThan(simplest.fitness)
						|| trial.fitness.equivalentTo(simplest.fitness)) {
					simplest = trial.clone();	// FIXME check if cloning is needed here
				}
			}
			madeSimpler = false;
		}

		((PshIndividual) ind).program = new Program(simplest.program);
	}

}
