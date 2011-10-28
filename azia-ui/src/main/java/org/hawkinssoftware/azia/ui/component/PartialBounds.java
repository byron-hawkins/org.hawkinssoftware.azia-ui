package org.hawkinssoftware.azia.ui.component;

import org.hawkinssoftware.azia.core.role.UserInterfaceDomains.DisplayBoundsDomain;
import org.hawkinssoftware.rns.core.publication.InvocationConstraint;

@InvocationConstraint(domains = DisplayBoundsDomain.class)
public class PartialBounds
{
	public final Integer x;
	public final Integer y;
	public final Integer width;
	public final Integer height;

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

	public EnclosureBounds applyValues(EnclosureBounds destination)
	{
		return new EnclosureBounds(x == null ? destination.x : x, y == null ? destination.y : y, width == null ? destination.width : width,
				height == null ? destination.height : height);
	}
}
