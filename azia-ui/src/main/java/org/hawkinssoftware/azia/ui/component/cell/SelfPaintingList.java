package org.hawkinssoftware.azia.ui.component.cell;

import org.hawkinssoftware.azia.ui.component.scalar.ScrollPane;
import org.hawkinssoftware.azia.ui.component.scalar.ScrollPaneComposite;

public class SelfPaintingList<ViewportType extends CellViewport> extends ScrollPaneComposite<SelfPaintingListViewport>
{
	public SelfPaintingList(ScrollPane component)
	{
		super(component);
	}
}
