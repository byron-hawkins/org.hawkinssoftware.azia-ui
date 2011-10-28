package org.hawkinssoftware.azia.ui.component;

import java.util.Collection;

public abstract class ComponentDataHandler implements UserInterfaceHandler
{
	public static class Key<HandlerType extends ComponentDataHandler>
	{
	}
	
	public interface Host
	{
		<HandlerType extends ComponentDataHandler> HandlerType getDataHandler(ComponentDataHandler.Key<HandlerType> key);

		Collection<ComponentDataHandler> getDataHandlers();
	}
	
	public final Key<?> key;

	protected ComponentDataHandler(Key<?> key)
	{
		this.key = key;
	}
}
