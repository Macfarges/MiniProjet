package helloandroid.ut3.miniprojet.view.activity;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import helloandroid.ut3.miniprojet.R;
import helloandroid.ut3.miniprojet.view.fragment.restaurant.ListRestaurantFragment;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.Theme_MiniProjet);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragmentContainerView, ListRestaurantFragment.class, null)
                .setReorderingAllowed(true)
                .commit();

    }
}