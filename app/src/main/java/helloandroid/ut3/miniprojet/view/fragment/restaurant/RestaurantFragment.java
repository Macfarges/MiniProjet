package helloandroid.ut3.miniprojet.view.fragment.restaurant;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import helloandroid.ut3.miniprojet.R;
import helloandroid.ut3.miniprojet.data.domain.Restaurant;

public class RestaurantFragment extends Fragment {

    private final Restaurant restaurant;

    private TextView restaurantNameTv;
    private TextView restauAdressTv;

    public RestaurantFragment(Restaurant restaurant) {
        super(R.layout.restaurant_fragment);
        this.restaurant = restaurant;
    }

    //TODO: Ajouter bouton retour
    //TODO: Ajouter nom
    //TODO: Ajouter adresse
    //TODO: Ajouter avis
    //TODO: Ajouter bouton donner son avis
    //TODO: Ajouter bouton réserver

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.restaurant_fragment, container, false);
        restaurantNameTv = view.findViewById(R.id.RestauName);
        restauAdressTv = view.findViewById(R.id.RestauAdress);
        restaurantNameTv.setText(restaurant.getTitle());
        restauAdressTv.setText(restaurant.getShortDesc());
        return view;
    }

    public void startMainActivity(View view) {
        /*Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);*/
    }

    public void startReserverActivity(View view) {
        /*Intent intent = new Intent(this, ReservationActivity.class);
        startActivity(intent);*/
    }
}