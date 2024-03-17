package helloandroid.ut3.miniprojet.view.fragment.restaurant;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;

import helloandroid.ut3.miniprojet.R;
import helloandroid.ut3.miniprojet.data.domain.Restaurant;

public class MapsRestaurantFragment extends Fragment {

    private final List<Restaurant> restaurants;

    MapsRestaurantFragment(List<Restaurant> restaurants) {
        this.restaurants = restaurants;
    }

    @SuppressLint("PotentialBehaviorOverride")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_maps_restaurant, container, false);
        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(googleMap -> {
                googleMap.setOnMarkerClickListener(marker -> {
                    System.out.println("Marker clicked, id: " + marker.getTag());
                    // Return true to indicate that the click event is handled
                    return true;
                });

                List<LatLng> latLngs = new ArrayList<>();
                restaurants.forEach(restaurant ->
                        {
                            latLngs.add(new LatLng(Double.parseDouble(restaurant.getLat()), Double.parseDouble(restaurant.getLon())));
                            Marker marker = googleMap.addMarker(new MarkerOptions()
                                    .position(latLngs.get(latLngs.size() - 1))
                                    .title(restaurant.getTitle()));
                            assert marker != null;
                            marker.setTag(restaurant.getId());
                        }
                );
                LatLngBounds.Builder builder = new LatLngBounds.Builder();
                for (LatLng latLng : latLngs) {
                    builder.include(latLng);
                }
                LatLngBounds bounds = builder.build();
                googleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 50));
                googleMap.setOnCameraMoveListener(() -> {
                    CameraPosition cameraPosition = googleMap.getCameraPosition();
                    if (!bounds.contains(cameraPosition.target)) {
                        LatLng target = restrictToBounds(cameraPosition.target, bounds);
                        if (!target.equals(cameraPosition.target)) {
                            googleMap.moveCamera(CameraUpdateFactory.newLatLng(target));
                        }
                    }
                });
                googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(requireContext(), R.raw.map_style));
            });
        }
        return v;
    }

    private LatLng restrictToBounds(LatLng latLng, LatLngBounds bounds) {
        double lat = Math.max(bounds.southwest.latitude, Math.min(latLng.latitude, bounds.northeast.latitude));
        double lng = Math.max(bounds.southwest.longitude, Math.min(latLng.longitude, bounds.northeast.longitude));
        return new LatLng(lat, lng);
    }
}