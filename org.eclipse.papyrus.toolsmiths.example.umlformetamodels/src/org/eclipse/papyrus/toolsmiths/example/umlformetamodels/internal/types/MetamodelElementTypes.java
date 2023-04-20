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

package org.eclipse.papyrus.toolsmiths.example.umlformetamodels.internal.types;

import org.eclipse.gmf.runtime.emf.type.core.ElementTypeRegistry;
import org.eclipse.gmf.runtime.emf.type.core.IElementType;
import org.eclipse.papyrus.uml.service.types.element.UMLElementTypes;

/**
 * Convenient access to the element types for UML Metamodeling.
 */
public class MetamodelElementTypes {

	private static final String PREFIX = "org.eclipse.papyrus.toolsmiths.example.umlformetamodels."; //$NON-NLS-1$
	private static final String METAMODEL = PREFIX + "Metamodel"; //$NON-NLS-1$
	private static final String METACLASS = PREFIX + "Metaclass"; //$NON-NLS-1$

	public static IElementType getMetamodelType() {
		return ElementTypeRegistry.getInstance().getType(METAMODEL);
	}

	public static IElementType getMetaclassType() {
		return ElementTypeRegistry.getInstance().getType(METACLASS);
	}

	public static IElementType getPropertyType() {
		return UMLElementTypes.PROPERTY;
	}

}
