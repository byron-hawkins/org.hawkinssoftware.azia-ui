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
package org.hawkinssoftware.azia.ui.paint.transaction.paint;

import org.hawkinssoftware.azia.core.action.UserInterfaceNotification;
import org.hawkinssoftware.azia.core.layout.BoundedEntity;
import org.hawkinssoftware.azia.core.layout.BoundedEntity.PanelRegion;
import org.hawkinssoftware.azia.ui.component.EnclosureBounds;

/**
 * DOC comment task awaits.
 * 
 * @author Byron Hawkins
 */
public class PaintRegionNotification extends UserInterfaceNotification
{
	final BoundedEntity.PanelRegion region;
	final EnclosureBounds bounds;

	public PaintRegionNotification(PanelRegion region, EnclosureBounds bounds)
	{
		this.region = region;
		this.bounds = bounds;
	}
}
