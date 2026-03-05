package com.marcosmoreiradev.uensdesktop.common.util;

import com.marcosmoreiradev.uensdesktop.api.client.ApiResult;
import com.marcosmoreiradev.uensdesktop.api.contract.PageResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.function.IntFunction;

/**
 * Aggregates paginated backend responses into a single in-memory page when a screen needs the
 * complete catalog.
 */
public final class PageFetchSupport {

    private PageFetchSupport() {
    }

    /**
     * Requests pages sequentially starting from page zero until the backend reports the last page.
     *
     * @param pageSize requested page size used to describe the aggregated response
     * @param pageFetcher function that loads one page for the supplied page index
     * @param <T> item type returned by the paginated endpoint
     * @return a successful one-page response with all collected items, or the first failure found
     */
    public static <T> ApiResult<PageResponse<T>> loadAllPages(
            int pageSize,
            IntFunction<ApiResult<PageResponse<T>>> pageFetcher) {
        ApiResult<PageResponse<T>> firstPageResult = pageFetcher.apply(0);
        if (!firstPageResult.isSuccess()) {
            return firstPageResult;
        }

        PageResponse<T> firstPage = firstPageResult.data().orElseThrow();
        List<T> items = new ArrayList<>(firstPage.getItems());
        int totalPages = Math.max(firstPage.getTotalPages(), 1);
        int currentPage = 1;

        while (currentPage < totalPages && !firstPage.isLast()) {
            ApiResult<PageResponse<T>> pageResult = pageFetcher.apply(currentPage);
            if (!pageResult.isSuccess()) {
                return pageResult;
            }
            PageResponse<T> page = pageResult.data().orElseThrow();
            items.addAll(page.getItems());
            if (page.isLast()) {
                break;
            }
            currentPage++;
        }

        PageResponse<T> aggregated = new PageResponse<>();
        aggregated.setItems(items);
        aggregated.setPage(0);
        aggregated.setSize(Math.max(pageSize, items.size()));
        aggregated.setTotalElements(items.size());
        aggregated.setTotalPages(1);
        aggregated.setNumberOfElements(items.size());
        aggregated.setFirst(true);
        aggregated.setLast(true);
        aggregated.setSort(firstPage.getSort());
        return ApiResult.success(aggregated);
    }
}
