package helloandroid.ut3.miniprojet;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import helloandroid.ut3.miniprojet.data.domain.Restaurant;
import helloandroid.ut3.miniprojet.data.service.FirebaseManager;

/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
public class ListRestaurantFragment extends Fragment {


    private ListView listView;

    public ListRestaurantFragment() {
        // Required empty public constructor
        super(R.layout.fragment_list_restaurant);
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View result = inflater.inflate(R.layout.fragment_list_restaurant, container, false);
        listView = result.findViewById(R.id.restaurantsList);
        final List<Restaurant> restaurantsArray = new ArrayList<>();
        final ArrayAdapter<Restaurant> arrayAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_1, restaurantsArray);

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
                        Bundle bundle = new Bundle();
                        Restaurant restaurant = restaurantsArray.get(position);
                        bundle.putString("INFOS",restaurant.toString());
                        bundle.putString("ADRESSE",restaurant.getContact());

                        Fragment restau = new RestaurantFragment();
                        restau.setArguments(bundle);
                        FragmentManager fragmentManager = getParentFragmentManager();
                        fragmentManager.beginTransaction()
                                .replace(R.id.fragmentContainerView, RestaurantFragment.class, null)
                                .setReorderingAllowed(true)
                                .addToBackStack("name") // Name can be null
                                .commit();
                    }
                });
            }@Override
            public void onError(Exception e) {
                System.out.println("Error: " + e.getMessage());
                // Handle the error
            }
        });
        return result;
    }
}