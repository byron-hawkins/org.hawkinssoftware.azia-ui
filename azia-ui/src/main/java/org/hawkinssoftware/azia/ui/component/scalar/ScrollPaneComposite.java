package org.hawkinssoftware.azia.ui.component.scalar;

import org.hawkinssoftware.azia.core.layout.Axis;
import org.hawkinssoftware.azia.core.role.UserInterfaceDomains.AssemblyDomain;
import org.hawkinssoftware.azia.ui.component.composition.AbstractComposite;
import org.hawkinssoftware.azia.ui.component.scalar.handler.ScrollPaneScrollbarContributor;
import org.hawkinssoftware.azia.ui.component.scalar.handler.ScrollPaneViewportContributor;
import org.hawkinssoftware.azia.ui.paint.basic.scalar.ScrollPanePainter;
import org.hawkinssoftware.azia.ui.paint.transaction.repaint.RepaintInstanceDirective;
import org.hawkinssoftware.rns.core.publication.InvocationConstraint;
import org.hawkinssoftware.rns.core.role.DomainRole;
import org.hawkinssoftware.rns.core.util.UnknownEnumConstantException;

@DomainRole.Join(membership = ScrollPaneComposite.ScrollPaneDomain.class)
public class ScrollPaneComposite<ViewportType extends ScrollPaneViewportComposite<?, ?>> extends AbstractComposite<ScrollPane, ScrollPanePainter>
{
	public static class ScrollPaneDomain extends DomainRole
	{
		@DomainRole.Instance
		public static final ScrollPaneDomain INSTANCE = new ScrollPaneDomain();
	}

	private final ScrollPaneScrollbarContributor horizontalScrollbarContributor = new ScrollPaneScrollbarContributor(this, Axis.H);
	private final ScrollPaneScrollbarContributor verticalScrollbarContributor = new ScrollPaneScrollbarContributor(this, Axis.V);
	private final ScrollPaneViewportContributor viewportContributor = new ScrollPaneViewportContributor(this);

	private SliderComposite<ScrollSlider> horizontalScrollbar;
	private SliderComposite<ScrollSlider> verticalScrollbar;
	private ViewportType viewport;

	private final RepaintInstanceDirective repaint = new RepaintInstanceDirective(this.getComponent());

	@InvocationConstraint
	public ScrollPaneComposite(ScrollPane component)
	{
		super(component);
	}

	public SliderComposite<ScrollSlider> getHorizontalScrollbar()
	{
		return horizontalScrollbar;
	}

	public SliderComposite<ScrollSlider> getVerticalScrollbar()
	{
		return verticalScrollbar;
	}

	public ViewportType getViewport()
	{
		return viewport;
	}

	@InvocationConstraint(domains = { ScrollPaneComposite.ScrollPaneDomain.class, AssemblyDomain.class })
	public void setScrollbar(SliderComposite<ScrollSlider> scrollbar)
	{
		Axis axis = scrollbar.getComponent().getAxis();
		if (getScrollbar(axis) != null)
		{
			getScrollbar(axis).removeHandler(getScrollbarContributor(axis));
		}
		switch (axis)
		{
			case H:
				horizontalScrollbar = scrollbar;
				break;
			case V:
				verticalScrollbar = scrollbar;
				break;
			default:
				throw new UnknownEnumConstantException(axis);
		}
		getScrollbar(axis).installHandler(getScrollbarContributor(axis));
	}

	@InvocationConstraint(domains = { ScrollPaneComposite.ScrollPaneDomain.class, AssemblyDomain.class })
	public void setViewport(ViewportType viewport)
	{
		if (viewport != null)
		{
			viewport.removeHandler(viewportContributor);
		}

		this.viewport = viewport;

		viewport.installHandler(viewportContributor);
	}

	public SliderComposite<ScrollSlider> getScrollbar(Axis axis)
	{
		switch (axis)
		{
			case H:
				return horizontalScrollbar;
			case V:
				return verticalScrollbar;
			default:
				throw new UnknownEnumConstantException(axis);
		}
	}

	private ScrollPaneScrollbarContributor getScrollbarContributor(Axis axis)
	{
		switch (axis)
		{
			case H:
				return horizontalScrollbarContributor;
			case V:
				return verticalScrollbarContributor;
			default:
				throw new UnknownEnumConstantException(axis);
		}
	}
}
