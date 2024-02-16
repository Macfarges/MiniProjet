package helloandroid.ut3.miniprojet.view.fragment.picture;

import static android.app.Activity.RESULT_OK;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import helloandroid.ut3.miniprojet.R;

public class PictureFormFragment extends Fragment {
    StorageReference storageReference;
    Uri picture;
    Button returnPictureBtn, selectPictureBtn;
    ImageView pictureView;
    private final ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            if (result.getResultCode() == RESULT_OK) {
                if (result.getData() != null) {
                    returnPictureBtn.setEnabled(true);
                    picture = result.getData().getData();
                    Glide.with(requireContext()).load(picture).into(pictureView);
                }
            }
        }
    });

    public PictureFormFragment() {
        //todo should have an uri as parameter for edition
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_picture_form, container, false);
        storageReference = FirebaseStorage.getInstance().getReference();
        pictureView = view.findViewById(R.id.pictureView);
        selectPictureBtn = view.findViewById(R.id.selectPicture);
        returnPictureBtn = view.findViewById(R.id.returnPicture);
        selectPictureBtn.setOnClickListener(view1 -> {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            activityResultLauncher.launch(intent);
        });
        returnPictureBtn.setOnClickListener(view2 -> returnImageUri());
        return view;
    }

    private void returnImageUri() {
        Bundle bundle = new Bundle();
        bundle.putString("newPictureURI", picture.toString());
        getParentFragmentManager().setFragmentResult("newPictureBundle", bundle);
        getParentFragmentManager().popBackStack();
    }
}