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
package org.hawkinssoftware.azia.ui.paint;

import org.hawkinssoftware.azia.core.layout.BoundedEntity;

/**
 * DOC comment task awaits.
 * 
 * @param <RegionType>
 *            the generic type
 * @author Byron Hawkins
 */
public interface RegionPainter<RegionType extends BoundedEntity.PanelRegion>
{
	RegionType getRegion();
	
	void setRegion(RegionType region);
	
	void paint(RegionType region);
}
