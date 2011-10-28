package org.hawkinssoftware.azia.ui.component.router;

import java.util.concurrent.atomic.AtomicInteger;

import org.hawkinssoftware.azia.ui.component.router.RouterImplementationGenerator.RouterType;
import org.hawkinssoftware.rns.core.aop.ClassLoadObserver.ObservedType;
import org.hawkinssoftware.rns.core.aop.ClassLoadObserver.TypeHierarchy;

class GeneratedRouter<RouterImplementationType extends AbstractComponentRouter>
{
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
