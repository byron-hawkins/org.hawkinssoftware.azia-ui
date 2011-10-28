package org.hawkinssoftware.azia.ui.paint.basic.text;

import java.awt.Rectangle;

class TextAreaLinePainter
{
	String lineText;
	Rectangle lineBounds = new Rectangle();
	
	TextAreaLinePainter(String lineText, Rectangle lineBounds)
	{
		this.lineText = lineText;
		this.lineBounds.setBounds(lineBounds);
	}
}
