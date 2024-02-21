package helloandroid.ut3.miniprojet.view.fragment.booking;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.RadioButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import org.jetbrains.annotations.NotNull;

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
                ((TextView) view.findViewById(R.id.nbPersonnes)).setText(String.valueOf(i));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        CalendarView calendarView = view.findViewById(R.id.calendarView);
        Date selectedDate = new Date(calendarView.getDate());
        RadioButton midi = view.findViewById(R.id.radioButton);
        TextView nbPersonnes = view.findViewById(R.id.nbPersonnes);
        final View.OnClickListener bookingSend = v -> {
            Booking result = new Booking(
                    selectedDate,
                    midi.isChecked(),
                    Integer.parseInt(nbPersonnes.getText().toString()),
                    this.restaurant.getTitle()
            );

            FirebaseManager.getInstance().addBooking(result, new FirebaseManager.DataCallback<Booking>() {
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
        view.findViewById(R.id.reserveBtn).setOnClickListener(bookingSend);
        return view;
    }
}