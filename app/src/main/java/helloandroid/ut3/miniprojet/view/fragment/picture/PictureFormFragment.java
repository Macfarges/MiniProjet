package helloandroid.ut3.miniprojet.view.fragment.picture;

import static android.app.Activity.RESULT_OK;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
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
import helloandroid.ut3.utils.FileUtils;

public class PictureFormFragment extends Fragment {
    StorageReference storageReference;
    Uri pictureUri;
    ViewGroup choosePictureLayout, filtersLayout;
    Button addPictureBtn, selectPictureBtn, takePictureBtn, filter1Btn, filter2Btn;
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
                                choosePictureLayout.setVisibility(View.GONE);
                                pictureView.setVisibility(View.VISIBLE);
                                pictureUri = FileUtils.saveBitmapToFile(requireContext(), photo);
                            }
                        }
                    } else if (data.getData() != null) {
                        pictureUri = data.getData();
                    }
                    choosePictureLayout.setVisibility(View.GONE);
                    filtersLayout.setVisibility(View.VISIBLE);
                    pictureView.setVisibility(View.VISIBLE);
                    Glide.with(requireContext()).load(pictureUri).into(pictureView);
                    addPictureBtn.setEnabled(true);
                }
            }
        }
    });
    boolean isOnFilter1, isOnFilter2;


    public PictureFormFragment() {
        //todo should have an uri as parameter for edition
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_picture_form, container, false);
        storageReference = FirebaseStorage.getInstance().getReference();
        pictureView = view.findViewById(R.id.pictureView);
        choosePictureLayout = view.findViewById(R.id.choosePictureLayout);
        filtersLayout = view.findViewById(R.id.filterLayout);
        filter1Btn = view.findViewById(R.id.filter1Btn);
        filter2Btn = view.findViewById(R.id.filter2Btn);
        selectPictureBtn = view.findViewById(R.id.selectPicture);
        takePictureBtn = view.findViewById(R.id.takePicture);
        addPictureBtn = view.findViewById(R.id.addPicture);
        selectPictureBtn.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            activityResultLauncher.launch(intent);
        });
        takePictureBtn.setOnClickListener(v -> {
            Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
            activityResultLauncher.launch(intent);
        });
        //TODO : Clic1 > Prise en compte de microphone + changement visuel
        //TODO : Clic2 > Confirmation visuel
        //TODO : Clic 3 > Annulation filtre (puis prochain click = Clic 1)
        filter1Btn.setOnClickListener(v -> {
            if (!isOnFilter1) {
                pictureView.setImageBitmap(
                        FileUtils.applyDankFilter(
                                FileUtils.getBitmapFromImageView(pictureView),
                                10F)
                );
                filter1Btn.setBackgroundColor(Color.RED);
            } else {
                Glide.with(requireContext()).load(pictureUri).into(pictureView);
                if (isOnFilter2) {
                    pictureView.setImageBitmap(
                            FileUtils.applyRandomColorFilter(
                                    FileUtils.getBitmapFromImageView(pictureView),
                                    10F)
                    );
                }
                filter1Btn.setBackgroundColor(androidx.appcompat.R.attr.colorPrimary);
            }
            isOnFilter1 = !isOnFilter1;
        });
        filter2Btn.setOnClickListener(v -> {
            if (!isOnFilter2) {
                pictureView.setImageBitmap(
                        FileUtils.applyRandomColorFilter(
                                FileUtils.getBitmapFromImageView(pictureView),
                                10F)
                );
                filter2Btn.setBackgroundColor(Color.RED);
            } else {
                Glide.with(requireContext()).load(pictureUri).into(pictureView);
                if (isOnFilter1) {
                    pictureView.setImageBitmap(
                            FileUtils.applyDankFilter(
                                    FileUtils.getBitmapFromImageView(pictureView),
                                    10F)
                    );
                }
                filter2Btn.setBackgroundColor(androidx.appcompat.R.attr.colorPrimary);
            }
            isOnFilter2 = !isOnFilter2;
        });
        addPictureBtn.setOnClickListener(v -> addPictureBtn());
        return view;
    }

    private void addPictureBtn() {
        Bitmap bitmap = FileUtils.getBitmapFromImageView(pictureView);
        pictureUri = FileUtils.saveBitmapToFile(requireContext(), bitmap);
        returnImageUri();
    }

    private void returnImageUri() {
        Bundle bundle = new Bundle();
        bundle.putString("newPictureURI", pictureUri.toString());
        getParentFragmentManager().setFragmentResult("newPictureBundle", bundle);
        getParentFragmentManager().popBackStack();
    }
}