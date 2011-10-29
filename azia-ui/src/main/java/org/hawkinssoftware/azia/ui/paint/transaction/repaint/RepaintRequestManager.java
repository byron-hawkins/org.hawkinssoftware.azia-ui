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

import java.util.HashMap;
import java.util.Map;

import org.hawkinssoftware.azia.core.action.TransactionRegistryCoordinator;
import org.hawkinssoftware.azia.core.action.UserInterfaceTransaction;
import org.hawkinssoftware.azia.core.action.UserInterfaceTransactionDomains.TransactionParticipant;
import org.hawkinssoftware.azia.ui.AziaUserInterfaceInitializer;
import org.hawkinssoftware.azia.ui.component.composition.CompositionRegistry;
import org.hawkinssoftware.rns.core.publication.InvocationConstraint;
import org.hawkinssoftware.rns.core.role.DomainRole;

// TODO: make one of these for re-layout requests, and keep them in sequence in the TxnReg
/**
 * DOC comment task awaits.
 * 
 * @author Byron Hawkins
 */
@DomainRole.Join(membership = TransactionParticipant.class)
public class RepaintRequestManager
{
	
	/**
	 * DOC comment task awaits.
	 * 
	 * @author Byron Hawkins
	 */
	private static class TransactionHook implements UserInterfaceTransaction.PostProcessor
	{
		public void sessionStarting()
		{
			RepaintRequestManager manager = MANAGERS.get();
			manager.requestsByHostThenKey.clear();
			manager.transactionSessionActive = true;
		}

		public void sessionCommitted()
		{
			RepaintRequestManager manager = MANAGERS.get();
			for (RepaintDirective.Host host : manager.requestsByHostThenKey.keySet())
			{
				host.invokeTransactionRepaints(manager.requestsByHostThenKey.get(host).values());
			}
		}

		@Override
		public void postProcessingCommitted()
		{
			RepaintRequestManager manager = MANAGERS.get();
			for (RepaintDirective.Host host : manager.requestsByHostThenKey.keySet())
			{
				host.applyTransactionRepaints();
			}

			manager.transactionSessionActive = false;
		}

		@Override
		public void transactionRolledBack()
		{
			RepaintRequestManager manager = MANAGERS.get();
			manager.transactionSessionActive = false;
		}
	}

	public static void requestRepaint(RepaintInstanceDirective request)
	{
		MANAGERS.get().registerRequest((RepaintInstanceDirective) request);
	}

	public static void requestRepaint(RepaintAtomRequest request)
	{
		MANAGERS.get().registerRequest(request);
	}

	@InvocationConstraint(types = AziaUserInterfaceInitializer.class)
	public static void initialize()
	{
		TransactionRegistryCoordinator.getInstance().addPostProcessor(new TransactionHook());
	}

	private static final ThreadLocal<RepaintRequestManager> MANAGERS = new ThreadLocal<RepaintRequestManager>() {
		protected RepaintRequestManager initialValue()
		{
			return new RepaintRequestManager();
		}
	};

	private final Map<RepaintDirective.Host, Map<Object, RepaintDirective>> requestsByHostThenKey = new HashMap<RepaintDirective.Host, Map<Object, RepaintDirective>>();
	private boolean transactionSessionActive = false;

	// so I don't have a context for the instance requests, only an actor. When there is an actor, I don't want the
	// aggregate anymore, because it means I'm repainting the entire component. Should I just make two maps?

	private void registerRequest(RepaintInstanceDirective request)
	{
		if (!transactionSessionActive)
		{
			throw new IllegalStateException("Cannot register a repaint request on thread " + Thread.currentThread().getName()
					+ " because no transaction is currently in assembly.");
		}

		RepaintDirective.Host host = CompositionRegistry.getRepaintHost(request.getActor());
		Map<Object, RepaintDirective> hostRequests = requestsByHostThenKey.get(host);
		if (hostRequests == null)
		{
			hostRequests = new HashMap<Object, RepaintDirective>();
			requestsByHostThenKey.put(host, hostRequests);
		}

		hostRequests.put(request.getInstanceKey(), request);
	}

	private void registerRequest(RepaintAtomRequest request)
	{
		if (!transactionSessionActive)
		{
			throw new IllegalStateException("Cannot register a repaint request on thread " + Thread.currentThread().getName()
					+ " because no transaction is currently in assembly.");
		}

		RepaintDirective.Host requestHost = CompositionRegistry.getRepaintHost(request.getPaintedActor());

		Map<Object, RepaintDirective> hostRequests = requestsByHostThenKey.get(requestHost);
		if (hostRequests == null)
		{
			hostRequests = new HashMap<Object, RepaintDirective>();
			requestsByHostThenKey.put(requestHost, hostRequests);
		}

		RepaintDirective repaint = hostRequests.get(request.getAggregationKey());
		if (repaint == null)
		{
			RepaintAtomCollection aggregateRequest = new RepaintAtomCollection(request);
			hostRequests.put(aggregateRequest.getInstanceKey(), aggregateRequest);
		}
		else if (repaint instanceof RepaintAtomCollection)
		{
			((RepaintAtomCollection) repaint).add(request);
		}
		// else it's a RepaintComponentDirective, which will repaint the whole actor
	}
}
