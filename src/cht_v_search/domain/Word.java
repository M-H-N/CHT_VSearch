package cht_v_search.domain;

import cht_v_search.core.VSearch;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class Word {
    private final int wordIndex;
    private final int textIndex;
    private final String text;

    public Word(int wordIndex, int textIndex, String text) {
        this.wordIndex = wordIndex;
        this.textIndex = textIndex;
        this.text = text;
    }

    public static List<Word> parse(String text) {
        List<Word> result = new ArrayList<>();
        String[] words = text.split(VSearch.WORD_SEPARATION_REGEX);
        for (int i = 0, lastIndex = 0, lastWordLength = 0; i < words.length; i++) {
            lastIndex = text.substring(lastIndex + lastWordLength).indexOf(words[i]) + lastIndex + lastWordLength;
            lastWordLength = words[i].length();
            result.add(new Word(i, lastIndex, words[i].trim()));
        }
        return result.stream().filter(word -> word.getText() != null && !word.getText().isEmpty() && !word.getText().isBlank()).collect(Collectors.toList());
    }

    public int getWordIndex() {
        return wordIndex;
    }

    public int getTextIndex() {
        return textIndex;
    }

    public String getText() {
        return text;
    }

    public boolean containsText(String text) {
        return this.text.contains(text);
    }

//    public boolean equals(Word word) {
//        if (word == null) return false;
//        return this.text.equals(word.getText());
//    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Word word = (Word) o;
        return wordIndex == word.wordIndex &&
                textIndex == word.textIndex &&
                Objects.equals(text, word.text);
    }

    @Override
    public int hashCode() {
        return Objects.hash(wordIndex, textIndex, text);
    }

    @Override
    public String toString() {
        return "Word{" +
                "wordIndex=" + wordIndex +
                ", textIndex=" + textIndex +
                ", text='" + text + '\'' +
                '}';
    }
}
