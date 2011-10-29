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
package org.hawkinssoftware.azia.ui.tile.transaction.modify;

import org.hawkinssoftware.azia.core.action.UserInterfaceDirective;
import org.hawkinssoftware.azia.core.action.UserInterfaceNotification;
import org.hawkinssoftware.azia.ui.tile.LayoutEntity;
import org.hawkinssoftware.azia.ui.tile.TopTile;

/**
 * DOC comment task awaits.
 * 
 * @param <KeyType>
 *            the generic type
 * @author Byron Hawkins
 */
public class InstallNewLayoutDirective<KeyType extends LayoutEntity.Key<KeyType>> extends UserInterfaceDirective
{
	
	/**
	 * DOC comment task awaits.
	 * 
	 * @author Byron Hawkins
	 */
	public class Notification extends UserInterfaceNotification
	{
		public TopTile<?> getModification()
		{
			return modified;
		}
	}

	public final TopTile<KeyType> modified;

	InstallNewLayoutDirective(TopTile<KeyType> original, TopTile<KeyType> modified)
	{
		super(original);

		this.modified = modified;
	}
	
	@Override
	public UserInterfaceNotification createNotification()
	{
		return new Notification();
	}
}
