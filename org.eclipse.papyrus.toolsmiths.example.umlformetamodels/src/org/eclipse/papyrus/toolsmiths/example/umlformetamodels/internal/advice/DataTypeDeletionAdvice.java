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
import java.util.stream.Collectors;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.gmf.runtime.common.core.command.AbstractCommand;
import org.eclipse.gmf.runtime.common.core.command.CommandResult;
import org.eclipse.gmf.runtime.common.core.command.CompositeCommand;
import org.eclipse.gmf.runtime.common.core.command.ICommand;
import org.eclipse.gmf.runtime.emf.type.core.edithelper.AbstractEditHelperAdvice;
import org.eclipse.gmf.runtime.emf.type.core.requests.DestroyDependentsRequest;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.uml2.common.util.CacheAdapter;
import org.eclipse.uml2.uml.DataType;
import org.eclipse.uml2.uml.NamedElement;
import org.eclipse.uml2.uml.TypedElement;
import org.eclipse.uml2.uml.UMLPackage;

/**
 * Advice for the deletion of datatypes to delete also attributes of that type (pursuant to user confirmation).
 */
public class DataTypeDeletionAdvice extends AbstractEditHelperAdvice {

	/**
	 * When destroying a {@link DataType}, destroy also any properties or operation parameters
	 * typed by it, with interactive user confirmation.
	 */
	@Override
	protected ICommand getBeforeDestroyDependentsCommand(DestroyDependentsRequest request) {
		ICommand result = null;

		// This covers the enumerations and primitive types that we can use in metamodels
		if (request.getElementToDestroy() instanceof DataType) {
			// Find attributes typed by this data type
			DataType dataType = (DataType) request.getElementToDestroy();

			// The UML Cache Adapter maintains an inverse reference map. We need to find
			// objects that reference our DataType; the DataType does not reference them
			CacheAdapter cache = CacheAdapter.getCacheAdapter(dataType);
			if (cache != null) {
				Collection<EStructuralFeature.Setting> settings = cache.getInverseReferences(dataType, UMLPackage.Literals.TYPED_ELEMENT__TYPE, true);
				if (!settings.isEmpty()) {
					result = deleteOwners(settings, request);
				}
			}
		}

		return CompositeCommand.compose(result, super.getBeforeDestroyDependentsCommand(request));
	}

	/**
	 * Obtain a command to delete the owners of reference {@code settings}.
	 *
	 * @param settings
	 *            settings of references to the object being deleted
	 * @param request
	 *            the request gathering dependents of the the object to delete that also should be deleted
	 * @return a command to delete the objects that own the reference {@code settings}
	 */
	private ICommand deleteOwners(Collection<EStructuralFeature.Setting> settings, DestroyDependentsRequest request) {
		Collection<TypedElement> typedElements = settings.stream().map(EStructuralFeature.Setting::getEObject)
				.map(TypedElement.class::cast).distinct().collect(Collectors.toList());

		String listOfNames = typedElements.stream().map(NamedElement::getQualifiedName).collect(Collectors.joining(", "));
		String prompt = NLS.bind("The following elements will also be deleted. Proceed?\n{0}.", listOfNames);

		// If this returns a cancel result, then the framework will undo any changes performed by earlier commands
		ICommand promptToConfirm = new PromptToConfirmCommand("Delete Typed Elements", prompt);
		ICommand destroyTypedElements = request.getDestroyDependentsCommand(typedElements);

		return CompositeCommand.compose(promptToConfirm, destroyTypedElements);
	}

	//
	// Nested types
	//

	/**
	 * A command that, when executed, shows the user a prompt message and asks for confirmation to proceed.
	 * If the user elects not to proceed, then command execution is cancelled.
	 */
	private static final class PromptToConfirmCommand extends AbstractCommand {
		private final String dialogTitle;
		private final String prompt;

		PromptToConfirmCommand(String dialogTitle, String prompt) {
			super("Prompt to Confirm");

			this.dialogTitle = dialogTitle;
			this.prompt = prompt;
		}

		@Override
		public boolean canExecute() {
			return true;
		}

		@Override
		protected CommandResult doExecuteWithResult(IProgressMonitor progressMonitor, IAdaptable info) throws ExecutionException {
			Shell parentShell = (info == null) ? Display.getCurrent().getActiveShell() : info.getAdapter(Shell.class);
			return MessageDialog.openConfirm(parentShell, dialogTitle, prompt)
					? CommandResult.newOKCommandResult()
					: CommandResult.newCancelledCommandResult(); // The framework will undo any changes performed by earlier commands
		}

		@Override
		protected CommandResult doRedoWithResult(IProgressMonitor progressMonitor, IAdaptable info) throws ExecutionException {
			// Nothing to confirm on undo
			return CommandResult.newOKCommandResult();
		}

		@Override
		protected CommandResult doUndoWithResult(IProgressMonitor progressMonitor, IAdaptable info) throws ExecutionException {
			// Nothing to confirm on redo
			return CommandResult.newOKCommandResult();
		}

	}

}
