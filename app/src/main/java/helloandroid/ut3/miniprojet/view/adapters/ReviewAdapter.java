package helloandroid.ut3.miniprojet.view.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import helloandroid.ut3.miniprojet.R;
import helloandroid.ut3.miniprojet.data.domain.Review;
import helloandroid.ut3.miniprojet.view.fragment.generic.TextPopupFragment;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ViewHolder> {
    private static final int MAX_DISPLAY_LINES = 4;
    //Pas tres joli mais tant pis
    private static final int MAX_TEXT_LENGTH = 114;
    private final List<Review> reviews;
    private final Context context;

    public ReviewAdapter(Context context, List<Review> reviews) {
        this.context = context;
        //Tri des avis par les plus recents d'abord
        this.reviews = reviews.stream()
                .sorted((r1, r2) -> r2.getDate().compareTo(r1.getDate()))
                .collect(Collectors.toList());
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.fragment_list_review_element, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Review review = reviews.get(position);
        holder.bind(review);
    }

    @Override
    public int getItemCount() {
        return reviews.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView nomInList;
        TextView noteInList;
        TextView dateInList;
        TextView textInList;
        Button showMoreButton; // Add this button
        boolean isFullTextDisplayed = false;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            nomInList = itemView.findViewById(R.id.nomInList);
            noteInList = itemView.findViewById(R.id.noteInList);
            dateInList = itemView.findViewById(R.id.dateInList);
            textInList = itemView.findViewById(R.id.textInList);
            showMoreButton = itemView.findViewById(R.id.showMoreButton);

        }

        public void bind(Review review) {
            // Set data for the current item
            nomInList.setText(review.getSource());
            noteInList.setText(String.valueOf(review.getRating()));

            // Format the date
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yy", Locale.getDefault());
            String formattedDate = dateFormat.format(review.getDate());
            dateInList.setText(formattedDate);

            textInList.setText(review.getText());
            //TODO : ET si pas d'images
            showMoreButton.setVisibility(review.getText().length() > MAX_TEXT_LENGTH
                    || review.getPictureUrls() != null ? View.VISIBLE : View.GONE);
            textInList.setMaxLines(MAX_DISPLAY_LINES);
            showMoreButton.setOnClickListener(v -> {
                FragmentManager fragmentManager = ((AppCompatActivity) itemView.getContext()).getSupportFragmentManager();
                isFullTextDisplayed = !isFullTextDisplayed;
                TextPopupFragment textPopupFragment = new TextPopupFragment(
                        "Avis de " + nomInList.getText().toString()
                );
                textPopupFragment.setText(textInList.getText().toString());
                textPopupFragment.setUrls(review.getPictureUrls());
                textPopupFragment.show(fragmentManager, "text_popup_fragment_tag");
            });
        }
    }
}
