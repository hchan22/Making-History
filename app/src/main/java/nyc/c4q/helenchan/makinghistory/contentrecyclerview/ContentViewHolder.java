package nyc.c4q.helenchan.makinghistory.contentrecyclerview;

import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import nyc.c4q.helenchan.makinghistory.R;
import nyc.c4q.helenchan.makinghistory.models.Content;

/**
 * Created by leighdouglas on 3/6/17.
 */

public class ContentViewHolder extends RecyclerView.ViewHolder {
    private ImageView contentImageView;
    private TextView contentTitleTextView;
    private TextView contentDescriptionTextView;
    private TextView contentYearTextView;

    public ContentViewHolder(View itemView) {
        super(itemView);
        contentImageView = (ImageView) itemView.findViewById(R.id.content_image);
        contentTitleTextView = (TextView) itemView.findViewById(R.id.content_title);
        contentDescriptionTextView = (TextView) itemView.findViewById(R.id.content_description);
        contentYearTextView = (TextView) itemView.findViewById(R.id.content_year);
        Typeface titleFont = Typeface.createFromAsset(itemView.getContext().getAssets(), "ArimaMadurai-Regular.ttf");
        Typeface bodyFont = Typeface.createFromAsset(itemView.getContext().getAssets(), "Raleway-Regular.ttf" );
        contentTitleTextView.setTypeface(titleFont);
    }

    public void bind(Content c) {
        Picasso.with(itemView.getContext()).load(c.getUrl()).resize(1200, 1200).centerCrop().into(contentImageView);

        contentTitleTextView.setText(c.getName());
        contentDescriptionTextView.setText(c.getText());
        contentYearTextView.setText(c.getYear());
    }

}
