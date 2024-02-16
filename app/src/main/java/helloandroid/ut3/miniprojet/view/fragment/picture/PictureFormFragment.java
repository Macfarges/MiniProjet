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
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.UUID;

import helloandroid.ut3.miniprojet.R;

public class PictureFormFragment extends Fragment {
    StorageReference storageReference;
    LinearProgressIndicator progressIndicator;
    Uri image;
    Button uploadImage, selectImage;
    ImageView imageView;
    private final ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            if (result.getResultCode() == RESULT_OK) {
                if (result.getData() != null) {
                    uploadImage.setEnabled(true);
                    image = result.getData().getData();
                    Glide.with(requireContext()).load(image).into(imageView);
                }
            } else if (image == null) {
                Toast.makeText(requireContext(), "Please select an image", Toast.LENGTH_SHORT).show();
            }
        }
    });

    public PictureFormFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_picture_form, container, false);

        storageReference = FirebaseStorage.getInstance().getReference();

        progressIndicator = view.findViewById(R.id.progress);

        imageView = view.findViewById(R.id.imageView);
        selectImage = view.findViewById(R.id.selectImage);
        uploadImage = view.findViewById(R.id.uploadImage);

        selectImage.setOnClickListener(view1 -> {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            activityResultLauncher.launch(intent);
        });

        uploadImage.setOnClickListener(view2 -> uploadImage(image));


        return view;
    }

    private void uploadImage(Uri file) {
        String filePath = "reviews/images/" + UUID.randomUUID().toString();
        StorageReference ref = storageReference.child(filePath);
        ref.putFile(file)
                .addOnSuccessListener(taskSnapshot -> {
                    Bundle bundle = new Bundle();
                    bundle.putString("newImageURI", filePath);
                    getParentFragmentManager().setFragmentResult("newImageBundle", bundle);
                    getParentFragmentManager().popBackStack();
                })
                .addOnFailureListener(e -> Toast.makeText(requireContext(), "Failed!" + e.getMessage(), Toast.LENGTH_SHORT).show())
                .addOnProgressListener(taskSnapshot -> {
                    progressIndicator.setMax(Math.toIntExact(taskSnapshot.getTotalByteCount()));
                    progressIndicator.setProgress(Math.toIntExact(taskSnapshot.getBytesTransferred()));
                });
    }

}