package helloandroid.ut3.miniprojet.view.fragment.review;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import helloandroid.ut3.miniprojet.R;

public class ReviewDetailstFragment extends Fragment {
    //TODO : appeler lors d'appui sur bouton avis sur restaurant
    public ReviewDetailstFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_list_review_element, container, false);
    }
}