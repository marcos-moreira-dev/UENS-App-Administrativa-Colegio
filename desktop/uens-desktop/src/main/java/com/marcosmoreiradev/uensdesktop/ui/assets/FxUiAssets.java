package com.marcosmoreiradev.uensdesktop.ui.assets;

import java.util.EnumMap;
import java.util.Map;
import java.util.Optional;
import javafx.scene.image.Image;
import javafx.scene.media.AudioClip;

public final class FxUiAssets {

    private static final Map<UiAssetId, Image> IMAGE_CACHE = new EnumMap<>(UiAssetId.class);
    private static final Map<UiAssetId, AudioClip> AUDIO_CACHE = new EnumMap<>(UiAssetId.class);

    private FxUiAssets() {
    }

    public static Optional<Image> image(UiAssetId assetId) {
        if (assetId == UiAssetId.LOGOUT_TICK) {
            return Optional.empty();
        }
        return Optional.ofNullable(IMAGE_CACHE.computeIfAbsent(assetId, FxUiAssets::loadImage));
    }

    public static Optional<AudioClip> audioClip(UiAssetId assetId) {
        if (assetId != UiAssetId.LOGOUT_TICK) {
            return Optional.empty();
        }
        return Optional.ofNullable(AUDIO_CACHE.computeIfAbsent(assetId, FxUiAssets::loadAudioClip));
    }

    private static Image loadImage(UiAssetId assetId) {
        return UiAssetCatalog.resourcePath(assetId)
                .map(FxUiAssets.class::getResource)
                .map(url -> new Image(url.toExternalForm()))
                .orElse(null);
    }

    private static AudioClip loadAudioClip(UiAssetId assetId) {
        return UiAssetCatalog.resourcePath(assetId)
                .map(FxUiAssets.class::getResource)
                .map(url -> new AudioClip(url.toExternalForm()))
                .orElse(null);
    }
}
