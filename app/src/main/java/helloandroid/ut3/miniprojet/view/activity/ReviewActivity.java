package helloandroid.ut3.miniprojet.view.activity;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class ReviewActivity extends AppCompatActivity {
    //TODO: Ajouter bouton retour
    //TODO: Ajouter bouton suivant
    //TODO: Ajouter saisie texte
    //TODO: Ajouter bouton mettre une photo
    //TODO: Ajouter liste des photos mises (cliquable)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // on crée un nouveau TextView, qui est un widget permettant
        // d'afficher du texte
        TextView tv = new TextView(this);
        // configurer le texte à faire afficher par notre widget
        tv.setText("review");
        // remplacer tout le contenu de notre activité par le TextView
        setContentView(tv);
        //setContentView(R.layout.activity_main);
    }
}