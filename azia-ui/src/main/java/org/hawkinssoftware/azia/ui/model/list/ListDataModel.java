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
package org.hawkinssoftware.azia.ui.model.list;

import java.util.ArrayList;
import java.util.List;

import org.hawkinssoftware.azia.core.action.UserInterfaceActor;
import org.hawkinssoftware.azia.core.action.UserInterfaceActorDelegate;
import org.hawkinssoftware.azia.core.action.UserInterfaceDirective;
import org.hawkinssoftware.azia.core.action.UserInterfaceNotification;
import org.hawkinssoftware.azia.core.action.UserInterfaceTransactionDomains.TransactionParticipant;
import org.hawkinssoftware.azia.core.role.UserInterfaceDomains.FlyweightCellDomain;
import org.hawkinssoftware.azia.ui.component.composition.CompositionElement;
import org.hawkinssoftware.azia.ui.component.composition.CompositionRegistry;
import org.hawkinssoftware.azia.ui.model.RowAddress;
import org.hawkinssoftware.azia.ui.model.RowAddress.Section;
import org.hawkinssoftware.rns.core.collection.AccessValidatingList;
import org.hawkinssoftware.rns.core.publication.InvocationConstraint;
import org.hawkinssoftware.rns.core.publication.VisibilityConstraint;
import org.hawkinssoftware.rns.core.role.DomainRole;
import org.hawkinssoftware.rns.core.util.UnknownEnumConstantException;

/**
 * DOC comment task awaits.
 * 
 * @author Byron Hawkins
 */
@VisibilityConstraint(domains = ListDataModel.ModelListDomain.class)
@InvocationConstraint(domains = ListDataModel.ModelListDomain.class)
@DomainRole.Join(membership = FlyweightCellDomain.class)
public class ListDataModel implements UserInterfaceActorDelegate, CompositionElement.Initializing
{
	
	/**
	 * DOC comment task awaits.
	 * 
	 * @author Byron Hawkins
	 */
	public static class ModelListDomain extends DomainRole
	{
		@DomainRole.Instance
		public static final ModelListDomain INSTANCE = new ModelListDomain();
	}

	/**
	 * DOC comment task awaits.
	 * 
	 * @author Byron Hawkins
	 */
	@DomainRole.Join(membership = FlyweightCellDomain.class)
	public interface ComponentContext
	{
		UserInterfaceActorDelegate getActor();

		RowAddress createAddress(int row, Section section);
	}

	/**
	 * DOC comment task awaits.
	 * 
	 * @author Byron Hawkins
	 */
	@VisibilityConstraint(domains = ModelListDomain.class)
	@DomainRole.Join(membership = { ModelListDomain.class, FlyweightCellDomain.class })
	public static class DataChangeNotification extends UserInterfaceNotification
	{
		
		/**
		 * DOC comment task awaits.
		 * 
		 * @author Byron Hawkins
		 */
		public enum Type
		{
			ADD,
			REPLACE,
			REMOVE,
			CHANGE;
		}

		public final Type type;
		public final Object datum;
		public final RowAddress address;

		DataChangeNotification(Type type, Object datum, RowAddress address)
		{
			this.type = type;
			this.datum = datum;
			this.address = address;
		}
	}

	/**
	 * DOC comment task awaits.
	 * 
	 * @author Byron Hawkins
	 */
	@VisibilityConstraint(domains = ModelListDomain.class)
	public static class StaticDataChangeNotification extends DataChangeNotification
	{
		StaticDataChangeNotification(Type type, Object datum, RowAddress address)
		{
			super(type, datum, address);
		}
	}

	/**
	 * DOC comment task awaits.
	 * 
	 * @author Byron Hawkins
	 */
	@VisibilityConstraint(domains = ModelListDomain.class)
	public static class ScrollableDataChangeNotification extends DataChangeNotification
	{
		ScrollableDataChangeNotification(Type type, Object datum, RowAddress address)
		{
			super(type, datum, address);
		}
	}

	/**
	 * DOC comment task awaits.
	 * 
	 * @author Byron Hawkins
	 */
	@VisibilityConstraint(domains = ModelListDomain.class)
	@InvocationConstraint(domains = { ModelListDomain.class, FlyweightCellDomain.class })
	@DomainRole.Join(membership = { ModelListDomain.class, FlyweightCellDomain.class })
	abstract class AbstractDataAction extends UserInterfaceDirective
	{
		final Object datum;
		final RowAddress address;

