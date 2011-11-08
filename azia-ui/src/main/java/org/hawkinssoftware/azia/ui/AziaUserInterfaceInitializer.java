/*
 * Copyright (c) 2011 HawkinsSoftware
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Byron Hawkins of HawkinsSoftware
 */
package org.hawkinssoftware.azia.ui;

import org.hawkinssoftware.azia.core.action.TransactionRegistry;
import org.hawkinssoftware.azia.core.lock.LockRegistry;
import org.hawkinssoftware.azia.core.log.AziaLogging.Tag;
import org.hawkinssoftware.azia.input.clipboard.ClipboardMonitor;
import org.hawkinssoftware.azia.input.key.HardwareKey;
import org.hawkinssoftware.azia.ui.component.router.CompositeRouter;
import org.hawkinssoftware.azia.ui.component.transaction.clipboard.ClipboardEventDispatch;
import org.hawkinssoftware.azia.ui.component.transaction.key.KeyEventDispatch;
import org.hawkinssoftware.azia.ui.component.transaction.resize.ComponentResizeTransaction;
import org.hawkinssoftware.azia.ui.input.InputDispatch;
import org.hawkinssoftware.azia.ui.paint.transaction.repaint.RepaintRequestManager;
import org.hawkinssoftware.azia.ui.paint.transaction.resize.PainterResizeTransaction;
import org.hawkinssoftware.azia.ui.tile.transaction.resize.ApplyLayoutSubTransaction;
import org.hawkinssoftware.rns.core.log.Log;
import org.hawkinssoftware.rns.core.moa.DomainObserver;
import org.hawkinssoftware.rns.core.role.CoreDomains.InitializationDomain;
import org.hawkinssoftware.rns.core.role.DomainRole;
import org.hawkinssoftware.rns.core.role.DomainSpecificationRegistry;
import org.hawkinssoftware.rns.core.role.DomainSpecificationRegistry.CollaborationEvaluation;
import org.hawkinssoftware.rns.core.role.DomainSpecifications;
import org.hawkinssoftware.rns.core.util.RNSUtils;

/**
 * DOC comment task awaits.
 * 
 * @author Byron Hawkins
 */
@DomainRole.Join(membership = InitializationDomain.class)
public class AziaUserInterfaceInitializer
{
	private static final String DOMAIN_SPECIFICATION_PATH = String.format("%s/azia-ui%s", RNSUtils.RNS_RESOURCE_FOLDER_NAME,
			DomainSpecifications.SPECIFICATION_FILENAME_SUFFIX);

	public static void initialize()
	{
		Log.addOutput(System.out);

		try
		{
			// InputStream in =
			// Thread.currentThread().getContextClassLoader().getResourceAsStream(DOMAIN_SPECIFICATION_PATH);
			// DomainSpecificationRegistry.getInstance().register(in);
			// ExecutionPath.Universe.getInstance().addObserver(new DomainObserver.Factory(new
			// OrthogonalityViolationReporter()));

			// ExecutionPath.Universe.getInstance().addObserver(new CollaborationObserver.Factory());
		}
		catch (Exception e)
		{
			Log.out(Tag.WARNING, e, "Failed to install the %s.", DomainObserver.class.getName());
		}

		TransactionRegistry.getInstance().toString();
		PainterResizeTransaction.TransactionRegistryListener.INSTANCE.toString();
		ComponentResizeTransaction.TransactionRegistryListener.INSTANCE.toString();
		ApplyLayoutSubTransaction.TransactionRegistryListener.INSTANCE.toString();
		RepaintRequestManager.initialize();

		HardwareKey.initialize();
		InputDispatch.start();
		CompositeRouter.initialize();
		LockRegistry.initialize();
		ClipboardMonitor.initialize();
		ClipboardEventDispatch.initialize();
		KeyEventDispatch.initialize();
	}

	/**
	 * DOC comment task awaits.
	 * 
	 * @author Byron Hawkins
	 */
	private static class OrthogonalityViolationReporter implements DomainObserver.Listener
	{
		@Override
		public void orthogonalityViolation(CollaborationEvaluation evaluation)
		{
			Log.out(Tag.DEBUG_CONTAIN, "Orthogonality violation: %d conflicts", evaluation.conflicts.size());
			for (DomainSpecificationRegistry.CollaborationEvaluation.Conflict conflict : evaluation.conflicts)
			{
				Log.out(Tag.DEBUG_CONTAIN, "    Domain %s may not collaborate with domain %s.", conflict.first.getClass().getSimpleName(), conflict.second
						.getClass().getSimpleName());
			}

			"stop-for-debug".toString();
		}
	}
}
