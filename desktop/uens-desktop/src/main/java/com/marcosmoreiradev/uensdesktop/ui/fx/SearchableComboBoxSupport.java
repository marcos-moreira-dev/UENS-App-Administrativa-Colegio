package com.marcosmoreiradev.uensdesktop.ui.fx;

import java.text.Normalizer;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.function.Function;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.SingleSelectionModel;
import javafx.util.Duration;
import javafx.util.StringConverter;

public final class SearchableComboBoxSupport {

    private static final Duration SEARCH_DEBOUNCE = Duration.millis(180);
    private static final String COMMIT_SELECTION_KEY =
            SearchableComboBoxSupport.class.getName() + ".commitSelection";

    private SearchableComboBoxSupport() {
    }

    public static <T> void installLocalSearch(
            ComboBox<T> comboBox,
            ObservableList<T> defaultItems,
            Function<T, String> formatter) {
        installBase(comboBox, defaultItems, formatter);
        installSearchBehavior(
                comboBox,
                defaultItems,
                formatter,
                query -> filterItems(defaultItems, formatter, query),
                ignored -> {
                },
                false);
    }

    public static <T> void installRemoteSearch(
            ComboBox<T> comboBox,
            ObservableList<T> defaultItems,
            Function<T, String> formatter,
            Function<String, List<T>> searchProvider,
            Consumer<List<T>> onResultsLoaded) {
        installBase(comboBox, defaultItems, formatter);
        installSearchBehavior(
                comboBox,
                defaultItems,
                formatter,
                searchProvider,
                onResultsLoaded == null ? ignored -> {
                } : onResultsLoaded,
                true);
    }

    public static void commitSelection(ComboBox<?> comboBox) {
        if (comboBox == null) {
            return;
        }
        Object action = comboBox.getProperties().get(COMMIT_SELECTION_KEY);
        if (action instanceof Runnable runnable) {
            runnable.run();
        }
    }

    private static <T> void installBase(
            ComboBox<T> comboBox,
            ObservableList<T> defaultItems,
            Function<T, String> formatter) {
        StringConverter<T> converter = new StringConverter<>() {
            @Override
            public String toString(T value) {
                return value == null ? "" : formatter.apply(value);
            }

            @Override
            public T fromString(String string) {
                return resolveValue(comboBox, defaultItems, formatter, string, null);
            }
        };
        comboBox.setEditable(true);
        comboBox.setVisibleRowCount(8);
        comboBox.setItems(defaultItems);
        comboBox.setConverter(converter);
        comboBox.setButtonCell(new ListCellWithConverter<>(converter));
        comboBox.setCellFactory(listView -> new ListCellWithConverter<>(converter));
    }

