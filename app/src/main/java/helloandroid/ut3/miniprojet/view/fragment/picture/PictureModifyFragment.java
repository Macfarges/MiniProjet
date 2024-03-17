package helloandroid.ut3.miniprojet.view.fragment.picture;

import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;

import helloandroid.ut3.miniprojet.R;

public class PictureModifyFragment extends Fragment {
    private final Uri pictureUri;

    public PictureModifyFragment(Uri pictureUri) {
        this.pictureUri = pictureUri;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_picture_modify, container, false);
        ImageView pictureView = view.findViewById(R.id.pictureView);
        Glide.with(requireContext()).load(pictureUri).into(pictureView);
        view.findViewById(R.id.deletePicture).setOnClickListener(
                v -> {
                    returnImageUri();
                }
        );
        return view;
    }

    private void returnImageUri() {
        Bundle bundle = new Bundle();
        bundle.putString("removedPictureURI", pictureUri.toString());
        getParentFragmentManager().setFragmentResult("removedPictureBundle", bundle);
        getParentFragmentManager().popBackStack();
    }
}
