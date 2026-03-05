package com.marcosmoreiradev.uensdesktop.ui.view;

import com.marcosmoreiradev.uensdesktop.nav.ViewId;
import com.marcosmoreiradev.uensdesktop.ui.assets.ViewArtworkSupport;
import com.marcosmoreiradev.uensdesktop.ui.effects.DrawerBackdropSupport;
import com.marcosmoreiradev.uensdesktop.ui.tooltip.TooltipSupport;
import javafx.scene.Parent;

public final class ViewEnhancer {

    private ViewEnhancer() {
    }

    public static void enhance(Parent root, ViewId viewId) {
        TooltipSupport.installSecondaryTooltips(root);
        ViewArtworkSupport.applyToView(root, viewId);
        DrawerBackdropSupport.enhance(root);
    }
}
