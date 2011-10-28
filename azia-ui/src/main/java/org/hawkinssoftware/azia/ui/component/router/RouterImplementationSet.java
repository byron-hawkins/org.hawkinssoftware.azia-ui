package org.hawkinssoftware.azia.ui.component.router;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.hawkinssoftware.rns.core.util.UnknownEnumConstantException;

class RouterImplementationSet
{
	private final List<GeneratedRouter<ComponentDirectiveRouter>> actionRouters = new ArrayList<GeneratedRouter<ComponentDirectiveRouter>>();
	private final List<GeneratedRouter<ComponentNotificationRouter>> notificationRouters = new ArrayList<GeneratedRouter<ComponentNotificationRouter>>();

	@SuppressWarnings("unchecked")
	public void add(GeneratedRouter<?> router)
	{
		switch (router.getDefinition().getRouterType())
		{
			case DIRECTIVE:
				actionRouters.add((GeneratedRouter<ComponentDirectiveRouter>) router);
				break;
			case NOTIFICATION:
				notificationRouters.add((GeneratedRouter<ComponentNotificationRouter>) router);
				break;
			default:
				throw new UnknownEnumConstantException(router.getDefinition().getRouterType());
		}
	}

	Collection<GeneratedRouter<ComponentDirectiveRouter>> getActionRouters()
	{
		return actionRouters;
	}

	Collection<GeneratedRouter<ComponentNotificationRouter>> getNoteRouters()
	{
		return notificationRouters;
	}
}
