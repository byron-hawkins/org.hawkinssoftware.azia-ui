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
package org.hawkinssoftware.azia.ui.tile;

import org.hawkinssoftware.azia.core.action.UserInterfaceNotification;
import org.hawkinssoftware.azia.core.layout.BoundedEntity;
import org.hawkinssoftware.azia.core.role.UserInterfaceDomains.DisplayBoundsDomain;
import org.hawkinssoftware.azia.core.role.UserInterfaceDomains.RenderingDomain;
import org.hawkinssoftware.azia.ui.tile.LayoutRegion.TileLayoutDomain;
import org.hawkinssoftware.rns.core.publication.InvocationConstraint;
import org.hawkinssoftware.rns.core.role.DomainRole;

// TODO: does it really make sense to have both BoundedEntity.LayoutContainerDomain and DisplayBoundsDomain?

/**
 * DOC comment task awaits.
 * 
 * @param <KeyType>
 *            the generic type
 * @author Byron Hawkins
 */
@InvocationConstraint(domains = TileLayoutDomain.class)
@DomainRole.Join(membership = { BoundedEntity.LayoutContainerDomain.class, DisplayBoundsDomain.class, RenderingDomain.class })
public interface LayoutEntity<KeyType extends LayoutEntity.Key<KeyType>> extends LayoutRegion
{
	
	/**
	 * DOC comment task awaits.
	 * 
	 * @author Byron Hawkins
	 */
	@DomainRole.Join(membership = TileLayoutDomain.class)
	public static class ChangeNotification extends UserInterfaceNotification
	{
		public final boolean isNew;
		public final LayoutEntity<?> modifiedEntity;

		public ChangeNotification(boolean isNew, LayoutEntity<?> modifiedEntity)
		{
			this.isNew = isNew;
			this.modifiedEntity = modifiedEntity;
		}
	}

	/**
	 * DOC comment task awaits.
	 * 
	 * @param <KeyType>
	 *            the generic type
	 * @author Byron Hawkins
	 */
	@InvocationConstraint(domains = TileLayoutDomain.class)
	@DomainRole.Join(membership = TileLayoutDomain.class)
	public interface Key<KeyType extends Key<KeyType>>
	{
		String getName();
	}

	KeyType getKey();
	
	boolean isConfigured();
}
