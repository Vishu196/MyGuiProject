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

package org.eclipse.papyrus.toolsmiths.example.umlformetamodels.internal.commands;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.gmf.runtime.common.core.command.ICommand;
import org.eclipse.gmf.runtime.emf.type.core.IClientContext;
import org.eclipse.gmf.runtime.emf.type.core.requests.ConfigureRequest;
import org.eclipse.papyrus.infra.core.resource.ModelSet;
import org.eclipse.papyrus.infra.core.services.ServiceException;
import org.eclipse.papyrus.infra.services.edit.context.TypeContext;
import org.eclipse.papyrus.infra.services.edit.service.ElementEditServiceUtils;
import org.eclipse.papyrus.infra.services.edit.service.IElementEditService;
import org.eclipse.papyrus.infra.services.edit.utils.RequestParameterConstants;
import org.eclipse.papyrus.toolsmiths.example.umlformetamodels.internal.Activator;
import org.eclipse.papyrus.toolsmiths.example.umlformetamodels.internal.types.MetamodelElementTypes;
import org.eclipse.papyrus.uml.diagram.common.commands.ModelCreationCommandBase;
import org.eclipse.uml2.common.util.UML2Util;
import org.eclipse.uml2.uml.Package;
import org.eclipse.uml2.uml.Profile;
import org.eclipse.uml2.uml.UMLFactory;
import org.eclipse.uml2.uml.UMLPackage;
import org.eclipse.uml2.uml.resource.UMLResource;

/**
 * Command that creates a new UML Metamodel.
 */
public class CreateUMLMetamodelCommand extends ModelCreationCommandBase {

	private final ThreadLocal<ModelSet> modelSet = new ThreadLocal<>();

	public CreateUMLMetamodelCommand() {
		super();
	}

	@Override
	public void createModel(ModelSet modelSet) {
		this.modelSet.set(modelSet);

		try {
			super.createModel(modelSet);
		} finally {
			this.modelSet.remove();
		}
	}

	@Override
	protected EObject createRootElement() {
		return UMLFactory.eINSTANCE.createModel();
	}

	@Override
	protected void initializeModel(EObject owner) {
		// This is done after attachment of the package to the resource, and thus the resource set,
		// which is required for the edit commands to work.

		Package metamodel = (Package) owner;

		// This is require a priori for various profile-based element-types to work
		applyProfile(metamodel);

		try {
			// Use the Element Types framework to configure the new root element using the
			// advice defined in this context
			IClientContext clientContext = TypeContext.getContext(modelSet.get());
			ConfigureRequest configureRequest = new ConfigureRequest(metamodel, MetamodelElementTypes.getMetamodelType());
			configureRequest.setClientContext(clientContext);
			configureRequest.setEditingDomain(modelSet.get().getTransactionalEditingDomain());

			// Papyrus provides an advice that sets a default name for a new NamedElement. Tell it a
			// specific name to set
			URI uri = metamodel.eResource().getURI();
			while (!uri.equals(uri.trimFileExtension())) {
				uri = uri.trimFileExtension();
			}
			configureRequest.setParameter(RequestParameterConstants.NAME_TO_SET, uri.lastSegment());

			// Obtain and execute the configure command
			IElementEditService edit = ElementEditServiceUtils.getCommandProvider(MetamodelElementTypes.getMetamodelType(), clientContext);
			ICommand configureCommand = edit.getEditCommand(configureRequest);
			if (configureCommand != null && configureCommand.canExecute()) {
				configureCommand.execute(new NullProgressMonitor(), null);
			}
		} catch (ServiceException | ExecutionException e) {
			Activator.log.error("Failed to initialize new metamodel.", e);
		}
	}

	/** Apply the UML Standard Profile to the new metamodel. */
	private void applyProfile(Package package_) {
		Profile standardProfile = (Profile) UML2Util.load(modelSet.get(), URI.createURI(UMLResource.STANDARD_PROFILE_URI), UMLPackage.Literals.PROFILE);
		if (standardProfile != null) {
			package_.applyProfile(standardProfile);
		} else {
			Activator.log.error("UML Standard Profile is not available.", null);
		}
	}

}
