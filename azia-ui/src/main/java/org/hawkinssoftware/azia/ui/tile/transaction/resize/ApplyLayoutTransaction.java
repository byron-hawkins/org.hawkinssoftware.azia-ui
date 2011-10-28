package org.hawkinssoftware.azia.ui.tile.transaction.resize;

import java.util.ArrayList;
import java.util.List;

import org.hawkinssoftware.azia.ui.component.EnclosureBounds;
import org.hawkinssoftware.azia.ui.tile.LayoutRegion;

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