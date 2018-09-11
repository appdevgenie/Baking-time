package com.appdevgenie.bakingtime.utils;

import android.os.Build;
import android.support.design.widget.Snackbar;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

public class SnackbarUtil {

    public static Snackbar snackBarBuilder(View viewLayout, String text) {

        Snackbar snackbar = Snackbar.make(viewLayout, text, Snackbar.LENGTH_SHORT);
        View view = snackbar.getView();
        TextView textView = view.findViewById(android.support.design.R.id.snackbar_text);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        } else {
            textView.setGravity(Gravity.CENTER_HORIZONTAL);
        }

        return snackbar;
    }
}
