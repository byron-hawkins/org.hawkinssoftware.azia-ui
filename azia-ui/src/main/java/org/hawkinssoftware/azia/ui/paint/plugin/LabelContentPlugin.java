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
package org.hawkinssoftware.azia.ui.paint.plugin;

import org.hawkinssoftware.azia.core.action.UserInterfaceActor;
import org.hawkinssoftware.azia.core.action.UserInterfaceActorDelegate;
import org.hawkinssoftware.azia.core.layout.Axis;
import org.hawkinssoftware.azia.core.role.UserInterfaceDomains.DisplayBoundsDomain;
import org.hawkinssoftware.azia.core.role.UserInterfaceDomains.RenderingDomain;
import org.hawkinssoftware.azia.ui.component.UserInterfaceHandler;
import org.hawkinssoftware.azia.ui.component.VirtualComponent;
import org.hawkinssoftware.azia.ui.component.composition.CompositionRegistry;
import org.hawkinssoftware.rns.core.role.DomainRole;
import org.hawkinssoftware.rns.core.validation.ValidateRead;
import org.hawkinssoftware.rns.core.validation.ValidateWrite;

/**
 * DOC comment task awaits.
 * 
 * @author Byron Hawkins
 */
@ValidateRead
@ValidateWrite
@DomainRole.Join(membership = { DisplayBoundsDomain.class, RenderingDomain.class })
public abstract class LabelContentPlugin implements UserInterfaceHandler, UserInterfaceActorDelegate
{
	private final UserInterfaceActorDelegate actor;

	protected LabelContentPlugin()
	{
		actor = CompositionRegistry.getService(UserInterfaceActorDelegate.class);
		
		// WIP: risk of mis-registration
		CompositionRegistry.getService(UserInterfaceHandler.Host.class).installHandler(this);
	}

	public abstract void paint(VirtualComponent component);

	public abstract int getPackedSize(Axis axis);

	@Override
	public UserInterfaceActor getActor()
	{
		return actor.getActor();
	}
}
