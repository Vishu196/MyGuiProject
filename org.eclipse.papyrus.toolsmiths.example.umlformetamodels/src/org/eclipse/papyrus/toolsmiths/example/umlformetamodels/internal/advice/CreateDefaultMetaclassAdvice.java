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

import org.eclipse.gmf.runtime.common.core.command.ICommand;
import org.eclipse.gmf.runtime.emf.type.core.edithelper.AbstractEditHelperAdvice;
import org.eclipse.gmf.runtime.emf.type.core.requests.ConfigureRequest;
import org.eclipse.gmf.runtime.emf.type.core.requests.CreateElementRequest;
import org.eclipse.papyrus.infra.services.edit.service.ElementEditServiceUtils;
import org.eclipse.papyrus.infra.services.edit.service.IElementEditService;
import org.eclipse.papyrus.infra.services.edit.utils.RequestParameterConstants;
import org.eclipse.papyrus.toolsmiths.example.umlformetamodels.internal.types.MetamodelElementTypes;
import org.eclipse.uml2.uml.Package;

/**
 * Advice that creates the default metaclass in a new metamodel package.
 */
public class CreateDefaultMetaclassAdvice extends AbstractEditHelperAdvice {

	/**
	 * Create a metaclass in a new metamodel as a "starter kit" for designing the metamodel.
	 */
	@Override
	protected ICommand getAfterConfigureCommand(ConfigureRequest request) {
		Package metamodel = (Package) request.getElementToConfigure();

		CreateElementRequest createRequest = new CreateElementRequest(metamodel, MetamodelElementTypes.getMetaclassType());
		createRequest.setParameter(RequestParameterConstants.NAME_TO_SET, "Element"); //$NON-NLS-1$

		IElementEditService edit = ElementEditServiceUtils.getCommandProvider(metamodel);
		return (edit == null) ? null : edit.getEditCommand(createRequest);
	}

}
