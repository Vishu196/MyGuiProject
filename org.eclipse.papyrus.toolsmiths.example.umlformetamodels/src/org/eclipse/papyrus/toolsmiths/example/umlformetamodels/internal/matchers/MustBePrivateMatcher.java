/*****************************************************************************
 * Copyright (c) 2021 Christian W. Damus, CEA LIST, and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *   Christian W. Damus - Initial API and implementation
 *
 *****************************************************************************/

package org.eclipse.papyrus.toolsmiths.example.umlformetamodels.internal.matchers;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.Switch;
import org.eclipse.gmf.runtime.emf.type.core.IElementMatcher;
import org.eclipse.uml2.uml.Association;
import org.eclipse.uml2.uml.util.UMLSwitch;

/**
 * A matcher for elements that must have private visibility in an UML metamodel.
 */
public class MustBePrivateMatcher implements IElementMatcher {

	private final Switch<Boolean> mustBePrivate = new MustBePrivate();

	@Override
	public boolean matches(EObject eObject) {
		return mustBePrivate.doSwitch(eObject);
	}

	//
	// Nested types
	//

	/** Associations must be private; nothing else must. */
	private static class MustBePrivate extends UMLSwitch<Boolean> {

		@Override
		public Boolean caseAssociation(Association object) {
			return true;
		}

		@Override
		public Boolean defaultCase(EObject object) {
			return false;
		}

	}

}
