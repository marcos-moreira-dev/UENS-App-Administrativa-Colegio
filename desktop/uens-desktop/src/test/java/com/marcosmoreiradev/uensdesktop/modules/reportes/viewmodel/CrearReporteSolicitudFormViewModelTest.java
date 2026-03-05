package com.marcosmoreiradev.uensdesktop.modules.reportes.viewmodel;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class CrearReporteSolicitudFormViewModelTest {

    @Test
    void startsWithContextExpectedByReportesScreen() {
        CrearReporteSolicitudFormViewModel viewModel = new CrearReporteSolicitudFormViewModel();

        assertThat(viewModel.bannerMessageProperty().get()).isEmpty();
        assertThat(viewModel.loadingProperty().get()).isFalse();
        assertThat(viewModel.seccionVisibleProperty().get()).isTrue();
        assertThat(viewModel.parcialVisibleProperty().get()).isFalse();
        assertThat(viewModel.canSubmitBinding().get()).isTrue();
    }

    @Test
    void loadingBlocksSubmissionWhileBackendWorkIsRunning() {
        CrearReporteSolicitudFormViewModel viewModel = new CrearReporteSolicitudFormViewModel();

        viewModel.loadingProperty().set(true);

        assertThat(viewModel.canSubmitBinding().get()).isFalse();
    }
}
