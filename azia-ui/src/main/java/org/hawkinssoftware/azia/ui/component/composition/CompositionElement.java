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
package org.hawkinssoftware.azia.ui.component.composition;

import org.hawkinssoftware.azia.core.role.UserInterfaceDomains.AssemblyDomain;
import org.hawkinssoftware.azia.ui.component.composition.CompositionRegistry.CompositionInitializationDomain;
import org.hawkinssoftware.rns.core.aop.InitializationAspect;
import org.hawkinssoftware.rns.core.publication.InvocationConstraint;
import org.hawkinssoftware.rns.core.role.CoreDomains.InitializationDomain;
import org.hawkinssoftware.rns.core.role.DomainRole;

/**
 * A marker interface indicating that the implementor will participate in implicit composition, as instrumented
 * according to the <code>@InitializationAspect</code> and facilitated by the <code>CompositionRegistry</code>.
 * 
 * @author Byron Hawkins
 */
@InitializationAspect(agent = CompositionElement.Agent.class)
public interface CompositionElement
{
	/**
	 * A marker interface indicating that the implementor would like to be notified when its implicit composition
	 * sequence has been completed.
	 * 
	 * @author Byron Hawkins
	 */
	public interface Initializing extends CompositionElement
	{
		// TODO: might be nice to enforce a call to super on all of these, since I think the override is always
		// additive, never intending to replace what super was doing.
		@InvocationConstraint(domains = CompositionInitializationDomain.class)
		void compositionCompleted();
	}

	/**
	 * Instantiation of any <code>CompositionElement</code> is pointcut by its <code>@InitializationAspect</code> to
	 * this <code>Agent</code>, which simply delegates to the <code>CompositionRegistry</code>.
	 * 
	 * @author Byron Hawkins
	 */
	@InvocationConstraint(extendedTypes = CompositionElement.class)
	@DomainRole.Join(membership = { InitializationDomain.class, AssemblyDomain.class })
	public static class Agent implements InitializationAspect.Agent<CompositionElement>
	{
		public static final Agent INSTANCE = new Agent();

		@Override
		public void initialize(CompositionElement instance)
		{
			CompositionRegistry.register(instance);
		}
	}
}
