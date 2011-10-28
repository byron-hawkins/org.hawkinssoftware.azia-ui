package org.hawkinssoftware.azia.ui.component.scalar;

import org.hawkinssoftware.azia.core.layout.Axis;
import org.hawkinssoftware.azia.ui.component.composition.AbstractComposite;
import org.hawkinssoftware.azia.ui.paint.basic.scalar.ScrollPaneViewportPainter;
import org.hawkinssoftware.rns.core.publication.InvocationConstraint;
import org.hawkinssoftware.rns.core.role.DomainRole;

@InvocationConstraint(domains = ScrollPaneViewportComposite.ScrollPaneViewportDomain.class)
public class ScrollPaneViewportComposite<ViewportType extends ScrollPaneViewport, PainterType extends ScrollPaneViewportPainter<ViewportType>> extends
		AbstractComposite<ViewportType, PainterType>
{  
	public static class ScrollPaneViewportDomain extends DomainRole
	{
		@DomainRole.Instance
		public static final ScrollPaneViewportDomain INSTANCE = new ScrollPaneViewportDomain();
	}

	@InvocationConstraint
	public ScrollPaneViewportComposite(ViewportType component)
	{
		super(component);
	}

	public int getScrollableSpan(Axis axis)
	{
		return bounds.getSpan(axis) - getPainter().getStaticContentSpan(axis);
	}
}    
