package org.hawkinssoftware.azia.ui.component.cell;

import org.hawkinssoftware.azia.ui.component.PaintableActor;
import org.hawkinssoftware.azia.ui.component.scalar.ScrollPaneViewportComposite;
import org.hawkinssoftware.azia.ui.model.RowAddress;
import org.hawkinssoftware.azia.ui.model.RowAddress.Section;
import org.hawkinssoftware.azia.ui.model.list.ListDataModel;
import org.hawkinssoftware.azia.ui.paint.basic.cell.AbstractCellContentPainter;
import org.hawkinssoftware.azia.ui.paint.basic.cell.CellViewportPainter;

public class CellViewportComposite<CellPainterType extends AbstractCellContentPainter> extends ScrollPaneViewportComposite<CellViewport, CellViewportPainter>
		implements ListDataModel.ComponentContext
{
	private CellPainterType cellPainter;

	public CellViewportComposite(CellViewport component)
	{
		super(component);
	}
	
	public boolean hasFocus()
	{
		return false;
	}

	public CellPainterType getCellPainter()
	{
		return cellPainter;
	}

	public void setCellPainter(CellPainterType cellPainter)
	{
		this.cellPainter = cellPainter;
	}

	@Override
	public RowAddress createAddress(int row, Section section)
	{
		return new RowAddress(getComponent(), row, section);
	}

	@Override
	public PaintableActor getActor()
	{
		return getComponent();
	}
}
