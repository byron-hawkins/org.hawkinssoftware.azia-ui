package org.hawkinssoftware.azia.ui.paint.basic.scalar;


import org.hawkinssoftware.azia.core.layout.Axis;
import org.hawkinssoftware.azia.ui.component.scalar.ScrollPane;
import org.hawkinssoftware.azia.ui.paint.ComponentPainter;
import org.hawkinssoftware.rns.core.validation.ValidateRead;
import org.hawkinssoftware.rns.core.validation.ValidateWrite;

@ValidateRead
@ValidateWrite
public class ScrollPanePainter extends ComponentPainter<ScrollPane> implements ScrollPane.Painter
{
	@Override
	public int getPackedSize(Axis axis)
	{
		return 0;
	}

	@Override
	public void paint(ScrollPane component)
	{
	}
}
