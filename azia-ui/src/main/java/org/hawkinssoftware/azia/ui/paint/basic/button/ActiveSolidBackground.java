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
package org.hawkinssoftware.azia.ui.paint.basic.button;

import java.awt.Color;

import org.hawkinssoftware.azia.ui.component.VirtualComponent;
import org.hawkinssoftware.azia.ui.component.transaction.mouse.MouseOverState;
import org.hawkinssoftware.azia.ui.component.transaction.mouse.MousePressedState;
import org.hawkinssoftware.azia.ui.paint.canvas.Canvas;
import org.hawkinssoftware.azia.ui.paint.plugin.BackgroundPlugin;

/**
 * DOC comment task awaits.
 * 
 * @param <ComponentType>
 *            the generic type
 * @author Byron Hawkins
 */
public class ActiveSolidBackground<ComponentType extends VirtualComponent> extends BackgroundPlugin<ComponentType>
{
	private static final Color PLAIN_COLOR = PushButtonPainter.PLAIN_BACKGROUND_COLOR;
	private static final Color MOUSE_OVER_COLOR = new Color(0xC0C0C0);
	private static final Color CLICKED_COLOR = new Color(0xAAAAAA);

	private Color plainColor = PLAIN_COLOR;
	private Color mouseOverColor = MOUSE_OVER_COLOR;
	private Color clickedColor = CLICKED_COLOR;

	public void setPlainColor(Color plainColor)
	{
		this.plainColor = plainColor;
	}

	public void setMouseOverColor(Color mouseOverColor)
	{
		this.mouseOverColor = mouseOverColor;
	}

	public void setClickedColor(Color clickedColor)
	{
		this.clickedColor = clickedColor;
	}

	@Override
	protected void paint(Canvas c, ComponentType component)
	{
		if (component.getDataHandler(MousePressedState.KEY).isPressed())
		{
			c.pushColor(clickedColor);
		}
		else if (component.getDataHandler(MouseOverState.KEY).isMouseOver())
		{
			c.pushColor(mouseOverColor);
		}
		else
		{
			c.pushColor(plainColor);
		}

		c.g.fillRect(0, 0, c.span().width, c.span().height);
	}
}
