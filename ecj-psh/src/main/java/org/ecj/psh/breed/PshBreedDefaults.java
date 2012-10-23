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

package org.ecj.psh.breed;

import org.ecj.psh.PshDefaults;

import ec.DefaultsForm;
import ec.util.Parameter;

public class PshBreedDefaults implements DefaultsForm {
	public static final String P_BREED = "breed";

	/** Returns the default base, which is built off of the PshDefaults base. */
	public static final Parameter base() {
		return PshDefaults.base().push(P_BREED);
	}

}
