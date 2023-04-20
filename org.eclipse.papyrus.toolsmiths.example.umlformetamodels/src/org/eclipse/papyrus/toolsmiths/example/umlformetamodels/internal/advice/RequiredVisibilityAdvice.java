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

package org.eclipse.papyrus.toolsmiths.example.umlformetamodels.internal.advice;

import org.eclipse.gmf.runtime.emf.type.core.IElementMatcher;
import org.eclipse.gmf.runtime.emf.type.core.edithelper.AbstractEditHelperAdvice;
import org.eclipse.gmf.runtime.emf.type.core.requests.IEditCommandRequest;
import org.eclipse.gmf.runtime.emf.type.core.requests.SetRequest;
import org.eclipse.papyrus.infra.emf.requests.UnsetRequest;
import org.eclipse.papyrus.toolsmiths.example.umlformetamodels.internal.matchers.MustBePrivateMatcher;
import org.eclipse.papyrus.toolsmiths.example.umlformetamodels.internal.matchers.MustBePublicMatcher;
import org.eclipse.uml2.uml.Enumeration;
import org.eclipse.uml2.uml.EnumerationLiteral;
import org.eclipse.uml2.uml.InstanceValue;
import org.eclipse.uml2.uml.UMLPackage;
import org.eclipse.uml2.uml.VisibilityKind;

/**
 * Advice that prevents setting an illegal visibility of a named element in an UML metamodel.
 */
public class RequiredVisibilityAdvice extends AbstractEditHelperAdvice {

	private final IElementMatcher mustBePublic = new MustBePublicMatcher();
	private final IElementMatcher mustBePrivate = new MustBePrivateMatcher();

	@Override
	public boolean approveRequest(IEditCommandRequest request) {
		if (request instanceof SetRequest) {
			return approveSetRequest((SetRequest) request);
		} else if (request instanceof UnsetRequest) {
			return approveUnsetRequest((UnsetRequest) request);
		} else {
			return super.approveRequest(request);
		}
	}

	/**
	 * Deny a request that would set the visibility of an element to anything other than the
	 * value that it must have, if it is restricted to some value.
	 */
	protected boolean approveSetRequest(SetRequest request) {
		boolean result = true;

		if (request.getFeature() == UMLPackage.Literals.NAMED_ELEMENT__VISIBILITY) {
			VisibilityKind visibility = getVisibilityKind(request.getValue());
			if (visibility != VisibilityKind.PUBLIC_LITERAL && mustBePublic.matches(request.getElementToEdit())) {
				result = false;
			}
			if (visibility != VisibilityKind.PRIVATE_LITERAL && mustBePrivate.matches(request.getElementToEdit())) {
				result = false;
			}
		}

		return result;
	}

	/**
	 * Deny a request that would unset the visibility of an element if it is required to have some value.
	 */
	protected boolean approveUnsetRequest(UnsetRequest request) {
		boolean result = true;

		if (request.getFeature() == UMLPackage.Literals.NAMED_ELEMENT__VISIBILITY) {
			// Unset a visibility makes it public
			if (mustBePrivate.matches(request.getElementToEdit())) {
				result = false;
			}
		}

		return result;
	}

	/**
	 * Infer by any means available the visibility kind intended by a {@code value}.
	 *
	 * @param value
	 *            a value requested to be set into the visibility of an element
	 * @return the inferred visibility enumeration literal, or {@code null} if none can be determined
	 */
	private VisibilityKind getVisibilityKind(Object value) {
		VisibilityKind result = null;

		if (value instanceof VisibilityKind) {
			result = (VisibilityKind) value;
		} else if (value instanceof InstanceValue) {
			InstanceValue instance = (InstanceValue) value;
			if (instance.getInstance() instanceof EnumerationLiteral) {
				result = getVisibilityKind(instance.getInstance());
			}
		} else if (value instanceof EnumerationLiteral) {
			EnumerationLiteral literal = (EnumerationLiteral) value;
			Enumeration enumeration = literal.getEnumeration();
			if (enumeration != null && "UML::VisibilityKind".equals(enumeration.getQualifiedName())) { //$NON-NLS-1$
				result = getVisibilityKind(literal.getName());
			}
		} else if (value instanceof String) {
			try {
				result = VisibilityKind.valueOf(((String) value).toLowerCase());
			} catch (Exception e) {
				// Not a value of this enumeration
			}
		}

		return result;
	}

}
