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

import java.awt.Rectangle;

/**
 * DOC comment task awaits.
 * 
 * @author Byron Hawkins
 */
class TextAreaLinePainter
{
	String lineText;
	Rectangle lineBounds = new Rectangle();
	int textBaseline;
	
	TextAreaLinePainter(String lineText, Rectangle lineBounds, int textBaseline)
	{
		this.lineText = lineText;
		this.lineBounds.setBounds(lineBounds);
		this.textBaseline = textBaseline;
	}
}
