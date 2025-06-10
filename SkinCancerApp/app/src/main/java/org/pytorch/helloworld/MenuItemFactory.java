package org.pytorch.helloworld;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class MenuItemFactory {
    public static final int MODEL_INFO = 1;
    public static final int DEVELOPER_INFO = 2;

    public static View newInfoDialog(Context context, int infoViewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        if (MODEL_INFO == infoViewType) {
            View view = inflater.inflate(R.layout.model_info, null, false);
            TextView titleTextView = view.findViewById(R.id.info_title);
            TextView descriptionTextView = view.findViewById(R.id.info_description);

            titleTextView.setText(R.string.model_info_title);
            StringBuilder sb = new StringBuilder(context.getString(R.string.model_info_description));
            descriptionTextView.setText(sb.toString());
            return view;
        } else if (DEVELOPER_INFO == infoViewType) {
            View view = inflater.inflate(R.layout.developer_info, null, false);
            ImageView linkedinIcon = view.findViewById(R.id.linkedinIcon_1);
            ImageView githubIcon = view.findViewById(R.id.githubIcon);

            linkedinIcon.setOnClickListener(v -> {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.linkedin.com/in/saarthak-vijayvargiya-18141922b/"));
                context.startActivity(intent);
            });

            githubIcon.setOnClickListener(v -> {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/Saarthak-Vijayvargiya-github/"));
                context.startActivity(intent);
            });
            return view;
        }
        throw new IllegalArgumentException("Unknown info view type");
    }
}
