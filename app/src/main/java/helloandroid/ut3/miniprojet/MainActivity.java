package helloandroid.ut3.miniprojet;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

import helloandroid.ut3.miniprojet.data.domain.Restaurant;
import helloandroid.ut3.miniprojet.data.service.FirebaseManager;

public class MainActivity extends AppCompatActivity {
    //TODO: Implementer click bouton map
    private ListView listView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // un Runnable qui sera appelé par le timer
        setContentView(R.layout.activity_main);
        listView = this.findViewById(R.id.restaurantsList);
        final List<Restaurant> restaurantsArray = new ArrayList<>();
        final ArrayAdapter<Restaurant> arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, restaurantsArray);

        FirebaseManager.getInstance().getRestaurants(new FirebaseManager.DataCallback<List<Restaurant>>() {
            @Override
            public void onSuccess(List<Restaurant> restaurants) {
                for (Restaurant restaurant : restaurants) {
                    restaurantsArray.add(restaurant);
                }
                listView.setAdapter(arrayAdapter);
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapter, View view, int position, long arg) {
                        Intent restauIntent = new Intent(MainActivity.this, RestaurantActivity.class);
                        Bundle bundle = new Bundle();
                        Restaurant restaurant = restaurantsArray.get(position);
                        bundle.putString("INFOS",restaurant.toString());
                        bundle.putString("ADRESSE",restaurant.getContact());
                        restauIntent.putExtras(bundle);
                        startActivity(restauIntent);
                    }
                });
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