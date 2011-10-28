package org.hawkinssoftware.azia.ui.component.router;

import org.hawkinssoftware.azia.core.action.UserInterfaceDirective;
import org.hawkinssoftware.azia.ui.component.UserInterfaceHandler;
import org.hawkinssoftware.rns.core.publication.ExtensionConstraint;

@ExtensionConstraint(packages = ExtensionConstraint.MY_PACKAGE)
public interface ComponentDirectiveRouter extends AbstractComponentRouter
{
	void route(UserInterfaceDirective directive, UserInterfaceHandler handler);
}
