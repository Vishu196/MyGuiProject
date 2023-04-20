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
import org.eclipse.uml2.uml.Classifier;
import org.eclipse.uml2.uml.EnumerationLiteral;
import org.eclipse.uml2.uml.Feature;
import org.eclipse.uml2.uml.Parameter;
import org.eclipse.uml2.uml.util.UMLSwitch;

/**
 * A matcher for elements that must have public visibility in an UML metamodel.
 */
public class MustBePublicMatcher implements IElementMatcher {

	private final Switch<Boolean> mustBePublic = new MustBePublic();

	@Override
	public boolean matches(EObject eObject) {
		return mustBePublic.doSwitch(eObject);
	}

	//
	// Nested types
	//

	/** Classifiers that are not associations must be public, as must features, operation parameters, and enumeration literals. */
	private static class MustBePublic extends UMLSwitch<Boolean> {

		@Override
		public Boolean caseAssociation(Association object) {
			return false;
		}

		@Override
		public Boolean caseClassifier(Classifier object) {
			return true;
		}

		@Override
		public Boolean caseFeature(Feature object) {
			return true;
		}

		@Override
		public Boolean caseParameter(Parameter object) {
			return true;
		}

		@Override
		public Boolean caseEnumerationLiteral(EnumerationLiteral object) {
			return true;
		}

		@Override
		public Boolean defaultCase(EObject object) {
			return false;
		}

	}

}