		AbstractDataAction(Object datum, RowAddress address)
		{
			super(ListDataModel.this.context.getActor());

			this.datum = datum;
			this.address = address;
		}

		Object getData()
		{
			return datum;
		}

		Section getSection()
		{
			return address.section;
		}

		@Override
		public UserInterfaceNotification createNotification()
		{
			DataChangeNotification.Type type = null;
			if (this instanceof AddAction)
			{
				type = DataChangeNotification.Type.ADD;
			}
			else if (this instanceof RemoveAction)
			{
				type = DataChangeNotification.Type.REMOVE;
			}
			else if (this instanceof ReplaceAction)
			{
				type = DataChangeNotification.Type.REPLACE;
			}
			else if (this instanceof ChangeDataAction)
			{
				type = DataChangeNotification.Type.CHANGE;
			}
			else
			{
				throw new IllegalStateException("No type assigned to data action " + getClass().getName());
			}

			if (address.section.isStatic())
			{
				return new StaticDataChangeNotification(type, datum, address);
			}
			else
			{
				return new ScrollableDataChangeNotification(type, datum, address);
			}
		}
	}

	/**
	 * DOC comment task awaits.
	 * 
	 * @author Byron Hawkins
	 */
	@DomainRole.Join(membership = FlyweightCellDomain.class)
	interface ModificationAction
	{
		RowAddress getAddress();
	}

	/**
	 * DOC comment task awaits.
	 * 
	 * @author Byron Hawkins
	 */
	@VisibilityConstraint(domains = ModelListDomain.class)
	public class AddAction extends AbstractDataAction
	{
		AddAction(Object datum, Section section)
		{
			super(datum, context.createAddress(-1, section));
		}

		AddAction(int row, Object datum, Section section)
		{
			super(datum, context.createAddress(row, section));
		}

		@Override
		public void commit()
		{
			if (address.row < 0)
			{
				getDataSection(address.section).add(datum);
			}
			else
			{
				getDataSection(address.section).add(address.row, datum);
			}
		}
	}

	/**
	 * DOC comment task awaits.
	 * 
	 * @author Byron Hawkins
	 */
	@VisibilityConstraint(domains = ModelListDomain.class)
	public class ReplaceAction extends AbstractDataAction implements ModificationAction
	{
		ReplaceAction(int row, Object datum, Section section)
		{
			super(datum, context.createAddress(row, section));
		}

		@Override
		public RowAddress getAddress()
		{
			return address;
		}

		@Override
		public void commit()
		{
			getDataSection(address.section).set(address.row, datum);
		}
	}

	/**
	 * DOC comment task awaits.
	 * 
	 * @author Byron Hawkins
	 */
	@VisibilityConstraint(domains = ModelListDomain.class)
	public class RemoveAction extends AbstractDataAction implements ModificationAction
	{
		RemoveAction(int row, Section section)
		{
			super(getDataSection(section).get(row), context.createAddress(row, section));
		}

		@Override
		public RowAddress getAddress()
		{
			return address;
		}

		@Override
		public void commit()
		{
			getDataSection(address.section).remove(address.row);
		}
	}

	/**
	 * DOC comment task awaits.
	 * 
	 * @param <DataType>
	 *            the generic type
	 * @author Byron Hawkins
	 */
	@VisibilityConstraint(domains = ModelListDomain.class)
	public class ChangeDataAction<DataType> extends AbstractDataAction
	{
		private final Section section;
		private final int row;
		private final DataChange<DataType> change;

		ChangeDataAction(Section section, int row, DataChange<DataType> change)
		{
			super(getDataSection(section).get(row), context.createAddress(row, section));

			this.section = section;
			this.row = row;
			this.change = change;
		}

		@SuppressWarnings("unchecked")
		@Override
		public void commit()
		{
			change.applyChange(section, row, (DataType) datum);
		}
	}

	/**
	 * DOC comment task awaits.
	 * 
	 * @param <DataType>
	 *            the generic type
	 * @author Byron Hawkins
	 */
	public class ChangeAllRowsAction<DataType> extends UserInterfaceDirective
	{
		private final DataChange<DataType> change;

