package cht_v_search.domain;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;

public class WordFreq {
    @SerializedName("key")
    private final String word;

    @SerializedName("value")
    private final int frequency;

    public WordFreq(String word, int frequency) {
        this.word = word;
        this.frequency = frequency;
    }

    public static List<WordFreq> loadAll() throws IOException {
        return new Gson().fromJson(
                Files.readString(Paths.get("persian-word-freq.json"), StandardCharsets.UTF_8),
                new TypeToken<List<WordFreq>>() {
                }.getType());
    }

    public String getWord() {
        return word;
    }

    public int getFrequency() {
        return frequency;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (!(object instanceof WordFreq)) return false;
        WordFreq wordFreq = (WordFreq) object;
        return getFrequency() == wordFreq.getFrequency() &&
                getWord().equals(wordFreq.getWord());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getWord(), getFrequency());
    }
}
