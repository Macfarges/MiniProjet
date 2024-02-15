package helloandroid.ut3.miniprojet.data.domain;

import java.util.Date;
import java.util.List;
import java.util.UUID;

public class Review {
    private String id;
    private String text;
    private int rating;
    private List<String> pictureUrls; // todo: maybe change to a list of Picture objects
    private Date date;
    private String restaurantId;

    protected Review() {
        // Default constructor required for calls to DataSnapshot.getValue(Review.class)
    }

    public Review(String text, int rating, List<String> pictureUrls, String restaurantId) {
        if (rating < 0 || rating > 5)
            throw new IllegalArgumentException("Rating must be between 0 and 5");
        this.id = UUID.randomUUID().toString();
        this.text = text;
        this.rating = rating;
        this.pictureUrls = pictureUrls;
        this.restaurantId = restaurantId;
        this.date = new Date(); // Automatically set based on the current timestamp
    }

    // Getters
    public String getId() {
        return id;
    }

    public String getText() {
        return text;
    }

    public int getRating() {
        return rating;
    }

    public List<String> getPictureUrls() {
        return pictureUrls;
    }

    public Date getDate() {
        return date;
    }

    public String getRestaurant() {
        return restaurantId;
    }
}