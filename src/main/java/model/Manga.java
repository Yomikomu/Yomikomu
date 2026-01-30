package model;

public record Manga(String id, String title) {
    @Override
    public String toString() {
        return title;
    }
}
