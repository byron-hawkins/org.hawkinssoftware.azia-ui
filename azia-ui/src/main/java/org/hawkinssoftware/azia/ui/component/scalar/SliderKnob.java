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
package org.hawkinssoftware.azia.ui.component.scalar;

import org.hawkinssoftware.azia.core.action.UserInterfaceActor;
import org.hawkinssoftware.azia.core.layout.Axis;
import org.hawkinssoftware.azia.core.layout.BoundedEntity.Expansion;
import org.hawkinssoftware.azia.core.role.UserInterfaceDomains.RenderingDomain;
import org.hawkinssoftware.azia.ui.component.AbstractComponent;
import org.hawkinssoftware.azia.ui.component.ComponentAssembly;
import org.hawkinssoftware.azia.ui.component.ComponentEnclosure;
import org.hawkinssoftware.azia.ui.component.transaction.mouse.MouseDragHandler;
import org.hawkinssoftware.azia.ui.component.transaction.mouse.MouseOverState;
import org.hawkinssoftware.azia.ui.component.transaction.mouse.MousePressedState;
import org.hawkinssoftware.azia.ui.paint.InstancePainter;
import org.hawkinssoftware.rns.core.publication.InvocationConstraint;
import org.hawkinssoftware.rns.core.role.DomainRole;
import org.hawkinssoftware.rns.core.validation.ValidateRead;
import org.hawkinssoftware.rns.core.validation.ValidateWrite;

/**
 * DOC comment task awaits.
 * 
 * @author Byron Hawkins
 */
@ValidateRead
@ValidateWrite
@InvocationConstraint(domains = SliderKnob.SliderKnobDomain.class)
public class SliderKnob extends AbstractComponent
{

	/**
	 * DOC comment task awaits.
	 * 
	 * @author Byron Hawkins
	 */
	public static class SliderKnobDomain extends DomainRole
	{
		@DomainRole.Instance
		public static final SliderKnobDomain INSTANCE = new SliderKnobDomain();
	}

	/**
	 * DOC comment task awaits.
	 * 
	 * @param <PainterType>
	 *            the generic type
	 * @author Byron Hawkins
	 */
	@DomainRole.Join(membership = RenderingDomain.class)
	public static class Assembly<PainterType extends InstancePainter<SliderKnob>> extends
			ComponentAssembly<SliderKnob, SliderKnob.Painter, ComponentEnclosure<SliderKnob, PainterType>>
	{
		public Assembly()
		{
			super(UserInterfaceActor.SynchronizationRole.SUBORDINATE);

			setComponent(new AbstractComponent.Key<SliderKnob>(SliderKnob.class));
			setEnclosure(ComponentEnclosure.SINGULAR);
		}

		/**
		 * @JTourBusStop 7.1, Usage of @DefinesIdentity in Azia, Exposure of relationships - MousePressedState installed
		 *               in a SliderKnob:
		 * 
		 *               To support dragging, a slider knob needs to know when the mouse button is pressed on it. This
		 *               awareness is obtained simply by installing the MousePressedState into the knob during assembly.
		 */
		@Override
		public void assemble(ComponentEnclosure<SliderKnob, PainterType> enclosure)
		{
			super.assemble(enclosure);

			MouseDragHandler.install(enclosure.getComponent());
			MouseOverState.install(enclosure.getComponent());
			MousePressedState.install(enclosure.getComponent());
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

	// RNS: `axis final or transactional?
	private Axis axis;

	@InvocationConstraint
	public SliderKnob()
	{
	}

	public Axis getAxis()
	{
		return axis;
	}

	public void setAxis(Axis axis)
	{
		this.axis = axis;
	}

	@Override
	public Expansion getExpansion(Axis axis)
	{
		return Expansion.FIT;
	}
}
