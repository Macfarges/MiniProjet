package helloandroid.ut3.miniprojet.view.fragment.generic;

import android.app.AlertDialog;
import android.app.Dialog;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.flexbox.AlignItems;
import com.google.android.flexbox.FlexboxLayout;
import com.google.android.flexbox.FlexboxLayoutManager;

import java.util.List;

import helloandroid.ut3.miniprojet.R;
import helloandroid.ut3.miniprojet.data.service.FirebaseManager;

public class TextPopupFragment extends DialogFragment {

    private String text;
    private String title;
    private List<String> urls;
    private FlexboxLayout picturesLayout;

    public TextPopupFragment() {
        // Required empty public constructor
    }

    public TextPopupFragment(String title) {
        this.title = title;
    }

    // Use this method to set the text to be displayed in the popup
    public void setText(String text) {
        this.text = text;
    }

    // Use this method to set the title of the popup
    public void setTitle(String title) {
        this.title = title;
    }

    public void setUrls(List<String> urls) {
        this.urls = urls;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        if (title == null || title.isEmpty()) {
            title = "Default Popup Title";
        }

        View view = requireActivity().getLayoutInflater().inflate(R.layout.fragment_review_details, null);
        picturesLayout = view.findViewById(R.id.picturesList);
        FlexboxLayoutManager.LayoutParams layoutParams = new FlexboxLayoutManager.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        layoutParams.setFlexGrow(1);
        layoutParams.setFlexBasisPercent(0.5f);
        layoutParams.setAlignSelf(AlignItems.CENTER);

        if (urls != null) {
            view.findViewById(R.id.loadingProgressBar).setVisibility(View.VISIBLE);
            FirebaseManager.getInstance().getRemoteUrls(urls, new FirebaseManager.DataCallback<List<String>>() {
                @Override
                public void onSuccess(List<String> data) {
                    if (data != null) {
                        for (String url : data) {
                            pushNewPictureToLayout(Uri.parse(url), layoutParams); // Pass the layout parameters
                        }
                        view.findViewById(R.id.loadingProgressBar).setVisibility(View.GONE);
                    }
                }

                @Override
                public void onError(Exception exception) {

                }
            });
        }


        builder.setTitle(title)
                .setMessage(text)
                .setView(view);

        return builder.create();
    }

    private ImageButton pushNewPictureToLayout(Uri pictureUri, FlexboxLayoutManager.LayoutParams layoutParams) {
        ImageButton pictureBtn = new ImageButton(requireContext());
        pictureBtn.setAdjustViewBounds(true);
        pictureBtn.setMaxHeight(500);
        RequestOptions requestOptions = new RequestOptions()
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                .skipMemoryCache(false);
        Glide.with(requireContext())
                .load(pictureUri)
                .apply(requestOptions)
                .into(pictureBtn);
        picturesLayout.addView(pictureBtn, layoutParams); // Add the image button with the layout parameters
        return pictureBtn;
    }
}