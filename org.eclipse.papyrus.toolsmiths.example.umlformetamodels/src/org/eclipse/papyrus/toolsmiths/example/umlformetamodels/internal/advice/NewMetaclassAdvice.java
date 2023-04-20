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

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.gmf.runtime.common.core.command.CompositeCommand;
import org.eclipse.gmf.runtime.common.core.command.ICommand;
import org.eclipse.gmf.runtime.emf.type.core.commands.DeferredSetValueCommand;
import org.eclipse.gmf.runtime.emf.type.core.edithelper.AbstractEditHelperAdvice;
import org.eclipse.gmf.runtime.emf.type.core.requests.ConfigureRequest;
import org.eclipse.gmf.runtime.emf.type.core.requests.CreateElementRequest;
import org.eclipse.gmf.runtime.emf.type.core.requests.IEditCommandRequest;
import org.eclipse.gmf.runtime.emf.type.core.requests.SetRequest;
import org.eclipse.papyrus.infra.services.edit.service.ElementEditServiceUtils;
import org.eclipse.papyrus.infra.services.edit.service.IElementEditService;
import org.eclipse.papyrus.infra.services.edit.utils.RequestParameterConstants;
import org.eclipse.papyrus.toolsmiths.example.umlformetamodels.internal.types.MetamodelElementTypes;
import org.eclipse.uml2.uml.Type;
import org.eclipse.uml2.uml.UMLPackage;
import org.eclipse.uml2.uml.resource.UMLResource;

/**
 * Advice that configures a new metaclass upon its creation in the model.
 */
public class NewMetaclassAdvice extends AbstractEditHelperAdvice {

	/**
	 * Create a new property "name" of string type in the new metaclass. Because we use the <em>Element Types</em>
	 * framework to create that property, the required visibility advice will assign it public visibility without
	 * this metaclass advice having to be concerned with it.
	 */
	@Override
	protected ICommand getAfterConfigureCommand(ConfigureRequest request) {
		org.eclipse.uml2.uml.Class metaclass = (org.eclipse.uml2.uml.Class) request.getElementToConfigure();

		// Create a new property
		CreateElementRequest createRequest = new CreateElementRequest(metaclass, MetamodelElementTypes.getPropertyType());
		createRequest.setParameter(RequestParameterConstants.NAME_TO_SET, "name"); //$NON-NLS-1$

		// Use the edit service to ensure that creation of the property, itself, is advised by element types
		IElementEditService edit = ElementEditServiceUtils.getCommandProvider(metaclass);
		ICommand createProperty = edit.getEditCommand(createRequest);

		// The type of the new property can only be set once the property exists, so defer setting it until then
		SetRequest setTypeRequest = new SetRequest(request.getEditingDomain(), null,
				UMLPackage.Literals.TYPED_ELEMENT__TYPE, getStringType(request));
		ICommand setType = new DeferredSetValueCommand(setTypeRequest) {
			@Override
			protected EObject getElementToEdit() {
				return createRequest.getNewElement();
			}
		};

		return CompositeCommand.compose(createProperty, setType);
	}

	/**
	 * Get the <tt>String</tt> type from the <em>UML Primitive Types</em> library in the context of the
	 * given edit {@code request}.
	 *
	 * @param request
	 *            an edit request bearing the editing domain context
	 * @return the UML <tt>String</tt> type
	 */
	protected Type getStringType(IEditCommandRequest request) {
		URI uri = URI.createURI(UMLResource.UML_PRIMITIVE_TYPES_LIBRARY_URI).appendFragment("String"); //$NON-NLS-1$
		return (Type) request.getEditingDomain().getResourceSet().getEObject(uri, true);
	}

}
