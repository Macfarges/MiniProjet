package helloandroid.ut3.miniprojet;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class RestaurantFragment extends Fragment {

    public RestaurantFragment(){
        super(R.layout.restaurant_fragment);
    }
    private TextView restauName;
    private TextView restauAdress;

    private Bundle entryData;

    //TODO: Ajouter bouton retour
    //TODO: Ajouter nom
    //TODO: Ajouter adresse
    //TODO: Ajouter avis
    //TODO: Ajouter bouton donner son avis
    //TODO: Ajouter bouton réserver


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View result = null;
        if (entryData != null) {
            result = inflater.inflate(R.layout.restaurant_fragment, container, false);
            restauName = result.findViewById(R.id.RestauName);
            restauAdress = result.findViewById(R.id.RestauAdress);
            restauName.setText(entryData.getString("INFOS"));
            restauAdress.setText(entryData.getString("ADRESSE"));
        }
        return result;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        entryData = this.getArguments();

    }

    public void startMainActivity(View view){
        /*Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);*/
    }

    public void startReserverActivity(View view){
        /*Intent intent = new Intent(this, ReservationActivity.class);
        startActivity(intent);*/
    }
}