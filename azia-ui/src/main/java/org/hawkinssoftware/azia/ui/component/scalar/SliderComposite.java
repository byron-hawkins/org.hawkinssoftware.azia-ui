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

import org.hawkinssoftware.azia.core.action.UserInterfaceActorPreview;
import org.hawkinssoftware.azia.core.action.UserInterfaceDirective;
import org.hawkinssoftware.azia.core.action.UserInterfaceTransaction.ActorBasedContributor;
import org.hawkinssoftware.azia.core.action.UserInterfaceTransactionQuery.Property;
import org.hawkinssoftware.azia.core.layout.Axis;
import org.hawkinssoftware.azia.core.layout.ScreenPosition;
import org.hawkinssoftware.azia.core.role.UserInterfaceDomains.AssemblyDomain;
import org.hawkinssoftware.azia.ui.component.ComponentEnclosure;
import org.hawkinssoftware.azia.ui.component.EnclosureBounds;
import org.hawkinssoftware.azia.ui.component.MutableBounds;
import org.hawkinssoftware.azia.ui.component.UserInterfaceHandler;
import org.hawkinssoftware.azia.ui.component.composition.AbstractComposite;
import org.hawkinssoftware.azia.ui.component.scalar.SliderKnob.SliderKnobDomain;
import org.hawkinssoftware.azia.ui.component.scalar.SliderTrack.SliderTrackDomain;
import org.hawkinssoftware.azia.ui.component.scalar.handler.SliderStandaloneActionContributor;
import org.hawkinssoftware.azia.ui.component.scalar.transaction.SetVisibleDirective;
import org.hawkinssoftware.azia.ui.component.transaction.resize.ComponentBoundsChangeDirective;
import org.hawkinssoftware.azia.ui.paint.InstancePainter;
import org.hawkinssoftware.azia.ui.paint.basic.scalar.SliderKnobPainter;
import org.hawkinssoftware.azia.ui.paint.basic.scalar.SliderTrackPainter;
import org.hawkinssoftware.rns.core.publication.InvocationConstraint;
import org.hawkinssoftware.rns.core.role.DomainRole;
import org.hawkinssoftware.rns.core.util.UnknownEnumConstantException;

/**
 * @param <SliderType>
 * @author b
 */
@InvocationConstraint(domains = { SliderComposite.SliderCompositeDomain.class })
@DomainRole.Join(membership = { SliderKnobDomain.class, SliderTrackDomain.class })
public class SliderComposite<SliderType extends AbstractSlider> extends AbstractComposite<SliderType, InstancePainter<SliderType>>
{

	/**
	 * DOC comment task awaits.
	 * 
	 * @author Byron Hawkins
	 */
	public static class SliderCompositeDomain extends DomainRole
	{
		@DomainRole.Instance
		public static final SliderCompositeDomain INSTANCE = new SliderCompositeDomain();
	}

	/**
	 * DOC comment task awaits.
	 * 
	 * @author Byron Hawkins
	 */
	public class VisibilityHandler implements UserInterfaceHandler, UserInterfaceActorPreview
	{
		public void visibilityChanged(SetVisibleDirective change)
		{
			setVisible(change.visible);
		}

		@Override
		public boolean affects(Property property)
		{
			return property.matches("isVisible");
		}

		@SuppressWarnings("unchecked")
		public <T> T getPreview(UserInterfaceDirective action, T value)
		{
			return (T) (Boolean) ((SetVisibleDirective) action).visible;
		}
	}

	public static final Key KEY = new Key(SliderComposite.class);

	private ComponentEnclosure<SliderTrack, SliderTrackPainter> track;
	private ComponentEnclosure<SliderKnob, SliderKnobPainter> knob;

	private UserInterfaceHandler trackDivisionContributor;
	private ActorBasedContributor knobDragContributor;

	@InvocationConstraint
	public SliderComposite(SliderType slider)
	{
		super(slider);

		installHandler(new VisibilityHandler());
	}

	@InvocationConstraint(domains = AssemblyDomain.class)
	public void setInitialSizes()
	{
		// FIXME: this init is a hack
		MutableBounds initialBounds = new MutableBounds();
		initialBounds.setSpan(12, getAxis().opposite());
		bounds = new EnclosureBounds(initialBounds);
		track.setBounds(bounds);
		knob.setBounds(bounds);
	}

	public Axis getAxis()
	{
		return component.getAxis();
	}

	@Override
	public void installHandler(UserInterfaceHandler handler)
	{
		super.installHandler(handler);

		// FIXME: initialization hack
		if (handler instanceof SliderStandaloneActionContributor)
		{
			MutableBounds initialBounds = new MutableBounds(knob.getBounds());
			initialBounds.setSpan(20, getAxis());
			knob.setBounds(new EnclosureBounds(initialBounds));
		}
	}

	public ComponentEnclosure<SliderKnob, SliderKnobPainter> getKnob()
	{
		return knob;
	}

	public ComponentEnclosure<SliderTrack, SliderTrackPainter> getTrack()
	{
		return track;
	}

	public boolean contactsKnob(ScreenPosition position)
	{
		int orientedPosition = getAxis().extractPosition(position);
		return ((orientedPosition > knob.getBounds().getPosition(getAxis()) && (orientedPosition < (knob.getBounds().getExtent(getAxis())))));
	}

	public int getMaxKnobPosition()
	{
		return bounds.getSpan(getAxis()) - knob.getBounds().getSpan(getAxis());
	}

	@InvocationConstraint(domains = AssemblyDomain.class)
	public void setKnob(ComponentEnclosure<SliderKnob, SliderKnobPainter> knob)
	{
		this.knob = knob;
		// TODO: should this be a directive?
		knob.getComponent().setAxis(getAxis());

		updateTrackDivisionContributor();
	}

	@InvocationConstraint(domains = AssemblyDomain.class)
	public void setTrack(ComponentEnclosure<SliderTrack, SliderTrackPainter> track)
	{
		this.track = track;
		track.getComponent().setAxis(getAxis());
		updateTrackDivisionContributor();
	}

	private void updateTrackDivisionContributor()
	{
		if (knob != null)
		{
			if (trackDivisionContributor != null)
			{
				knob.removeHandler(trackDivisionContributor);
			}

			trackDivisionContributor = track.getComponent().createDivisionContributor();
			knob.installHandler(trackDivisionContributor);
		}
	}

	public ComponentBoundsChangeDirective moveKnob(int newKnobPosition)
	{
		MutableBounds newKnobBounds;
		switch (getAxis())
		{
			case H:
				newKnobBounds = new MutableBounds(newKnobPosition, bounds.y, knob.getBounds().width, knob.getBounds().height);
				break;
			case V:
				newKnobBounds = new MutableBounds(bounds.x, newKnobPosition, knob.getBounds().width, knob.getBounds().height);
				break;
			default:
				throw new UnknownEnumConstantException(getAxis());
		}

		Axis opposite = getComponent().getAxis().opposite();
		newKnobBounds.setPosition(bounds.getPosition(opposite), opposite);
		newKnobBounds.setSpan(bounds.getSpan(opposite), opposite);

		return new ComponentBoundsChangeDirective(knob.getComponent(), new EnclosureBounds(newKnobBounds));
	}
}
