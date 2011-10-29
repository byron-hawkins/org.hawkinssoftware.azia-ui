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
package org.hawkinssoftware.azia.ui.component.router;

import java.util.concurrent.atomic.AtomicInteger;

import org.hawkinssoftware.azia.ui.component.router.RouterImplementationGenerator.RouterType;
import org.hawkinssoftware.rns.core.aop.ClassLoadObserver.ObservedType;
import org.hawkinssoftware.rns.core.aop.ClassLoadObserver.TypeHierarchy;

/**
 * DOC comment task awaits.
 * 
 * @param <RouterImplementationType>
 *            the generic type
 * @author Byron Hawkins
 */
class GeneratedRouter<RouterImplementationType extends AbstractComponentRouter>
{
	
	/**
	 * DOC comment task awaits.
	 * 
	 * @author Byron Hawkins
	 */
	static class Definition implements RouterImplementationGenerator.InstanceDefinition
	{
		private static final AtomicInteger ROUTER_INDEX = new AtomicInteger();

		private final String instrumentedRouterSimpleName = INSTRUMENTED_ROUTER_SIMPLE_NAME + "_" + ROUTER_INDEX.incrementAndGet();
		private final RouterType routerType;
		private final ObservedType handlerType;
		private final String methodName;
		private final TypeHierarchy parameter;

		public Definition(RouterType routerType, ObservedType handlerType, String methodName, TypeHierarchy parameter)
		{
			this.routerType = routerType;
			this.handlerType = handlerType;
			this.methodName = methodName;
			this.parameter = parameter;
		}

		@Override
		public RouterType getRouterType()
		{
			return routerType;
		}

		@Override
		public TypeHierarchy getParameterType()
		{
			return parameter;
		}

		@Override
		public String getParameterClassname()
		{
			return parameter.qualifiedName;
		}

		@Override
		public boolean isHandlerInterface()
		{
			return handlerType.isInterface;
		}

		@Override
		public String getHandlerClassname()
		{
			return handlerType.typeHierarchy.qualifiedName;
		}

		@Override
		public String getHandlerMethodName()
		{
			return methodName;
		}

		@Override
		public String getInstrumentedRouterPackageName()
		{
			return PACKAGE_NAME;
		}

		@Override
		public String getInstrumentedRouterSimpleName()
		{
			return instrumentedRouterSimpleName;
		}
	}

	private static final String PACKAGE_NAME = RouterImplementationFactory.class.getPackage().getName();
	private static final String INSTRUMENTED_ROUTER_SIMPLE_NAME = "InstrumentedRouter";

	private final Definition definition;
	private final RouterImplementationType implementation;

	GeneratedRouter(Definition definition, RouterImplementationType implementation)
	{
		this.definition = definition;
		this.implementation = implementation;
	}

	Definition getDefinition()
	{
		return definition;
	}

	RouterImplementationType getImplementation()
	{
		return implementation;
	}
}
