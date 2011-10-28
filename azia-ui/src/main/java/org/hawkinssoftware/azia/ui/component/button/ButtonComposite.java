package org.hawkinssoftware.azia.ui.component.button;

import org.hawkinssoftware.azia.ui.component.composition.AbstractComposite;
import org.hawkinssoftware.azia.ui.paint.ComponentPainter;
import org.hawkinssoftware.rns.core.publication.InvocationConstraint;

public class ButtonComposite<ButtonType extends AbstractButton, PainterType extends ComponentPainter<ButtonType>> extends
		AbstractComposite<ButtonType, PainterType>
{
	@InvocationConstraint
	public ButtonComposite(ButtonType component)
	{
		super(component);
	}
}
