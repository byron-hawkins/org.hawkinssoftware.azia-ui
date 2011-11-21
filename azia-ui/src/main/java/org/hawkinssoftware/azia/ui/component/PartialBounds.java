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
package org.hawkinssoftware.azia.ui.component;

import org.hawkinssoftware.azia.core.layout.Axis;
import org.hawkinssoftware.azia.core.role.UserInterfaceDomains.AssemblyDomain;
import org.hawkinssoftware.azia.core.role.UserInterfaceDomains.DisplayBoundsDomain;
import org.hawkinssoftware.rns.core.publication.InvocationConstraint;
import org.hawkinssoftware.rns.core.role.DomainRole;
import org.hawkinssoftware.rns.core.util.UnknownEnumConstantException;

/**
 * DOC comment task awaits.
 * 
 * @author Byron Hawkins
 */
@InvocationConstraint(domains = { DisplayBoundsDomain.class, AssemblyDomain.class })
@DomainRole.Join(membership = DisplayBoundsDomain.class)
public class PartialBounds
{
	public final Integer x;
	public final Integer y;
	public final Integer width;
	public final Integer height;

	public PartialBounds()
	{
		x = y = width = height = null;
	}

	public PartialBounds(Integer x, Integer y, Integer width, Integer height)
	{
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}

	public PartialBounds(EnclosureBounds bounds)
	{
		this.x = bounds.x;
		this.y = bounds.y;
		this.width = bounds.width;
		this.height = bounds.height;
	}

	public PartialBounds fillWith(PartialBounds other)
	{
		return new PartialBounds((this.x == null) ? other.x : this.x, (this.y == null) ? other.y : this.y, (this.width == null) ? other.width : this.width,
				(this.height == null) ? other.height : this.height);
	}

	public EnclosureBounds applyValues(EnclosureBounds destination)
	{
		return new EnclosureBounds(x == null ? destination.x : x, y == null ? destination.y : y, width == null ? destination.width : width,
				height == null ? destination.height : height);
	}

	public boolean hasPosition(Axis axis)
	{
		switch (axis)
		{
			case H:
				return x != null;
			case V:
				return y != null;
			default:
				throw new UnknownEnumConstantException(axis);
		}
	}

	public Integer getPosition(Axis axis)
	{
		switch (axis)
		{
			case H:
				return x;
			case V:
				return y;
			default:
				throw new UnknownEnumConstantException(axis);
		}
	}

	public boolean hasSpan(Axis axis)
	{
		switch (axis)
		{
			case H:
				return width != null;
			case V:
				return height != null;
			default:
				throw new UnknownEnumConstantException(axis);
		}
	}

	public Integer getSpan(Axis axis)
	{
		switch (axis)
		{
			case H:
				return width;
			case V:
				return height;
			default:
				throw new UnknownEnumConstantException(axis);
		}
	}
}
