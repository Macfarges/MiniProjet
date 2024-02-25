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
import java.util.concurrent.atomic.AtomicLong;

import helloandroid.ut3.miniprojet.R;
import helloandroid.ut3.miniprojet.data.domain.Restaurant;
import helloandroid.ut3.miniprojet.data.domain.Review;
import helloandroid.ut3.miniprojet.data.service.FirebaseManager;
import helloandroid.ut3.miniprojet.view.fragment.picture.PictureFormFragment;
import helloandroid.ut3.utils.FileUtils;

public class ReviewFormFragment extends Fragment {
    private final Restaurant restaurant;
    private final List<ImageButton> pictures = new ArrayList<>();
    private final List<Uri> pictureURIs = new ArrayList<>();
    private final String genericErrorMessage = "Echec de l'ajout de votre avis";
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
        view.findViewById(R.id.addPictureBtn).setOnClickListener(v ->
                getParentFragmentManager().beginTransaction()
                        .replace(R.id.fragmentContainerView, new PictureFormFragment(), null)
                        .setReorderingAllowed(true)
                        .addToBackStack("pictureForm")
                        .commit()
        );
        getParentFragmentManager().setFragmentResultListener("newPictureBundle", this, (requestKey, result) -> {
            String newPictureURI = result.getString("newPictureURI");
            if (newPictureURI == null)
                return;
            pictureURIs.add(Uri.parse(newPictureURI));
            pictures.add(pushNewPictureToLayout(Uri.parse(newPictureURI)));
            updatePicturesCount(pictures.size());
        });
        picturesTv = view.findViewById(R.id.picturesTv);
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
        for (ImageButton pictureBtn : pictures) {
            picturesLayout.addView(pictureBtn);
        }
        updatePicturesCount(pictures.size());
        view.findViewById(R.id.submitReviewBtn).setOnClickListener(v -> onSubmitClick(view));
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

    private void onSubmitClick(View view) {
        FirebaseManager.getInstance().addReview(
                new Review(
                        ((TextView) view.findViewById(R.id.reviewEditText)).getText().toString(),
                        ((TextView) view.findViewById(R.id.sourceEditText)).getText().toString(),
                        rating,
                        pushPictures(),
                        restaurant.getId()
                )
                , new FirebaseManager.DataCallback<Review>() {
                    @Override
                    public void onSuccess(Review review) {
                        Toast.makeText(requireContext(), "Votre avis a été envoyé !", Toast.LENGTH_SHORT).show();
                        getParentFragmentManager().popBackStack();
                    }

                    @Override
                    public void onError(Exception e) {
                        // Handle error
                        Toast.makeText(requireContext(), genericErrorMessage, Toast.LENGTH_SHORT).show();
                    }
                }
        );
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

    private List<String> pushPictures() {
        List<String> pictureURLs = new ArrayList<>();
        long totalBytes = 0;
        AtomicLong currentUploadedBytes = new AtomicLong();
        for (Uri pictureURI : pictureURIs) {
            totalBytes += FileUtils.getFileSizeFromUri(requireContext(), pictureURI);
        }
        progressIndicator.setMax(Math.toIntExact(totalBytes));
        for (Uri pictureURI : pictureURIs) {
            String imagePath = String.format("reviews/%s/pictures/%s", restaurant.getId(), UUID.randomUUID().toString());
            pictureURLs.add(imagePath);
            storageReference.child(imagePath).putFile(pictureURI)
                    .addOnSuccessListener(taskSnapshot -> {
                    })
                    .addOnFailureListener(e -> Toast.makeText(requireContext(), genericErrorMessage,
                            Toast.LENGTH_SHORT).show()
                    )
                    .addOnProgressListener(taskSnapshot -> {
                        currentUploadedBytes.addAndGet(taskSnapshot.getBytesTransferred());
                        progressIndicator.setProgress(Math.toIntExact(currentUploadedBytes.get()));
                    });
        }
        return pictureURLs;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        for (ImageButton pictureBtn : pictures) {
            picturesLayout.removeView(pictureBtn);
        }
    }
}
