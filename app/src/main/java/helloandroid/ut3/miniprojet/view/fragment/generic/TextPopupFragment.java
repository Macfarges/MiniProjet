package helloandroid.ut3.miniprojet.view.fragment.generic;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.android.flexbox.FlexboxLayout;

import java.util.List;

import helloandroid.ut3.miniprojet.R;

public class TextPopupFragment extends DialogFragment {

    private String text;
    private String title;
    private List<String> urls;

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
        FlexboxLayout picturesLayout = view.findViewById(R.id.picturesList);
        if (urls != null) {
            for (String url : urls) {
                //ImageView imageView = new ImageView(requireActivity());
                //Glide.with(requireContext()).load(url).into(imageView);
                //picturesLayout.addView(imageView);
            }
        }

        builder.setTitle(title)
                .setMessage(text)
                //set view as view with pictures
                .setView(view);

        return builder.create();
    }
}