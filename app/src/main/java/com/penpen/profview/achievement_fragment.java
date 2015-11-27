package com.penpen.profview;

import android.app.Dialog;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import adapter.NothingSelectedSpinnerAdapter;
import app.authorization;

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
        final EditText name = (EditText) view.findViewById(R.id.achievement_add_name);
        final DatePicker date = (DatePicker) view.findViewById(R.id.achievemen_add_date);
        imageView = (ImageView) view.findViewById(R.id.achievemen_add_Proof_Pic);
        final Spinner category = (Spinner) view.findViewById(R.id.achievemen_add_category);
        ArrayAdapter<CharSequence> categoryadapter = ArrayAdapter.createFromResource(view.getContext(), R.array.achievement_category, android.R.layout.simple_spinner_item);
        categoryadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        category.setPrompt("Выберите направление достижения");
        category.setAdapter(
                new NothingSelectedSpinnerAdapter(
                        categoryadapter,
                        R.layout.achievement_category_spinner,
                        view.getContext()));
        final Spinner subcategory = (Spinner) view.findViewById(R.id.achievemen_add_subcategory);
        subcategory.setEnabled(false);
        category.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            private void setadapter(int arrayresource) {
                ArrayAdapter<CharSequence> subcategory1adapter = ArrayAdapter.createFromResource(view.getContext(), arrayresource, R.layout.spinner_multiline_fix);
                //subcategoryadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                subcategory.setPrompt("Выберите категорию достижения");
                subcategory.setAdapter(
                        new NothingSelectedSpinnerAdapter(
                                subcategory1adapter,
                                R.layout.achievement_subcategory_spinner,
                                view.getContext()));
                subcategory.setEnabled(true);
            }

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        subcategory.setEnabled(false);
                        break;
                    case 1:
                        setadapter(R.array.achievement_subcategory_study);
                        break;
                    case 2:
                        setadapter(R.array.achievement_subcategory_science);
                        break;
                    case 3:
                        setadapter(R.array.achievement_subcategory_sport);
                        break;
                    case 4:
                        setadapter(R.array.achievement_subcategory_art);
                        break;
                    case 5:
                        setadapter(R.array.achievement_subcategory_social);
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        Button proof = (Button) view.findViewById(R.id.achievemen_add_proof_button);
        proof.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openImageIntent();
            }
        });
        Button send = (Button) view.findViewById(R.id.achievemen_add_send_button);
        send.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String strdate="";
                if (date.getMonth()<10) {
                    strdate=String.valueOf(date.getDayOfMonth())+".0"+String.valueOf(date.getMonth())+"."+String.valueOf(date.getYear());
                } else {
                    strdate = String.valueOf(date.getDayOfMonth())+"."+String.valueOf(date.getMonth())+"."+String.valueOf(date.getYear());
                }
                Log.d("out", strdate);
                Log.d("out", name.getText().toString());
                if (name.getText().toString().length() == 0) {
                    Toast toast = Toast.makeText(getActivity().getApplicationContext(), "Введите название достижения", Toast.LENGTH_SHORT);
                    toast.show();
                    name.requestFocus();
                }
                if (subcategory.getSelectedItemPosition() == 0) {
                    Toast toast = Toast.makeText(getActivity().getApplicationContext(), "Выберите категорию", Toast.LENGTH_SHORT);
                    toast.show();
                    subcategory.requestFocus();
                    return;
                }
                int categoryid;
                switch (Integer.parseInt(String.valueOf(category.getSelectedItemPosition()))) {
                    case 1:
                        categoryid = subcategory.getSelectedItemPosition()+getResources().getInteger(R.integer.achievement_study_start_id);
                        break;
                    case 2:
                        categoryid = subcategory.getSelectedItemPosition()+getResources().getInteger(R.integer.achievement_art_start_id);
                        break;
                    case 3:
                        categoryid = subcategory.getSelectedItemPosition()+getResources().getInteger(R.integer.achievement_sport_start_id);
                        break;
                    case 4:
                        categoryid = subcategory.getSelectedItemPosition()+getResources().getInteger(R.integer.achievement_social_start_id);
                        break;
                    case 5:
                        categoryid = subcategory.getSelectedItemPosition()+getResources().getInteger(R.integer.achievement_science_start_id);
                        break;
                    default:
                        Toast toast = Toast.makeText(getActivity().getApplicationContext(), "Выберите направление", Toast.LENGTH_SHORT);
                        toast.show();
                        category.requestFocus();
                        categoryid=0;
                        return;
                }

                Log.d("out", String.valueOf(categoryid));
                new sendachievement().execute(name.getText().toString(), strdate, String.valueOf(categoryid));
            }
        });


        return view;
    }

    /* Checks if external storage is available for read and write */
    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    private void openImageIntent() {

// Determine Uri of camera image to save.
        final String fname = "img_" + System.currentTimeMillis() + ".jpg";
        File sdImageMainDirectory;
        if (isExternalStorageWritable()) {
            final File root = new File(getActivity().getExternalFilesDir(null) + File.separator + "MyDir" + File.separator);
            root.mkdirs();
            sdImageMainDirectory = new File(root, fname);
            Log.d("path", sdImageMainDirectory.toString());
        } else {
            final File root = new File(getActivity().getFilesDir() + File.separator + "MyDir" + File.separator);
            root.mkdirs();
            sdImageMainDirectory = new File(root, fname);
            Log.d("path", sdImageMainDirectory.toString());
        }
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

    class sendachievement extends AsyncTask<String, Void, Boolean> {
        @Override
        protected Boolean doInBackground(String... params) {
            Boolean result = false;
            String line;
            StringBuffer jsonString = new StringBuffer();
            //if (params.length == 9) {
                try {
                    URL url = new URL("http://irk.yourplus.ru/rating/achievements/add/");
                    String payload = "------WebKitFormBoundarylHpOTMpTFaqnnId8\n" +
                            "Content-Disposition: form-data; name=\"action\"\n" +
                            "\n" +
                            "add\n" +
                            "------WebKitFormBoundarylHpOTMpTFaqnnId8\n" +
                            "Content-Disposition: form-data; name=\"NAME\"\n" +
                            "\n" +
                            params[0]+"\n" +
                            "------WebKitFormBoundarylHpOTMpTFaqnnId8\n" +
                            "Content-Disposition: form-data; name=\"DATE\"\n" +
                            "\n" +
                            params[1]+"\n" +
                            "------WebKitFormBoundarylHpOTMpTFaqnnId8\n" +
                            "Content-Disposition: form-data; name=\"award[0][type]\"\n" +
                            "\n" +
                            params[2]+"\n" +
                            "------WebKitFormBoundarylHpOTMpTFaqnnId8\n" +
                            "Content-Disposition: form-data; name=\"files[]\"; filename=\"\"\n" +
                            "Content-Type: application/octet-stream\n" +
                            "\n" +
                            "\n" +
                            "------WebKitFormBoundarylHpOTMpTFaqnnId8--";
                    Log.d("req", payload);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setDoInput(true);
                    connection.setDoOutput(true);
                    connection.setRequestMethod("POST");
                    connection.addRequestProperty("Content-Type", "multipart/form-data; boundary=----WebKitFormBoundarylHpOTMpTFaqnnId8");
                    connection.addRequestProperty("Cookie", authorization.cookie);
                    connection.addRequestProperty("Upgrade-Insecure-Requests", "1");
                    OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream(), "UTF-8");
                    writer.write(payload);
                    writer.close();
                    BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    while ((line = br.readLine()) != null) {
                        jsonString.append(line);
                    }
                    br.close();
                    connection.disconnect();
                } catch (Exception e) {
                    throw new RuntimeException(e.getMessage());
                }
            //}
            return result;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            try {
                File file = new File(outputFileUri.getPath());
                file.delete();
            } catch (Exception e) {

            }

        }
    }
}
