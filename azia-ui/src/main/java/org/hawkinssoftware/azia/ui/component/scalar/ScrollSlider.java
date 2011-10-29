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
import org.hawkinssoftware.azia.core.role.UserInterfaceDomains.DisplayBoundsDomain;
import org.hawkinssoftware.azia.ui.component.AbstractComponent;
import org.hawkinssoftware.azia.ui.component.ComponentEnclosure;
import org.hawkinssoftware.azia.ui.component.ComponentRegistry;
import org.hawkinssoftware.azia.ui.component.composition.CompositeAssembly;
import org.hawkinssoftware.azia.ui.component.scalar.handler.SliderCollaborationActionContributor;
import org.hawkinssoftware.azia.ui.component.scalar.handler.SliderKnobDragContributor;
import org.hawkinssoftware.azia.ui.component.scalar.handler.SliderMouseHandler;
import org.hawkinssoftware.azia.ui.component.scalar.handler.SliderPaintHandler;
import org.hawkinssoftware.azia.ui.component.scalar.handler.SliderResizeHandler;
import org.hawkinssoftware.azia.ui.component.scalar.handler.SliderStandaloneActionContributor;
import org.hawkinssoftware.azia.ui.paint.InstancePainter;
import org.hawkinssoftware.azia.ui.paint.basic.scalar.SliderKnobPainter;
import org.hawkinssoftware.azia.ui.paint.basic.scalar.SliderPainter;
import org.hawkinssoftware.azia.ui.paint.basic.scalar.SliderTrackPainter;
import org.hawkinssoftware.azia.ui.paint.transaction.repaint.RepaintInstanceDirective;
import org.hawkinssoftware.rns.core.publication.InvocationConstraint;
import org.hawkinssoftware.rns.core.role.DomainRole;

/**
 * DOC comment task awaits.
 * 
 * @author Byron Hawkins
 */
public class ScrollSlider extends AbstractSlider
{
	
	/**
	 * DOC comment task awaits.
	 * 
	 * @param <SliderCompositeType>
	 *            the generic type
	 * @author Byron Hawkins
	 */
	@DomainRole.Join(membership = DisplayBoundsDomain.class)
	public static class Assembly<SliderCompositeType extends SliderComposite<ScrollSlider>> extends
			CompositeAssembly<ScrollSlider, AbstractSlider.Painter, SliderCompositeType>
	{
		private final SliderTrack.Assembly<SliderTrackPainter> sliderTrackAssembly = new SliderTrack.Assembly<SliderTrackPainter>();
		private final SliderKnob.Assembly<SliderKnobPainter> sliderKnobAssembly = new SliderKnob.Assembly<SliderKnobPainter>();

		private Axis axis;
		private boolean isStandalone;

		public Assembly(Axis axis)
		{
			this(axis, false);
		}

		@SuppressWarnings("unchecked")
		public Assembly(Axis axis, boolean isStandalone)
		{
			super(UserInterfaceActor.SynchronizationRole.DEPENDENT);

			this.axis = axis;
			this.isStandalone = isStandalone;

			setComponent(new AbstractComponent.Key<ScrollSlider>(ScrollSlider.class));
			setPainter(InstancePainter.Key.createKey(AbstractSlider.Painter.class, (Class<SliderPainter<ScrollSlider>>) (Class<?>) SliderPainter.class));
			setEnclosure(new ComponentEnclosure.Key(SliderComposite.class));
		}

		@Override
		public void assemble(SliderCompositeType slider)
		{
			super.assemble(slider);
  
			slider.getComponent().setAxis(axis);
			slider.setTrack(ComponentRegistry.getInstance().getComponent(sliderTrackAssembly));
			slider.getTrack().getComponent().setRepaintAction(new RepaintInstanceDirective(slider.getComponent()));

			ComponentEnclosure<SliderKnob, SliderKnobPainter> knob = ComponentRegistry.getInstance().getComponent(sliderKnobAssembly);
			slider.setKnob(knob);
			knob.getComponent().installHandler(new SliderKnobDragContributor(slider));

			slider.installHandler(new SliderMouseHandler(slider));
			slider.installHandler(new SliderPaintHandler(slider));
			slider.installHandler(new SliderResizeHandler(slider));

			if (isStandalone)
			{
				slider.getComponent().installHandler(new SliderStandaloneActionContributor(slider));
			}
			else
			{
				slider.getComponent().installHandler(new SliderCollaborationActionContributor(slider));
			}
			slider.setInitialSizes();
		}
	}

	@InvocationConstraint
	public ScrollSlider()
	{
	}

	@Override
	public Expansion getExpansion(Axis axis)
	{
		if (axis == this.axis)
		{
			return Expansion.FILL;
		}
		else
		{
			return Expansion.FIT;
		}
	}
}
