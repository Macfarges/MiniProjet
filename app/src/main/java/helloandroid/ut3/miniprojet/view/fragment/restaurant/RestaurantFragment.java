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
    private final View.OnClickListener leaveReviewAction = v -> {
        // TODO implement action
    };

    //TODO: Ajouter nom
    //TODO: Ajouter adresse
    //TODO: Ajouter avis
    //TODO: Ajouter bouton donner son avis
    //TODO: Ajouter bouton réserver
    private final View.OnClickListener reserveAction = v -> {
        // TODO implement action
    };

    public RestaurantFragment(Restaurant restaurant) {
        this.restaurant = restaurant;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.restaurant_fragment, container, false);
        ((TextView) view.findViewById(R.id.restaurantName)).setText(restaurant.getTitle());
        ((TextView) view.findViewById(R.id.restaurantShortDesc)).setText(restaurant.getShortDesc());
        view.findViewById(R.id.leaveReviewBtn).setOnClickListener(leaveReviewAction);
        view.findViewById(R.id.reserveBtn).setOnClickListener(reserveAction);
        return view;
    }
}