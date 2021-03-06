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
import org.hawkinssoftware.azia.ui.component.AbstractComponent;
import org.hawkinssoftware.azia.ui.component.ComponentAssembly;
import org.hawkinssoftware.azia.ui.component.ComponentEnclosure;
import org.hawkinssoftware.azia.ui.component.ComponentRegistry;
import org.hawkinssoftware.azia.ui.component.composition.CompositeAssembly;
import org.hawkinssoftware.azia.ui.component.scalar.handler.ScrollPaneMouseHandler;
import org.hawkinssoftware.azia.ui.component.scalar.handler.ScrollPanePaintHandler;
import org.hawkinssoftware.azia.ui.component.scalar.handler.ScrollPaneResizeHandler;
import org.hawkinssoftware.azia.ui.component.scalar.handler.ScrollPaneSizeDelegate;
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
public class ScrollPane extends AbstractComponent
{
	
	/**
	 * DOC comment task awaits.
	 * 
	 * @param <ViewportCompositeType>
	 *            the generic type
	 * @param <ScrollPaneType>
	 *            the generic type
	 * @author Byron Hawkins
	 */
	public static class Assembly<ViewportCompositeType extends ScrollPaneViewportComposite<?, ?>, ScrollPaneType extends ScrollPaneComposite<ViewportCompositeType>>
			extends CompositeAssembly<ScrollPane, ScrollPane.Painter, ScrollPaneType>
	{
		private final ScrollSlider.Assembly<SliderComposite<ScrollSlider>> horizontalScrollbarAssembly = new ScrollSlider.Assembly<SliderComposite<ScrollSlider>>(
				Axis.H);
		private final ScrollSlider.Assembly<SliderComposite<ScrollSlider>> verticalScrollbarAssembly = new ScrollSlider.Assembly<SliderComposite<ScrollSlider>>(
				Axis.V);
		private final ComponentAssembly<? extends ScrollPaneViewport, ? extends ScrollPaneViewport.Painter, ?> viewportAssembly;

		public Assembly(Class<ScrollPaneType> enclosure,
				ComponentAssembly<? extends ScrollPaneViewport, ? extends ScrollPaneViewport.Painter, ?> viewportAssembly)
		{
			super(UserInterfaceActor.SynchronizationRole.AUTONOMOUS);

			this.viewportAssembly = viewportAssembly;

			setComponent(new AbstractComponent.Key<ScrollPane>(ScrollPane.class));
			setEnclosure(new ComponentEnclosure.Key(enclosure));
		}

		@SuppressWarnings("unchecked")
		@Override
		public void assemble(ScrollPaneType scrollPane)
		{    
			super.assemble(scrollPane); 
			
			scrollPane.setScrollbar(ComponentRegistry.getInstance().getComponent(horizontalScrollbarAssembly));
			scrollPane.setScrollbar(ComponentRegistry.getInstance().getComponent(verticalScrollbarAssembly));
			scrollPane.setViewport((ViewportCompositeType) ComponentRegistry.getInstance().getComponent(viewportAssembly));
			scrollPane.installHandler(new ScrollPaneMouseHandler(scrollPane));
			scrollPane.installHandler(new ScrollPanePaintHandler(scrollPane));
			scrollPane.installHandler(new ScrollPaneResizeHandler(scrollPane));
			scrollPane.installSizeDelegate(new ScrollPaneSizeDelegate(scrollPane));
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
	public ScrollPane()
	{
	}

	@Override
	public Expansion getExpansion(Axis axis)
	{
		return Expansion.FILL;
	}
}
