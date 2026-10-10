package cinema.model;

public class Movie {
    private final String id;
    private final String title;
    private final String genre;
    private final int durationMinutes;
    private final int ageLimit;
    private final boolean showing;

    public Movie(String id, String title, String genre, int durationMinutes, int ageLimit, boolean showing) {
        this.id = id;
        this.title = title;
        this.genre = genre;
        this.durationMinutes = durationMinutes;
        this.ageLimit = ageLimit;
        this.showing = showing;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getGenre() {
        return genre;
    }

    public int getDurationMinutes() {
        return durationMinutes;
    }

    public int getAgeLimit() {
        return ageLimit;
    }

    public boolean isShowing() {
        return showing;
    }

    @Override
    public String toString() {
        return title + " (" + genre + ")";
    }
}