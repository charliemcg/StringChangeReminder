package com.violenthoboenterprises.tonetracker;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;

import static android.app.Activity.RESULT_OK;

public class EditActivityPresenterImpl implements EditActivityPresenter {

    private EditActivityView editActivityView;
    private Context context;
    private Activity activity;

    public EditActivityPresenterImpl(EditActivityView editActivityView, Context context, Activity activity){
        this.editActivityView = editActivityView;
        this.context = context;
        this.activity = activity;
    }

    @Override
    public void takePhoto() {
        activity.startActivityForResult(new Intent("android.media.action.IMAGE_CAPTURE"), 2);
    }

    @Override
    public void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        activity.startActivityForResult(Intent.createChooser(intent, context.getString(R.string.select_image)), 1);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            Bitmap bitmap;
            //chosen from gallery
            if (requestCode == 1 && resultCode == RESULT_OK
                    && null != data) {
                Uri selectedImage = data.getData();
                bitmap = BitmapFactory.decodeStream(context.getContentResolver()
                        .openInputStream(selectedImage));
                //scaling image down to save on memory
                bitmap = Bitmap.createScaledBitmap(bitmap, (bitmap.getWidth() / 2),
                        (bitmap.getHeight() / 2), false);
                editActivityView.setInstrumentImage(bitmap);
            //picture taken from camera
            } else if (requestCode == 2 && resultCode == RESULT_OK) {
                bitmap = (Bitmap) data.getExtras().get("data");
                //scaling image down to save on memory
                bitmap = Bitmap.createScaledBitmap(bitmap, (bitmap.getWidth() / 2),
                        (bitmap.getHeight() / 2), false);
                editActivityView.setInstrumentImage(bitmap);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
