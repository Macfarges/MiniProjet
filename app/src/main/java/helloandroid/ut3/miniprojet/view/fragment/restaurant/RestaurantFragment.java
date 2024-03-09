package helloandroid.ut3.miniprojet.view.fragment.restaurant;

import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import helloandroid.ut3.miniprojet.R;
import helloandroid.ut3.miniprojet.data.domain.Restaurant;
import helloandroid.ut3.miniprojet.data.domain.Review;
import helloandroid.ut3.miniprojet.data.service.FirebaseManager;
import helloandroid.ut3.miniprojet.view.adapters.ReviewAdapter;
import helloandroid.ut3.miniprojet.view.fragment.booking.BookingFragment;
import helloandroid.ut3.miniprojet.view.fragment.review.form.ReviewFormFragment;

public class RestaurantFragment extends Fragment {

    private final Restaurant restaurant;

    public RestaurantFragment(@NotNull Restaurant restaurant) {
        this.restaurant = restaurant;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_restaurant, container, false);
        FirebaseManager.getInstance().getReviewsForRestaurant(restaurant.getId(), new FirebaseManager.DataCallback<List<Review>>() {
            @Override
            public void onSuccess(List<Review> data) {

                final View.OnClickListener leaveReviewAction = v -> getParentFragmentManager().beginTransaction()
                        .replace(R.id.fragmentContainerView, new ReviewFormFragment(restaurant), null)
                        .setReorderingAllowed(true)
                        .addToBackStack(null)
                        .commit();
                final View.OnClickListener reserveAction = v -> {
                    getParentFragmentManager().beginTransaction()
                            .replace(R.id.fragmentContainerView, new BookingFragment(restaurant), null)
                            .setReorderingAllowed(true)
                            .addToBackStack(null)
                            .commit();
                };
                String noteText;
                RecyclerView reviewsList = view.findViewById(R.id.reviewsList);
                reviewsList.setLayoutManager(new LinearLayoutManager(requireContext()));
                if (!data.isEmpty()) {
                    float avrgGrade = 0f;
                    List<Review> arrayReviews = new ArrayList<>();
                    arrayReviews.addAll(data);
                    final ReviewAdapter arrayAdapter = new ReviewAdapter(
                            requireContext(),
                            arrayReviews
                    );
                    reviewsList.setAdapter(arrayAdapter);
                    for (Review rev : data) {
                        avrgGrade += rev.getRating();
                    }
                    avrgGrade /= data.size();
                    // Format avrgGrade to display two decimal places
                    noteText = String.format("%.1f", avrgGrade);
                } else {
                    noteText = "Pas noté";
                }
                //TODO : empecher de voir les avis s'il n'y en a pas
                final TextView restaurantBody = view.findViewById(R.id.restaurantBody);
                Button informationsToggleButton = view.findViewById(R.id.informationsToggleButton);
                Button avisToggleButton = view.findViewById(R.id.avisToggleButton);
                ((TextView) view.findViewById(R.id.restaurantName)).setText(restaurant.getTitle());
                ((TextView) view.findViewById(R.id.note)).setText(noteText);
                restaurantBody.setText(Html.fromHtml(
                        restaurant.getInfos().replaceAll("<img[^>]*>", ""),
                        Html.FROM_HTML_MODE_LEGACY)
                );
                view.findViewById(R.id.leaveReviewBtn).setOnClickListener(leaveReviewAction);
                view.findViewById(R.id.reserveBtn).setOnClickListener(reserveAction);
                informationsToggleButton.setOnClickListener(v -> {
                    if (!avisToggleButton.isEnabled()) {
                        restaurantBody.setVisibility(View.VISIBLE);
                        reviewsList.setVisibility(View.GONE);
                        informationsToggleButton.setEnabled(false);
                        avisToggleButton.setEnabled(true);
                    }
                });

                avisToggleButton.setOnClickListener(v -> {
                    if (!informationsToggleButton.isEnabled()) {
                        restaurantBody.setVisibility(View.GONE);
                        reviewsList.setVisibility(View.VISIBLE);
                        informationsToggleButton.setEnabled(true);
                        avisToggleButton.setEnabled(false);
                    }
                });

                // Set default visibility and button state
                restaurantBody.setVisibility(View.VISIBLE);
                //TODO : dans la reviewsList, si une review est trop longue, bouton afficher plus into popup
                reviewsList.setVisibility(View.GONE);
                informationsToggleButton.setEnabled(false);
                avisToggleButton.setEnabled(true);
            }

            @Override
            public void onError(Exception exception) {

            }
        });

        return view;
    }
}