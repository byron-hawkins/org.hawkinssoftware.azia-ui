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

import java.io.IOException;

import org.hawkinssoftware.azia.core.action.UserInterfaceDirective;
import org.hawkinssoftware.azia.core.action.UserInterfaceNotification;
import org.hawkinssoftware.azia.core.action.UserInterfaceTransaction.ActorBasedContributor.PendingTransaction;
import org.hawkinssoftware.azia.ui.component.UserInterfaceHandler;
import org.hawkinssoftware.azia.ui.component.router.RouterImplementationGenerator.RouterType;
import org.hawkinssoftware.rns.core.aop.ClassLoadObserver.ObservedMethod;
import org.hawkinssoftware.rns.core.aop.ClassLoadObserver.ObservedType;
import org.hawkinssoftware.rns.core.util.UnknownEnumConstantException;

/**
 * A factory for creating RouterImplementation objects.
 */
class RouterImplementationFactory
{
	private static RouterImplementationFactory INSTANCE;

	static RouterImplementationFactory getInstance()
	{
		synchronized (RouterImplementationFactory.class)
		{
			if (INSTANCE == null)
			{
				INSTANCE = new RouterImplementationFactory();
			}
		}
		return INSTANCE;
	}

	/**
	 * DOC comment task awaits.
	 * 
	 * @author Byron Hawkins
	 */
	private static class GeneratorDefinition implements RouterImplementationGenerator.BaseClassnames
	{
		@Override
		public String getRouterBaseClassname(RouterType routerType)
		{
			switch (routerType)
			{
				case DIRECTIVE:
					return ComponentDirectiveRouter.class.getName();
				case NOTIFICATION:
					return ComponentNotificationRouter.class.getName();
				default:
					throw new UnknownEnumConstantException(routerType);
			}
		}

		@Override
		public String getTransactionElementBaseClassname(RouterType routerType)
		{
			switch (routerType)
			{
				case DIRECTIVE:
					return UserInterfaceDirective.class.getName();
				case NOTIFICATION:
					return UserInterfaceNotification.class.getName();
				default:
					throw new UnknownEnumConstantException(routerType);
			}
		}

		@Override
		public String getHandlerBaseClassname()
		{
			return UserInterfaceHandler.class.getName();
		}

		@Override
		public String getTransactionClassname()
		{
			return TRANSACTION_CLASS.getName();
		}
	}

	/**
	 * DOC comment task awaits.
	 * 
	 * @author Byron Hawkins
	 */
	private class ClassCreator extends ClassLoader
	{
		public ClassCreator()
		{
			super(AbstractComponentRouter.class.getClassLoader());
		}

		Class<?> createClass(String name, byte[] classfileBytes)
		{
			return defineClass(name, classfileBytes, 0, classfileBytes.length);
		}
	}

	private static final Class<?> TRANSACTION_CLASS = PendingTransaction.class;

	private final RouterImplementationGenerator generator = new RouterImplementationGenerator(new GeneratorDefinition());
	private final ClassCreator classCreator = new ClassCreator();

	@SuppressWarnings("unchecked")
	RouterImplementationSet instrumentRouter(ObservedType handlerType) throws InstantiationException, IllegalAccessException, IOException
	{
		RouterImplementationSet routers = new RouterImplementationSet();

		for (ObservedMethod method : handlerType.observedMethods)
		{
			GeneratedRouter.Definition definition = getInstrumentationDefinition(handlerType, method);
			if (definition == null)
			{
				continue;
			}

			byte[] classfile = generator.create(definition);
			Class<? extends AbstractComponentRouter> routerClass = (Class<? extends AbstractComponentRouter>) classCreator.createClass(
					definition.getInstrumentedRouterPackageName() + "." + definition.getInstrumentedRouterSimpleName(), classfile);
			AbstractComponentRouter routerInstance = routerClass.newInstance();
			GeneratedRouter<?> router = new GeneratedRouter<AbstractComponentRouter>(definition, routerInstance);
			routers.add(router);
		}

		return routers;
	}

	private GeneratedRouter.Definition getInstrumentationDefinition(ObservedType handlerType, ObservedMethod method)
	{
		if (method.parameters.length == 1)
		{
			return new GeneratedRouter.Definition(RouterImplementationGenerator.RouterType.DIRECTIVE, handlerType, method.name, method.parameters[0]);
		}
		else if (method.parameters.length == 2)
		{
			return new GeneratedRouter.Definition(RouterImplementationGenerator.RouterType.NOTIFICATION, handlerType, method.name, method.parameters[0]);
		}
		else
		{
			return null;
		}
	}
}
