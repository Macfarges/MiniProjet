package helloandroid.ut3.miniprojet.view.fragment.picture;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;

import helloandroid.ut3.miniprojet.R;

public class PictureModifyFragment extends Fragment {
    private final Uri pictureUri;
    private String newPictureURI = null;

    public PictureModifyFragment(Uri pictureUri) {
        this.pictureUri = pictureUri;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                Bundle bundle = new Bundle();
                bundle.putString("newPictureURI", newPictureURI);
                getParentFragmentManager().setFragmentResult("newPictureBundle", bundle);
                Log.d("Going back", bundle.getString("newPictureUri"));
                getParentFragmentManager().popBackStack();
            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_picture_modify, container, false);
        ImageView pictureView = view.findViewById(R.id.pictureView);
        Glide.with(requireContext()).load(pictureUri).into(pictureView);
        getParentFragmentManager().setFragmentResultListener("newPictureBundle", this, (requestKey, result) -> {
            newPictureURI = result.getString("newPictureURI");
            if (newPictureURI == null) {
                return;
            }
            Glide.with(requireContext()).load(newPictureURI).into(pictureView);
            //TODO: voir comment mettre en place la modification en soit
            Log.d("PictureURI", newPictureURI);
        });
        view.findViewById(R.id.modifyPicture).setOnClickListener(
                v -> {
                    getParentFragmentManager().beginTransaction()
                            .replace(R.id.fragmentContainerView, new PictureFormFragment(), null)
                            .setReorderingAllowed(true)
                            .addToBackStack("pictureForm")
                            .commit();
                }
        );
        return view;
    }


}
