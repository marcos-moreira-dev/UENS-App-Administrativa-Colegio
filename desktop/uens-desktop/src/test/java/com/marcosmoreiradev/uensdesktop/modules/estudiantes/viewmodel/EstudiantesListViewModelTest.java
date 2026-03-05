package com.marcosmoreiradev.uensdesktop.modules.estudiantes.viewmodel;

import static org.assertj.core.api.Assertions.assertThat;

import com.marcosmoreiradev.uensdesktop.api.modules.estudiantes.dto.EstudianteListItemDto;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;

class EstudiantesListViewModelTest {

    @Test
    void startsWithConsistentInitialStateForPaginatedListings() {
        EstudiantesListViewModel viewModel = new EstudiantesListViewModel();

        assertThat(viewModel.items()).isEmpty();
        assertThat(viewModel.statusTextProperty().get()).isEqualTo("Cargando estudiantes...");
        assertThat(viewModel.pageTextProperty().get()).isEqualTo("Página 1 de 1");
        assertThat(viewModel.totalTextProperty().get()).isEqualTo("0 registros");
        assertThat(viewModel.queryTextProperty().get()).isEmpty();
        assertThat(viewModel.loadingProperty()).isNotNull();
        assertThat(viewModel.loadingProperty().get()).isFalse();
        assertThat(viewModel.previousPageAvailableProperty().get()).isFalse();
        assertThat(viewModel.nextPageAvailableProperty().get()).isFalse();
        assertThat(viewModel.pageProperty().get()).isZero();
        assertThat(viewModel.totalPagesProperty().get()).isEqualTo(1);
    }

    @Test
    void exposesMutableObservableStateForControllers() {
        EstudiantesListViewModel viewModel = new EstudiantesListViewModel();
        EstudianteListItemDto estudiante = new EstudianteListItemDto(
                5L,
                "Lucia",
                "Mora",
                LocalDate.of(2017, 3, 14),
                "ACTIVO",
                12L,
                3L);

        viewModel.items().setAll(estudiante);
        viewModel.loadingProperty().set(true);
        viewModel.queryTextProperty().set("lucia");
        viewModel.pageProperty().set(2);
        viewModel.totalPagesProperty().set(4);
        viewModel.pageTextProperty().set("Página 3 de 4");
        viewModel.totalTextProperty().set("31 registros");
        viewModel.previousPageAvailableProperty().set(true);
        viewModel.nextPageAvailableProperty().set(true);

        assertThat(viewModel.items()).containsExactly(estudiante);
        assertThat(viewModel.items().getFirst().displayName()).isEqualTo("Mora, Lucia");
        assertThat(viewModel.loadingProperty().get()).isTrue();
        assertThat(viewModel.queryTextProperty().get()).isEqualTo("lucia");
        assertThat(viewModel.pageProperty().get()).isEqualTo(2);
        assertThat(viewModel.totalPagesProperty().get()).isEqualTo(4);
        assertThat(viewModel.pageTextProperty().get()).isEqualTo("Página 3 de 4");
        assertThat(viewModel.totalTextProperty().get()).isEqualTo("31 registros");
        assertThat(viewModel.previousPageAvailableProperty().get()).isTrue();
        assertThat(viewModel.nextPageAvailableProperty().get()).isTrue();
    }
}
