package helloandroid.ut3.miniprojet.view.fragment.restaurant;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import helloandroid.ut3.miniprojet.R;
import helloandroid.ut3.miniprojet.data.domain.Restaurant;
import helloandroid.ut3.miniprojet.data.service.FirebaseManager;

public class ListRestaurantFragment extends Fragment {
    private boolean isFirstLoad = true;

    public ListRestaurantFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_list_restaurant, container, false);
        // Replace loadingProgressBar with loadingView
        ListView listView = view.findViewById(R.id.restaurantsList);
        FloatingActionButton fabMap = view.findViewById(R.id.fabMap);

        final List<Restaurant> restaurantsArray = new ArrayList<>();
        final ArrayAdapter<Restaurant> arrayAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_1, restaurantsArray);

        View loadingView;
        // Show splash screen only on the first load
        if (isFirstLoad) {
            loadingView = view.findViewById(R.id.splash_screen);
            loadingView.setVisibility(View.VISIBLE);
        } else {
            loadingView = view.findViewById(R.id.loadingProgressBar);
            loadingView.setVisibility(View.VISIBLE);
        }

        FirebaseManager.getInstance().getRestaurants(new FirebaseManager.DataCallback<List<Restaurant>>() {
            @Override
            public void onSuccess(List<Restaurant> restaurants) {
                fabMap.setOnClickListener(v -> {
                    Fragment mapsFragment = new MapsRestaurantFragment(restaurants);
                    FragmentManager fragmentManager = getParentFragmentManager();
                    fragmentManager.beginTransaction()
                            .replace(R.id.fragmentContainerView, mapsFragment, null)
                            .setReorderingAllowed(true)
                            .addToBackStack(null)
                            .commit();
                });
                restaurantsArray.addAll(restaurants);
                listView.setAdapter(arrayAdapter);
                listView.setOnItemClickListener((adapter, view, position, arg) -> {
                    getParentFragmentManager().beginTransaction()
                            .replace(R.id.fragmentContainerView, new RestaurantFragment(restaurantsArray.get(position)), null)
                            .setReorderingAllowed(true)
                            .addToBackStack(null)
                            .commit();
                });

                // Hide loading view (splash screen or progress bar) after data is loaded
                loadingView.setVisibility(View.GONE);
                // Set isFirstLoad to false after the first load
                isFirstLoad = false;
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
