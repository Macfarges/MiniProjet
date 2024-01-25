package helloandroid.ut3.miniprojet.data.service;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

import helloandroid.ut3.miniprojet.data.domain.Restaurant;

public class FirebaseManager {
    private static final String RESTAURANTS_NODE = "restaurants";
    private static final String ADVICES_NODE = "advices";
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

    // Get reference to the root of the database
    public DatabaseReference getRootRef() {
        return database.getReference();
    }

    // Get reference to the restaurants node
    public DatabaseReference getRestaurantsRef() {
        return getRootRef().child(RESTAURANTS_NODE);
    }

    // Get reference to the advices node
    public DatabaseReference getAdvicesRef() {
        return getRootRef().child(ADVICES_NODE);
    }

    // Get reference to the reservations node
    public DatabaseReference getReservationsRef() {
        return getRootRef().child(RESERVATIONS_NODE);
    }

    // Get reference to the images node
    public DatabaseReference getImagesRef() {
        return getRootRef().child(IMAGES_NODE);
    }
}
