package com.penpen.profview;

import android.app.Dialog;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by penpen on 13.10.15.
 */
public class achievement_fragment extends Fragment {
    private Uri outputFileUri;
    private Integer YOUR_SELECT_PICTURE_REQUEST_CODE = 2244;
    private ImageView imageView;
    View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.achievement_fragment, container, false);
        imageView = (ImageView) view.findViewById(R.id.Proof_Pic);
        Button category = (Button) view.findViewById(R.id.categoryofachbutton);
        category.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //final Context context = getContext();
                Log.d("b", "shown");
                final Dialog dialog = new Dialog(getContext());
                dialog.setContentView(R.layout.category_dialog_layout);
                dialog.setTitle("Выберите категорию достижения");
                //кнопки выбора категории
                Button studycategorybutton = (Button) dialog.findViewById(R.id.study_category_button);
                Button sportcategorybutton = (Button) dialog.findViewById(R.id.sport_category_button);
                Button artcategorybutton = (Button) dialog.findViewById(R.id.art_category_button);
                Button sciencecategorybutton = (Button) dialog.findViewById(R.id.science_category_button);
                Button socialcategorybutton = (Button) dialog.findViewById(R.id.social_category_button);
                // if button is clicked, close the custom dialog
                studycategorybutton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        LinearLayout studylay = (LinearLayout) dialog.findViewById(R.id.study_lay);
                        studylay.setVisibility(View.VISIBLE);
                        LinearLayout alllay= (LinearLayout) dialog.findViewById(R.id.all_category);
                        alllay.setVisibility(View.INVISIBLE);
                        //dialog.dismiss();
                    }
                });
                sportcategorybutton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        LinearLayout studylay = (LinearLayout) dialog.findViewById(R.id.sport_lay);
                        studylay.setVisibility(View.VISIBLE);
                        LinearLayout alllay= (LinearLayout) dialog.findViewById(R.id.all_category);
                        alllay.setVisibility(View.INVISIBLE);
                        //dialog.dismiss();
                    }
                });
                artcategorybutton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        LinearLayout studylay = (LinearLayout) dialog.findViewById(R.id.art_lay);
                        studylay.setVisibility(View.VISIBLE);
                        LinearLayout alllay= (LinearLayout) dialog.findViewById(R.id.all_category);
                        alllay.setVisibility(View.INVISIBLE);
                        //dialog.dismiss();
                    }
                });
                sciencecategorybutton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        LinearLayout studylay = (LinearLayout) dialog.findViewById(R.id.science_lay);
                        studylay.setVisibility(View.VISIBLE);
                        LinearLayout alllay= (LinearLayout) dialog.findViewById(R.id.all_category);
                        alllay.setVisibility(View.INVISIBLE);
                        //dialog.dismiss();
                    }
                });
                socialcategorybutton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        LinearLayout studylay = (LinearLayout) dialog.findViewById(R.id.social_lay);
                        studylay.setVisibility(View.VISIBLE);
                        LinearLayout alllay= (LinearLayout) dialog.findViewById(R.id.all_category);
                        alllay.setVisibility(View.INVISIBLE);
                        //dialog.dismiss();
                    }
                });
                //кнопки выбора подкатегории

                dialog.show();

            }
        });
        Button proof = (Button) view.findViewById(R.id.proof_button);
        proof.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openImageIntent();
            }
        });


        return view;
    }


    private void openImageIntent() {

// Determine Uri of camera image to save.
        final File root = new File(Environment.getExternalStorageDirectory() + File.separator + "MyDir" + File.separator);
        root.mkdirs();
        final String fname = "img_" + System.currentTimeMillis() + ".jpg";
        final File sdImageMainDirectory = new File(root, fname);
        outputFileUri = Uri.fromFile(sdImageMainDirectory);

        // Camera.
        final List<Intent> cameraIntents = new ArrayList<Intent>();
        final Intent captureIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        final PackageManager packageManager = getActivity().getPackageManager();
        final List<ResolveInfo> listCam = packageManager.queryIntentActivities(captureIntent, 0);
        for(ResolveInfo res : listCam) {
            final String packageName = res.activityInfo.packageName;
            final Intent intent = new Intent(captureIntent);
            intent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
            intent.setPackage(packageName);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
            cameraIntents.add(intent);
        }

        // Filesystem.
        final Intent galleryIntent = new Intent();
        galleryIntent.setType("image/*");
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);

        // Chooser of filesystem options.
        final Intent chooserIntent = Intent.createChooser(galleryIntent, "Выберите источник");

        // Add the camera options.
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, cameraIntents.toArray(new Parcelable[cameraIntents.size()]));

        startActivityForResult(chooserIntent, YOUR_SELECT_PICTURE_REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == getActivity().RESULT_OK) {
            if (requestCode == YOUR_SELECT_PICTURE_REQUEST_CODE) {
                final boolean isCamera;
                if (data == null) {
                    isCamera = true;
                } else {
                    final String action = data.getAction();
                    if (action == null) {
                        isCamera = false;
                    } else {
                        isCamera = action.equals(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    }
                }

                Uri selectedImageUri;
                if (isCamera) {
                    selectedImageUri = outputFileUri;
                } else {
                    selectedImageUri = data == null ? null : data.getData();
                }

                imageView.setImageURI(selectedImageUri);
            }
        }
    }
}
