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

import helloandroid.ut3.miniprojet.R;
import helloandroid.ut3.miniprojet.data.domain.Restaurant;
import helloandroid.ut3.miniprojet.view.fragment.booking.BookingFragment;
import helloandroid.ut3.miniprojet.view.fragment.review.form.ReviewFormFragment;

public class RestaurantFragment extends Fragment {

    private final View.OnClickListener leaveReviewAction = v -> {
        getParentFragmentManager().beginTransaction()
                .replace(R.id.fragmentContainerView, new ReviewFormFragment("test"), null)
                .setReorderingAllowed(true)
                .addToBackStack(null)
                .commit();
    };
    private final Restaurant restaurant;
    //TODO: Ajouter adresse
    //TODO: Ajouter avis


    public RestaurantFragment(@NotNull Restaurant restaurant) {
        this.restaurant = restaurant;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View.OnClickListener reserveAction = v -> {
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.fragmentContainerView, new BookingFragment(this.restaurant), null)
                    .setReorderingAllowed(true)
                    .addToBackStack(null)
                    .commit();
        };
        View view = inflater.inflate(R.layout.restaurant_fragment, container, false);
        ((TextView) view.findViewById(R.id.restaurantName)).setText(restaurant.getTitle());
        ((TextView) view.findViewById(R.id.restaurantBody)).setText(Html.fromHtml(restaurant.getInfos().replaceAll("<img[^>]*>", ""), Html.FROM_HTML_MODE_LEGACY));
        view.findViewById(R.id.leaveReviewBtn).setOnClickListener(leaveReviewAction);
        view.findViewById(R.id.reserveBtn).setOnClickListener(reserveAction);
        return view;
    }
}