package helloandroid.ut3.miniprojet.data.service;

import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;

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
    private static final String PICTURES_REVIEWS_STORAGE_PATH = "reviews/%s/pictures";
    private static FirebaseManager instance;
    private final FirebaseDatabase database;
    private final FirebaseStorage storage;

    private FirebaseManager() {
        database = FirebaseDatabase.getInstance("https://miniprojet-b19a8-default-rtdb.europe-west1.firebasedatabase.app");
        storage = FirebaseStorage.getInstance("gs://miniprojet-b19a8.appspot.com");
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

    public void addReview(Review review, final DataCallback<Review> listener) {
        getReviewsRef().child(review.getId()).setValue(review, (databaseError, databaseReference) -> {
            if (databaseError == null) {
                listener.onSuccess(review);
            } else {
                listener.onError(databaseError.toException());
            }
        });
    }

    public void addBooking(Booking booking, final DataCallback<Booking> listener) {
        getReservationsRef().child(booking.getId()).setValue(booking, (databaseError, databaseReference) -> {
            if (databaseError == null) {
                listener.onSuccess(booking);
            } else {
                listener.onError(databaseError.toException());
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

    private StorageReference getImagesRef() {
        return storage.getReference().child(IMAGES_NODE);
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
        reviewsRef.orderByChild("restaurant").equalTo(restaurantId)
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
                        Log.e("DataCallback", "onCancelled invoked with error: " + databaseError.getMessage());
                        callback.onError(databaseError.toException());
                    }
                });
    }

    public Task<List<StorageReference>> getAllImagesForRestaurant(String restaurantId) {
        String imagePath = String.format(PICTURES_REVIEWS_STORAGE_PATH, restaurantId);
        StorageReference restaurantRef = storage.getReference().child(imagePath);

        return restaurantRef.listAll().continueWith(task -> {
            List<StorageReference> imageRefs = new ArrayList<>();
            if (task.isSuccessful()) {
                ListResult result = task.getResult();
                if (result != null) {
                    imageRefs.addAll(result.getItems());
                }
            } else {
                Exception exception = task.getException();
                if (exception != null) {
                    throw exception;
                }
            }
            return imageRefs;
        });
    }

    public void getRemoteUrls(List<String> picturesUrls, final DataCallback<List<String>> callback) {
        List<Task<Uri>> tasks = new ArrayList<>();
        for (String url : picturesUrls) {
            StorageReference imageRef = storage.getReference().child(url);
            Task<Uri> task = imageRef.getDownloadUrl();
            tasks.add(task);
        }

        Tasks.whenAll(tasks).addOnSuccessListener(ignored -> {
            List<String> result = new ArrayList<>();
            for (Task<Uri> task : tasks) {
                if (task.isSuccessful()) {
                    Uri uri = task.getResult();
                    result.add(uri.toString());
                } else {
                    // Handle failure, you might want to log or notify the user
                    Log.e("FirebaseManager", "Failed to fetch URL: " + task.getException().getMessage());
                }
            }
            callback.onSuccess(result);
        }).addOnFailureListener(e -> {
            // Handle failure, you might want to log or notify the user
            Log.e("FirebaseManager", "Failed to fetch URLs: " + e.getMessage());
            callback.onError(e);
        });
    }

    public interface DataCallback<T> {
        void onSuccess(T data);

        void onError(Exception exception);
    }
}
