package com.marcosmoreiradev.uensdesktop.ui.assets;

import com.marcosmoreiradev.uensdesktop.nav.ViewId;
import com.marcosmoreiradev.uensdesktop.ui.fx.FxNodeTree;
import com.marcosmoreiradev.uensdesktop.common.constants.UiDefaults;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.image.ImageView;

public final class ViewArtworkSupport {

    private ViewArtworkSupport() {
    }

    public static void applyToView(Parent root, ViewId viewId) {
        FxUiAssets.image(UiAssetCatalog.artworkFor(viewId)).ifPresent(image ->
                FxNodeTree.visit(root, node -> applyImage(node, image)));
    }

    private static void applyImage(Node node, javafx.scene.image.Image image) {
        if (!(node instanceof ImageView imageView)) {
            return;
        }
        if (imageView.getStyleClass().contains("view-artwork-view")) {
            imageView.setImage(image);
            imageView.setOpacity(UiDefaults.DRAWER_ARTWORK_OPACITY);
        }
        if (imageView.getStyleClass().contains("sidebar-artwork-view")) {
            imageView.setImage(image);
            imageView.setOpacity(UiDefaults.SIDEBAR_ARTWORK_OPACITY);
        }
        if (imageView.getStyleClass().contains("panel-artwork-view")) {
            imageView.setImage(image);
            imageView.setOpacity(UiDefaults.PANEL_ARTWORK_OPACITY);
        }
    }
}