		ChangeAllRowsAction(DataChange<DataType> change)
		{
			super(ListDataModel.this.context.getActor());

			this.change = change;
		}

		@SuppressWarnings("unchecked")
		@Override
		public void commit()
		{
			for (int i = 0; i < northData.size(); i++)
			{
				change.applyChange(Section.NORTH, i, (DataType) northData.get(i));
			}
			for (int i = 0; i < scrollableData.size(); i++)
			{
				change.applyChange(Section.SCROLLABLE, i, (DataType) scrollableData.get(i));
			}
			for (int i = 0; i < southData.size(); i++)
			{
				change.applyChange(Section.SOUTH, i, (DataType) southData.get(i));
			}
		}
	}

	/**
	 * DOC comment task awaits.
	 * 
	 * @param <DataType>
	 *            the generic type
	 * @author Byron Hawkins
	 */
	@DomainRole.Join(membership = { ModelListDomain.class, FlyweightCellDomain.class })
	public interface DataChange<DataType>
	{
		void applyChange(Section section, int row, DataType data);
	}

	/**
	 * DOC comment task awaits.
	 * 
	 * @author Byron Hawkins
	 */
	@DomainRole.Join(membership = { ModelListDomain.class, TransactionParticipant.class })
	public class Session
	{
		// WIP: at present, the client is responsible for relative index changes caused by `insert() and `remove().
		// Might be better to handle that internally.
		private final ListDataModelTransaction transaction;

		Session(ListDataModelTransaction transaction)
		{
			this.transaction = transaction;
		}

		public void add(Object datum)
		{
			add(datum, Section.SCROLLABLE);
		}

		public void add(Object datum, Section section)
		{
			transaction.addDataAction(new AddAction(datum, section));
		}

		public void insert(int row, Object datum, Section section)
		{
			// WIP: this needs to be aware of other actions in the transaction. It probably will be necessary to create
			// a pending copy of the entire model, apply changes directly to it and responding to queries (within the
			// same transaction) from it. But there can still be sequence confusion if rows are referred by position;
			// should probably require rows to be referred by a reliable ID.
			if (getDataSection(section).isEmpty())
			{
				add(datum, section);
			}
			else
			{
				transaction.addDataAction(new AddAction(row, datum, section));
			}
		}

		public void replace(int row, Object datum)
		{
			replace(row, datum, Section.SCROLLABLE);
		}

		public void replace(int row, Object datum, Section section)
		{
			transaction.addDataAction(new ReplaceAction(row, datum, section));
		}

		public void remove(int row)
		{
			remove(row, Section.SCROLLABLE);
		}

		public void remove(int row, Section section)
		{
			transaction.addDataAction(new RemoveAction(row, section));
		}

		public <DataType> void change(int row, Section section, DataChange<DataType> dataChange)
		{
			transaction.addDataAction(new ChangeDataAction<DataType>(section, row, dataChange));
		}

		public <DataType> void changeAllRows(DataChange<DataType> dataChange)
		{
			transaction.addFinalAction(new ChangeAllRowsAction<DataType>(dataChange));
		}
	}

	private final List<Object> scrollableData = AccessValidatingList.create(new ArrayList<Object>());
	private final List<Object> northData = AccessValidatingList.create(new ArrayList<Object>());
	private final List<Object> southData = AccessValidatingList.create(new ArrayList<Object>());

	private ComponentContext context;

	public Session createSession(ListDataModelTransaction transaction)
	{
		return new Session(transaction);
	}

	private List<Object> getDataSection(Section section)
	{
		switch (section)
		{
			case NORTH:
				return northData;
			case SOUTH:
				return southData;
			case SCROLLABLE:
				return scrollableData;
			default:
				throw new UnknownEnumConstantException(section);
		}
	}

	public Object get(RowAddress address)
	{
		return getDataSection(address.section).get(address.row);
	}

	public int getRowCount(Section section)
	{
		return getDataSection(section).size();
	}

	@Override
	public UserInterfaceActor getActor()
	{
		return context.getActor().getActor();
	}

	@Override
	public void compositionCompleted()
	{
		context = CompositionRegistry.getService(ComponentContext.class);
	}
}