    private static <T> void installSearchBehavior(
            ComboBox<T> comboBox,
            ObservableList<T> defaultItems,
            Function<T, String> formatter,
            Function<String, List<T>> searchProvider,
            Consumer<List<T>> onResultsLoaded,
            boolean remote) {
        PauseTransition debounce = new PauseTransition(SEARCH_DEBOUNCE);
        AtomicBoolean internalTextChange = new AtomicBoolean(false);
        AtomicBoolean internalItemsChange = new AtomicBoolean(false);
        AtomicLong searchVersion = new AtomicLong();
        AtomicReference<T> committedValue = new AtomicReference<>(comboBox.getValue());
        comboBox.getProperties().put(
                COMMIT_SELECTION_KEY,
                (Runnable) () -> commitSelectionInternal(
                        comboBox,
                        defaultItems,
                        formatter,
                        internalTextChange,
                        internalItemsChange,
                        committedValue));

        comboBox.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (internalItemsChange.get()) {
                return;
            }
            if (newValue == null) {
                return;
            }
            committedValue.set(newValue);
            if (!comboBox.getEditor().isFocused()) {
                setEditorText(comboBox, formatter.apply(newValue), internalTextChange);
            }
            restoreDefaultItems(comboBox, defaultItems, internalTextChange, internalItemsChange);
        });

        comboBox.setOnShowing(event -> {
            String query = comboBox.getEditor().getText();
            String committedText = committedValue.get() == null ? "" : formatter.apply(committedValue.get());
            if (isBrowseMode(query, committedText)) {
                restoreDefaultItems(comboBox, defaultItems, internalTextChange, internalItemsChange);
                return;
            }
            if (remote) {
                long currentVersion = searchVersion.incrementAndGet();
                FxExecutors.submitIo(() -> searchProvider.apply(query.trim()), results -> {
                    if (searchVersion.get() != currentVersion) {
                        return;
                    }
                    if (!comboBox.isShowing()) {
                        return;
                    }
                    if (results == null || results.isEmpty()) {
                        restoreDefaultItems(comboBox, defaultItems, internalTextChange, internalItemsChange);
                        return;
                    }
                    applySearchResults(
                            comboBox,
                            defaultItems,
                            results,
                            internalTextChange,
                            internalItemsChange);
                    onResultsLoaded.accept(results);
                });
                return;
            }
            List<T> results = searchProvider.apply(query.trim());
            if (results == null || results.isEmpty()) {
                restoreDefaultItems(comboBox, defaultItems, internalTextChange, internalItemsChange);
                return;
            }
            applySearchResults(
                    comboBox,
                    defaultItems,
                    results,
                    internalTextChange,
                    internalItemsChange);
        });

        comboBox.setOnHidden(event -> {
            if (comboBox.getEditor().isFocused() && !comboBox.getEditor().getText().isBlank()) {
                return;
            }
            restoreDefaultItems(
                    comboBox,
                    defaultItems,
                    internalTextChange,
                    internalItemsChange);
        });

        comboBox.getEditor().focusedProperty().addListener((observable, oldValue, focused) -> {
            if (focused) {
                if (comboBox.getEditor().getText().isBlank()) {
                    comboBox.setItems(defaultItems);
                }
                return;
            }
            debounce.stop();
            restoreDefaultItems(comboBox, defaultItems, internalTextChange, internalItemsChange);
            T resolvedValue = resolveValue(
                    comboBox,
                    defaultItems,
                    formatter,
                    comboBox.getEditor().getText(),
                    null);
            if (resolvedValue != null) {
                comboBox.getSelectionModel().select(resolvedValue);
                committedValue.set(resolvedValue);
                setEditorText(comboBox, formatter.apply(resolvedValue), internalTextChange);
            } else {
                comboBox.getSelectionModel().clearSelection();
                comboBox.setValue(null);
                committedValue.set(null);
                setEditorText(comboBox, "", internalTextChange);
            }
        });

        comboBox.getEditor().textProperty().addListener((observable, oldValue, newValue) -> {
            if (internalTextChange.get() || internalItemsChange.get() || !comboBox.getEditor().isFocused()) {
                return;
            }
            String query = newValue == null ? "" : newValue.trim();
            clearSelectionIfEditing(comboBox, formatter, query, internalItemsChange);
            if (!remote) {
                debounce.stop();
                if (query.isBlank()) {
                    restoreDefaultItems(comboBox, defaultItems, internalTextChange, internalItemsChange);
                    if (comboBox.getEditor().isFocused() && !defaultItems.isEmpty()) {
                        comboBox.show();
                    }
                    return;
                }
                applySearchResults(
                        comboBox,
                        defaultItems,
                        searchProvider.apply(query),
                        internalTextChange,
                        internalItemsChange);
                return;
            }
            debounce.setOnFinished(event -> {
                if (query.isBlank()) {
                    restoreDefaultItems(comboBox, defaultItems, internalTextChange, internalItemsChange);
                    if (comboBox.getEditor().isFocused() && !defaultItems.isEmpty()) {
                        comboBox.show();
                    }
                    return;
                }
                if (remote) {
                    long currentVersion = searchVersion.incrementAndGet();
                    FxExecutors.submitIo(() -> searchProvider.apply(query), results -> {
                        if (searchVersion.get() != currentVersion) {
                            return;
                        }
                        if (!comboBox.getEditor().isFocused()) {
                            return;
                        }
                        if (!Objects.equals(comboBox.getEditor().getText().trim(), query)) {
                            return;
                        }
                        onResultsLoaded.accept(results);
                        applySearchResults(
                                comboBox,
                                defaultItems,
                                results,
                                internalTextChange,
                                internalItemsChange);
                    });
                    return;
                }
                applySearchResults(
                        comboBox,
                        defaultItems,
                        searchProvider.apply(query),
                        internalTextChange,
                        internalItemsChange);
            });
            debounce.playFromStart();
        });

        comboBox.getEditor().setOnAction(event -> {
            if (comboBox.getValue() != null) {
                return;
            }
            if (!comboBox.getItems().isEmpty()) {
                comboBox.getSelectionModel().selectFirst();
                T selectedValue = comboBox.getSelectionModel().getSelectedItem();
                if (selectedValue != null) {
                    committedValue.set(selectedValue);
                }
            }
        });
    }

    private static <T> void applySearchResults(
            ComboBox<T> comboBox,
            ObservableList<T> defaultItems,
            List<T> results,
            AtomicBoolean internalTextChange,
            AtomicBoolean internalItemsChange) {
        if (results == null || results.isEmpty()) {
            restoreDefaultItems(comboBox, defaultItems, internalTextChange, internalItemsChange);
            if (comboBox.isShowing()) {
                Platform.runLater(comboBox::hide);
            }
            return;
        }
        String editorText = comboBox.getEditor().getText();
        int caretPosition = comboBox.getEditor().getCaretPosition();
        T selectedValue = comboBox.getValue();
        internalItemsChange.set(true);
        try {
            if (sameItems(defaultItems, results)) {
                comboBox.setItems(defaultItems);
            } else {
                comboBox.setItems(FXCollections.observableArrayList(results));
            }
            SingleSelectionModel<T> selectionModel = comboBox.getSelectionModel();
            if (selectedValue != null && comboBox.getItems().contains(selectedValue)) {
                selectionModel.select(selectedValue);
            } else {
                selectionModel.clearSelection();
            }
        } finally {
            internalItemsChange.set(false);
        }
        if (comboBox.getEditor().isFocused()) {
            restoreEditorState(comboBox, editorText, caretPosition, internalTextChange);
        }
        Platform.runLater(() -> {
            if (!comboBox.getEditor().isFocused()) {
                return;
            }
            if (!comboBox.isShowing() && !comboBox.getItems().isEmpty()) {
                comboBox.show();
            }
        });
    }

    private static <T> void restoreDefaultItems(
            ComboBox<T> comboBox,
            ObservableList<T> defaultItems,
            AtomicBoolean internalTextChange,
            AtomicBoolean internalItemsChange) {
        if (comboBox.getItems() == defaultItems) {
            return;
        }
        String editorText = comboBox.getEditor().getText();
        int caretPosition = comboBox.getEditor().getCaretPosition();
        T selectedValue = comboBox.getValue();
        SingleSelectionModel<T> selectionModel = comboBox.getSelectionModel();
        internalItemsChange.set(true);
        try {
            comboBox.setItems(defaultItems);
            if (selectedValue != null) {
                selectionModel.select(selectedValue);
            } else {
                selectionModel.clearSelection();
            }
        } finally {
            internalItemsChange.set(false);
        }
        if (comboBox.getEditor().isFocused()) {
            restoreEditorState(comboBox, editorText, caretPosition, internalTextChange);
        }
    }

    private static <T> void commitSelectionInternal(
            ComboBox<T> comboBox,
            ObservableList<T> defaultItems,
            Function<T, String> formatter,
            AtomicBoolean internalTextChange,
            AtomicBoolean internalItemsChange,
            AtomicReference<T> committedValue) {
        String editorText = comboBox.getEditor().getText();
        if (editorText == null || editorText.isBlank()) {
            comboBox.getSelectionModel().clearSelection();
            comboBox.setValue(null);
            committedValue.set(null);
            setEditorText(comboBox, "", internalTextChange);
            restoreDefaultItems(comboBox, defaultItems, internalTextChange, internalItemsChange);
            return;
        }
        T resolvedValue = resolveValue(comboBox, defaultItems, formatter, editorText, null);
        restoreDefaultItems(comboBox, defaultItems, internalTextChange, internalItemsChange);
        if (resolvedValue != null) {
            comboBox.getSelectionModel().select(resolvedValue);
            committedValue.set(resolvedValue);
            setEditorText(comboBox, formatter.apply(resolvedValue), internalTextChange);
            return;
        }
        comboBox.getSelectionModel().clearSelection();
        comboBox.setValue(null);
        committedValue.set(null);
        setEditorText(comboBox, "", internalTextChange);
    }

    private static <T> List<T> filterItems(
            ObservableList<T> items,
            Function<T, String> formatter,
            String query) {
        String normalizedQuery = normalize(query);
        return items.stream()
                .filter(item -> normalize(formatter.apply(item)).contains(normalizedQuery))
                .toList();
    }

    private static boolean sameItems(ObservableList<?> defaultItems, List<?> results) {
        return defaultItems.size() == results.size() && defaultItems.containsAll(results);
    }

    private static String normalize(String value) {
        String safeValue = value == null ? "" : value;
        String normalized = Normalizer.normalize(safeValue, Normalizer.Form.NFD);
        String withoutMarks = normalized.replaceAll("\\p{M}+", "");
        return withoutMarks
                .replaceAll("[^\\p{IsAlphabetic}\\p{IsDigit}]+", "")
                .toLowerCase(Locale.ROOT);
    }

    private static <T> void setEditorText(
            ComboBox<T> comboBox,
            String text,
            AtomicBoolean internalTextChange) {
        internalTextChange.set(true);
        comboBox.getEditor().setText(text == null ? "" : text);
        internalTextChange.set(false);
    }

    private static <T> void restoreEditorState(
            ComboBox<T> comboBox,
            String editorText,
            int caretPosition,
            AtomicBoolean internalTextChange) {
        setEditorText(comboBox, editorText, internalTextChange);
        comboBox.getEditor().positionCaret(Math.min(caretPosition, comboBox.getEditor().getText().length()));
    }

    private static <T> T resolveValue(
            ComboBox<T> comboBox,
            ObservableList<T> defaultItems,
            Function<T, String> formatter,
            String text,
            T fallbackValue) {
        String normalizedText = normalize(text).trim();
        if (normalizedText.isBlank()) {
            return fallbackValue;
        }
        T currentValue = comboBox.getValue();
        if (matches(currentValue, formatter, normalizedText)) {
            return currentValue;
        }
        T exactMatch = findExactMatch(comboBox.getItems(), formatter, normalizedText);
        if (exactMatch != null) {
            return exactMatch;
        }
        exactMatch = findExactMatch(defaultItems, formatter, normalizedText);
        if (exactMatch != null) {
            return exactMatch;
        }
        T uniqueContainsMatch = findUniqueContainsMatch(comboBox.getItems(), formatter, normalizedText);
        if (uniqueContainsMatch != null) {
            return uniqueContainsMatch;
        }
        uniqueContainsMatch = findUniqueContainsMatch(defaultItems, formatter, normalizedText);
        if (uniqueContainsMatch != null) {
            return uniqueContainsMatch;
        }
        return fallbackValue;
    }

    private static <T> void clearSelectionIfEditing(
            ComboBox<T> comboBox,
            Function<T, String> formatter,
            String query,
            AtomicBoolean internalItemsChange) {
        T currentValue = comboBox.getValue();
        if (currentValue == null) {
            return;
        }
        if (normalize(formatter.apply(currentValue)).equals(normalize(query))) {
            return;
        }
        internalItemsChange.set(true);
        try {
            comboBox.getSelectionModel().clearSelection();
            comboBox.setValue(null);
        } finally {
            internalItemsChange.set(false);
        }
    }

    private static <T> T findExactMatch(
            Iterable<T> items,
            Function<T, String> formatter,
            String normalizedText) {
        for (T item : items) {
            if (matches(item, formatter, normalizedText)) {
                return item;
            }
        }
        return null;
    }

    private static <T> T findUniqueContainsMatch(
            Iterable<T> items,
            Function<T, String> formatter,
            String normalizedText) {
        T match = null;
        for (T item : items) {
            if (!containsMatch(item, formatter, normalizedText)) {
                continue;
            }
            if (match != null && !Objects.equals(match, item)) {
                return null;
            }
            match = item;
        }
        return match;
    }

    private static <T> boolean matches(T item, Function<T, String> formatter, String normalizedText) {
        return item != null && normalize(formatter.apply(item)).equals(normalizedText);
    }

    private static <T> boolean containsMatch(T item, Function<T, String> formatter, String normalizedText) {
        return item != null && normalize(formatter.apply(item)).contains(normalizedText);
    }

    private static boolean isBrowseMode(String query, String committedText) {
        String normalizedQuery = normalize(query).trim();
        if (normalizedQuery.isBlank()) {
            return true;
        }
        return normalizedQuery.equals(normalize(committedText).trim());
    }

    private static final class ListCellWithConverter<T> extends ListCell<T> {

        private final StringConverter<T> converter;

        private ListCellWithConverter(StringConverter<T> converter) {
            this.converter = converter;
        }

        @Override
        protected void updateItem(T item, boolean empty) {
            super.updateItem(item, empty);
            setText(empty ? null : converter.toString(item));
            setGraphic(null);
        }
    }
}
