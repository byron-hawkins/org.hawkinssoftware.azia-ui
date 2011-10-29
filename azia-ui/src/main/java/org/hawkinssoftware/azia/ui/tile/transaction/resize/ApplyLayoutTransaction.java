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
package org.hawkinssoftware.azia.ui.tile.transaction.resize;

import java.util.ArrayList;
import java.util.List;

import org.hawkinssoftware.azia.ui.component.EnclosureBounds;
import org.hawkinssoftware.azia.ui.tile.LayoutRegion;

/**
 * DOC comment task awaits.
 * 
 * @author Byron Hawkins
 */
public class ApplyLayoutTransaction extends AbstractApplyLayoutTransaction
{
	private final List<TileBoundsChangeDirective> initialCommands = new ArrayList<TileBoundsChangeDirective>();

	public void addRegion(LayoutRegion region)
	{
		initialCommands.add(new TileBoundsChangeDirective(region));
	}

	public void addRegion(LayoutRegion region, EnclosureBounds bounds)
	{
		initialCommands.add(new TileBoundsChangeDirective(region, bounds));
	}

	public void beginAssembly()
	{
		transaction.addAll(initialCommands);
		for (TileBoundsChangeDirective initialCommand : initialCommands)
		{
			session.postAction(initialCommand);
		}
		initialCommands.clear();
	}
}