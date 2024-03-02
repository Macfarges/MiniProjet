package helloandroid.ut3.miniprojet.view.fragment.picture;

import static android.app.Activity.RESULT_OK;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
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
import helloandroid.ut3.utils.AccelerometerUtils;
import helloandroid.ut3.utils.FileUtils;
import helloandroid.ut3.utils.MicrophoneUtils;
import helloandroid.ut3.utils.PictureFiltersUtils;

public class PictureFormFragment extends Fragment implements MicrophoneUtils.MicrophoneCallback, AccelerometerUtils.AccelerometerCallback {
    private static final Handler mainHandler = new Handler(Looper.getMainLooper()); // Create handler associated with the main thread
    static Uri pictureUri;
    static ImageView pictureView;
    static Bitmap previousPicture1 = null;
    static Bitmap previousPicture2 = null;
    static Bitmap currentPicture1 = null;
    static Bitmap currentPicture2 = null;
    private final Handler accHandler = new Handler();
    private final Handler micHandler = new Handler();
    private final Handler backgroundHandler = new Handler(Looper.getMainLooper());
    StorageReference storageReference;
    ViewGroup choosePictureLayout, filtersLayout;
    Button addPictureBtn, selectPictureBtn, takePictureBtn, filter1Btn, filter2Btn;
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
    private int filter1State = 0, filter2State = 0;

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
        filter1Btn.setOnClickListener(v -> {
            switch (filter1State) {
                case 0:
                    // Apply Filter 1
                    filter2Btn.setVisibility(View.GONE);
                    filter1Btn.setText("Valider filtre");
                    filter1Btn.setBackgroundColor(Color.GREEN);
                    previousPicture1 = FileUtils.getBitmapFromImageView(pictureView);
                    MicrophoneUtils.setMicrophoneCallback(this);
                    MicrophoneUtils.startRecording(requireContext(), requireActivity());
                    filter1State = 1;
                    break;

                case 1:
                    // Return the last filter
                    MicrophoneUtils.stopRecording(() -> {
                        currentPicture1 = FileUtils.getBitmapFromImageView(pictureView);
                        filter1Btn.setBackgroundColor(Color.RED);
                        filter1Btn.setText("Annuler filtre 1");
                        filter1State = 2;
                        filter2Btn.setVisibility(View.VISIBLE);
                    });
                    break;

                case 2:
                    // Remove the filter and show the original image
                    if (currentPicture2 != null) {
                        pictureView.setImageBitmap(currentPicture2);
                    } else {
                        Glide.with(requireContext()).load(pictureUri).into(pictureView);
                    }
                    filter1Btn.setBackgroundColor(androidx.appcompat.R.attr.colorPrimary);
                    filter1Btn.setText(R.string.filtre_1);
                    previousPicture1 = null;
                    currentPicture1 = null;
                    filter1State = 0;
                    break;
            }
        });

        filter2Btn.setOnClickListener(v -> {
            switch (filter2State) {
                case 0:
                    // Apply Filter 2 based on accelerometer data
                    filter1Btn.setVisibility(View.GONE);
                    filter2Btn.setText("Valider filtre");
                    filter2Btn.setBackgroundColor(Color.GREEN);
                    previousPicture2 = FileUtils.getBitmapFromImageView(pictureView);
                    AccelerometerUtils.setAccelerometerCallback(this);
                    AccelerometerUtils.startAccelerometer(requireContext());
                    filter2State = 1;
                    break;
                case 1:
                    // Return the last filter
                    currentPicture2 = FileUtils.getBitmapFromImageView(pictureView);
                    AccelerometerUtils.stopAccelerometer();
                    filter2Btn.setBackgroundColor(Color.RED);
                    filter2Btn.setText("Annuler filtre 2");
                    filter2State = 2;
                    filter1Btn.setVisibility(View.VISIBLE);
                    break;

                case 2:
                    // Remove the filter and show the original image
                    if (currentPicture1 != null) {
                        pictureView.setImageBitmap(currentPicture1);
                    } else {
                        Glide.with(requireContext()).load(pictureUri).into(pictureView);
                    }
                    filter2Btn.setBackgroundColor(androidx.appcompat.R.attr.colorPrimary);
                    filter2Btn.setText(R.string.filtre_2);
                    previousPicture2 = null;
                    currentPicture2 = null;
                    filter2State = 0;
                    break;
            }
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

    @Override
    public void onDestroy() {
        super.onDestroy();
        //MicrophoneUtils.stopRecording();
        AccelerometerUtils.stopAccelerometer();
    }

    @Override
    public void onAccelerationChanged(float acceleration) {
        accHandler.post(() -> {
            // Apply the filter during recording on a background thread using a Runnable
            backgroundHandler.post(new ImageProcessingRunnable(
                    acceleration,
                    PictureFiltersUtils.FilterType.RANDOM_COLOR, requireContext())
            );
        });
    }

    @Override
    public void onVolumeLevelChanged(float volumeLevel) {
        micHandler.post(() -> {
            // Apply the filter during recording
            backgroundHandler.post(new ImageProcessingRunnable(
                    volumeLevel,
                    PictureFiltersUtils.FilterType.DANK, requireContext())
            );
        });
    }

    private static class ImageProcessingRunnable implements Runnable {
        private final float effectLevel;
        private final PictureFiltersUtils.FilterType filterType;
        private final Context context;
        private ImageView imgView = null;

        public ImageProcessingRunnable(float effectLevel, PictureFiltersUtils.FilterType filterType, Context context) {
            this.effectLevel = effectLevel;
            this.filterType = filterType;
            this.context = context;
        }

        @Override
        public void run() {
            imgView = pictureView;
            if (previousPicture1 != null && previousPicture2 != null) {
                if (currentPicture2 != null) {
                    Log.d("LOL", "Applying filter 1");
                    imgView.setImageBitmap(currentPicture2);
                } else {
                    Log.d("LOL", "Applying filter 2");
                    imgView.setImageBitmap(currentPicture1);
                }
            } else {
                Log.d("LOL", "Applying a filter");
                Glide.with(context).load(pictureUri).into(imgView);
            }
            PictureFiltersUtils.applyFilter(
                    imgView,
                    effectLevel,
                    filterType
            );
        }
    }
}