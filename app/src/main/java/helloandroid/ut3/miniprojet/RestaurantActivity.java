package helloandroid.ut3.miniprojet;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class RestaurantActivity extends AppCompatActivity {
    //TODO: Ajouter bouton retour
    //TODO: Ajouter nom
    //TODO: Ajouter adresse
    //TODO: Ajouter avis
    //TODO: Ajouter bouton donner son avis
    //TODO: Ajouter bouton réserver
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // on crée un nouveau TextView, qui est un widget permettant
        // d'afficher du texte
        TextView tv = new TextView(this);
        // configurer le texte à faire afficher par notre widget
        tv.setText("deatils restau");
        // remplacer tout le contenu de notre activité par le TextView
        setContentView(tv);
        //setContentView(R.layout.activity_main);
    }
}