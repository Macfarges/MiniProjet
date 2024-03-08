package helloandroid.ut3.miniprojet.view.fragment.review;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import helloandroid.ut3.miniprojet.R;
import helloandroid.ut3.miniprojet.view.fragment.picture.PictureFormFragment;

public class ReviewListFragment extends Fragment {
    //TODO : Supprimer OU appeler lors d'appui sur bouton avis sur restaurant
    public ReviewListFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getParentFragmentManager().beginTransaction()
                .replace(R.id.fragmentContainerView, new PictureFormFragment(), null)
                .setReorderingAllowed(true)
                .addToBackStack(null)
                .commit();
        return inflater.inflate(R.layout.fragment_list_review_element, container, false);
    }
}