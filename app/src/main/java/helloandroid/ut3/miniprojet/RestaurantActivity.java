package helloandroid.ut3.miniprojet;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class RestaurantActivity extends AppCompatActivity {
    private TextView restauName;
    private TextView restauAdress;

    //TODO: Ajouter bouton retour
    //TODO: Ajouter nom
    //TODO: Ajouter adresse
    //TODO: Ajouter avis
    //TODO: Ajouter bouton donner son avis
    //TODO: Ajouter bouton réserver
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.restaurant_activity);
        restauName = this.findViewById(R.id.RestauName);
        restauName.setText(getIntent().getExtras().getString("INFOS"));
        restauAdress = this.findViewById(R.id.RestauAdress);
        restauAdress.setText(getIntent().getExtras().getString("ADRESSE"));
    }

    public void startMainActivity(View view){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    public void startReserverActivity(View view){
        Intent intent = new Intent(this, ReservationActivity.class);
        startActivity(intent);
    }
}