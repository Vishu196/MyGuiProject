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
import java.util.List;
import java.util.Map;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.gmf.runtime.common.core.command.CompositeCommand;
import org.eclipse.gmf.runtime.common.core.command.ICommand;
import org.eclipse.gmf.runtime.emf.type.core.IElementType;
import org.eclipse.gmf.runtime.emf.type.core.edithelper.AbstractEditHelperAdvice;
import org.eclipse.gmf.runtime.emf.type.core.requests.CreateElementRequest;
import org.eclipse.gmf.runtime.emf.type.core.requests.DestroyDependentsRequest;
import org.eclipse.gmf.runtime.emf.type.core.requests.IEditCommandRequest;
import org.eclipse.gmf.runtime.emf.type.core.requests.MoveRequest;
import org.eclipse.gmf.runtime.emf.type.core.requests.SetRequest;
import org.eclipse.papyrus.infra.emf.requests.UnsetRequest;
import org.eclipse.papyrus.infra.services.edit.service.ElementEditServiceUtils;
import org.eclipse.papyrus.infra.services.edit.service.IElementEditService;
import org.eclipse.papyrus.uml.service.types.utils.RequestParameterUtils;
import org.eclipse.uml2.uml.Association;
import org.eclipse.uml2.uml.Property;
import org.eclipse.uml2.uml.UMLPackage;

/**
 * Advice for the editing of associations.
 */
public class AssociationEditAdvice extends AbstractEditHelperAdvice {

	/**
	 * Associations must be binary. Do not permit creating or adding a third end.
	 */
	@Override
	public boolean approveRequest(IEditCommandRequest request) {
		if (request instanceof CreateElementRequest) {
			return approveCreateElementRequest((CreateElementRequest) request);
		} else if (request instanceof SetRequest) {
			return approveSetRequest((SetRequest) request);
		} else if (request instanceof MoveRequest) {
			return approveMoveRequest((MoveRequest) request);
		} else {
			return super.approveRequest(request);
		}
	}

	/**
	 * Deny creation of a member end if the association already has two ends.
	 */
	protected boolean approveCreateElementRequest(CreateElementRequest request) {
		boolean result = true;

		EObject container = request.getContainer();
		if (container instanceof Association && ((Association) container).getMemberEnds().size() >= 2) {
			IElementType typeToCreate = request.getElementType();
			result = (typeToCreate == null) || !UMLPackage.Literals.PROPERTY.isSuperTypeOf(typeToCreate.getEClass());
		}

		return result;
	}

	/**
	 * Deny setting the member ends of an association to some number of ends other than two.
	 */
	protected boolean approveSetRequest(SetRequest request) {
		boolean result = true;

		if (request.getFeature() == UMLPackage.Literals.ASSOCIATION__MEMBER_END
				|| request.getFeature() == UMLPackage.Literals.ASSOCIATION__OWNED_END) {

			Association association = (Association) request.getElementToEdit();

			result = !isBreakingMultiplicity(association, request.getFeature(), 2, 2, request.getValue());
		}

		return result;
	}

	/**
	 * Deny moving a property into the member ends of an association that already has two ends.
	 */
	protected boolean approveMoveRequest(MoveRequest request) {
		boolean result = true;

		EObject container = request.getTargetContainer();
		if (container instanceof Association) {
			Association association = (Association) container;
			int endCount = association.getMemberEnds().size();

			@SuppressWarnings("unchecked")
			Map<EObject, EReference> moves = request.getElementsToMove();
			for (Map.Entry<EObject, EReference> next : moves.entrySet()) {
				if (next.getKey() instanceof Property) {
					endCount = endCount + 1;
					result = endCount <= 2;

					if (!result) {
						break;
					}
				}
			}
		}

		return result;
	}

	/**
	 * Query whether a {@code value} to be added to or set into a {@code feature} of an {@code owner} would break
	 * our multiplicity constraint for the {@code feature}.
	 *
	 * @param owner
	 *            the object that owns the {@code feature} being edited
	 * @param feature
	 *            the feature of the {@code owner} that is being edited
	 * @param minSize
	 *            the minimal size that the {@code feature} must have
	 * @param maxSize
	 *            the maximal size that the {@code feature} may have
	 * @param value
	 *            the value to be added (if not a collection) or set (if a collection), according to the contract of the
	 *            {@link SetRequest} API
	 * @return whether this {@code value} would break the multiplicity constraint on the {@code feature}
	 */
	protected boolean isBreakingMultiplicity(EObject owner, EStructuralFeature feature, int minSize, int maxSize, Object value) {
		Object currentValue = owner.eGet(feature);
		int existingSize = feature.isMany() ? ((Collection<?>) currentValue).size() : currentValue == null ? 0 : 1;

		// If it's not a collection, then it is a value to be added
		boolean result = !(value instanceof Collection<?>) && existingSize >= maxSize;

		if (!result) {
			// Is the collection to replace the existing collection a collection of invalid size?
			int newSize = ((Collection<?>) value).size();

			result = newSize < minSize || newSize > maxSize;
		}

		return result;
	}

	@Override
	protected ICommand getBeforeDestroyDependentsCommand(DestroyDependentsRequest request) {
		ICommand result = null;

		if (request.getElementToDestroy() instanceof Association) {
			// The association advice following this will attempt to destroy the member ends, so block that
			Association association = (Association) request.getElementToDestroy();
			protectMemberEnds(association, request);

			// But as these ends will no longer be in an association, they should not be subsets
			for (Property end : association.getMemberEnds()) {
				if (end.getOwningAssociation() != association && end.eIsSet(UMLPackage.Literals.PROPERTY__SUBSETTED_PROPERTY)) {
					result = CompositeCommand.compose(result, getUnsetCommand(request, end, UMLPackage.Literals.PROPERTY__SUBSETTED_PROPERTY));
				}
			}
		}

		return CompositeCommand.compose(result, super.getBeforeDestroyDependentsCommand(request));
	}

	/**
	 * Configure the <em>dependents to keep</em> parameter of the {@code request} to preserve the
	 * member ends of the {@code association} that it does not own.
	 *
	 * @param association
	 *            an association being destroyed
	 * @param request
	 *            the gathering dependencies of the {@code association} that also should be destroyed
	 */
	protected void protectMemberEnds(Association association, DestroyDependentsRequest request) {
		List<EObject> dependentsToKeep = RequestParameterUtils.getDependentsToKeep(request, true);

		for (Property end : association.getMemberEnds()) {
			if (end.getOwningAssociation() != association) {
				dependentsToKeep.add(end);
			}
		}
	}

	/**
	 * Create a command to unset the given {@code feature} of an {@code owner} of that feature.
	 *
	 * @param parentRequest
	 *            the request in which edit context we are fetching a corollary command
	 * @param owner
	 *            the object that owns the {@code feature} to unset
	 * @param feature
	 *            the feature to unset in the {@code owner}
	 * @return the unset command, or {@code null} if no relevant command can be obtained
	 */
	protected ICommand getUnsetCommand(IEditCommandRequest parentRequest, EObject owner, EStructuralFeature feature) {
		ICommand result = null;

		IElementEditService edit = ElementEditServiceUtils.getCommandProvider(owner, parentRequest.getClientContext());
		if (edit != null) {
			result = edit.getEditCommand(new UnsetRequest(parentRequest.getEditingDomain(), owner, feature));
		}

		return result;
	}

}
