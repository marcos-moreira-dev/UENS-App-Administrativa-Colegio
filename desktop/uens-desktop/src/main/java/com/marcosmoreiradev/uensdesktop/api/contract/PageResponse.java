package com.marcosmoreiradev.uensdesktop.api.contract;

import java.util.List;

/**
 * Generic pagination payload returned by list endpoints.
 *
 * @param <T> item type contained in the page
 */
public class PageResponse<T> {

    private List<T> items;
    private int page;
    private int size;
    private long totalElements;
    private int totalPages;
    private int numberOfElements;
    private boolean first;
    private boolean last;
    private String sort;

    /**
     * @return items contained in the current page
     */
    public List<T> getItems() {
        return items;
    }

    /**
     * @param items items contained in the current page
     */
    public void setItems(List<T> items) {
        this.items = items;
    }

    /**
     * @return zero-based page index returned by the backend
     */
    public int getPage() {
        return page;
    }

    /**
     * @param page zero-based page index returned by the backend
     */
    public void setPage(int page) {
        this.page = page;
    }

    /**
     * @return requested page size
     */
    public int getSize() {
        return size;
    }

    /**
     * @param size requested page size
     */
    public void setSize(int size) {
        this.size = size;
    }

    /**
     * @return total number of items available across all pages
     */
    public long getTotalElements() {
        return totalElements;
    }

    /**
     * @param totalElements total number of items available across all pages
     */
    public void setTotalElements(long totalElements) {
        this.totalElements = totalElements;
    }

    /**
     * @return total number of pages reported by the backend
     */
    public int getTotalPages() {
        return totalPages;
    }

    /**
     * @param totalPages total number of pages reported by the backend
     */
    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

    /**
     * @return number of items present in the current page
     */
    public int getNumberOfElements() {
        return numberOfElements;
    }

    /**
     * @param numberOfElements number of items present in the current page
     */
    public void setNumberOfElements(int numberOfElements) {
        this.numberOfElements = numberOfElements;
    }

    /**
     * @return whether the current page is the first page
     */
    public boolean isFirst() {
        return first;
    }

    /**
     * @param first whether the current page is the first page
     */
    public void setFirst(boolean first) {
        this.first = first;
    }

    /**
     * @return whether the current page is the last page
     */
    public boolean isLast() {
        return last;
    }

    /**
     * @param last whether the current page is the last page
     */
    public void setLast(boolean last) {
        this.last = last;
    }

    /**
     * @return sort descriptor returned by the backend
     */
    public String getSort() {
        return sort;
    }

    /**
     * @param sort sort descriptor returned by the backend
     */
    public void setSort(String sort) {
        this.sort = sort;
    }
}
