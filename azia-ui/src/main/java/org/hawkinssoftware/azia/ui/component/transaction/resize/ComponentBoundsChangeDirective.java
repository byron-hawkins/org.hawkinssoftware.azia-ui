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
package org.hawkinssoftware.azia.ui.component.transaction.resize;

import org.hawkinssoftware.azia.core.action.UserInterfaceDirective;
import org.hawkinssoftware.azia.core.action.UserInterfaceNotification;
import org.hawkinssoftware.azia.core.action.UserInterfaceTransaction.ActorBasedContributor.PendingTransaction;
import org.hawkinssoftware.azia.core.layout.Axis;
import org.hawkinssoftware.azia.core.layout.BoundedEntity;
import org.hawkinssoftware.azia.core.role.UserInterfaceDomains.DisplayBoundsDomain;
import org.hawkinssoftware.azia.core.role.UserInterfaceDomains.RenderingDomain;
import org.hawkinssoftware.azia.ui.component.AbstractComponent;
import org.hawkinssoftware.azia.ui.component.EnclosureBounds;
import org.hawkinssoftware.azia.ui.component.PartialBounds;
import org.hawkinssoftware.azia.ui.component.UserInterfaceHandler;
import org.hawkinssoftware.rns.core.publication.InvocationConstraint;
import org.hawkinssoftware.rns.core.role.DomainRole;
import org.hawkinssoftware.rns.core.util.UnknownEnumConstantException;

/**
 * DOC comment task awaits.
 * 
 * @author Byron Hawkins
 */
@InvocationConstraint(domains = DisplayBoundsDomain.class)
@DomainRole.Join(membership = DisplayBoundsDomain.class)
public class ComponentBoundsChangeDirective extends UserInterfaceDirective
{
	
	/**
	 * DOC comment task awaits.
	 * 
	 * @author Byron Hawkins
	 */
	public interface Handler extends UserInterfaceHandler
	{
		void resizePosted(ComponentBoundsChangeDirective.Notification resize, PendingTransaction transaction);

		void resize(ComponentBoundsChangeDirective resizeAction);
	}

	/**
	 * DOC comment task awaits.
	 * 
	 * @author Byron Hawkins
	 */
	@InvocationConstraint(domains = DisplayBoundsDomain.class)
	@DomainRole.Join(membership = DisplayBoundsDomain.class)
	public class Notification extends BoundedEntity.PositionNotification
	{
		@InvocationConstraint(domains = RenderingDomain.class)
		public AbstractComponent getComponent()
		{
			return ((AbstractComponent) getActor());
		}

		public PartialBounds getBoundsChange()
		{
			return bounds;
		}

		@Override
		public Integer getPosition(Axis axis)
		{
			switch (axis)
			{
				case H:
					return bounds.x;
				case V:
					return bounds.y;
				default:
					throw new UnknownEnumConstantException(axis);
			}
		}

		public Integer getSpan(Axis axis)
		{
			switch (axis)
			{
				case H:
					return bounds.width;
				case V:
					return bounds.height;
				default:
					throw new UnknownEnumConstantException(axis);
			}
		}

		public Integer getExtent(Axis axis)
		{
			switch (axis)
			{
				case H:
					return bounds.x + bounds.width;
				case V:
					return bounds.y + bounds.height;
				default:
					throw new UnknownEnumConstantException(axis);
			}
		}

		public boolean hasPosition(Axis axis)
		{
			switch (axis)
			{
				case H:
					return (bounds.x != null);
				case V:
					return (bounds.y != null);
				default:
					throw new UnknownEnumConstantException(axis);
			}
		}

		public boolean hasSpan(Axis axis)
		{
			switch (axis)
			{
				case H:
					return (bounds.width != null);
				case V:
					return (bounds.height != null);
				default:
					throw new UnknownEnumConstantException(axis);
			}
		}

		public boolean hasExtent(Axis axis)
		{
			switch (axis)
			{
				case H:
					return (bounds.x != null) && (bounds.width != null);
				case V:
					return (bounds.y != null) && (bounds.height != null);
				default:
					throw new UnknownEnumConstantException(axis);
			}
		}
	}

	public static ComponentBoundsChangeDirective changePosition(AbstractComponent component, Axis axis, int value)
	{
		switch (axis)
		{
			case H:
				return new ComponentBoundsChangeDirective(component, value, null, null, null);
			case V:
				return new ComponentBoundsChangeDirective(component, null, value, null, null);
			default:
				throw new UnknownEnumConstantException(axis);
		}
	}

	public static ComponentBoundsChangeDirective changeSpan(AbstractComponent component, Axis axis, int value)
	{
		switch (axis)
		{
			case H:
				return new ComponentBoundsChangeDirective(component, null, null, value, null);
			case V:
				return new ComponentBoundsChangeDirective(component, null, null, null, value);
			default:
				throw new UnknownEnumConstantException(axis);
		}
	}

	// RNS: disallow mutable values on a UIDirective? ok here, but enforce it
	// could make @Immutable and analyze declarations for mutability, though it may be difficult to discern in the case
	// of collections and such things, especially with binary types that can't be traversed and may be difficult to
	// analyze.

	public final PartialBounds bounds;

	public ComponentBoundsChangeDirective(AbstractComponent component, EnclosureBounds bounds)
	{
		super(component);

		this.bounds = new PartialBounds(bounds);
	}

	public ComponentBoundsChangeDirective(AbstractComponent component, PartialBounds bounds)
	{
		super(component);

		this.bounds = bounds;
	}

	public ComponentBoundsChangeDirective(AbstractComponent component, Integer x, Integer y, Integer width, Integer height)
	{
		super(component);

		this.bounds = new PartialBounds(x, y, width, height);
	}

	public ComponentBoundsChangeDirective forward(AbstractComponent component)
	{
		return new ComponentBoundsChangeDirective(component, bounds);
	}

	@Override
	public UserInterfaceNotification createNotification()
	{
		return new Notification();
	}
}
