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
import org.hawkinssoftware.azia.core.layout.BoundedEntity;
import org.hawkinssoftware.azia.ui.component.AbstractComponent;
import org.hawkinssoftware.azia.ui.component.ComponentEnclosure;
import org.hawkinssoftware.azia.ui.component.composition.CompositeAssembly;
import org.hawkinssoftware.azia.ui.component.text.handler.LabelTextSizeChangeHandler;
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
public class TextArea extends AbstractComponent
{
	
	/**
	 * DOC comment task awaits.
	 * 
	 * @author Byron Hawkins
	 */
	public static class Assembly extends CompositeAssembly<TextArea, TextArea.Painter, TextAreaComposite<TextArea, ?>>
	{
		public Assembly()
		{
			super(UserInterfaceActor.SynchronizationRole.DEPENDENT);

			setComponent(new AbstractComponent.Key<TextArea>(TextArea.class));
			setEnclosure(new ComponentEnclosure.Key(TextAreaComposite.class));
		}

		public void assemble(TextAreaComposite<TextArea, ?> enclosure)
		{
			super.assemble(enclosure);

			enclosure.installHandler(new LabelTextSizeChangeHandler(enclosure));
			MouseOverState.install(enclosure.getComponent());
		}
	}

	/**
	 * DOC comment task awaits.
	 * 
	 * @author Byron Hawkins
	 */
	public interface Painter
	{
		// marker
	}

	@InvocationConstraint
	public TextArea()
	{
	}

	@Override
	public BoundedEntity.Expansion getExpansion(Axis axis)
	{
		return BoundedEntity.Expansion.FIT;
	}
}
