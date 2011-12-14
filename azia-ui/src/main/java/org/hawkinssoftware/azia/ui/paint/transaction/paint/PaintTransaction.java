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
package org.hawkinssoftware.azia.ui.paint.transaction.paint;

import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.hawkinssoftware.azia.core.action.UserInterfaceActor;
import org.hawkinssoftware.azia.core.action.UserInterfaceActorDelegate;
import org.hawkinssoftware.azia.core.action.UserInterfaceDirective;
import org.hawkinssoftware.azia.core.action.UserInterfaceNotification;
import org.hawkinssoftware.azia.core.action.UserInterfaceTransaction;
import org.hawkinssoftware.azia.core.layout.BoundedEntity;
import org.hawkinssoftware.azia.core.log.AziaLogging.Tag;
import org.hawkinssoftware.azia.core.role.UserInterfaceDomains.RenderingDomain;
import org.hawkinssoftware.azia.ui.component.AbstractComponent;
import org.hawkinssoftware.azia.ui.component.ComponentEnclosure;
import org.hawkinssoftware.azia.ui.component.EnclosureBounds;
import org.hawkinssoftware.azia.ui.paint.AggregatePainter;
import org.hawkinssoftware.azia.ui.paint.PainterRegistry;
import org.hawkinssoftware.azia.ui.paint.canvas.Canvas;
import org.hawkinssoftware.azia.ui.paint.transaction.repaint.RepaintAtomCollection;
import org.hawkinssoftware.azia.ui.paint.transaction.repaint.RepaintDirective;
import org.hawkinssoftware.azia.ui.paint.transaction.repaint.RepaintRequestManager;
import org.hawkinssoftware.rns.core.log.Log;
import org.hawkinssoftware.rns.core.publication.InvocationConstraint;
import org.hawkinssoftware.rns.core.role.DomainRole;

/**
 * Internal transaction which executes painting of the screen. Operates either on an entire window, or on individual
 * repaint requests submitted to the <code>RepaintRequestManager</code>.
 * 
 * @author Byron Hawkins
 * @see RepaintRequestManager
 */
@DomainRole.Join(membership = RenderingDomain.class)
public class PaintTransaction implements UserInterfaceTransaction.Iterative
{
	private Graphics2D g;

	private Session session;

	private final List<PaintComponentNotification> paintComponentCommands = new ArrayList<PaintComponentNotification>();
	private final List<PaintRegionNotification> paintRegionCommands = new ArrayList<PaintRegionNotification>();
	private final List<RepaintAtomCollection> paintAtomCollections = new ArrayList<RepaintAtomCollection>();
	private final List<PaintIncludeNotification> includes = new ArrayList<PaintIncludeNotification>();

	@InvocationConstraint(domains = RenderingDomain.class)
	public void configure(Graphics2D g)
	{
		this.g = g;
	}

	private <ComponentType extends AbstractComponent> void paint(RepaintAtomCollection atomCollection)
	{
		ComponentEnclosure<AbstractComponent, AggregatePainter<AbstractComponent>> enclosure = atomCollection.getEnclosure();
		Canvas c = Canvas.get();

		c.pushBounds(enclosure.getBounds());
		enclosure.getPainter().paint(enclosure.getComponent(), atomCollection.getAtoms());
		c.popBounds();
	}

	@Override
	public void setSession(Session session)
	{
		this.session = session;
	}

	@Override
	public void addActionsOn(List<UserInterfaceDirective> actions, UserInterfaceActor actor)
	{
		// non-calculable
	}

	public void addRegion(BoundedEntity.PanelRegion region, EnclosureBounds bounds)
	{
		paintRegionCommands.add(new PaintRegionNotification(region, bounds));
	}

	@InvocationConstraint(domains = RenderingDomain.class)
	public void start(UserInterfaceActorDelegate actor)
	{
		session.postAction(actor, new PaintIncludeNotification(actor));
	}

	/**
	 * @JTourBusStop 10.2, Stack-based properties for java.awt.Graphics, Coordination of application repainting also
	 *               relies on the stack for integrity of the Graphics properties:
	 * 
	 *               ...and the repaint requests are simply executed in sequence, knowing that the stack will always
	 *               keep the original Graphics properties intact.
	 */
	@InvocationConstraint(domains = RenderingDomain.class)
	public void start(Collection<RepaintDirective> repaints)
	{
		for (RepaintDirective repaint : repaints)
		{
			if (repaint instanceof RepaintAtomCollection)
			{
				paintAtomCollections.add((RepaintAtomCollection) repaint);
			}
			else
			{
				session.postAction(repaint, new PaintIncludeNotification(repaint));
			}
		}
	}

	@Override
	public boolean hasMoreIterations()
	{
		return !includes.isEmpty();
	}

	@Override
	public void iterate()
	{
		for (PaintIncludeNotification include : includes)
		{
			session.postAction(include, include);
		}
		includes.clear();
	}

	@Override
	public void transactionIntroduced(Class<? extends UserInterfaceTransaction> introducedTransactionType)
	{
	}

	@Override
	public void postDirectResponse(UserInterfaceDirective... actions)
	{
	}

	@Override
	public void postDirectResponse(UserInterfaceNotification... notifications)
	{
		for (UserInterfaceNotification notification : notifications)
		{
			if (notification instanceof PaintComponentNotification)
			{
				paintComponentCommands.add((PaintComponentNotification) notification);
			}
			else if (notification instanceof PaintIncludeNotification)
			{
				includes.add((PaintIncludeNotification) notification);
			}
			else if (notification instanceof PaintRegionNotification)
			{
				paintRegionCommands.add((PaintRegionNotification) notification);
			}
		}
	}

	@Override
	public void postNotificationFromAnotherTransaction(UserInterfaceNotification notification)
	{
	}

	@Override
	public void commitTransaction()
	{
		try
		{
			Canvas c = Canvas.installExecutionContext(g);

			for (PaintRegionNotification paintRegion : paintRegionCommands)
			{
				c.pushBounds(paintRegion.bounds);
				PainterRegistry.getInstance().getPainter(paintRegion.region).paint(paintRegion.region);
				c.popBounds();
			}
			for (PaintComponentNotification paintComponent : paintComponentCommands)
			{
				c.pushBounds(paintComponent.bounds);
				PainterRegistry.getInstance().getPainter(paintComponent.component).paint(paintComponent.component);
				c.popBounds();
			}
			for (RepaintAtomCollection repaintAtoms : paintAtomCollections)
			{
				paint(repaintAtoms);
			}
		}
		catch (Throwable t)
		{
			Log.out(Tag.CRITICAL, t, "Failed to paint!");
		}
		finally
		{
			Canvas.removeExecutionContext();
		}
	}

	@Override
	public void transactionRolledBack()
	{
	}

	@Override
	public boolean isEmpty()
	{
		return paintRegionCommands.isEmpty() && paintComponentCommands.isEmpty() && paintAtomCollections.isEmpty();
	}
}
