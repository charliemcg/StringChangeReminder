package com.violenthoboenterprises.tonetracker;

import android.content.Intent;

public interface EditActivityPresenter {
    void takePhoto();

    void chooseImage();

    void onActivityResult(int requestCode, int resultCode, Intent data);

}
