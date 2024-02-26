package helloandroid.ut3.miniprojet.view.fragment.picture;

import static android.app.Activity.RESULT_OK;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import helloandroid.ut3.miniprojet.R;
import helloandroid.ut3.utils.FileUtils;

public class PictureFormFragment extends Fragment {
    StorageReference storageReference;
    Uri picture;
    LinearLayout choosePictureButtons;
    Button addPictureBtn, selectPictureBtn, takePictureBtn;
    ImageView pictureView;
    private final ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            if (result.getResultCode() == RESULT_OK) {
                Intent data = result.getData();
                if (data != null) {
                    if (data.getAction() != null && data.getAction().equals("inline-data")) {
                        Bundle extras = data.getExtras();
                        if (extras != null) {
                            Bitmap photo = (Bitmap) extras.get("data");
                            if (photo != null) {
                                choosePictureButtons.setVisibility(View.GONE);
                                pictureView.setVisibility(View.VISIBLE);
                                picture = FileUtils.saveBitmapToFile(requireContext(), photo);
                            }
                        }
                    } else if (data.getData() != null) {
                        picture = data.getData();
                    }
                    choosePictureButtons.setVisibility(View.GONE);
                    pictureView.setVisibility(View.VISIBLE);
                    Glide.with(requireContext()).load(picture).into(pictureView);
                    addPictureBtn.setEnabled(true);
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
        choosePictureButtons = view.findViewById(R.id.choosePictureButtons);
        selectPictureBtn = view.findViewById(R.id.selectPicture);
        takePictureBtn = view.findViewById(R.id.takePicture);
        addPictureBtn = view.findViewById(R.id.addPicture);
        selectPictureBtn.setOnClickListener(view1 -> {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            activityResultLauncher.launch(intent);
        });
        takePictureBtn.setOnClickListener(view12 -> {
            Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
            activityResultLauncher.launch(intent);
        });
        addPictureBtn.setOnClickListener(view2 -> returnImageUri());
        return view;
    }

    private void returnImageUri() {
        Bundle bundle = new Bundle();
        bundle.putString("newPictureURI", picture.toString());
        getParentFragmentManager().setFragmentResult("newPictureBundle", bundle);
        getParentFragmentManager().popBackStack();
    }
}