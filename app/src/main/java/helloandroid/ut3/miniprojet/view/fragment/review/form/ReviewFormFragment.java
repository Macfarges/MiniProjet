package helloandroid.ut3.miniprojet.view.fragment.review.form;

import android.net.Uri;
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

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.flexbox.FlexboxLayout;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import helloandroid.ut3.miniprojet.R;
import helloandroid.ut3.miniprojet.data.domain.Restaurant;
import helloandroid.ut3.miniprojet.view.fragment.picture.PictureFormFragment;

public class ReviewFormFragment extends Fragment {
    private final Restaurant restaurant;
    private final List<ImageButton> pictures = new ArrayList<>();
    private TextView reviewEditText;
    private TextView picturesTv;
    private StorageReference storageReference;
    private FlexboxLayout picturesLayout;
    private LinearProgressIndicator progressIndicator;
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
        storageReference = FirebaseStorage.getInstance().getReference();
        progressIndicator = view.findViewById(R.id.progressIndicator);
        ((TextView) view.findViewById(R.id.restaurantTitle)).setText(restaurant.getTitle());
        view.findViewById(R.id.addPictureBtn).setOnClickListener(v -> getParentFragmentManager().beginTransaction()
                .replace(R.id.fragmentContainerView, new PictureFormFragment(), null)
                .setReorderingAllowed(true)
                .addToBackStack("pictureForm")
                .commit());
        getParentFragmentManager().setFragmentResultListener("newPictureBundle", this, (requestKey, result) -> {
            String newPictureURI = result.getString("newPictureURI");
            if (newPictureURI == null)
                return;
            pictures.add(pushNewPictureToLayout(Uri.parse(newPictureURI)));
        });
        reviewEditText = view.findViewById(R.id.reviewEditText);
        picturesTv = view.findViewById(R.id.picturesTv);
        reviewEditText.setText(savedInstanceState != null ? savedInstanceState.getCharSequence("reviewEditTextText") : "");
        stars = new ArrayList<>();
        stars.add(view.findViewById(R.id.star1));
        stars.add(view.findViewById(R.id.star2));
        stars.add(view.findViewById(R.id.star3));
        stars.add(view.findViewById(R.id.star4));
        stars.add(view.findViewById(R.id.star5));
        updateStarsUI();
        for (ImageView star : stars) {
            star.setOnClickListener(v -> onStarClick(star));
        }
        picturesLayout = view.findViewById(R.id.picturesLayout);
        for (ImageButton picture : pictures) {
//            picturesLayout.removeView(picture); for the ondestroy or try listview todo
            picturesLayout.addView(picture);
        }
        updatePicturesCount(pictures.size());
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
        Toast.makeText(requireContext(), "Review submitted with rating: " + rating, Toast.LENGTH_SHORT).show();
    }

    public void updatePicturesCount(int count) {
        picturesTv.setText(getString(R.string.picturesLabel, count));
    }

    private ImageButton pushNewPictureToLayout(Uri pictureUri) {
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
                .into(pictureBtn);
        picturesLayout.addView(pictureBtn);
        return pictureBtn;
    }

    private void pushPictures() {
        String filePath = "reviews/pictures/" + UUID.randomUUID().toString();
        StorageReference ref = storageReference.child(filePath);
        for (ImageButton picture : pictures) {
            ref.putFile((Uri) picture.getTag())
                    .addOnSuccessListener(taskSnapshot -> {

                    })
                    .addOnFailureListener(e -> Toast.makeText(requireContext(), "Failed!" + e.getMessage(), Toast.LENGTH_SHORT).show())
                    .addOnProgressListener(taskSnapshot -> {
                        progressIndicator.setMax(Math.toIntExact(taskSnapshot.getTotalByteCount()));
                        progressIndicator.setProgress(Math.toIntExact(taskSnapshot.getBytesTransferred()));
                    });
        }
    }
}
