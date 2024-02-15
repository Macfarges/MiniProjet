package helloandroid.ut3.miniprojet.data.service;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import helloandroid.ut3.miniprojet.data.domain.Booking;
import helloandroid.ut3.miniprojet.data.domain.Restaurant;
import helloandroid.ut3.miniprojet.data.domain.Review;

public class FirebaseManager {
    private static final String RESTAURANTS_NODE = "restaurants";
    private static final String REVIEWS_NODE = "reviews";
    private static final String RESERVATIONS_NODE = "reservations";
    private static final String IMAGES_NODE = "images";

    private static FirebaseManager instance;
    private final FirebaseDatabase database;

    private FirebaseManager() {
        database = FirebaseDatabase.getInstance("https://miniprojet-b19a8-default-rtdb.europe-west1.firebasedatabase.app/");
    }

    public static synchronized FirebaseManager getInstance() {
        if (instance == null) {
            instance = new FirebaseManager();
        }
        return instance;
    }

    public void saveRestaurants(List<Restaurant> restaurants) {
        for (Restaurant restaurant : restaurants) {
            getRestaurantsRef().child(restaurant.getId()).setValue(restaurant);
        }
    }

    public void addReview(Review review) {
        getReviewsRef().child(review.getId()).setValue(review);
    }

    public void addBooking(Booking booking, final OnItemAddListener listener) {
        getReservationsRef().child(booking.getId()).setValue(booking, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                if (databaseError == null) {
                    // Review added successfully
                    listener.onSuccess();
                } else {
                    // Error occurred while adding the review
                    listener.onError(databaseError.getMessage());
                }
            }
        });
    }

    private DatabaseReference getRootRef() {
        return database.getReference();
    }

    private DatabaseReference getRestaurantsRef() {
        return getRootRef().child(RESTAURANTS_NODE);
    }

    private DatabaseReference getReviewsRef() {
        return getRootRef().child(REVIEWS_NODE);
    }

    private DatabaseReference getReservationsRef() {
        return getRootRef().child(RESERVATIONS_NODE);
    }

    private DatabaseReference getImagesRef() {
        return getRootRef().child(IMAGES_NODE);
    }

    public void getRestaurants(final DataCallback<List<Restaurant>> callback) {
        DatabaseReference restaurantsRef = getRestaurantsRef();
        restaurantsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<Restaurant> restaurantList = new ArrayList<>();
                for (DataSnapshot restaurantSnapshot : dataSnapshot.getChildren()) {
                    Restaurant restaurant = restaurantSnapshot.getValue(Restaurant.class);
                    if (restaurant != null) {
                        restaurantList.add(restaurant);
                    }
                }
                callback.onSuccess(restaurantList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                callback.onError(databaseError.toException());
            }
        });
    }

    public void getReviewsForRestaurant(String restaurantId, final DataCallback<List<Review>> callback) {
        DatabaseReference reviewsRef = getReviewsRef();
        reviewsRef.orderByChild("restaurant/id").equalTo(restaurantId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        List<Review> reviewList = new ArrayList<>();
                        for (DataSnapshot reviewSnapshot : dataSnapshot.getChildren()) {
                            Review review = reviewSnapshot.getValue(Review.class);
                            if (review != null) {
                                reviewList.add(review);
                            }
                        }
                        callback.onSuccess(reviewList);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        callback.onError(databaseError.toException());
                    }
                });
    }

    public interface OnItemAddListener {
        void onSuccess();

        void onError(String errorMessage);
    }

    public interface DataCallback<T> {
        void onSuccess(T data);

        void onError(Exception exception);
    }
}
