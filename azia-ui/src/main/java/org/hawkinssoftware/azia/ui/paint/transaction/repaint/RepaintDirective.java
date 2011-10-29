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
package org.hawkinssoftware.azia.ui.paint.transaction.repaint;

import java.util.Collection;

import org.hawkinssoftware.azia.core.role.UserInterfaceDomains.RenderingDomain;
import org.hawkinssoftware.azia.ui.component.PaintableActor;
import org.hawkinssoftware.azia.ui.component.PaintableActorDelegate;
import org.hawkinssoftware.rns.core.publication.InvocationConstraint;
import org.hawkinssoftware.rns.core.role.DomainRole;

/**
 * DOC comment task awaits.
 * 
 * @author Byron Hawkins
 */
@InvocationConstraint(packages = InvocationConstraint.MY_PACKAGE)
@DomainRole.Join(membership = RenderingDomain.class)
public abstract class RepaintDirective implements PaintableActorDelegate
{
	
	/**
	 * DOC comment task awaits.
	 * 
	 * @author Byron Hawkins
	 */
	@InvocationConstraint(packages = InvocationConstraint.MY_PACKAGE)
	public interface Host
	{
		void invokeTransactionRepaints(Collection<RepaintDirective> repaints);

		void applyTransactionRepaints();
	}

	// No access to the actor when it appears as a RepaintDirective
	@InvocationConstraint(types = RepaintRequestManager.class)
	public abstract PaintableActor getActor();

	public Object getInstanceKey()
	{
		return getActor();
	}
}
