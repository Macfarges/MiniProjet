package helloandroid.ut3.miniprojet.fragments;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import helloandroid.ut3.miniprojet.R;
import helloandroid.ut3.miniprojet.data.domain.Restaurant;
import helloandroid.ut3.miniprojet.data.service.FirebaseManager;

public class RestaurantFragment extends Fragment {
    private ListView listView;
    private View laView;
    private ProgressDialog progressDialog;

    public RestaurantFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        laView = inflater.inflate(R.layout.fragment_liste_restaurants, container, false);
        listView = laView.findViewById(R.id.listZones);
        getRestaurants();
        return laView;
    }

    // Get a reference to our posts


    private void getRestaurants() {
            final ArrayList<Restaurant> arrDesZones = Restaurant.getZones();
            System.out.println(arrDesZones);
            requireActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    final ArrayAdapter<Zone> arrayAdapter = new ArrayAdapter<>(laView.getContext(), android.R.layout.simple_list_item_1, arrDesZones);
                    listView.setAdapter(arrayAdapter);
                }
            });
        }
    }
}
