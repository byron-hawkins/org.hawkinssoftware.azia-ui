package org.hawkinssoftware.azia.ui.component.router;

import org.hawkinssoftware.azia.core.action.UserInterfaceNotification;
import org.hawkinssoftware.azia.core.action.UserInterfaceTransaction.ActorBasedContributor.PendingTransaction;
import org.hawkinssoftware.azia.ui.component.UserInterfaceHandler;
import org.hawkinssoftware.rns.core.publication.ExtensionConstraint;

@ExtensionConstraint(packages = ExtensionConstraint.MY_PACKAGE)
public interface ComponentNotificationRouter extends AbstractComponentRouter
{
	void route(UserInterfaceNotification notification, UserInterfaceHandler handler, PendingTransaction transaction);
}
