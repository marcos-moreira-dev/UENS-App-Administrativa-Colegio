package com.marcosmoreiradev.uensdesktop.ui.effects;

import com.marcosmoreiradev.uensdesktop.ui.fx.FxNodeTree;
import javafx.scene.Parent;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;

/**
 * Detects drawer panels in a view tree and wires the backdrop events that dim the shell behind
 * them.
 */
public final class DrawerBackdropSupport {

    private static final String INSTALLED_KEY = "drawerBackdropSupport.installed";

    private DrawerBackdropSupport() {
    }

    /**
     * Scans the view tree and installs backdrop support on every drawer panel that follows the
     * shared CSS convention.
     *
     * @param root root node of the view that may contain drawer panels
     */
    public static void enhance(Parent root) {
        FxNodeTree.visit(root, node -> {
            if (!(node instanceof Region drawerNode)) {
                return;
            }
            if (!drawerNode.getStyleClass().contains("drawer-panel")) {
                return;
            }
            if (!(drawerNode.getParent() instanceof StackPane host)) {
                return;
            }
            install(host, drawerNode);
        });
    }

    /**
     * Installs the visible-state listener that emits show/hide backdrop events for a drawer.
     *
     * @param host stack pane that hosts the drawer and receives the backdrop event
     * @param drawerNode drawer region that controls whether the backdrop should appear
     */
    private static void install(StackPane host, Region drawerNode) {
        if (Boolean.TRUE.equals(drawerNode.getProperties().get(INSTALLED_KEY))) {
            return;
        }
        drawerNode.visibleProperty().addListener((observable, oldValue, visible) -> host.fireEvent(new DrawerBackdropEvent(
                visible ? DrawerBackdropEvent.SHOW : DrawerBackdropEvent.HIDE,
                drawerNode,
                host)));
        drawerNode.getProperties().put(INSTALLED_KEY, Boolean.TRUE);
    }
}
