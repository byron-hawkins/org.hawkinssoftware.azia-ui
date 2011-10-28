package org.hawkinssoftware.azia.ui.component.text;

import org.hawkinssoftware.azia.core.action.UserInterfaceActor;
import org.hawkinssoftware.azia.core.layout.Axis;
import org.hawkinssoftware.azia.core.layout.BoundedEntity.Expansion;
import org.hawkinssoftware.azia.ui.component.AbstractComponent;
import org.hawkinssoftware.azia.ui.component.ComponentAssembly;
import org.hawkinssoftware.azia.ui.component.ComponentEnclosure;
import org.hawkinssoftware.azia.ui.component.scalar.ScrollPaneViewport;
import org.hawkinssoftware.azia.ui.component.scalar.ScrollPaneViewportComposite;
import org.hawkinssoftware.azia.ui.component.text.handler.TextViewportSizeChangeHandler;
import org.hawkinssoftware.azia.ui.component.transaction.mouse.MouseOverState;
import org.hawkinssoftware.rns.core.publication.InvocationConstraint;
import org.hawkinssoftware.rns.core.validation.ValidateRead;
import org.hawkinssoftware.rns.core.validation.ValidateWrite;

@ValidateRead
@ValidateWrite
public class TextViewport extends ScrollPaneViewport
{
	// Assembly classes are defined in the component class because some components have no composite enclosure
	// (otherwise that would be the natural place to define them)
	public static class Assembly extends ComponentAssembly<TextViewport, TextViewport.Painter, ScrollPaneViewportComposite<TextViewport, ?>>
	{
		public Assembly()
		{
			super(UserInterfaceActor.SynchronizationRole.SUBORDINATE);

			setComponent((Key<? extends TextViewport>) new AbstractComponent.Key<TextViewport>(TextViewport.class));
			setEnclosure(new ComponentEnclosure.Key(ScrollPaneViewportComposite.class));
		}

		@Override
		public void assemble(ScrollPaneViewportComposite<TextViewport, ?> viewport)
		{
			super.assemble(viewport);

			viewport.installHandler(new TextViewportSizeChangeHandler(viewport.getComponent()));
			MouseOverState.install(viewport.getComponent());
		}
	}

	public interface Painter extends ScrollPaneViewport.Painter
	{
		// marker
	}

	@InvocationConstraint 
	public TextViewport()
	{
	}

	@Override
	public Expansion getExpansion(Axis axis)
	{
		return Expansion.FILL;
	}
}
