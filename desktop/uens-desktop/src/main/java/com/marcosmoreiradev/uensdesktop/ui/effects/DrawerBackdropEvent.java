package com.marcosmoreiradev.uensdesktop.ui.effects;

import javafx.event.Event;
import javafx.event.EventTarget;
import javafx.event.EventType;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;

public final class DrawerBackdropEvent extends Event {

    public static final EventType<DrawerBackdropEvent> ANY =
            new EventType<>(Event.ANY, "DRAWER_BACKDROP_ANY");
    public static final EventType<DrawerBackdropEvent> SHOW =
            new EventType<>(ANY, "DRAWER_BACKDROP_SHOW");
    public static final EventType<DrawerBackdropEvent> HIDE =
            new EventType<>(ANY, "DRAWER_BACKDROP_HIDE");

    private final Region drawerNode;
    private final StackPane originHost;

    public DrawerBackdropEvent(
            EventType<? extends Event> eventType,
            Region drawerNode,
            StackPane originHost) {
        super(eventType);
        this.drawerNode = drawerNode;
        this.originHost = originHost;
    }

    public Region drawerNode() {
        return drawerNode;
    }

    public StackPane originHost() {
        return originHost;
    }

    @Override
    public DrawerBackdropEvent copyFor(Object newSource, EventTarget newTarget) {
        return (DrawerBackdropEvent) super.copyFor(newSource, newTarget);
    }

    @Override
    public EventType<? extends DrawerBackdropEvent> getEventType() {
        return (EventType<? extends DrawerBackdropEvent>) super.getEventType();
    }
}
