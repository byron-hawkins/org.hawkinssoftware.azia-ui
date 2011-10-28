package org.hawkinssoftware.azia.ui.component;

import java.lang.reflect.Constructor;

import org.hawkinssoftware.azia.core.role.UserInterfaceDomains.AssemblyDomain;
import org.hawkinssoftware.azia.core.role.UserInterfaceDomains.RenderingDomain;
import org.hawkinssoftware.azia.ui.component.composition.AbstractComposite;
import org.hawkinssoftware.azia.ui.component.composition.CompositionRegistry;
import org.hawkinssoftware.azia.ui.paint.InstancePainter;
import org.hawkinssoftware.azia.ui.paint.transaction.repaint.RepaintDirective;
import org.hawkinssoftware.rns.core.role.DomainRole;

// WIP: build is still not finding all dependency deltas, e.g. putting this factory in the RenderingDomain revealed a need for AssemblyDomain permissions

@DomainRole.Join(membership = { RenderingDomain.class, AssemblyDomain.class })
class ComponentFactory
{
	private static class FactoryException extends RuntimeException
	{
		FactoryException(String message, Throwable cause)
		{
			super(message, cause);
		}
	}

	static <ComponentType extends AbstractComponent, PainterType extends InstancePainter<ComponentType>, EnclosureType extends ComponentEnclosure<ComponentType, PainterType>> EnclosureType createComponent(
			ComponentAssembly<ComponentType, PainterType, EnclosureType> assembly)
	{
		return createComponent(assembly, null);
	}

	@SuppressWarnings({ "unchecked" })
	static <ComponentType extends AbstractComponent, PainterType extends InstancePainter<ComponentType>, EnclosureType extends ComponentEnclosure<ComponentType, PainterType>> EnclosureType createComponent(
			ComponentAssembly<ComponentType, PainterType, EnclosureType> assembly, RepaintDirective.Host repaintHost)
	{
		try
		{
			boolean composing = false;
			if (AbstractComposite.class.isAssignableFrom(assembly.getEnclosure().enclosureType))
			{
				composing = true;
				CompositionRegistry.beginComposition((Class<? extends AbstractComposite<?, ?>>) assembly.getEnclosure().enclosureType);

				if (repaintHost != null)
				{
					CompositionRegistry.registerRepaintHost(repaintHost);
				}
			}

			ComponentType component = (ComponentType) assembly.getComponent().componentType.newInstance();

			Class<? extends EnclosureType> enclosureType = (Class<? extends EnclosureType>) assembly.getEnclosure().enclosureType;

			Constructor<? extends EnclosureType> enclosureConstructor = null;
			for (Constructor<?> constructor : enclosureType.getConstructors())
			{
				Class<?>[] parameterTypes = constructor.getParameterTypes();
				if ((parameterTypes.length == 1) && AbstractComponent.class.isAssignableFrom(parameterTypes[0]))
				{
					enclosureConstructor = (Constructor<? extends EnclosureType>) constructor;
					break;
				}
			}
			if (enclosureConstructor == null)
			{
				throwFailure(assembly.getComponent().componentType, "The constructor for " + enclosureType.getSimpleName() + " could not be found.", null);
			}

			EnclosureType enclosure = (EnclosureType) enclosureConstructor.newInstance(component);

			assembly.assemble(enclosure);

			if (composing)
			{
				CompositionRegistry.endComposition();
			}

			return enclosure;
		}
		catch (Throwable t)
		{
			if (t instanceof FactoryException)
			{
				throw (FactoryException) t;
			}
			throwFailure(assembly.getComponent().componentType, "An unrecognized exception occurred.", t);
			return null; // unreachable
		}
	}

	private static void throwFailure(Class<? extends AbstractComponent> componentType, String message, Throwable cause)
	{
		throw new FactoryException("Failed to construct a " + componentType.getSimpleName() + ": " + message, cause);
	}
}
