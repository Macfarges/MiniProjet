package helloandroid.ut3.miniprojet.view.fragment.generic;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class TextPopupFragment extends DialogFragment {

    private String text;
    private String title;

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

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        if (title == null || title.isEmpty()) {
            title = "Default Popup Title";
        }
        builder.setTitle(title)
                .setMessage(text)
                .setPositiveButton("Retour", (dialog, which) -> dismiss());

        return builder.create();
    }
}