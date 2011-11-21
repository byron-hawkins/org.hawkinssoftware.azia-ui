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
package org.hawkinssoftware.azia.ui.paint;

import java.awt.Font;
import java.awt.font.FontRenderContext;
import java.awt.geom.Rectangle2D;

import javax.swing.JLabel;

import org.hawkinssoftware.azia.core.layout.Axis;
import org.hawkinssoftware.azia.core.layout.BoundedEntity;
import org.hawkinssoftware.azia.core.role.UserInterfaceDomains.AssemblyDomain;
import org.hawkinssoftware.azia.core.role.UserInterfaceDomains.DisplayBoundsDomain;
import org.hawkinssoftware.azia.core.role.UserInterfaceDomains.RenderingDomain;
import org.hawkinssoftware.azia.ui.component.AbstractComponent;
import org.hawkinssoftware.azia.ui.component.ComponentAssembly;
import org.hawkinssoftware.azia.ui.paint.canvas.Size;
import org.hawkinssoftware.rns.core.publication.InvocationConstraint;
import org.hawkinssoftware.rns.core.publication.VisibilityConstraint;
import org.hawkinssoftware.rns.core.role.DomainRole;
import org.hawkinssoftware.rns.core.util.UnknownEnumConstantException;

/**
 * DOC comment task awaits.
 * 
 * @param <ComponentType>
 *            the generic type
 * @author Byron Hawkins
 */
@InvocationConstraint(domains = RenderingDomain.class)
@VisibilityConstraint(domains = RenderingDomain.class, types = ComponentAssembly.class)
@DomainRole.Join(membership = { RenderingDomain.class, DisplayBoundsDomain.class })
public interface InstancePainter<ComponentType extends AbstractComponent>
{

	/**
	 * DOC comment task awaits.
	 * 
	 * @param <MarkerType>
	 *            the generic type
	 * @param <PainterType>
	 *            the generic type
	 * @author Byron Hawkins
	 */
	@InvocationConstraint(packages = InvocationConstraint.MY_PACKAGE, domains = AssemblyDomain.class)
	@VisibilityConstraint(packages = InvocationConstraint.MY_PACKAGE, domains = AssemblyDomain.class)
	public static final class Key<MarkerType, PainterType extends MarkerType>
	{
		public static <MarkerType, PainterType extends MarkerType> Key<MarkerType, PainterType> createKey(Class<MarkerType> marker,
				Class<PainterType> painterType)
		{
			return new Key<MarkerType, PainterType>(painterType);
		}

		public final Class<PainterType> painterType;

		private Key(Class<PainterType> painterType)
		{
			this.painterType = painterType;
		}
	}

	/**
	 * DOC comment task awaits.
	 * 
	 * @author Byron Hawkins
	 */
	@VisibilityConstraint(domains = { RenderingDomain.class, AssemblyDomain.class })
	public static final class TextMetrics
	{

		/**
		 * DOC comment task awaits.
		 * 
		 * @author Byron Hawkins
		 */
		public enum BoundsType
		{
			TEXT,
			GLYPH;
		}

		public static final TextMetrics INSTANCE = new TextMetrics();

		private final Font font = new JLabel().getFont();
		private final FontRenderContext renderContext = new FontRenderContext(null, true, true);
		private final int fontAscent = calculateFontBaselineForText();

		public Size getSize(String text, BoundsType type)
		{
			Rectangle2D bounds = getBounds(text, type);
			return new Size((int) Math.round(bounds.getWidth()), (int) Math.round(bounds.getHeight()));
		}

		public Rectangle2D getBounds(String text, BoundsType type)
		{
			switch (type)
			{
				case TEXT:
					return font.getStringBounds(text, renderContext);
				case GLYPH:
					return font.createGlyphVector(renderContext, text).getVisualBounds();
				default:
					throw new UnknownEnumConstantException(type);
			}
		}

		public int getFontAscent()
		{
			return fontAscent;
		}

		public int getTypicalBaseline(int height)
		{
			return fontAscent + (int) Math.round((height - getBounds("A", BoundsType.GLYPH).getHeight()) / 2);
		}

		private int calculateFontBaselineForText()
		{
			double height = font.createGlyphVector(renderContext, "A").getGlyphMetrics(0).getBounds2D().getHeight();
			return (int) Math.round(height);
		}
	}

	ComponentType getComponent();

	void setComponent(ComponentType component);

	void paint(ComponentType component);

	@InvocationConstraint(domains = DisplayBoundsDomain.class)
	int getPackedSize(Axis axis);

	@InvocationConstraint(domains = DisplayBoundsDomain.class)
	BoundedEntity.MaximumSize getMaxSize(Axis axis);
}
