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
package org.hawkinssoftware.azia.ui.input;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hawkinssoftware.azia.core.action.UserInterfaceActor;
import org.hawkinssoftware.azia.core.action.UserInterfaceNotification;
import org.hawkinssoftware.azia.core.action.UserInterfaceTransactionDomains.TransactionParticipant;
import org.hawkinssoftware.azia.core.role.UserInterfaceDomains.DisplayBoundsDomain;
import org.hawkinssoftware.azia.input.MouseInputEvent;
import org.hawkinssoftware.azia.input.MouseInputEvent.Change;
import org.hawkinssoftware.azia.ui.component.UserInterfaceHandler;
import org.hawkinssoftware.rns.core.publication.InvocationConstraint;
import org.hawkinssoftware.rns.core.role.DomainRole;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

/**
 * Excluded from its own `MouseEventDomain because a MouseAware is expected to delegate, and only those delegates belong
 * in the domain with its permission
 * 
 * @author b
 */
public interface MouseAware extends UserInterfaceActor
{
	
	/**
	 * DOC comment task awaits.
	 * 
	 * @author Byron Hawkins
	 */
	public static class MouseEventDomain extends DisplayBoundsDomain
	{
		@DomainRole.Instance
		public static final MouseEventDomain INSTANCE = new MouseEventDomain();
	}

	/**
	 * DOC comment task awaits.
	 * 
	 * @author Byron Hawkins
	 */
	@InvocationConstraint(domains = MouseEventDomain.class)
	public static class State
	{
		
		/**
		 * DOC comment task awaits.
		 * 
		 * @author Byron Hawkins
		 */
		public static class MouseFrame
		{
			private int id;
			private MouseInputEvent event;
			private long timestamp;

			MouseFrame()
			{
				id = -1;
			}

			public int id()
			{
				return id;
			}

			public MouseInputEvent event()
			{
				return event;
			}

			public long timestamp()
			{
				return timestamp;
			}
		}

		/**
		 * DOC comment task awaits.
		 * 
		 * @author Byron Hawkins
		 */
		static class Contact
		{
			final MouseAware entity;
			final int entryFrameId;
			final long timestamp;

			Contact(MouseAware entity, int entryFrameId, long timestamp)
			{
				this.entity = entity;
				this.entryFrameId = entryFrameId;
				this.timestamp = timestamp;
			}
		}

		private static final int FRAME_COUNT = 20;

		private final MouseFrame[] frames = new MouseFrame[FRAME_COUNT];

		private final Map<MouseAware, Contact> contactByEntity = new HashMap<MouseAware, Contact>();
		private final List<MouseAware> contactedThisFrame = new ArrayList<MouseAware>();
		private final List<MouseAware> contactProcessing = new ArrayList<MouseAware>();
		private final Multimap<MouseInputEvent.Button, MouseAware> pressedEntitiesByButton = ArrayListMultimap.create();

		private int currentFrameIndex;

		public State()
		{
			for (int i = 0; i < frames.length; i++)
			{
				frames[i] = new MouseFrame();
			}
		}

		public MouseInputEvent event()
		{
			return frames[currentFrameIndex].event;
		}

		public MouseFrame getFrame(int frameId)
		{
			if ((frames[currentFrameIndex].id - frameId) < FRAME_COUNT)
			{
				return getRecentFrame(frames[currentFrameIndex].id - frameId);
			}
			else
			{
				return null;
			}
		}

		public MouseFrame getRecentFrame(int stepsBack)
		{
			int index = currentFrameIndex - stepsBack;
			if (index < 0)
			{
				index += FRAME_COUNT;
			}

			if (frames[index].id < 0)
			{
				return null;
			}

			return frames[index];
		}

		public boolean wasInContact(MouseAware entity)
		{
			Contact contact = contactByEntity.get(entity);
			if (contact == null)
			{
				return false;
			}
			return contact.entryFrameId < frames[currentFrameIndex].id;
		}

		public void newMouseFrame(MouseInputEvent event)
		{
			int newId = frames[currentFrameIndex].id + 1;
			currentFrameIndex = (currentFrameIndex + 1) % FRAME_COUNT;
			frames[currentFrameIndex].id = newId;
			frames[currentFrameIndex].event = event;
			frames[currentFrameIndex].timestamp = System.currentTimeMillis();
		}

