package helloandroid.ut3.miniprojet.view.fragment.restaurant;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;

import androidx.fragment.app.Fragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import helloandroid.ut3.miniprojet.R;
import helloandroid.ut3.miniprojet.data.domain.Restaurant;
import helloandroid.ut3.miniprojet.data.service.FirebaseManager;

public class ListRestaurantFragment extends Fragment {
    public ListRestaurantFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_list_restaurant, container, false);
        ProgressBar loadingProgressBar = view.findViewById(R.id.loadingProgressBar);
        loadingProgressBar.setVisibility(View.VISIBLE);
        ListView listView = view.findViewById(R.id.restaurantsList);
        FloatingActionButton fabMap = view.findViewById(R.id.fabMap);
        fabMap.setOnClickListener(v -> {
//        TODO: Implementer click bouton map
//            Fragment mapFragment = new MapFragment();
//            FragmentManager fragmentManager = getParentFragmentManager();
//            fragmentManager.beginTransaction()
//                    .replace(R.id.fragmentContainerView, mapFragment, null)
//                    .setReorderingAllowed(true)
//                    .addToBackStack(null)
//                    .commit();
        });

        final List<Restaurant> restaurantsArray = new ArrayList<>();
        final ArrayAdapter<Restaurant> arrayAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_1, restaurantsArray);

        FirebaseManager.getInstance().getRestaurants(new FirebaseManager.DataCallback<List<Restaurant>>() {
            @Override
            public void onSuccess(List<Restaurant> restaurants) {
                restaurantsArray.addAll(restaurants);
                listView.setAdapter(arrayAdapter);
                listView.setOnItemClickListener((adapter, view, position, arg) -> {
                    getParentFragmentManager().beginTransaction()
                            .replace(R.id.fragmentContainerView, new RestaurantFragment(restaurantsArray.get(position)), null)
                            .setReorderingAllowed(true)
                            .addToBackStack(null)
                            .commit();
                });
                loadingProgressBar.setVisibility(View.GONE);
            }

            @Override
            public void onError(Exception e) {
                System.out.println("Error: " + e.getMessage());
                // TODO Handle the error
            }
        });
        return view;
    }

}