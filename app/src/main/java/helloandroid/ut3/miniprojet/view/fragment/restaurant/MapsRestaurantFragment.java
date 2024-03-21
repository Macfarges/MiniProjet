package helloandroid.ut3.miniprojet.view.fragment.restaurant;

import android.annotation.SuppressLint;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import helloandroid.ut3.miniprojet.R;
import helloandroid.ut3.miniprojet.data.domain.Restaurant;
import helloandroid.ut3.miniprojet.data.service.FirebaseManager;
import helloandroid.ut3.miniprojet.view.fragment.picture.PictureModifyFragment;

public class MapsRestaurantFragment extends Fragment {

    private final List<Restaurant> restaurants;
    private ViewGroup modalRestaurantLayout, mapsLayout, picturesLayout;
    private String restaurantSelectedId;

    MapsRestaurantFragment(List<Restaurant> restaurants) {
        this.restaurants = restaurants;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (mapsLayout.getVisibility() == View.GONE) {
                    mapsLayout.setVisibility(View.VISIBLE);
                } else {
                    getParentFragmentManager().popBackStack();
                }

            }
        });
    }

    @SuppressLint("PotentialBehaviorOverride")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_maps_restaurant, container, false);
        modalRestaurantLayout = v.findViewById(R.id.modalRestaurantLayout);
        picturesLayout = v.findViewById(R.id.picturesLayout);
        mapsLayout = v.findViewById(R.id.mapsLayout);
        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(googleMap -> {
                googleMap.setOnMarkerClickListener(marker -> {
                    FirebaseManager.getInstance().getAllImagesForRestaurant((String) marker.getTag())
                            .addOnSuccessListener(imageRefs -> {
                                if (Objects.equals((String) marker.getTag(), restaurantSelectedId)) {
                                    return;
                                }
                                restaurantSelectedId = (String) marker.getTag();

                                picturesLayout.removeAllViews();

                                Button modalRestaurantNameBtn = v.findViewById(R.id.modalRestaurantNameBtn);
                                modalRestaurantNameBtn.setOnClickListener(v1 -> {
                                    Optional<Restaurant> optionalRestaurant = restaurants.stream()
                                            .filter(restaurant -> restaurant.getId().equals(restaurantSelectedId))
                                            .findFirst();

                                    if (optionalRestaurant.isPresent()) {
                                        mapsLayout.setVisibility(View.GONE);
                                        getParentFragmentManager().beginTransaction()
                                                .replace(
                                                        R.id.containerOnMaps,
                                                        new RestaurantFragment(
                                                                optionalRestaurant.get()),
                                                        null
                                                )
                                                .setReorderingAllowed(true)
                                                .commit();
                                    } else {
                                        Toast.makeText(requireContext(), "Restaurant not found", Toast.LENGTH_SHORT).show();
                                    }


                                });
                                modalRestaurantNameBtn.setText(marker.getTitle());

                                for (StorageReference imageRef : imageRefs) {
                                    // Once the download URL is available, add the picture to the layout
                                    imageRef.getDownloadUrl().addOnSuccessListener(this::pushNewPictureToLayout).addOnFailureListener(e -> {
                                        // Handle any errors that occur during the URL retrieval process
                                        Log.e("FirebaseManager", "Error getting download URL: " + e.getMessage());
                                    });
                                }
                                modalRestaurantLayout.setVisibility(View.VISIBLE);
                            })
                            .addOnFailureListener(e -> {
                                // Handle any errors that occur during the retrieval process
                                Log.e("FirebaseManager", "Error fetching images: " + e.getMessage());
                            });

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

    private void pushNewPictureToLayout(Uri pictureUri) {
        ImageButton pictureBtn = new ImageButton(requireContext());
        int pictureSize = getResources().getDimensionPixelSize(R.dimen.imgLittleSquareDim);
        pictureBtn.setLayoutParams(new ViewGroup.LayoutParams(pictureSize, pictureSize));
        pictureBtn.setScaleType(ImageView.ScaleType.CENTER_CROP);
        pictureBtn.setBackground(null);
        pictureBtn.setPadding(0, 0, 0, 0);
        RequestOptions requestOptions = new RequestOptions()
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                .skipMemoryCache(false);
        Glide.with(requireContext())
                .load(pictureUri)
                .apply(requestOptions)
                .addListener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        pictureBtn.setOnClickListener(v -> onViewClick(pictureUri));
                        return false;
                    }
                })
                .into(pictureBtn);
        picturesLayout.addView(pictureBtn);
    }

    private void onViewClick(Uri pictureUri) {
        mapsLayout.setVisibility(View.GONE);
        getParentFragmentManager().beginTransaction()
                .replace(
                        R.id.containerOnMaps,
                        new PictureModifyFragment(pictureUri, true),
                        null
                )
                .setReorderingAllowed(true)
                .commit();
    }
}