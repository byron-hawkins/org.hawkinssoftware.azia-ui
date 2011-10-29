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
package org.hawkinssoftware.azia.ui.tile.transaction.modify;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hawkinssoftware.azia.core.action.InstantiationTask;
import org.hawkinssoftware.azia.core.action.LayoutTransaction;
import org.hawkinssoftware.azia.core.action.UserInterfaceActor.SynchronizationRole;
import org.hawkinssoftware.azia.core.action.UserInterfaceDirective;
import org.hawkinssoftware.azia.core.action.UserInterfaceNotification;
import org.hawkinssoftware.azia.core.action.UserInterfaceTransaction;
import org.hawkinssoftware.azia.core.layout.Axis;
import org.hawkinssoftware.azia.core.layout.BoundedEntity;
import org.hawkinssoftware.azia.core.layout.BoundedEntity.LayoutRoot;
import org.hawkinssoftware.azia.ui.component.AbstractComponent;
import org.hawkinssoftware.azia.ui.component.ComponentEnclosure;
import org.hawkinssoftware.azia.ui.component.DesktopWindow;
import org.hawkinssoftware.azia.ui.tile.ComponentTile;
import org.hawkinssoftware.azia.ui.tile.FloatingUnitTile;
import org.hawkinssoftware.azia.ui.tile.LayoutCloner;
import org.hawkinssoftware.azia.ui.tile.LayoutEntity;
import org.hawkinssoftware.azia.ui.tile.LayoutRegion.TileLayoutDomain;
import org.hawkinssoftware.azia.ui.tile.LayoutUnit;
import org.hawkinssoftware.azia.ui.tile.PairTile;
import org.hawkinssoftware.azia.ui.tile.TopTile;
import org.hawkinssoftware.azia.ui.tile.UnitTile;
import org.hawkinssoftware.rns.core.role.DomainRole;

/**
 * DOC comment task awaits.
 * 
 * @param <KeyType>
 *            the generic type
 * @author Byron Hawkins
 */
@DomainRole.Join(membership = TileLayoutDomain.class)
public class ModifyLayoutTransaction<KeyType extends LayoutEntity.Key<KeyType>> implements UserInterfaceTransaction, LayoutTransaction
{
	
	/**
	 * DOC comment task awaits.
	 * 
	 * @author Byron Hawkins
	 */
	@DomainRole.Join(membership = TileLayoutDomain.class)
	private class CreateWindowTask extends InstantiationTask.Producer<CreateWindowTask>
	{
		DesktopWindow<KeyType> window = null;
		private final KeyType key;
		private final DesktopWindow.FrameType frameType;
		private final String title;

		public CreateWindowTask(KeyType key, DesktopWindow.FrameType frameType, String title)
		{
			super(SynchronizationRole.AUTONOMOUS, "Window");

			this.key = key;
			this.frameType = frameType;
			this.title = title;
		}

		@Override
		protected void execute()
		{
			ModifyLayoutTransaction.this.original = new TopTile<KeyType>(key);
			window = new DesktopWindow<KeyType>(frameType, ModifyLayoutTransaction.this.original, title);
			modified = new TopTile<KeyType>(original.getKey());
		}
	}

	private TopTile<KeyType> original;
	private TopTile<KeyType> modified;
	private TopHandle topHandle;
	private Session session;

	private final Map<KeyType, Handle<? extends LayoutEntity<KeyType>>> transactionHandles = new HashMap<KeyType, Handle<? extends LayoutEntity<KeyType>>>();

	private final List<UserInterfaceDirective> transaction = new ArrayList<UserInterfaceDirective>();

	@Override
	public void setSession(Session session)
	{
		this.session = session;
	}

	public DesktopWindow<KeyType> createWindow(final KeyType key, DesktopWindow.FrameType frameType,  final String title)
	{
		DesktopWindow<KeyType> window = new CreateWindowTask(key, frameType, title).start().window;

		LayoutCloner.cloneLayout(original, modified);
		topHandle = new TopHandle(modified);

		return window;
	}

	@Override
	public LayoutRoot getLayoutRoot()
	{
		return modified;
	}

	public ComponentHandle createComponentTile(KeyType key)
	{
		ComponentTile<KeyType> container = new ComponentTile<KeyType>(key);
		modified.addEntity(container);

		ComponentHandle handle = new ComponentHandle(container);
		handle.setNew();
		transactionHandles.put(key, handle);
		return handle;
	}

	public UnitHandle createUnitTile(KeyType key)
	{
		UnitTile<KeyType> unit = new UnitTile<KeyType>(key);
		modified.addEntity(unit);

		UnitHandle handle = new UnitHandle(unit);
		handle.setNew();
		transactionHandles.put(key, handle);
		return handle;
	}

