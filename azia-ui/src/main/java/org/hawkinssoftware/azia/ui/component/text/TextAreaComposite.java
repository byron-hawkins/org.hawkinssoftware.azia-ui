package org.hawkinssoftware.azia.ui.component.text;

import org.hawkinssoftware.azia.ui.component.composition.AbstractComposite;
import org.hawkinssoftware.azia.ui.paint.ComponentPainter;
import org.hawkinssoftware.rns.core.publication.InvocationConstraint;

public class TextAreaComposite<LabelType extends TextArea, PainterType extends ComponentPainter<LabelType>> extends AbstractComposite<LabelType, PainterType>
{
	@InvocationConstraint
	public TextAreaComposite(LabelType component)
	{
		super(component);
	}
}
