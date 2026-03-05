package com.marcosmoreiradev.uensdesktop.ui.fx;

import java.util.function.Consumer;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.ScrollPane;

public final class FxNodeTree {

    private FxNodeTree() {
    }

    public static void visit(Node root, Consumer<Node> visitor) {
        visitor.accept(root);
        if (root instanceof ScrollPane scrollPane && scrollPane.getContent() != null) {
            visit(scrollPane.getContent(), visitor);
        }
        if (root instanceof Parent parent) {
            for (Node child : parent.getChildrenUnmodifiable()) {
                if (!(root instanceof ScrollPane) || child != ((ScrollPane) root).getContent()) {
                    visit(child, visitor);
                }
            }
        }
    }
}
