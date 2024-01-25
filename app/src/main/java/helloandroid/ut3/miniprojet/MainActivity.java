package helloandroid.ut3.miniprojet;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    //TODO: Afficher liste de restaurants
    //TODO: Afficher Bouton d'accès à la carte avec photos

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // on crée un nouveau TextView, qui est un widget permettant
        // d'afficher du texte
        TextView tv = new TextView(this);
        // configurer le texte à faire afficher par notre widget
        tv.setText("MainActivity");
        // remplacer tout le contenu de notre activité par le TextView
        setContentView(tv);
        setContentView(R.layout.activity_main);
    }
}