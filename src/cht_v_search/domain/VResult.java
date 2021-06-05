package cht_v_search.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


public class VResult {
    private final int textStartIndex, textEndIndex;
    private final List<Word> words;
    private final String text;
    private String textLong;

    public VResult(int textStartIndex, int textEndIndex, List<Word> words, String text, String textLong) {
        this.textStartIndex = textStartIndex;
        this.textEndIndex = textEndIndex;
        this.words = words;
        this.text = text;
        this.textLong = textLong;
    }

    public VResult combineIfIntersect(VResult vResult) {
        try {
            if (!this.intersectsWith(vResult)) return null;
            final int newTextStartIndex = Math.min(this.textStartIndex, vResult.textStartIndex);
            final int newTextEndIndex = Math.max(this.textEndIndex, vResult.textEndIndex);

            final List<Word> newWords = new ArrayList<>();
            newWords.addAll(this.words);
            newWords.addAll(vResult.words);

            final String newText;
            if (this.textStartIndex > vResult.textStartIndex) {
                newText = vResult.text + this.text.substring(vResult.textEndIndex - this.textStartIndex);
            } else {
                newText = this.text + vResult.text.substring(this.textEndIndex - vResult.textStartIndex);
            }

            return new VResult(newTextStartIndex, newTextEndIndex, newWords.stream().distinct().collect(Collectors.toList()), newText, null);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean intersectsWith(VResult vResult) {
        System.out.println("intersectsWith");
        return (this.textStartIndex <= vResult.textStartIndex && this.textEndIndex >= vResult.textStartIndex) ||
                (vResult.textStartIndex <= this.textStartIndex && vResult.textEndIndex >= this.textStartIndex);
    }

    public int getTextStartIndex() {
        return textStartIndex;
    }

    public int getTextEndIndex() {
        return textEndIndex;
    }

    public List<Word> getWords() {
        return words;
    }

    public String getText() {
        return text;
    }

    public String getTextLong() {
        return textLong;
    }

    public VResult setTextLong(String textLong) {
        this.textLong = textLong;
        return this;
    }

    @Override
    public String toString() {
        return "VResult{" +
                "textStartIndex=" + textStartIndex +
                ", textEndIndex=" + textEndIndex +
                ", words=" + words +
                ", text='" + text + '\'' +
                '}';
    }
}
