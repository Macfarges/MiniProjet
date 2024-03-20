package helloandroid.ut3.miniprojet.view.fragment.review.form;

import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
import com.google.android.flexbox.FlexboxLayout;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

import helloandroid.ut3.miniprojet.R;
import helloandroid.ut3.miniprojet.data.domain.Restaurant;
import helloandroid.ut3.miniprojet.data.domain.Review;
import helloandroid.ut3.miniprojet.data.service.FirebaseManager;
import helloandroid.ut3.miniprojet.view.fragment.picture.PictureFormFragment;
import helloandroid.ut3.miniprojet.view.fragment.picture.PictureModifyFragment;
import helloandroid.ut3.utils.FileUtils;

public class ReviewFormFragment extends Fragment {
    private final Restaurant restaurant;
    private final Map<Uri, ImageButton> picturesMap = new HashMap<>();
    private final String genericErrorMessage = "Echec de l'ajout de votre avis";
    // TextWatcher to listen for changes in review and source EditText fields
    private final TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            // Enable submit button only if both review and source fields are not empty
            EditText reviewEditText = requireView().findViewById(R.id.reviewEditText);
            EditText sourceEditText = requireView().findViewById(R.id.sourceEditText);
            Button submitReviewBtn = requireView().findViewById(R.id.submitReviewBtn);
            submitReviewBtn.setEnabled(!TextUtils.isEmpty(reviewEditText.getText()) &&
                    !TextUtils.isEmpty(sourceEditText.getText()));
        }
    };
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
            if (picturesMap.containsValue(newPictureURI)) {
                Toast.makeText(requireContext(), "L'image est déjà présente", Toast.LENGTH_SHORT).show();
                return;
            }
            pushNewPictureToLayout(Uri.parse(newPictureURI));
            //Ceci est un bug je pense. Si on met pas + 1 c pas bon
            updatePicturesCount(picturesMap.size() + 1);
        });
        getParentFragmentManager().setFragmentResultListener("removedPictureBundle", this, (requestKey, result) -> {
            String toRemovePictureURI = result.getString("removedPictureURI");
            if (toRemovePictureURI == null)
                return;
            picturesLayout.removeView(picturesMap.get(Uri.parse(toRemovePictureURI)));
            picturesMap.remove(Uri.parse(toRemovePictureURI));
            updatePicturesCount(picturesMap.size());
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
        for (Uri uri : picturesMap.keySet()) {
            ImageButton pictureBtn = picturesMap.get(uri);
            picturesLayout.addView(pictureBtn);
            pictureBtn.setOnClickListener(v -> onModifyClick(uri));
        }
        updatePicturesCount(picturesMap.size());
        EditText reviewEditText = view.findViewById(R.id.reviewEditText);
        EditText sourceEditText = view.findViewById(R.id.sourceEditText);
        Button submitReviewBtn = view.findViewById(R.id.submitReviewBtn);

        reviewEditText.addTextChangedListener(textWatcher);
        sourceEditText.addTextChangedListener(textWatcher);

        // Initially disable submit button
        submitReviewBtn.setEnabled(false);

        submitReviewBtn.setOnClickListener(v -> onSubmitClick(view));
        return view;
    }

    private void onModifyClick(Uri pictureUri) {
        getParentFragmentManager().beginTransaction()
                .replace(
                        R.id.fragmentContainerView,
                        new PictureModifyFragment(pictureUri, false),
                        null
                )
                .setReorderingAllowed(true)
                .addToBackStack("pictureModify")
                .commit();
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
                ),
                new FirebaseManager.DataCallback<Review>() {
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
                .addListener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        // Handle load failure if needed
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        picturesMap.put(pictureUri, pictureBtn);
                        pictureBtn.setOnClickListener(v -> onModifyClick(pictureUri));
                        // Notify the fragment that the picture has been loaded
                        return false;
                    }
                })
                .into(pictureBtn);
        picturesLayout.addView(pictureBtn);
        return pictureBtn;
    }

    private List<String> pushPictures() {
        List<String> pictureURLs = new ArrayList<>();
        long totalBytes = 0;
        AtomicLong currentUploadedBytes = new AtomicLong();
        for (Uri pictureURI : picturesMap.keySet()) {
            totalBytes += FileUtils.getFileSizeFromUri(requireContext(), pictureURI);
        }
        progressIndicator.setMax(Math.toIntExact(totalBytes));
        for (Uri pictureURI : picturesMap.keySet()) {
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
        for (ImageButton pictureBtn : picturesMap.values()) {
            picturesLayout.removeView(pictureBtn);
        }
    }
}
