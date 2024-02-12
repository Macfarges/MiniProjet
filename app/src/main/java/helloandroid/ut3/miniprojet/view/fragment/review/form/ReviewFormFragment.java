package helloandroid.ut3.miniprojet.view.fragment.review.form;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.List;

import helloandroid.ut3.miniprojet.R;

public class ReviewFormFragment extends Fragment {
    private final String restaurantId;
    private TextView reviewTv;
    private TextView picturesTv;
    private List<ImageView> stars;
    private int rating = 0;

    public ReviewFormFragment(String restaurantId) {
        this.restaurantId = restaurantId;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_review_form, container, false);
        reviewTv = rootView.findViewById(R.id.reviewTv);
        picturesTv = rootView.findViewById(R.id.picturesTv);
        updatePicturesCount(0);
        stars = new ArrayList<>();
        stars.add((ImageView) rootView.findViewById(R.id.star1));
        stars.add((ImageView) rootView.findViewById(R.id.star2));
        stars.add((ImageView) rootView.findViewById(R.id.star3));
        stars.add((ImageView) rootView.findViewById(R.id.star4));
        stars.add((ImageView) rootView.findViewById(R.id.star5));
        for (ImageView star : stars) {
            star.setOnClickListener(v -> onStarClick(star));
        }
        rootView.findViewById(R.id.submitReviewBtn).setOnClickListener(v -> onSubmitClick());
        return rootView;
    }

    private void onStarClick(ImageView clickedStar) {
        int clickedIndex = stars.indexOf(clickedStar);
        rating = clickedIndex + 1;
        updateStarsUI();
    }

    private void updateStarsUI() {
        for (int i = 0; i < 5; i++) {
            if (i < rating) {
                stars.get(i).setImageResource(R.drawable.ic_star_filled_34);
            } else {
                stars.get(i).setImageResource(R.drawable.ic_star_outline_34);
            }
        }
    }

    private void onSubmitClick() {
        // You can submit the review with the selected rating and review text
        Toast.makeText(requireContext(), "Review submitted with rating: " + rating, Toast.LENGTH_SHORT).show();
    }

    // This method updates the number of pictures added to the review
    public void updatePicturesCount(int count) {
        picturesTv.setText(getString(R.string.picturesLabel, count));
    }
}
