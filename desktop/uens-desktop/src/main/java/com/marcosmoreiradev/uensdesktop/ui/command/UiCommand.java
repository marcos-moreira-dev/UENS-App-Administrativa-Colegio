package com.marcosmoreiradev.uensdesktop.ui.command;

/**
 * Minimal command abstraction used to package UI actions behind an executable object.
 */
@FunctionalInterface
public interface UiCommand {

    /**
     * Executes the wrapped UI action.
     */
    void execute();
}
