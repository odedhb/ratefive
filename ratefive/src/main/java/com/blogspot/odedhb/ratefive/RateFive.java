package com.blogspot.odedhb.ratefive;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.widget.Button;
import android.widget.RatingBar;

/**
 * Created by oded on 2/22/15.
 */
public class RateFive {

    private final Dialog dialog;
    private RatingBar ratingBar;
    private Button rateButton;

    public RateFive(final Context context, final String feedbackEmail) {
        dialog = new Dialog(context);
        dialog.setContentView(R.layout.dialog);
        dialog.setTitle(context.getString(R.string.title));
        rateButton = (Button) dialog.findViewById(R.id.rate_button);
        ratingBar = (RatingBar) dialog.findViewById(R.id.rating_bar);
        rateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ratingBar.getRating() < 5) {
                    Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                            "mailto", feedbackEmail, null));
                    emailIntent.putExtra(Intent.EXTRA_SUBJECT, context.getString(R.string.feedback_email_subject));
                    context.startActivity(emailIntent);
                } else {
                    final String appPackageName = context.getPackageName();
                    try {
                        context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                    } catch (android.content.ActivityNotFoundException anfe) {
                        context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                    }
                }
                dialog.dismiss();
            }
        });
        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                rateButton.setVisibility(View.VISIBLE);
            }
        });
    }


    public void show() {
        dialog.show();
    }

}
