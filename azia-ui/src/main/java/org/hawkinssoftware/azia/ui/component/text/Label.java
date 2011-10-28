package org.hawkinssoftware.azia.ui.component.text;

import org.hawkinssoftware.azia.core.action.UserInterfaceActor;
import org.hawkinssoftware.azia.core.layout.Axis;
import org.hawkinssoftware.azia.core.layout.BoundedEntity;
import org.hawkinssoftware.azia.ui.component.AbstractComponent;
import org.hawkinssoftware.azia.ui.component.ComponentEnclosure;
import org.hawkinssoftware.azia.ui.component.UserInterfaceHandler;
import org.hawkinssoftware.azia.ui.component.composition.CompositeAssembly;
import org.hawkinssoftware.azia.ui.component.text.handler.PlainTextHandler;
import org.hawkinssoftware.rns.core.publication.InvocationConstraint;

public class Label extends AbstractComponent
{
	public static class Assembly extends CompositeAssembly<Label, Label.Painter, LabelComposite<Label, ?>>
	{
		public Assembly()
		{
			super(UserInterfaceActor.SynchronizationRole.AUTONOMOUS);

			setComponent(new AbstractComponent.Key<Label>(Label.class));
			setEnclosure(new ComponentEnclosure.Key(LabelComposite.class));
		}

		@Override
		public void assemble(LabelComposite<Label, ?> enclosure)
		{
			super.assemble(enclosure);

			enclosure.getComponent().installHandler(new PlainTextHandler.Basic(enclosure.getComponent()));
		}
	}

	public interface Painter extends UserInterfaceHandler
	{
		// marker
	}

	@InvocationConstraint
	public Label()
	{
	}

	@Override
	public BoundedEntity.Expansion getExpansion(Axis axis)
	{
		return BoundedEntity.Expansion.FIT;
	}
}
