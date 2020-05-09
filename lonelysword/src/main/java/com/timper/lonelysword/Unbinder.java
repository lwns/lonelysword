package com.timper.lonelysword;

import androidx.annotation.UiThread;

import android.view.View;

/**
 * An unbinder contract that will unbind views when called.
 */
public interface Unbinder {

    View initViews(View container);

    void afterViews();

    @UiThread
    void unbind();

    Unbinder EMPTY = new Unbinder() {

        @Override
        public View initViews(View container) {
            return null;
        }

        @Override
        public void afterViews() {
        }

        @Override
        public void unbind() {
        }
    };
}
