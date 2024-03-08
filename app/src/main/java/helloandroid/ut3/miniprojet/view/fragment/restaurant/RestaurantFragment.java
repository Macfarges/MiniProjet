package helloandroid.ut3.miniprojet.view.fragment.restaurant;

import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import helloandroid.ut3.miniprojet.R;
import helloandroid.ut3.miniprojet.data.domain.Restaurant;
import helloandroid.ut3.miniprojet.data.domain.Review;
import helloandroid.ut3.miniprojet.data.service.FirebaseManager;
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

                if (!data.isEmpty()) {
                    float avrgGrade = 0f;
                    for (Review rev : data) {
                        avrgGrade += rev.getRating();
                    }
                    avrgGrade /= data.size();
                    // Format avrgGrade to display two decimal places
                    noteText = String.format("%.1f", avrgGrade);
                } else {
                    noteText = "Pas noté";
                }
                ((TextView) view.findViewById(R.id.restaurantName)).setText(restaurant.getTitle());
                ((TextView) view.findViewById(R.id.note)).setText(noteText);
                ((TextView) view.findViewById(R.id.restaurantBody)).setText(Html.fromHtml(restaurant.getInfos().replaceAll("<img[^>]*>", ""), Html.FROM_HTML_MODE_LEGACY));
                view.findViewById(R.id.leaveReviewBtn).setOnClickListener(leaveReviewAction);
                view.findViewById(R.id.reserveBtn).setOnClickListener(reserveAction);
            }

            @Override
            public void onError(Exception exception) {

            }
        });

        return view;
    }
}