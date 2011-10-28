package org.hawkinssoftware.azia.ui.component.text;

import org.hawkinssoftware.azia.ui.component.composition.AbstractComposite;
import org.hawkinssoftware.azia.ui.paint.ComponentPainter;

public class LabelComposite<LabelType extends Label, PainterType extends ComponentPainter<LabelType>> extends AbstractComposite<LabelType, PainterType>
{
	public LabelComposite(LabelType component)
	{
		super(component);
	}
}
