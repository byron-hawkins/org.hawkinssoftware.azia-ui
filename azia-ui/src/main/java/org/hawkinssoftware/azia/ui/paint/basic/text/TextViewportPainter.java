package org.hawkinssoftware.azia.ui.paint.basic.text;

import java.awt.Color;

import org.hawkinssoftware.azia.core.layout.Axis;
import org.hawkinssoftware.azia.ui.component.composition.CompositionRegistry;
import org.hawkinssoftware.azia.ui.component.scalar.ScrollPaneViewportComposite;
import org.hawkinssoftware.azia.ui.component.text.TextViewport;
import org.hawkinssoftware.azia.ui.component.transaction.mouse.MouseOverState;
import org.hawkinssoftware.azia.ui.paint.basic.scalar.ScrollPaneViewportPainter;
import org.hawkinssoftware.azia.ui.paint.canvas.Canvas;
import org.hawkinssoftware.rns.core.util.UnknownEnumConstantException;
import org.hawkinssoftware.rns.core.validation.ValidateRead;
import org.hawkinssoftware.rns.core.validation.ValidateWrite;

@ValidateRead
@ValidateWrite
public class TextViewportPainter extends ScrollPaneViewportPainter<TextViewport>
{
	private static final Color NORMAL_BACKGROUND = Color.black;
	private static final Color MOUSEOVER_BACKGROUND = new Color(0x009900);

	private static final Color FOREGROUND = Color.white;

	private TextBlockPainter textPainter;

	@Override
	public void compositionCompleted()
	{
		super.compositionCompleted();

		ScrollPaneViewportComposite<?, ?> viewport = CompositionRegistry.getComposite(ScrollPaneViewportComposite.class);
		textPainter = new TextBlockPainter(viewport);
	}

	@Override
	public void paint(TextViewport component)
	{
		Canvas c = Canvas.get();
		
		if (component.getDataHandler(MouseOverState.KEY).isMouseOver())
		{
			c.pushColor(MOUSEOVER_BACKGROUND);
		}
		else
		{
			c.pushColor(NORMAL_BACKGROUND);
		}
		c.g.fillRect(0, 0, c.span().width, c.span().height);

		c.pushBoundsPosition(-component.xViewport(), -component.yViewport());
		textPainter.paint(c, FOREGROUND);
		c.popBounds();
	}

	@Override
	public int getPackedSize(Axis axis)
	{
		// arbitrary for a viewport, because scrollability makes arbitrary reduction possible
		return 50;
	}

	@Override
	public int getScrollableContentSize(Axis axis)
	{
		switch (axis)
		{
			case H:
				return textPainter.getTextBounds().width;
			case V:
				return textPainter.getTextBounds().height;
			default:
				throw new UnknownEnumConstantException(axis);
		}
	}

	@Override
	public int getStaticContentSpan(Axis axis)
	{
		return 0;
	}
}
