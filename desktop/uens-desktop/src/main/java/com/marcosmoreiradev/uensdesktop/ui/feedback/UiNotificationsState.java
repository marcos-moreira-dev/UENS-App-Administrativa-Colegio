package com.marcosmoreiradev.uensdesktop.ui.feedback;

import java.util.Optional;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

/**
 * Holds the latest transient notification so it can be pushed by services and consumed by the UI.
 */
public final class UiNotificationsState {

    private final ObjectProperty<FlashMessage> flashMessage = new SimpleObjectProperty<>();

    /**
     * Publishes an informational flash message.
     *
     * @param message text intended to inform without implying success or failure
     */
    public void pushInfo(String message) {
        flashMessage.set(new FlashMessage(FlashMessage.Level.INFO, message));
    }

    /**
     * Publishes a success flash message.
     *
     * @param message confirmation shown after a completed operation
     */
    public void pushSuccess(String message) {
        flashMessage.set(new FlashMessage(FlashMessage.Level.SUCCESS, message));
    }

    /**
     * Publishes a warning flash message.
     *
     * @param message text that highlights a non-fatal but relevant condition
     */
    public void pushWarning(String message) {
        flashMessage.set(new FlashMessage(FlashMessage.Level.WARNING, message));
    }

    /**
     * Publishes an error flash message.
     *
     * @param message text that should surface an operation failure
     */
    public void pushError(String message) {
        flashMessage.set(new FlashMessage(FlashMessage.Level.ERROR, message));
    }

    /**
     * Returns the current flash message and clears it so it is shown only once.
     *
     * @return optional with the pending message, or empty when there is nothing to consume
     */
    public Optional<FlashMessage> consume() {
        FlashMessage current = flashMessage.get();
        flashMessage.set(null);
        return Optional.ofNullable(current);
    }

    /**
     * Exposes the pending flash message as an observable property for bindings.
     *
     * @return property containing the latest notification snapshot
     */
    public ObjectProperty<FlashMessage> flashMessageProperty() {
        return flashMessage;
    }

    /**
     * Immutable notification payload consumed by toast/banner presenters.
     *
     * @param level semantic severity of the notification
     * @param message text that should be shown to the operator
     */
    public record FlashMessage(Level level, String message) {
        /**
         * Semantic levels supported by the desktop notification system.
         */
        public enum Level {
            INFO,
            SUCCESS,
            WARNING,
            ERROR
        }
    }
}
