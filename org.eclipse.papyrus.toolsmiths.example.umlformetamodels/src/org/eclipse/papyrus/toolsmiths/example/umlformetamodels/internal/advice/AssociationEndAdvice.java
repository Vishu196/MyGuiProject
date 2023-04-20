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

import java.util.Collection;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.gmf.runtime.emf.type.core.edithelper.AbstractEditHelperAdvice;
import org.eclipse.gmf.runtime.emf.type.core.requests.IEditCommandRequest;
import org.eclipse.gmf.runtime.emf.type.core.requests.SetRequest;
import org.eclipse.uml2.uml.Property;
import org.eclipse.uml2.uml.UMLPackage;

/**
 * Advice that constrains the editing of association ends in UML metamodels.
 */
public class AssociationEndAdvice extends AbstractEditHelperAdvice {

	@Override
	public boolean approveRequest(IEditCommandRequest request) {
		if (request instanceof SetRequest) {
			return approveSetRequest((SetRequest) request);
		} else {
			return super.approveRequest(request);
		}
	}

	/**
	 * Deny a request that would set subsetted properties of a {@link Property} that is not an
	 * end of some {@link Association}.
	 */
	protected boolean approveSetRequest(SetRequest request) {
		boolean result = true;

		if (request.getFeature() == UMLPackage.Literals.PROPERTY__SUBSETTED_PROPERTY) {
			// Unconditionally allow an unset
			result = isAssociationEnd(request.getElementToEdit()) || isEmpty(request.getValue());
		}

		return result;
	}

	/** Query whether a {@code property} is an association end. */
	protected final boolean isAssociationEnd(EObject property) {
		return (property instanceof Property) && ((Property) property).getAssociation() != null;
	}

	/** Query whether a {@code value} is {@code null} or an empty collection. */
	protected boolean isEmpty(Object value) {
		return value == null || (value instanceof Collection<?> && ((Collection<?>) value).isEmpty());
	}

}
