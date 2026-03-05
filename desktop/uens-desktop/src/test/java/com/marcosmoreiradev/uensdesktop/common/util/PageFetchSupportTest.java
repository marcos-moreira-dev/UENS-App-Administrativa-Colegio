package com.marcosmoreiradev.uensdesktop.common.util;

import static org.assertj.core.api.Assertions.assertThat;

import com.marcosmoreiradev.uensdesktop.api.client.ApiResult;
import com.marcosmoreiradev.uensdesktop.api.contract.PageResponse;
import com.marcosmoreiradev.uensdesktop.common.error.ErrorCategory;
import com.marcosmoreiradev.uensdesktop.common.error.ErrorInfo;
import java.util.List;
import org.junit.jupiter.api.Test;

class PageFetchSupportTest {

    @Test
    void loadAllPagesAggregatesEveryAvailablePageIntoASingleResponse() {
        ApiResult<PageResponse<String>> result = PageFetchSupport.loadAllPages(20, page -> switch (page) {
            case 0 -> ApiResult.success(pageOf(List.of("A", "B"), 0, 3, false, "nombre,asc"));
            case 1 -> ApiResult.success(pageOf(List.of("C"), 1, 3, false, "nombre,asc"));
            case 2 -> ApiResult.success(pageOf(List.of("D", "E"), 2, 3, true, "nombre,asc"));
            default -> throw new IllegalArgumentException("Página inesperada: " + page);
        });

        assertThat(result.isSuccess()).isTrue();
        PageResponse<String> aggregated = result.data().orElseThrow();
        assertThat(aggregated.getItems()).containsExactly("A", "B", "C", "D", "E");
        assertThat(aggregated.getPage()).isZero();
        assertThat(aggregated.getTotalPages()).isEqualTo(1);
        assertThat(aggregated.isFirst()).isTrue();
        assertThat(aggregated.isLast()).isTrue();
        assertThat(aggregated.getSort()).isEqualTo("nombre,asc");
    }

    @Test
    void loadAllPagesStopsAndReturnsFailureWhenAnyPageFails() {
        ErrorInfo error = new ErrorInfo(ErrorCategory.NETWORK, "Sin conexión", "NETWORK_ERROR", null);

        ApiResult<PageResponse<String>> result = PageFetchSupport.loadAllPages(20, page -> switch (page) {
            case 0 -> ApiResult.success(pageOf(List.of("A"), 0, 3, false, "id,asc"));
            case 1 -> ApiResult.failure(error);
            default -> throw new IllegalArgumentException("Página inesperada: " + page);
        });

        assertThat(result.isSuccess()).isFalse();
        assertThat(result.error()).contains(error);
    }

    private static <T> PageResponse<T> pageOf(
            List<T> items,
            int page,
            int totalPages,
            boolean last,
            String sort) {
        PageResponse<T> response = new PageResponse<>();
        response.setItems(items);
        response.setPage(page);
        response.setSize(items.size());
        response.setTotalElements((long) items.size() * totalPages);
        response.setTotalPages(totalPages);
        response.setNumberOfElements(items.size());
        response.setFirst(page == 0);
        response.setLast(last);
        response.setSort(sort);
        return response;
    }
}
