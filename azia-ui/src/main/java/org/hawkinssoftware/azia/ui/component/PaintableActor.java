package org.hawkinssoftware.azia.ui.component;

import org.hawkinssoftware.azia.core.action.UserInterfaceActor;
import org.hawkinssoftware.azia.ui.component.composition.CompositionElement;

public interface PaintableActor extends UserInterfaceActor, PaintableActorDelegate, CompositionElement
{
}
