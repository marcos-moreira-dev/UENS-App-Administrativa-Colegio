package com.marcosmoreiradev.uensdesktop.modules.estudiantes.viewmodel;

import static org.assertj.core.api.Assertions.assertThat;

import com.marcosmoreiradev.uensdesktop.modules.estudiantes.model.FormMode;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;

class EstudianteFormViewModelTest {

    @Test
    void createMode_requiresNamesLastNamesAndBirthDate() {
        EstudianteFormViewModel viewModel = new EstudianteFormViewModel();

        assertThat(viewModel.canSubmitBinding().get()).isFalse();

        viewModel.nombresProperty().set("Ana");
        viewModel.apellidosProperty().set("Mora");
        viewModel.fechaNacimientoProperty().set(LocalDate.of(2016, 8, 9));

        assertThat(viewModel.canSubmitBinding().get()).isTrue();
    }

    @Test
    void loadingDisablesSubmitEvenWithValidData() {
        EstudianteFormViewModel viewModel = new EstudianteFormViewModel();
        viewModel.nombresProperty().set("Ana");
        viewModel.apellidosProperty().set("Mora");
        viewModel.fechaNacimientoProperty().set(LocalDate.of(2016, 8, 9));
        viewModel.loadingProperty().set(true);

        assertThat(viewModel.canSubmitBinding().get()).isFalse();
    }

    @Test
    void assignSectionMode_dependsOnlyOnSelectedSection() {
        EstudianteFormViewModel viewModel = new EstudianteFormViewModel();
        viewModel.modeProperty().set(FormMode.ASSIGN_SECTION);
        viewModel.nombresProperty().set("");
        viewModel.apellidosProperty().set("");
        viewModel.fechaNacimientoProperty().set(null);

        assertThat(viewModel.canSubmitBinding().get()).isFalse();

        viewModel.sectionSelectedProperty().set(true);

        assertThat(viewModel.canSubmitBinding().get()).isTrue();
    }
}
