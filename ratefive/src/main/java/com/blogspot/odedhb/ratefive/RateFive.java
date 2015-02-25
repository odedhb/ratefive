package com.blogspot.odedhb.ratefive;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.text.format.DateUtils;
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
    private Context context;
    private String feedbackEmail;
    private String LAST_CHECKED = "LAST_CHECKED";
    private String RATED = "RATED";

    public RateFive(final Context context, final String feedbackEmail) {
        this.context = context;
        this.feedbackEmail = feedbackEmail;
        dialog = new Dialog(context);
    }

    private boolean shouldShow() {

        if (mem().getBoolean(RATED, false)) {
            return false;
        }

        long lastChecked = mem().getLong(LAST_CHECKED, 0l);

        if (lastChecked == 0l) {
            rememberShown();
            return false;
        }

        if ((System.currentTimeMillis() - lastChecked) < (DateUtils.DAY_IN_MILLIS * 3)) {
            return false;
        }

        return true;
    }

    private void rememberShown() {
        mem().edit().putLong(LAST_CHECKED, System.currentTimeMillis()).commit();
    }

    private SharedPreferences mem() {
        return context.getSharedPreferences(
                context.getPackageName() + "ratefive", Context.MODE_PRIVATE);
    }

    public void show() {
        if (!shouldShow()) return;

        dialog.setContentView(R.layout.dialog);
        dialog.setTitle(context.getString(R.string.title));
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                rememberShown();
            }
        });
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
                mem().edit().putBoolean(RATED, true).commit();
                dialog.dismiss();
            }
        });
        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                rateButton.setVisibility(View.VISIBLE);
            }
        });

        dialog.show();
    }

}