		public void contact(MouseAware entity)
		{
			contactedThisFrame.add(entity);
			if (!contactByEntity.containsKey(entity))
			{
				contactByEntity.put(entity, new Contact(entity, frames[currentFrameIndex].id, frames[currentFrameIndex].timestamp));
			}
		}

		/**
		 * DOC comment task awaits.
		 * 
		 * @author Byron Hawkins
		 */
		public static class FrameConclusion
		{
			public final List<MouseAware> satellitePositionForwards = new ArrayList<MouseAware>();
			public final List<MouseAware> terminations = new ArrayList<MouseAware>();
		}

		public void prepareFrameConclusion(EventPass pass, FrameConclusion conclusion)
		{
			// forward position changes to all entities that were clicked, until the clicked button is released
			if (pass.event().changes().contains(Change.POSITION))
			{
				for (MouseInputEvent.Button button : MouseInputEvent.Button.values())
				{
					for (MouseAware entity : pressedEntitiesByButton.get(button))
					{
						if (!contactedThisFrame.contains(entity))
						{
							conclusion.satellitePositionForwards.add(entity);
						}
					}
				}
			}

			contactProcessing.clear();
			contactProcessing.addAll(contactByEntity.keySet());
			contactProcessing.removeAll(contactedThisFrame);
			for (MouseAware notContacted : contactProcessing)
			{
				// TODO: if a dragged entity continues to report contact, it will not receive this termination call
				// until the mouse button is released. It may want a mouse exit event, though of course it can calculate
				// that easily
				conclusion.terminations.add(notContacted);
				contactByEntity.remove(notContacted);
			}
		}

		public void closeFrame(EventPass pass)
		{
			// memoize the entities contacted by every mouse button press
			MouseInputEvent.Button pressed = pass.event().getButtonPress();
			if (pressed != null)
			{
				pressedEntitiesByButton.putAll(pressed, contactedThisFrame);
			}
			// terminate "button down" for all entities clicked by that button that do not contain the mouse anymore
			MouseInputEvent.Button released = pass.event().getButtonRelease();
			if (released != null)
			{
				pressedEntitiesByButton.removeAll(released);
			}

			contactedThisFrame.clear();
		}
	}

	/**
	 * DOC comment task awaits.
	 * 
	 * @author Byron Hawkins
	 */
	public static final class Contact extends UserInterfaceNotification
	{
		private final MouseAware entity;

		public Contact(MouseAware entity)
		{
			this.entity = entity;
		}

		@InvocationConstraint(domains = MouseEventDomain.class)
		public MouseAware getEntity()
		{
			return entity;
		}
	}

	/**
	 * DOC comment task awaits.
	 * 
	 * @author Byron Hawkins
	 */
	public static final class Forward extends UserInterfaceNotification
	{
		private final MouseAware entity;

		public Forward(MouseAware entity)
		{
			this.entity = entity;
		}

		@InvocationConstraint(domains = MouseEventDomain.class)
		public MouseAware getEntity()
		{
			return entity;
		}
	}

	/**
	 * DOC comment task awaits.
	 * 
	 * @author Byron Hawkins
	 */
	@DomainRole.Join(membership = MouseEventDomain.class)
	public static abstract class EventPass extends UserInterfaceNotification
	{
		public abstract MouseInputEvent event();

		public abstract boolean wasInContact(MouseAware entity);
	}

	/**
	 * DOC comment task awaits.
	 * 
	 * @author Byron Hawkins
	 */
	@DomainRole.Join(membership = MouseEventDomain.class)
	public static abstract class EventPassTermination extends UserInterfaceNotification
	{
		public abstract MouseInputEvent event();
	}

	/**
	 * DOC comment task awaits.
	 * 
	 * @author Byron Hawkins
	 */
	@DomainRole.Join(membership = { TransactionParticipant.class, DisplayBoundsDomain.class })
	public interface MouseHandler extends UserInterfaceHandler
	{
		void mouseStateChange(EventPass pass, PendingTransaction transaction);

		void mouseStateTerminated(EventPassTermination termination, PendingTransaction transaction);
	}
}
