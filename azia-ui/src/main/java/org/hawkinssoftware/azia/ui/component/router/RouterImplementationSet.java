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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.hawkinssoftware.rns.core.util.UnknownEnumConstantException;

/**
 * DOC comment task awaits.
 * 
 * @author Byron Hawkins
 */
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
