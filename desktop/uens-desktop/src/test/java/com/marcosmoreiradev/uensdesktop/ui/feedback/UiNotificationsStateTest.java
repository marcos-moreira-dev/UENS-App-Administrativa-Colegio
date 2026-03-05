package com.marcosmoreiradev.uensdesktop.ui.feedback;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class UiNotificationsStateTest {

    @Test
    void consumeReturnsLastFlashMessageAndClearsIt() {
        UiNotificationsState notificationsState = new UiNotificationsState();
        notificationsState.pushWarning("Cupos casi agotados");

        UiNotificationsState.FlashMessage flashMessage = notificationsState.consume().orElseThrow();

        assertThat(flashMessage.level()).isEqualTo(UiNotificationsState.FlashMessage.Level.WARNING);
        assertThat(flashMessage.message()).isEqualTo("Cupos casi agotados");
        assertThat(notificationsState.consume()).isEmpty();
        assertThat(notificationsState.flashMessageProperty().get()).isNull();
    }

    @Test
    void laterNotificationsReplacePreviousOne() {
        UiNotificationsState notificationsState = new UiNotificationsState();
        notificationsState.pushInfo("Primero");
        notificationsState.pushSuccess("Guardado");

        UiNotificationsState.FlashMessage flashMessage = notificationsState.consume().orElseThrow();

        assertThat(flashMessage.level()).isEqualTo(UiNotificationsState.FlashMessage.Level.SUCCESS);
        assertThat(flashMessage.message()).isEqualTo("Guardado");
    }
}
