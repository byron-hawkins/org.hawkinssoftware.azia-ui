package org.hawkinssoftware.azia.ui.component.text;

import org.hawkinssoftware.azia.ui.component.scalar.ScrollPane;
import org.hawkinssoftware.azia.ui.component.scalar.ScrollPaneComposite;
import org.hawkinssoftware.azia.ui.component.scalar.ScrollPaneViewportComposite;
import org.hawkinssoftware.azia.ui.paint.basic.text.TextViewportPainter;

public class TextViewportComposite extends ScrollPaneViewportComposite<TextViewport, TextViewportPainter>
{
	public static class ScrollPaneAssembly extends ScrollPane.Assembly<TextViewportComposite, ScrollPaneComposite<TextViewportComposite>>
	{
		@SuppressWarnings("unchecked")
		public ScrollPaneAssembly()
		{
			super((Class<ScrollPaneComposite<TextViewportComposite>>) (Class<?>) ScrollPaneComposite.class, new TextViewport.Assembly());
		}
	}
	
	public TextViewportComposite(TextViewport component)
	{
		super(component);
	}
}
