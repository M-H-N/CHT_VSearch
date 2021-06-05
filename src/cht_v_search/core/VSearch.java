package cht_v_search.core;


import cht_v_search.domain.VResult;
import cht_v_search.domain.Word;
import cht_v_search.utils.Utility;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static cht_v_search.utils.Utility.getAllOccurrencesOf;

public class VSearch {
    public static final String WORD_SEPARATION_REGEX = "[ \\-_~!@#%$^&*\\(\\)\\[\\]\\{\\}/\\:;\"|,./?`]";
    public static final String RESULT_DELIMITER = "\n------------------------------------------\n";
    private static final int WORDS_COUNT_THRESHOLD = 50;
    private static int maxWordsBound = 100;
    private static int maxCharsBound = 500;
    private final String sourceString;
    private final List<Word> sourceWords;
    private SearchWorker worker = null;

    public VSearch(final File sourceTxtFile) throws IOException {
        this(Files.readString(sourceTxtFile.toPath(), StandardCharsets.UTF_8));
    }

    public VSearch(final String sourceString) {
        this.sourceString = Utility.unifyUnicodeString(sourceString);
        this.sourceWords = Word.parse(this.sourceString);
    }

    public void search(String text, Consumer<List<VResult>> onResult) {
//        if (this.worker != null) this.worker.cancel(true);
        this.worker = new SearchWorker(Utility.unifyUnicodeString(text), sourceString, this.sourceWords, onResult);
        this.worker.execute();
    }

    public int getMaxWordsBound() {
        return maxWordsBound;
    }

    public VSearch setMaxWordsBound(int maxWordsBound) {
        VSearch.maxWordsBound = maxWordsBound;
        return this;
    }

    public int getMaxCharsBound() {
        return maxCharsBound;
    }

    public VSearch setMaxCharsBound(int maxCharsBound) {
        VSearch.maxCharsBound = maxCharsBound;
        return this;
    }

    public String getSourceString() {
        return sourceString;
    }

    public List<Word> getSourceWords() {
        return sourceWords;
    }

    public SearchWorker getWorker() {
        return worker;
    }

    public VSearch setWorker(SearchWorker worker) {
        this.worker = worker;
        return this;
    }

    private static class SearchWorker extends SwingWorker<Void, Void> {
        private final String text;
        private final String sourceText;
        private final List<Word> searchWords;
        private final List<Word> sourceWords;
        private final Consumer<List<VResult>> listener;
        private final List<VResult> result = new ArrayList<>();

        public SearchWorker(String text, String sourceText, List<Word> sourceWords, Consumer<List<VResult>> listener) {
            this.text = text;
            this.sourceText = sourceText;
            this.sourceWords = sourceWords;
            this.searchWords = Word.parse(text);
            this.listener = listener;
        }

        @Override
        protected Void doInBackground() {
            System.out.println("doing in background flag-00: text: " + this.text);
            if (this.sourceText.contains(this.text)) {
                for (Integer index : getAllOccurrencesOf(this.sourceText, this.text)) {
                    final int startIndex = Math.max(0, index - maxCharsBound);
                    final int endIndex = Math.min(this.sourceText.length() - 1, startIndex + this.text.length() + (2 * maxCharsBound));

                    final int startLongIndex = Math.max(0, index - maxCharsBound * 5);
                    final int endLongIndex = Math.min(this.sourceText.length() - 1, startIndex + this.text.length() + (10 * maxCharsBound));

                    final String resultText = this.sourceText.substring(startIndex, endIndex);
                    final String resultLongText = this.sourceText.substring(startLongIndex, endLongIndex);
                    result.add(new VResult(startIndex, endIndex, this.searchWords, resultText, resultLongText));
                }
                return null;
            }
//            System.out.println("doing in background: words:\n" + this.sourceWords.collect(Collectors.joining(" | ")));
            List<Word> foundWords = new ArrayList<>();
            this.searchWords.parallelStream().forEach(sw -> {
                System.out.println("doing in background flag-01: sw: " + sw);
                List<Word> foundWordsL = this.sourceWords.stream().filter(word -> word.containsText(sw.getText())).collect(Collectors.toList());
                if (foundWordsL.size() > WORDS_COUNT_THRESHOLD) return;
                System.out.println("doing in background flag-02: sw: " + sw);
                foundWords.addAll(foundWordsL);
            });

//            int averageIndex = foundWords.stream().collect(Collectors.averagingInt(Word::getWordIndex)).intValue();
            List<VResult> resultInternal = foundWords
                    .stream()
                    .map(word -> {
                        final int startTextIndex = Math.max(0, word.getTextIndex() - maxWordsBound);
                        final int endTextIndex = Math.min(this.sourceText.length() - 1, word.getTextIndex() + maxWordsBound);
                        final int startLongTextIndex = Math.max(0, word.getTextIndex() - (maxWordsBound * 5));
                        final int endLongTextIndex = Math.min(this.sourceText.length() - 1, word.getTextIndex() + (maxWordsBound * 5));
                        final String text = this.sourceText.substring(startTextIndex, endTextIndex);
                        final String textLong = this.sourceText.substring(startLongTextIndex, endLongTextIndex);
                        final List<Word> fWords = this.searchWords.stream().filter(sWord -> text.contains(sWord.getText())).collect(Collectors.toList());
                        return new VResult(startTextIndex, endTextIndex, fWords, text, textLong);
                    })
                    .sorted((o1, o2) -> {
                        long o1v = this.searchWords.stream().filter(word -> o1.getText().contains(word.getText())).count();
                        long o2v = this.searchWords.stream().filter(word -> o2.getText().contains(word.getText())).count();
                        return Long.compare(o2v, o1v);
                    })
                    .collect(Collectors.toList());
            this.result.addAll(resultInternal);
            for (int i = 0; i < this.result.size() - 1; i++) {
                var r0 = this.result.get(i);
                var r1 = this.result.get(i + 1);
                final var combined = r0.combineIfIntersect(r1);
                if (combined == null) continue;
                final int newLongIndexStart = Math.max(0, combined.getTextStartIndex() - (maxWordsBound * 5));
                final int newLongIndexEnd = Math.min(this.sourceText.length() - 1, combined.getTextEndIndex() + (maxWordsBound * 5));
                combined.setTextLong(this.sourceText.substring(newLongIndexStart, newLongIndexEnd));
                System.out.println("combining");
                this.result.remove(i);
                this.result.add(i, combined);
                this.result.remove(i + 1);
            }
            return null;
        }

        @Override
        protected void done() {
            super.done();
            if (listener != null) listener.accept(result);
        }

        public String getText() {
            return text;
        }

        public Consumer<List<VResult>> getListener() {
            return listener;
        }

    }
}
