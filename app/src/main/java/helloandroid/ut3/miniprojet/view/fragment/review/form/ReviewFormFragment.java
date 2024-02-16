package helloandroid.ut3.miniprojet.view.fragment.review.form;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentResultListener;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.flexbox.FlexboxLayout;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import helloandroid.ut3.miniprojet.R;
import helloandroid.ut3.miniprojet.data.domain.Restaurant;
import helloandroid.ut3.miniprojet.view.fragment.picture.PictureFormFragment;

public class ReviewFormFragment extends Fragment {
    private final Restaurant restaurant;
    private TextView reviewEditText;
    private TextView picturesTv;
    private List<ImageView> stars;
    private int rating = 3;

    public ReviewFormFragment(Restaurant restaurant) {
        this.restaurant = restaurant;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_review_form, container, false);
        ((TextView) view.findViewById(R.id.restaurantTitle)).setText(restaurant.getTitle());
        view.findViewById(R.id.addImageButton).setOnClickListener(v -> getParentFragmentManager().beginTransaction()
                .replace(R.id.fragmentContainerView, new PictureFormFragment(), null)
                .setReorderingAllowed(true)
                .addToBackStack(null)
                .commit());
        getParentFragmentManager().setFragmentResultListener("newImageBundle", this, new FragmentResultListener() {
            @Override
            public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle result) {
                // Handle the result here
                String value = result.getString("newImageURI");
                if (value != null) {
                    //todo display the new image in the layout
                    //todo push new items of images containing the image path and the review id on the submit button
                }
            }
        });
        reviewEditText = view.findViewById(R.id.reviewEditText);
        picturesTv = view.findViewById(R.id.picturesTv);
        updatePicturesCount(0);
        stars = new ArrayList<>();
        stars.add(view.findViewById(R.id.star1));
        stars.add(view.findViewById(R.id.star2));
        stars.add(view.findViewById(R.id.star3));
        stars.add(view.findViewById(R.id.star4));
        stars.add(view.findViewById(R.id.star5));
        for (ImageView star : stars) {
            star.setOnClickListener(v -> onStarClick(star));
        }

        FlexboxLayout imagesLayout = view.findViewById(R.id.imagesLayout);
        List<String> imageUrlList = Arrays.asList(
                "https://via.placeholder.com/150",
                "https://via.placeholder.com/200",
                "https://via.placeholder.com/250"
        );

        for (String imageUrl : imageUrlList) {
            ImageButton imageButton = new ImageButton(requireContext());
            int imageSize = getResources().getDimensionPixelSize(R.dimen.imgLittleSquareDim);
            imageButton.setLayoutParams(new ViewGroup.LayoutParams(imageSize, imageSize));
            imageButton.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageButton.setBackground(null);
            imageButton.setPadding(0, 0, 0, 0);

            // Load image from URL using Glide library
            RequestOptions requestOptions = new RequestOptions()
                    .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                    .skipMemoryCache(false);
            Glide.with(requireContext())
                    .load(imageUrl)
                    .apply(requestOptions)
                    .into(imageButton);
            imagesLayout.addView(imageButton);
        }


        view.findViewById(R.id.submitReviewBtn).setOnClickListener(v -> onSubmitClick());
        return view;
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
