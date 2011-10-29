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
package org.hawkinssoftware.azia.ui.paint.basic.text;

import java.awt.Color;

import org.hawkinssoftware.azia.core.layout.Axis;
import org.hawkinssoftware.azia.ui.component.composition.AbstractComposite;
import org.hawkinssoftware.azia.ui.component.composition.CompositionRegistry;
import org.hawkinssoftware.azia.ui.component.text.TextArea;
import org.hawkinssoftware.azia.ui.paint.ComponentPainter;
import org.hawkinssoftware.azia.ui.paint.canvas.Canvas;
import org.hawkinssoftware.rns.core.util.UnknownEnumConstantException;
import org.hawkinssoftware.rns.core.validation.ValidateRead;
import org.hawkinssoftware.rns.core.validation.ValidateWrite;

/**
 * DOC comment task awaits.
 * 
 * @param <LabelType>
 *            the generic type
 * @author Byron Hawkins
 */
@ValidateRead
@ValidateWrite
public class TextAreaPainter<LabelType extends TextArea> extends ComponentPainter<LabelType> implements TextArea.Painter
{
	protected static final Color BACKGROUND = Color.white;
	protected static final Color FOREGROUND = Color.black;

	protected TextBlockPainter textPainter;

	@Override
	public void compositionCompleted()
	{
		super.compositionCompleted();

		this.textPainter = new TextBlockPainter(CompositionRegistry.getComposite(AbstractComposite.class));
	}

	@Override
	public int getPackedSize(Axis axis)
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
	public void paint(TextArea label)
	{
		Canvas c = Canvas.get();

		c.pushColor(BACKGROUND);
		c.g.fillRect(0, 0, c.span().width, c.span().height);

		int xText = ((c.size().width - textPainter.getTextBounds().width) / 2);
		int yText = TextMetrics.INSTANCE.getFontAscent() + ((c.size().height - textPainter.getTextBounds().height) / 2);
		c.pushBoundsPosition(xText, yText);
		textPainter.paint(c, FOREGROUND);
	}
}
