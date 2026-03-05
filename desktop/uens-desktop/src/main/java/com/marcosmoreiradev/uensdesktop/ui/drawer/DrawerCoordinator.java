package com.marcosmoreiradev.uensdesktop.ui.drawer;

import java.util.LinkedHashSet;
import java.util.Set;
import javafx.scene.Node;

public final class DrawerCoordinator {

    private final Node drawerNode;
    private final Set<Node> sections = new LinkedHashSet<>();

    public DrawerCoordinator(Node drawerNode, Node... sections) {
        this.drawerNode = drawerNode;
        if (sections != null) {
            for (Node section : sections) {
                if (section != null) {
                    this.sections.add(section);
                }
            }
        }
    }

    public void hideAll() {
        setVisibleManaged(drawerNode, false);
        sections.forEach(section -> setVisibleManaged(section, false));
    }

    public void show() {
        setVisibleManaged(drawerNode, true);
    }

    public void showOnly(Node sectionToShow) {
        setVisibleManaged(drawerNode, true);
        for (Node section : sections) {
            setVisibleManaged(section, section == sectionToShow);
        }
    }

    public static void setVisibleManaged(Node node, boolean visible) {
        if (node == null) {
            return;
        }
        node.setVisible(visible);
        node.setManaged(visible);
    }
}
