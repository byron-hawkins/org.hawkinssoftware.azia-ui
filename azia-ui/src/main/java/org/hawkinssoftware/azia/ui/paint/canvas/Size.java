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
package org.hawkinssoftware.azia.ui.paint.canvas;

import java.awt.Rectangle;

import org.hawkinssoftware.rns.core.moa.ExecutionPath;

/**
 * DOC comment task awaits.
 * 
 * @author Byron Hawkins
 */
@ExecutionPath.NoFrame
public class Size
{
	public static final Size EMPTY = new Size(0, 0);

	public final int width;
	public final int height;

	public Size(int width, int height)
	{
		this.width = width;
		this.height = height;
	}
	
	public void applyTo(Rectangle r)
	{
		r.width = width;
		r.height = height;
	}

	@Override
	public String toString()
	{
		return "[" + width + "x" + height + "]";
	}
}