	public FloaterHandle createFloaterTile(KeyType key)
	{
		FloatingUnitTile<KeyType> floater = new FloatingUnitTile<KeyType>(key);
		modified.addEntity(floater);

		FloaterHandle handle = new FloaterHandle(floater);
		handle.setNew();
		transactionHandles.put(key, handle);
		return handle;
	}

	public PairHandle createPairTile(KeyType key, Axis axis)
	{
		PairTile<KeyType> pair = new PairTile<KeyType>(key, axis);
		modified.addEntity(pair);

		PairHandle handle = new PairHandle(pair);
		handle.setNew();
		transactionHandles.put(key, handle);
		return handle;
	}

	public TopHandle getTopHandle()
	{
		return topHandle;
	}

	@SuppressWarnings("unchecked")
	public ComponentHandle getComponent(KeyType key)
	{
		try
		{
			ComponentHandle handle = (ComponentHandle) transactionHandles.get(key);
			if (handle == null)
			{
				handle = new ComponentHandle((ComponentTile<KeyType>) modified.getEntity(key));
				transactionHandles.put(key, handle);
			}
			return handle;
		}
		catch (ClassCastException e)
		{
			throw new IllegalArgumentException("The LayoutEntity for key " + key.getName() + " must be a ComponentContainer, but it is found to be a "
					+ modified.getEntity(key).getClass().getName());
		}
	}

	@SuppressWarnings("unchecked")
	public UnitHandle getUnit(KeyType key)
	{
		try
		{
			UnitHandle handle = (UnitHandle) transactionHandles.get(key);
			if (handle == null)
			{
				handle = new UnitHandle((UnitTile<KeyType>) modified.getEntity(key));
				transactionHandles.put(key, handle);
			}
			return handle;
		}
		catch (ClassCastException e)
		{
			throw new IllegalArgumentException("The LayoutEntity for key " + key.getName() + " must be a UnitTile, but it is found to be a "
					+ modified.getEntity(key).getClass().getName());
		}
	}

	@SuppressWarnings("unchecked")
	public FloaterHandle getFloater(KeyType key)
	{
		try
		{
			FloaterHandle handle = (FloaterHandle) transactionHandles.get(key);
			if (handle == null)
			{
				handle = new FloaterHandle((FloatingUnitTile<KeyType>) modified.getEntity(key));
				transactionHandles.put(key, handle);
			}
			return handle;
		}
		catch (ClassCastException e)
		{
			throw new IllegalArgumentException("The LayoutEntity for key " + key.getName() + " must be a FloatingUnitTile, but it is found to be a "
					+ modified.getEntity(key).getClass().getName());
		}
	}

	@SuppressWarnings("unchecked")
	public PairHandle getPair(KeyType key)
	{
		try
		{
			PairHandle handle = (PairHandle) transactionHandles.get(key);
			if (handle == null)
			{
				handle = new PairHandle((PairTile<KeyType>) modified.getEntity(key));
				transactionHandles.put(key, handle);
			}
			return handle;
		}
		catch (ClassCastException e)
		{
			throw new IllegalArgumentException("The LayoutEntity for key " + key.getName() + " must be a PairTile, but it is found to be a "
					+ modified.getEntity(key).getClass().getName());
		}
	}

	public void assemble()
	{
		InstallNewLayoutDirective<KeyType> installCommand = new InstallNewLayoutDirective<KeyType>(original, modified);
		transaction.add(installCommand);

		session.postAction(installCommand);
		for (Handle<? extends LayoutEntity<KeyType>> handle : transactionHandles.values())
		{
			if (handle.handleObsolete)
			{
				throw new RuntimeException("Attempt to add an obsolete layout handle to a " + getClass().getSimpleName());
			}
			if (!handle.isConfigured())
			{
				throw new RuntimeException("Attempt to assemble a " + getClass().getSimpleName() + " with a " + handle.getClass().getSimpleName()
						+ " that is not completely configured.");
			}
			session.postAction(installCommand, handle.createNotification());
			handle.handleObsolete = true;
		}
	}

	@Override
	public void transactionIntroduced(Class<? extends UserInterfaceTransaction> introducedTransactionType)
	{
	}

	@Override
	public void postDirectResponse(UserInterfaceDirective... actions)
	{
		for (UserInterfaceDirective action : actions)
		{
			session.postAction(action);
		}
	}

	@Override
	public void postDirectResponse(UserInterfaceNotification... notifications)
	{
		for (UserInterfaceNotification notification : notifications)
		{
			session.postNotification(notification);
		}
	}

	@Override
	public void postNotificationFromAnotherTransaction(UserInterfaceNotification notification)
	{
	}

