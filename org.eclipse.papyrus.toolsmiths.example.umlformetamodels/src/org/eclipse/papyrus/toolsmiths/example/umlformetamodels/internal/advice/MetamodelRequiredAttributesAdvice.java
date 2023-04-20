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

import org.eclipse.gmf.runtime.emf.type.core.edithelper.AbstractEditHelperAdvice;
import org.eclipse.gmf.runtime.emf.type.core.requests.IEditCommandRequest;
import org.eclipse.gmf.runtime.emf.type.core.requests.SetRequest;
import org.eclipse.papyrus.infra.emf.requests.UnsetRequest;
import org.eclipse.uml2.uml.UMLPackage;

/**
 * Advice that prevents unsetting of required attributes of UML metamodels.
 */
public class MetamodelRequiredAttributesAdvice extends AbstractEditHelperAdvice {

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
	 * Deny a request that would set the package URI to a {@code null} or blank value.
	 */
	protected boolean approveSetRequest(SetRequest request) {
		boolean result = true;

		if (request.getFeature() == UMLPackage.Literals.PACKAGE__URI) {
			result = request.getValue() != null && !String.valueOf(request.getValue()).isBlank();
		}

		return result;
	}

	/**
	 * Deny a request that would unset the package URI.
	 */
	protected boolean approveUnsetRequest(UnsetRequest request) {
		boolean result = true;

		if (request.getFeature() == UMLPackage.Literals.PACKAGE__URI) {
			result = false;
		}

		return result;
	}

}
