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
 * 
 * @JTourBusStop 2, Homogenous initialization using @InitializationAspect, Simulating a constructor for the
 *               CompositionElement interface:
 * 
 *               The @InitializationAspect annotation is recognized by the RNS bytecode instrumentation agent, which
 *               creates a pointcut in every CompositionElement implementor's constructor to
 *               CompositionElement.Agent.initialize(). This is not quite as effective as having an actual constructor
 *               for CompositionElement, fully governed by the JVM, but it simulates the essential behavior. The
 *               pointcut is inserted just before every constructor exit point, so it is guaranteed to be executed on a
 *               fully inflated instance of every CompositionElement while it remains under JVM constructor supervision.
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
	 * 
	 * @JTourBusStop 5, Homogenous initialization using @InitializationAspect, Conclusion:
	 * 
	 *               Annotating a constructor pointcut to an initialization agent isn't quite as tidy as an actual
	 *               constructor, but the code is every bit as clean, and in some ways more consistent. A class can have
	 *               many constructors, any of which can be skipped in a particular instantiation, but this
	 *               initialization universally routes to only one method of the initialization agent.
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
