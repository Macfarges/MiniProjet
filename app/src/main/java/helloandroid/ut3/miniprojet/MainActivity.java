package helloandroid.ut3.miniprojet;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

import helloandroid.ut3.miniprojet.data.domain.Restaurant;
import helloandroid.ut3.miniprojet.data.service.FirebaseManager;

public class MainActivity extends AppCompatActivity {
    //TODO: Afficher liste de restaurants
    //TODO: Afficher Bouton d'accès à la carte avec photos

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FirebaseManager.getInstance().getRestaurants(new FirebaseManager.DataCallback<List<Restaurant>>() {
            @Override
            public void onSuccess(List<Restaurant> restaurants) {
                for (Restaurant restaurant : restaurants) {
                    System.out.println(restaurant.getTitle());
                }
            }

            @Override
            public void onError(Exception e) {
                System.out.println("Error: " + e.getMessage());
                // Handle the error
            }
        });
//        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
    }
}