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
package org.hawkinssoftware.azia.ui.component.router;

import org.hawkinssoftware.azia.core.action.UserInterfaceNotification;
import org.hawkinssoftware.azia.core.action.UserInterfaceTransaction.ActorBasedContributor.PendingTransaction;
import org.hawkinssoftware.azia.ui.component.UserInterfaceHandler;
import org.hawkinssoftware.rns.core.publication.ExtensionConstraint;

/**
 * Trivial interface for a <code>UserInterfaceNotification</code> router; implemented only by the
 * <code>RouterImplementationFactory</code>.
 * 
 * @author Byron Hawkins
 */
@ExtensionConstraint(packages = ExtensionConstraint.MY_PACKAGE)
public interface ComponentNotificationRouter extends AbstractComponentRouter
{
	void route(UserInterfaceNotification notification, UserInterfaceHandler handler, PendingTransaction transaction);
}
