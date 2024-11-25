package com.example.smokeapplicationapppro.ui.home;

import android.widget.ImageView;
import androidx.databinding.BindingAdapter;

public class BindingAdapters {

    // Binding Adapter for ImageView's srcCompat attribute
    @BindingAdapter("app:srcCompat")
    public static void setImageResource(ImageView view, Integer resourceId) {
        if (resourceId != null && resourceId != 0) {
            view.setImageResource(resourceId);
        }
    }
}
