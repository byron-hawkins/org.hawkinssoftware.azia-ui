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

/**
 * Simple push button. All characteristic behaviors are pluggable in the containing <code>ButtonComposite</code>, so
 * this component itself is an empty shell.
 * 
 * @author Byron Hawkins
 */
@ValidateRead
@ValidateWrite
@DomainRole.Join(membership = PushButton.PushButtonDomain.class)
public class PushButton extends AbstractButton
{
	/**
	 * Domain specific to interaction with a <code>PushButton</code>.
	 * 
	 * @author Byron Hawkins
	 */
	public static class PushButtonDomain extends DomainRole
	{
		@DomainRole.Instance
		public static final PushButtonDomain INSTANCE = new PushButtonDomain();
	}

	/**
	 * Generic assembly for a <code>PushButton</code>. This assembly is abstract because it doesn't plug any
	 * characteristic behaviors into the button, which therefore does not even draw on the screen.
	 * 
	 * @author Byron Hawkins
	 */
	public static abstract class Assembly extends CompositeAssembly<PushButton, PushButton.Painter, ButtonComposite<PushButton, ?>>
	{
		public Assembly()
		{
			super(UserInterfaceActor.SynchronizationRole.AUTONOMOUS);

			setComponent(new AbstractComponent.Key<PushButton>(PushButton.class));
			setEnclosure(new ComponentEnclosure.Key(ButtonComposite.class));
		}
	}

	/**
	 * Assembly for a simple <code>PushButton</code> having a centered text label, a pressed state and a mouseover
	 * state.
	 * 
	 * @author Byron Hawkins
	 * 
	 * @JTourBusStop 7.2, Usage of @DefinesIdentity in Azia, Exposure of relationships - MousePressedState installed in
	 *               a PushButton:
	 * 
	 *               A PushButton of course needs to know when it is pushed, and though it does not respond to the mouse
	 *               like a scrollbar knob would, it can still use the same data handler. The @DefinesIdentity
	 *               annotation requires the MousePressedState to be defined as a separate entity from the scrollbar
	 *               knob, making it available for use in this PushButton. Of course the knob could define its own
	 *               MousePressedState as a private inner class, but even in that case the private handler would be very
	 *               easy to share with other classes--simply changing its declaration to "public static" would make it
	 *               globally available. Conversely, if the knob defined its button press handling within its own
	 *               methods, it would be very difficult to share that functionality, because the essential relationship
	 *               between the knob and its mouse press handling would be compressed inside the knob's code.
	 */
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

	/**
	 * Marker interface
	 * 
	 * @author Byron Hawkins
	 */
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
