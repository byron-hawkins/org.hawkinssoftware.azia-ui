package org.hawkinssoftware.azia.ui.component.transaction.window;

import java.util.ArrayList;
import java.util.List;

import org.hawkinssoftware.azia.ui.component.ComponentEnclosure;
import org.hawkinssoftware.azia.ui.component.DesktopContainer;

class WindowFocusCycle
{
	final DesktopContainer<?> window;
	private int focusedComponentIndex = -1;
	private final List<ComponentEnclosure<?, ?>> focusableComponents = new ArrayList<ComponentEnclosure<?, ?>>();

	WindowFocusCycle(DesktopContainer<?> window)
	{
		this.window = window;
	}

	void add(ComponentEnclosure<?, ?> component)
	{
		if (focusableComponents.isEmpty())
		{
			focusedComponentIndex = 0;
		}

		focusableComponents.add(component);
	}

	ComponentEnclosure<?, ?> getFocusedComponent()
	{
		if (focusedComponentIndex < 0)
		{
			return null;
		}
		return focusableComponents.get(focusedComponentIndex);
	}

	ComponentEnclosure<?, ?> changeFocus(ComponentEnclosure<?, ?> focusedComponent)
	{
		ComponentEnclosure<?, ?> previousFocusedComponent = getFocusedComponent();
		focusedComponentIndex = focusableComponents.indexOf(focusedComponent);
		return previousFocusedComponent;
	}

	int getComponentCount()
	{
		return focusableComponents.size();
	}

	ComponentEnclosure<?, ?> getNextComponent()
	{
		int index = focusedComponentIndex + 1;
		if (index >= focusableComponents.size())
		{
			index = 0;
		}
		return focusableComponents.get(index);
	}

	ComponentEnclosure<?, ?> getPreviousComponent()
	{
		int index = focusedComponentIndex - 1;
		if (index < 0)
		{
			index = focusableComponents.size() - 1;
		}
		return focusableComponents.get(index);
	}
	
	void requestRepaintAll()
	{
		for (ComponentEnclosure<?, ?> component : focusableComponents)
		{
			component.getComponent().requestRepaint();
		}
	}
}
