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
package org.hawkinssoftware.azia.ui.component.text;

import org.hawkinssoftware.azia.core.action.UserInterfaceActor;
import org.hawkinssoftware.azia.core.layout.Axis;
import org.hawkinssoftware.azia.core.layout.BoundedEntity.Expansion;
import org.hawkinssoftware.azia.ui.component.AbstractComponent;
import org.hawkinssoftware.azia.ui.component.ComponentAssembly;
import org.hawkinssoftware.azia.ui.component.ComponentEnclosure;
import org.hawkinssoftware.azia.ui.component.scalar.ScrollPaneViewport;
import org.hawkinssoftware.azia.ui.component.text.handler.TextViewportSizeChangeHandler;
import org.hawkinssoftware.azia.ui.component.transaction.mouse.MouseOverState;
import org.hawkinssoftware.rns.core.publication.InvocationConstraint;
import org.hawkinssoftware.rns.core.validation.ValidateRead;
import org.hawkinssoftware.rns.core.validation.ValidateWrite;

/**
 * DOC comment task awaits.
 * 
 * @author Byron Hawkins
 */
@ValidateRead
@ValidateWrite
public class TextViewport extends ScrollPaneViewport
{
	// Assembly classes are defined in the component class because some components have no composite enclosure
	// (otherwise that would be the natural place to define them)
	/**
	 * DOC comment task awaits.
	 * 
	 * @author Byron Hawkins
	 */
	public static class Assembly extends ComponentAssembly<TextViewport, TextViewport.Painter, TextViewportComposite>
	{
		public Assembly()
		{
			super(UserInterfaceActor.SynchronizationRole.SUBORDINATE);

			setComponent((Key<? extends TextViewport>) new AbstractComponent.Key<TextViewport>(TextViewport.class));
			setEnclosure(new ComponentEnclosure.Key(TextViewportComposite.class));
		}

		@Override
		public void assemble(TextViewportComposite viewport)
		{
			super.assemble(viewport);

			viewport.installHandler(new TextViewportSizeChangeHandler(viewport.getComponent()));
			MouseOverState.install(viewport.getComponent());
		}
	}

	/**
	 * DOC comment task awaits.
	 * 
	 * @author Byron Hawkins
	 */
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
