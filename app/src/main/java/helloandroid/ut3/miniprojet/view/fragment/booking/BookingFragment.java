package helloandroid.ut3.miniprojet.view.fragment.booking;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.database.annotations.NotNull;

import java.util.Date;

import helloandroid.ut3.miniprojet.R;
import helloandroid.ut3.miniprojet.data.domain.Booking;
import helloandroid.ut3.miniprojet.data.domain.Restaurant;
import helloandroid.ut3.miniprojet.data.service.FirebaseManager;

public class BookingFragment extends Fragment {

    private final Restaurant restaurant;

    public BookingFragment(@NotNull Restaurant restaurant) {
        this.restaurant = restaurant;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_booking, container, false);
        ((TextView) view.findViewById(R.id.restaurantName)).setText(restaurant.getTitle());

        SeekBar sb = view.findViewById(R.id.seekBar);
        sb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                ((TextView) view.findViewById(R.id.nbPersons)).setText(String.valueOf(i));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        Button reserveBtn = view.findViewById(R.id.reserveBtn);
        reserveBtn.setEnabled(false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        EditText editTextText = view.findViewById(R.id.editTextText);

        // Set up TextWatcher for EditText fields
        editTextText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Enable reserve button only if the editTextText field is not empty
                Button reserveBtn = view.findViewById(R.id.reserveBtn);
                reserveBtn.setEnabled(!TextUtils.isEmpty(editTextText.getText()));
            }
        });

        Date selectedDate = new Date(((CalendarView) view.findViewById(R.id.calendarView)).getDate());

        final View.OnClickListener bookingSend = v -> {
            FirebaseManager.getInstance().addBooking(new Booking(
                    selectedDate,
                    ((RadioButton) view.findViewById(R.id.radioButton)).isChecked(),
                    Integer.parseInt(((TextView) view.findViewById(R.id.nbPersons)).getText().toString()),
                    restaurant.getTitle(),
                    editTextText.getText().toString()
            ), new FirebaseManager.DataCallback<Booking>() {
                @Override
                public void onSuccess(Booking booking) {
                    Toast.makeText(requireContext(), "Réservation réussie", Toast.LENGTH_SHORT).show();
                    getParentFragmentManager().popBackStack();
                }

                @Override
                public void onError(Exception e) {
                    // Handle error
                    Toast.makeText(requireContext(), "Echec de réservation : une erreur inattendue est survenue", Toast.LENGTH_SHORT).show();
                }
            });
        };
        Button reserveBtn = view.findViewById(R.id.reserveBtn);
        reserveBtn.setOnClickListener(bookingSend);
    }
}