	// from TransactionRegistry only
	@Override
	public void commitTransaction()
	{
		for (UserInterfaceDirective action : transaction)
		{
			action.commit();
		}
	}

	@Override
	public boolean isEmpty()
	{
		return transaction.isEmpty();
	}

	/************* Handles *************/

	@DomainRole.Join(membership = TileLayoutDomain.class)
	private abstract class Handle<EntityType extends LayoutEntity<KeyType>>
	{
		boolean isNew = false;
		boolean handleObsolete = false;

		final EntityType entity;

		public Handle(EntityType entity)
		{
			this.entity = entity;
		}

		boolean isConfigured()
		{
			return entity.isConfigured();
		}

		void setNew()
		{
			this.isNew = true;
		}

		UserInterfaceNotification createNotification()
		{
			return new LayoutEntity.ChangeNotification(isNew, entity);
		}
	}

	/**
	 * DOC comment task awaits.
	 * 
	 * @author Byron Hawkins
	 */
	public class ComponentHandle extends Handle<ComponentTile<KeyType>>
	{
		public ComponentHandle(ComponentTile<KeyType> container)
		{
			super(container);
		}

		public void removeComponent()
		{
			entity.setComponent(null);
		}

		public void setComponent(ComponentEnclosure<? extends AbstractComponent, ?> component)
		{
			entity.setComponent(component);
		}
	}

	/**
	 * DOC comment task awaits.
	 * 
	 * @author Byron Hawkins
	 */
	public class TopHandle extends Handle<TopTile<KeyType>>
	{
		public TopHandle(TopTile<KeyType> unit)
		{
			super(unit);
		}

		public void removeUnit()
		{
			entity.setUnit((LayoutUnit<KeyType>) null);
		}

		public void setUnit(ComponentHandle component)
		{
			entity.setUnit(component.entity);
		}

		public void setUnit(PairHandle pair)
		{
			entity.setUnit(pair.entity);
		}
	}

	/**
	 * DOC comment task awaits.
	 * 
	 * @author Byron Hawkins
	 */
	public class UnitHandle extends Handle<UnitTile<KeyType>>
	{
		public UnitHandle(UnitTile<KeyType> unit)
		{
			super(unit);
		}

		public void removeUnit()
		{
			entity.setUnit(null);
		}

		// TODO: validate handle references and reject any belonging to some other transaction
		public void setUnit(ComponentHandle component)
		{
			entity.setUnit(component.entity);
		}

		public void setUnit(PairHandle pair)
		{
			entity.setUnit(pair.entity);
		}
		
		public void addFloater(FloaterHandle floater)
		{
			entity.addFloater(floater.entity);
		}

		public void setLayoutPolicy(Axis axis, UnitTile.Layout policy)
		{
			entity.setLayoutPolicy(axis, policy);
		}

		public void setPadding(int top, int right, int bottom, int left)
		{
			entity.setPadding(top, right, bottom, left);
		}
	}

	/**
	 * DOC comment task awaits.
	 * 
	 * @author Byron Hawkins
	 */
	public class FloaterHandle extends Handle<FloatingUnitTile<KeyType>>
	{
		public FloaterHandle(FloatingUnitTile<KeyType> entity)
		{
			super(entity);
		}

		public void setUnit(ComponentHandle component)
		{
			entity.setUnit(component.entity);
		}

		public void setUnit(PairHandle pair)
		{
			entity.setUnit(pair.entity);
		}
		
		public void setEdge(LayoutUnit.Floater.Edge edge)
		{
			entity.setEdge(edge);
		}

		public void setPadding(int top, int right, int bottom, int left)
		{
			entity.setPadding(top, right, bottom, left);
		}
	}

	/**
	 * DOC comment task awaits.
	 * 
	 * @author Byron Hawkins
	 */
	public class PairHandle extends Handle<PairTile<KeyType>>
	{
		public PairHandle(PairTile<KeyType> pair)
		{
			super(pair);
		}

		public void removeFirstTile()
		{
			entity.setFirst(null);
		}

		public void removeSecondTile()
		{
			entity.setSecond(null);
		}

		public void setFirstTile(UnitHandle unit)
		{
			entity.setFirst(unit.entity);
		}

		public void setFirstTile(PairHandle childPair)
		{
			entity.setFirst(childPair.entity);
		}

		public void setSecondTile(UnitHandle unit)
		{
			entity.setSecond(unit.entity);
		}

		public void setSecondTile(PairHandle childPair)
		{
			entity.setSecond(childPair.entity);
		}

		public void setCrossExpansionPolicy(BoundedEntity.Expansion policy)
		{
			entity.setCrossExpansion(policy);
		}
	}
}
