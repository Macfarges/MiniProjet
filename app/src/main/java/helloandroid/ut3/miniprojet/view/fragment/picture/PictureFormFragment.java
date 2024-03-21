package helloandroid.ut3.miniprojet.view.fragment.picture;

import static android.app.Activity.RESULT_OK;

import android.Manifest;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.util.Log;
import android.util.Pair;
import android.util.TypedValue;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import helloandroid.ut3.miniprojet.R;
import helloandroid.ut3.utils.AccelerometerUtils;
import helloandroid.ut3.utils.FileUtils;
import helloandroid.ut3.utils.MicrophoneUtils;
import helloandroid.ut3.utils.PictureFiltersUtils;

public class PictureFormFragment extends Fragment implements MicrophoneUtils.MicrophoneCallback, AccelerometerUtils.AccelerometerCallback {
    private static final int YOUR_REQUEST_CODE = 15;
    static Uri pictureUri;
    static ImageView pictureView;
    static Bitmap previousPicture1 = null;
    static Bitmap previousPicture2 = null;
    static Bitmap currentPicture1 = null;
    static Bitmap currentPicture2 = null;
    private static List<Pair<Drawable, float[]>> stickersList;
    private final Handler accHandler = new Handler();
    private final Handler micHandler = new Handler();
    private final Handler backgroundHandler = new Handler(Looper.getMainLooper());
    StorageReference storageReference;
    Button addPictureBtn, selectPictureBtn, takePictureBtn, filter1Btn, filter2Btn;
    private ViewGroup choosePictureLayout, filtersLayout;
    private final ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            if (result.getResultCode() == RESULT_OK) {
                Intent data = result.getData();
                if (data != null) {
                    if (data.getData() != null) {
                        pictureUri = data.getData();
                    }
                }
                choosePictureLayout.setVisibility(View.GONE);
                filtersLayout.setVisibility(View.VISIBLE);
                pictureView.setVisibility(View.VISIBLE);
                Glide.with(requireContext()).load(pictureUri).into(pictureView);
                addPictureBtn.setEnabled(true);
            }
        }
    });
    private int filter1State = 0, filter2State = 0;
    private float effect1Level = 0;
    private float effect2Level = 0;
    private ImageView smallImageView;
    private ImageView smallImageView2;

    public PictureFormFragment() {
        //todo should have an uri as parameter for edition
    }

    //TODO : Corriger bug filtre 2 (aspect grisé parfois, si utilisé après filtre 1)
    //TODO : Corriger bug annulation autre filtre (effectLevel pas correct à priori)
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (filter1State == 1 || filter2State == 1) {
                    Toast.makeText(requireContext(), "Validez le filtre avant de quitter", Toast.LENGTH_SHORT).show();
                } else {
                    effect1Level = 0;
                    effect2Level = 0;
                    previousPicture1 = null;
                    currentPicture1 = null;
                    previousPicture2 = null;
                    currentPicture2 = null;
                    getParentFragmentManager().popBackStack();
                }
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //TODO : mettre cela dans une methode
        TypedValue typedValue = new TypedValue();
        requireContext().getTheme().resolveAttribute(R.attr.colorPrimary, typedValue, true);
        int colorPrimary = typedValue.data;

        stickersList = new ArrayList<>();
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
            File picturesDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
            String fileName = "image_" + new Date().getTime() + ".jpg";
            File imageFile = new File(picturesDirectory, fileName);
            pictureUri = FileProvider.getUriForFile(requireContext(), requireContext().getApplicationContext().getPackageName() + ".provider", imageFile);

            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, pictureUri);
            intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            activityResultLauncher.launch(intent);
        });

        filter1Btn.setOnClickListener(v -> {
            switch (filter1State) {
                case 0:
                    // Apply Filter 1
                    if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.RECORD_AUDIO}, YOUR_REQUEST_CODE);
                    } else {
                        filter2Btn.setVisibility(View.GONE);
                        addPictureBtn.setVisibility(View.GONE);
                        smallImageView.setVisibility(View.GONE);
                        smallImageView2.setVisibility(View.GONE);
                        filter1Btn.setText("Valider filtre");
                        filter1Btn.setBackgroundColor(Color.GREEN);
                        previousPicture1 = FileUtils.getBitmapFromImageView(pictureView);
                        MicrophoneUtils.setMicrophoneCallback(this);
                        MicrophoneUtils.startRecording(requireContext(), requireActivity());
                        filter1State = 1;
                    }
                    break;

                case 1:
                    // Return the last filter
                    MicrophoneUtils.stopRecording(() -> {
                        currentPicture1 = FileUtils.getBitmapFromImageView(pictureView);
                        filter1Btn.setBackgroundColor(Color.RED);
                        filter1Btn.setText("Annuler filtre 1");
                        filter1State = 2;
                        filter2Btn.setVisibility(View.VISIBLE);
                        addPictureBtn.setVisibility(View.VISIBLE);
                        smallImageView.setVisibility(View.VISIBLE);
                        smallImageView2.setVisibility(View.VISIBLE);
                    });
                    break;

                case 2:
                    // Remove the filter and show the original image
                    Glide.with(requireContext()).load(pictureUri).into(pictureView);
                    if (currentPicture2 != null) {
                        PictureFiltersUtils.applyFilter(
                                pictureView,
                                effect2Level,
                                PictureFiltersUtils.FilterType.RANDOM_COLOR
                        );
                        effect2Level = 0;
                    }
                    FileUtils.applyStickersToImageView(pictureView, stickersList);
                    filter1Btn.setBackgroundColor(colorPrimary);
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
                    addPictureBtn.setVisibility(View.GONE);
                    smallImageView.setVisibility(View.GONE);
                    smallImageView2.setVisibility(View.GONE);
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
                    addPictureBtn.setVisibility(View.VISIBLE);
                    smallImageView.setVisibility(View.VISIBLE);
                    smallImageView2.setVisibility(View.VISIBLE);
                    break;

                case 2:
                    // Remove the filter and show the original image
                    Glide.with(requireContext()).load(pictureUri).into(pictureView);
                    if (currentPicture1 != null) {
                        PictureFiltersUtils.applyFilter(
                                pictureView,
                                effect1Level,
                                PictureFiltersUtils.FilterType.DANK
                        );
                        effect1Level = 0;
                    }
                    FileUtils.applyStickersToImageView(pictureView, stickersList);
                    filter2Btn.setBackgroundColor(colorPrimary);
                    filter2Btn.setText(R.string.filtre_2);
                    previousPicture2 = null;
                    currentPicture2 = null;
                    filter2State = 0;
                    break;
            }
        });
        smallImageView = view.findViewById(R.id.smallImageView);
        smallImageView2 = view.findViewById(R.id.smallImageView2);
        smallImageView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                ClipData data = ClipData.newPlainText("", "");
                View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(v);
                v.startDragAndDrop(data, shadowBuilder, v, 0);
                return true;
            }
        });
        smallImageView2.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                ClipData data = ClipData.newPlainText("", "");
                View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(v);
                v.startDragAndDrop(data, shadowBuilder, v, 0);
                return true;
            }
        });
        pictureView.setOnDragListener(new View.OnDragListener() {
            @Override
            public boolean onDrag(View v, DragEvent event) {
                if (event.getAction() == DragEvent.ACTION_DROP) {// Handle the drop event
                    Log.d("Dropped", "Dropped");
                    handleDrop(event);
                    return true;
                }
                return true;
            }
        });
        addPictureBtn.setOnClickListener(v -> addPictureBtn());
        return view;
    }

    private void handleDrop(DragEvent event) {
        // Get the dragged view
        ImageView draggedView = (ImageView) event.getLocalState();

        // Ensure it's the small image (if needed)
        if (draggedView.getId() == R.id.smallImageView || draggedView.getId() == R.id.smallImageView2) {
            float x = event.getX();
            float y = event.getY();
            // Get the Drawable associated with the smallImageView
            Drawable drawable = draggedView.getDrawable();

            // Calculate the coordinates for the center of the sticker
            int stickerWidth = drawable.getIntrinsicWidth();
            int stickerHeight = drawable.getIntrinsicHeight();
            float centerX = x - (stickerWidth / 2);
            float centerY = y - (stickerHeight / 2);

            // Add the drawable and coordinates to the list
            stickersList.add(new Pair<>(drawable, new float[]{centerX, centerY}));
            FileUtils.applyStickersToImageView(pictureView, stickersList);
        }
    }


    private void addPictureBtn() {
        if ((!stickersList.isEmpty()) || (currentPicture2 != null || currentPicture1 != null)) {
            Bitmap bitmap = FileUtils.getBitmapFromImageView(pictureView);
            pictureUri = FileUtils.saveBitmapToFile(requireContext(), bitmap);
        }
        previousPicture1 = null;
        currentPicture1 = null;
        previousPicture2 = null;
        currentPicture2 = null;
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
        if (isAdded()) {
            MicrophoneUtils.stopRecording(() -> {
                // Handle any additional tasks after recording has stopped

            });
        }
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
        effect1Level = acceleration;

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
        effect2Level = volumeLevel;
    }

    private static class ImageProcessingRunnable implements Runnable {
        private final float effectLevel;
        private final PictureFiltersUtils.FilterType filterType;
        private final Context context;
        private ImageView imgView = pictureView;

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
                    imgView.setImageBitmap(currentPicture2);
                } else {
                    imgView.setImageBitmap(currentPicture1);
                }
            } else {
                Glide.with(context).load(pictureUri).into(imgView);
            }
            PictureFiltersUtils.applyFilter(
                    imgView,
                    effectLevel,
                    filterType
            );
            FileUtils.applyStickersToImageView(imgView, stickersList);

        }
    }
}