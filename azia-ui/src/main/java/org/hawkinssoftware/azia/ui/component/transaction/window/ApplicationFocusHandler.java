package org.hawkinssoftware.azia.ui.component.transaction.window;

import java.util.HashMap;
import java.util.Map;

import org.hawkinssoftware.azia.core.action.InstantiationTask;
import org.hawkinssoftware.azia.core.log.AziaLogging.Tag;
import org.hawkinssoftware.azia.core.role.UserInterfaceDomains.AssemblyDomain;
import org.hawkinssoftware.azia.input.KeyboardInputEvent;
import org.hawkinssoftware.azia.input.KeyboardInputEvent.State;
import org.hawkinssoftware.azia.input.key.HardwareKey;
import org.hawkinssoftware.azia.ui.component.AbstractEventDispatch;
import org.hawkinssoftware.azia.ui.component.ComponentEnclosure;
import org.hawkinssoftware.azia.ui.component.ComponentRegistry;
import org.hawkinssoftware.azia.ui.component.DesktopContainer;
import org.hawkinssoftware.azia.ui.component.UserInterfaceHandler;
import org.hawkinssoftware.azia.ui.component.composition.CompositionElement;
import org.hawkinssoftware.azia.ui.component.composition.CompositionRegistry;
import org.hawkinssoftware.azia.ui.component.transaction.key.KeyEventDispatch;
import org.hawkinssoftware.azia.ui.component.transaction.key.KeyboardInputNotification;
import org.hawkinssoftware.azia.ui.component.transaction.state.SetFocusAction;
import org.hawkinssoftware.azia.ui.paint.transaction.repaint.RepaintInstanceDirective;
import org.hawkinssoftware.azia.ui.paint.transaction.repaint.RepaintRequestManager;
import org.hawkinssoftware.rns.core.log.Log;
import org.hawkinssoftware.rns.core.publication.InvocationConstraint;

// * repaint comps (lost + gained) on component focus change
public class ApplicationFocusHandler extends AbstractEventDispatch
{
	public interface CycleKeySelector
	{
		boolean isFocusCycleForward(KeyboardInputEvent event);

		boolean isFocusCycleBackward(KeyboardInputEvent event);
	}

	@InvocationConstraint(domains = AssemblyDomain.class)
	public static void install()
	{
		new InitializationTask().start();
	}

	private final StateHandler stateHandler = new StateHandler();
	private CycleKeySelector keySelector = new TabSelector();

	private DesktopContainer<?> focusedWindow;
	private Map<DesktopContainer<?>, WindowFocusCycle> windowFocusCycles = new HashMap<DesktopContainer<?>, WindowFocusCycle>();

	public ApplicationFocusHandler()
	{
		installHandler(stateHandler);
		KeyEventDispatch.getInstance().installHandler(stateHandler);
	}

	public void registerWindow(DesktopContainer<?> window)
	{
		establishWindowComponents(window);
		window.installHandler(stateHandler);
	}

	public void registerComponent(ComponentEnclosure<?, ?> enclosure)
	{
		establishWindowComponents(CompositionRegistry.getWindow(enclosure.getComponent())).add(enclosure);
	}

	private WindowFocusCycle establishWindowComponents(DesktopContainer<?> window)
	{
		WindowFocusCycle cycle = windowFocusCycles.get(window);
		if (cycle == null)
		{
			cycle = new WindowFocusCycle(window);
			windowFocusCycles.put(window, cycle);
		}
		return cycle;
	}

	public boolean applicationHasFocus()
	{
		return focusedWindow != null;
	}

	public boolean windowHasFocus(CompositionElement element)
	{
		return (focusedWindow == CompositionRegistry.getWindow(element));
	}

	public ComponentEnclosure<?, ?> getFocusedComponent(CompositionElement focusPeer)
	{
		WindowFocusCycle cycle = windowFocusCycles.get(CompositionRegistry.getWindow(focusPeer));
		if (cycle == null)
		{
			Log.out(Tag.WARNING, "Warning: attempt to get the focused component for a CompositionElement for which no focus cycle can be found: %s", focusPeer
					.getClass().getSimpleName());
			return null;
		}
		return cycle.getFocusedComponent();
	}

	public class StateHandler implements UserInterfaceHandler
	{
		public void windowFocused(WindowFocusAction focus)
		{
			if (focusedWindow != null)
			{
				requestRepaint(focusedWindow);
			}

			if (focus.focused)
			{
				focusedWindow = focus.window;
			}
			else if (focusedWindow == focus.window)
			{
				focusedWindow = null;
			}

			if (focusedWindow != null)
			{
				requestRepaint(focusedWindow);
			}
		}

		public void componentFocused(SetFocusAction action)
		{
			DesktopContainer<?> window = CompositionRegistry.getWindow(action.activate);
			WindowFocusCycle cycle = windowFocusCycles.get(window);
			changeFocus(cycle, action.activate);
		}

		public void keyEvent(KeyboardInputNotification key, PendingTransaction transaction)
		{
			if (focusedWindow == null)
			{
				return;
			}
			WindowFocusCycle cycle = windowFocusCycles.get(focusedWindow);

			if (cycle.getComponentCount() < 2)
			{
				return;
			}

			ComponentEnclosure<?, ?> nextComponent;
			if (keySelector.isFocusCycleForward(key.event))
			{
				nextComponent = cycle.getNextComponent();
			}
			else if (keySelector.isFocusCycleBackward(key.event))
			{
				nextComponent = cycle.getPreviousComponent();
			}
			else
			{
				return;
			}
			changeFocus(cycle, nextComponent);
		}

		private void requestRepaint(DesktopContainer<?> window)
		{
			WindowFocusCycle cycle = windowFocusCycles.get(window);
			if (cycle != null)
			{
				cycle.requestRepaintAll();
			}
		}

		private void changeFocus(WindowFocusCycle cycle, ComponentEnclosure<?, ?> component)
		{
			ComponentEnclosure<?, ?> previousFocusedComponent = cycle.changeFocus(component);
			if (previousFocusedComponent != null)
			{
				RepaintRequestManager.requestRepaint(new RepaintInstanceDirective(previousFocusedComponent.getComponent()));
			}
			RepaintRequestManager.requestRepaint(new RepaintInstanceDirective(component.getComponent()));
		}
	}

	private class TabSelector implements CycleKeySelector
	{
		@Override
		public boolean isFocusCycleBackward(KeyboardInputEvent event)
		{
			return ((event.state == State.DOWN) && (event.pressedHardwareKeys.size() == 2) && event.pressedHardwareKeys.contains(HardwareKey.SHIFT) && event.pressedHardwareKeys
					.contains(HardwareKey.TAB));
		}

		@Override
		public boolean isFocusCycleForward(KeyboardInputEvent event)
		{
			return ((event.state == State.DOWN) && (event.pressedHardwareKeys.size() == 1) && event.pressedHardwareKeys.contains(HardwareKey.TAB));
		}
	}

	/**
	 * DOC comment task awaits.
	 * 
	 * @author Byron Hawkins
	 */
	private static class InitializationTask extends InstantiationTask.StandaloneInstantiationTask
	{
		public InitializationTask()
		{
			super(SynchronizationRole.AUTONOMOUS, ApplicationFocusHandler.class.getSimpleName());
		}

		@Override
		protected void executeInTransaction()
		{
			ComponentRegistry.getInstance().installFocusHandler(new ApplicationFocusHandler());
		}
	}
}
