package org.hawkinssoftware.azia.ui.component.button;

import org.hawkinssoftware.azia.core.action.UserInterfaceActor;
import org.hawkinssoftware.azia.core.layout.Axis;
import org.hawkinssoftware.azia.core.layout.BoundedEntity;
import org.hawkinssoftware.azia.ui.component.AbstractComponent;
import org.hawkinssoftware.azia.ui.component.ComponentEnclosure;
import org.hawkinssoftware.azia.ui.component.UserInterfaceHandler;
import org.hawkinssoftware.azia.ui.component.composition.CompositeAssembly;
import org.hawkinssoftware.azia.ui.component.text.handler.PlainTextHandler;
import org.hawkinssoftware.azia.ui.component.transaction.mouse.MouseOverState;
import org.hawkinssoftware.azia.ui.component.transaction.mouse.MousePressedState;
import org.hawkinssoftware.azia.ui.paint.basic.button.PushButtonPainter;
import org.hawkinssoftware.azia.ui.paint.plugin.LabelTextPlugin;
import org.hawkinssoftware.rns.core.publication.InvocationConstraint;
import org.hawkinssoftware.rns.core.role.DomainRole;
import org.hawkinssoftware.rns.core.validation.ValidateRead;
import org.hawkinssoftware.rns.core.validation.ValidateWrite;

@ValidateRead
@ValidateWrite
@DomainRole.Join(membership = PushButton.PushButtonDomain.class)
public class PushButton extends AbstractButton
{
	public static class PushButtonDomain extends DomainRole
	{
		@DomainRole.Instance
		public static final PushButtonDomain INSTANCE = new PushButtonDomain();
	}

	public static abstract class Assembly extends CompositeAssembly<PushButton, PushButton.Painter, ButtonComposite<PushButton, ?>>
	{
		public Assembly()
		{
			super(UserInterfaceActor.SynchronizationRole.AUTONOMOUS);

			setComponent(new AbstractComponent.Key<PushButton>(PushButton.class));
			setEnclosure(new ComponentEnclosure.Key(ButtonComposite.class));
		}
	}
	
	public static class TextButtonAssembly extends Assembly
	{
		@Override
		public void assemble(ButtonComposite<PushButton, ?> enclosure)
		{
			super.assemble(enclosure);

			enclosure.getComponent().installHandler(new PlainTextHandler.Basic(enclosure.getComponent()));
			MouseOverState.install(enclosure.getComponent());
			MousePressedState.install(enclosure.getComponent());
			((PushButtonPainter) enclosure.getPainter()).setTextPlugin(new LabelTextPlugin.Center());
		}
	}

	public interface Painter extends UserInterfaceHandler
	{
		// marker
	}

	@InvocationConstraint
	public PushButton()
	{
	}

	@Override
	public BoundedEntity.Expansion getExpansion(Axis axis)
	{
		return BoundedEntity.Expansion.FIT;
	}
}
