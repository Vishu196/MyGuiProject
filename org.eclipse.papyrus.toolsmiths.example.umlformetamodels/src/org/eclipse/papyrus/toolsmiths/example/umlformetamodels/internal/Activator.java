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

package org.eclipse.papyrus.toolsmiths.example.umlformetamodels.internal;

import org.eclipse.core.runtime.Plugin;
import org.eclipse.papyrus.infra.core.log.LogHelper;
import org.osgi.framework.BundleContext;

/**
 * OSGi bundle activator.
 */
public class Activator extends Plugin {

	/** The bundle symbolic name (plug-in ID). */
	public static final String PLUGIN_ID = "org.eclipse.papyrus.toolsmiths.example.umlformetamodels"; //$NON-NLS-1$

	/** The activator instance. */
	private static Activator plugin;

	/** Logging helper */
	public static LogHelper log;

	public Activator() {
		super();
	}

	@Override
	public void start(BundleContext context) throws Exception {
		super.start(context);

		plugin = this;
		log = new LogHelper(this);
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		log = null;
		plugin = null;

		super.stop(context);
	}

	/**
	 * Obtain the activator instance.
	 *
	 * @return the activator instance
	 */
	public static Activator getDefault() {
		return plugin;
	}

}
