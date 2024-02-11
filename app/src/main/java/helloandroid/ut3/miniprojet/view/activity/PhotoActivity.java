package helloandroid.ut3.miniprojet.view.activity;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class PhotoActivity extends AppCompatActivity {
    //TODO: Ajouter bouton retour
    //TODO: Ajouter bouton suivant
    //TODO: Ajouter image prise par l'appareil photo
    //TODO: Ajouter bouton filtre 1
    //TODO: Ajouter bouton filtre 2
    //TODO: Ajouter bouton stickers => implique activité stickers ?
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // on crée un nouveau TextView, qui est un widget permettant
        // d'afficher du texte
        TextView tv = new TextView(this);
        // configurer le texte à faire afficher par notre widget
        tv.setText("Photo");
        // remplacer tout le contenu de notre activité par le TextView
        setContentView(tv);
        //setContentView(R.layout.activity_main);
    }
}