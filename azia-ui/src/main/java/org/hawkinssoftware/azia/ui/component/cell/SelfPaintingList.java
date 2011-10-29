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
package org.hawkinssoftware.azia.ui.component.cell;

import org.hawkinssoftware.azia.ui.component.scalar.ScrollPane;
import org.hawkinssoftware.azia.ui.component.scalar.ScrollPaneComposite;

/**
 * DOC comment task awaits.
 * 
 * @param <ViewportType>
 *            the generic type
 * @author Byron Hawkins
 */
public class SelfPaintingList<ViewportType extends CellViewport> extends ScrollPaneComposite<SelfPaintingListViewport>
{
	public SelfPaintingList(ScrollPane component)
	{
		super(component);
	}
}
