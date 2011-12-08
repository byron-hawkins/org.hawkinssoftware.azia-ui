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
import org.hawkinssoftware.azia.core.action.UserInterfaceTransaction.ActorBasedContributor.PendingTransaction;
import org.hawkinssoftware.azia.core.layout.Axis;
import org.hawkinssoftware.azia.core.layout.BoundedEntity;
import org.hawkinssoftware.azia.core.layout.BoundedEntity.Expansion;
import org.hawkinssoftware.azia.core.role.UserInterfaceDomains.AssemblyDomain;
import org.hawkinssoftware.azia.core.role.UserInterfaceDomains.DisplayBoundsDomain;
import org.hawkinssoftware.azia.core.role.UserInterfaceDomains.RenderingDomain;
import org.hawkinssoftware.azia.ui.component.AbstractComponent;
import org.hawkinssoftware.azia.ui.component.ComponentAssembly;
import org.hawkinssoftware.azia.ui.component.ComponentEnclosure;
import org.hawkinssoftware.azia.ui.component.UserInterfaceHandler;
import org.hawkinssoftware.azia.ui.component.scalar.handler.SliderTrackDivisionHandler;
import org.hawkinssoftware.azia.ui.component.transaction.state.ChangeTrackDivisionDirective;
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
@InvocationConstraint(domains = SliderTrack.SliderTrackDomain.class)
public class SliderTrack extends AbstractComponent
{
	// TODO: not totally sure about this sub-domain configuration
	/**
	 * DOC comment task awaits.
	 * 
	 * @author Byron Hawkins
	 */
	public static class SliderTrackDomain extends DisplayBoundsDomain
	{
		@DomainRole.Instance
		public static final SliderTrackDomain INSTANCE = new SliderTrackDomain();
	}

	/**
	 * DOC comment task awaits.
	 * 
	 * @param <PainterType>
	 *            the generic type
	 * @author Byron Hawkins
	 */
	@DomainRole.Join(membership = RenderingDomain.class)
	public static class Assembly<PainterType extends InstancePainter<SliderTrack>> extends
			ComponentAssembly<SliderTrack, SliderTrack.Painter, ComponentEnclosure<SliderTrack, PainterType>>
	{
		public Assembly()
		{
			super(UserInterfaceActor.SynchronizationRole.SUBORDINATE);

			setComponent(new AbstractComponent.Key<SliderTrack>(SliderTrack.class));
			setEnclosure(ComponentEnclosure.SINGULAR);
		}

		@Override
		public void assemble(ComponentEnclosure<SliderTrack, PainterType> enclosure)
		{
			super.assemble(enclosure);

			enclosure.getComponent().setDivisionHandler(new SliderTrackDivisionHandler(enclosure.getComponent()));
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

	protected Axis axis;

	private SliderTrackDivisionHandler divisionHandler;

	@InvocationConstraint
	public SliderTrack()
	{
	}

	public SliderTrackDivisionHandler getDivisionHandler()
	{
		return divisionHandler;
	}

	@InvocationConstraint(domains = AssemblyDomain.class)
	public void setDivisionHandler(SliderTrackDivisionHandler divisionHandler)
	{
		this.divisionHandler = divisionHandler;
	}

	public Axis getAxis()
	{
		return axis;
	}

	public void setAxis(Axis axis)
	{
		this.axis = axis;
	}

	public int getDivisionPoint()
	{
		return divisionHandler.getDivisionPoint();
	}

	@Override
	public Expansion getExpansion(Axis axis)
	{
		return Expansion.FIT;
	}

	public UserInterfaceHandler createDivisionContributor()
	{
		return new DivisionHook();
	}
	
	/**
	 * DOC comment task awaits.
	 * 
	 * @author Byron Hawkins
	 */
	@DomainRole.Join(membership = { SliderTrackDomain.class, DisplayBoundsDomain.class })
	public class DivisionHook implements UserInterfaceHandler
	{
		public void positionChanged(BoundedEntity.PositionNotification note, PendingTransaction transaction)
		{
			// TODO: clean init in txn safety zone, instead of stuff like this:
			if (getAxis() == null)
			{
				return;
			}
			Integer newDivisionPoint = note.getPosition(getAxis());
			if (newDivisionPoint != null)
			{
				transaction.contribute(new ChangeTrackDivisionDirective(SliderTrack.this, newDivisionPoint + 1));
			}
		}
	}
}
