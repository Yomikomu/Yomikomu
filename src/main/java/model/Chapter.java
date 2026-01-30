package model;

public record Chapter(String id, String title, String number) {
    @Override
    public String toString() {
      return "Ch." + number + (title.isEmpty() ? "" : " - " + title);
    }
}
