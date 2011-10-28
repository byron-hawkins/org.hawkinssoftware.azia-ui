package org.hawkinssoftware.azia.ui.paint.basic.cell;

import java.awt.Color;
import java.util.Collection;

import org.hawkinssoftware.azia.core.layout.Axis;
import org.hawkinssoftware.azia.ui.component.cell.CellViewport;
import org.hawkinssoftware.azia.ui.component.cell.CellViewportComposite;
import org.hawkinssoftware.azia.ui.component.composition.CompositionRegistry;
import org.hawkinssoftware.azia.ui.component.scalar.ScrollPaneViewport;
import org.hawkinssoftware.azia.ui.paint.AggregatePainter;
import org.hawkinssoftware.azia.ui.paint.basic.scalar.ScrollPaneViewportPainter;
import org.hawkinssoftware.azia.ui.paint.canvas.Canvas;
import org.hawkinssoftware.azia.ui.paint.plugin.BorderPlugins;

public class CellViewportPainter extends ScrollPaneViewportPainter<CellViewport> implements AggregatePainter<CellViewport>, ScrollPaneViewport.Painter
{
	private CellViewportComposite<?> viewport;
	public BorderPlugins<CellViewport> borderPlugins = new BorderPlugins<CellViewport>();

	@Override
	public void compositionCompleted()
	{
		super.compositionCompleted();

		viewport = CompositionRegistry.getComposite(CellViewportComposite.class);
	}

	@Override
	public int getScrollableContentSize(Axis axis)
	{
		return viewport.getCellPainter().getScrollableContentSize(axis);
	}

	@Override
	public int getStaticContentSpan(Axis axis)
	{
		return viewport.getCellPainter().getStaticContentSpan(axis);
	}

	@Override
	public int getPackedSize(Axis axis)
	{
		return 50;
	}

	@Override
	public void paint(CellViewport viewport)
	{
		Canvas c = Canvas.get();

		c.pushColor(Color.white);
		c.g.fillRect(0, 0, this.viewport.getBounds().width, c.size().height);

		borderPlugins.paintAndNarrow(c, viewport);

		this.viewport.getCellPainter().paint();
	}

	@Override
	public void paint(CellViewport viewport, Collection<AggregatePainter.Atom> atoms)
	{
		Canvas c = Canvas.get();
		
		borderPlugins.narrow(c);

		for (AggregatePainter.Atom cell : atoms)
		{
			cell.paint();
		}
	}
}
