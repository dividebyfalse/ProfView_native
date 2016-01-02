package com.penpen.profview;

import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import adapter.NothingSelectedSpinnerAdapter;
import app.authorization;

/**
 *
 * Created by penpen on 13.10.15.
 *
 */
public class achievement_fragment extends Fragment {
    private Uri outputFileUri;
    private Integer YOUR_SELECT_PICTURE_REQUEST_CODE = 2244;
    private ImageView imageView;
    private Button send;
    private String achid = "";
    private ArrayAdapter<CharSequence> subcategory1adapter;
    private MainActivity ma;
    View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.achievement_fragment, container, false);
        Bundle  bundle = getArguments();
        ma = (MainActivity) getActivity();
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
        category.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        subcategory.setEnabled(false);
                        break;
                    case 1:
                        setadapter(R.array.achievement_subcategory_study, subcategory);
                        break;
                    case 2:
                        setadapter(R.array.achievement_subcategory_science, subcategory);
                        break;
                    case 3:
                        setadapter(R.array.achievement_subcategory_sport, subcategory);
                        break;
                    case 4:
                        setadapter(R.array.achievement_subcategory_art, subcategory);
                        break;
                    case 5:
                        setadapter(R.array.achievement_subcategory_social, subcategory);
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
        send = (Button) view.findViewById(R.id.achievemen_add_send_button);
        send.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String strdate;
                if (date.getMonth() < 10) {
                    strdate = String.valueOf(date.getDayOfMonth()) + ".0" + String.valueOf(date.getMonth()) + "." + String.valueOf(date.getYear());
                } else {
                    strdate = String.valueOf(date.getDayOfMonth()) + "." + String.valueOf(date.getMonth()) + "." + String.valueOf(date.getYear());
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
                        categoryid = subcategory.getSelectedItemPosition() + getResources().getInteger(R.integer.achievement_study_start_id);
                        break;
                    case 2:
                        if (subcategory.getSelectedItemPosition()<4) {
                            categoryid = subcategory.getSelectedItemPosition() + getResources().getInteger(R.integer.achievement_science_start_id);
                        } else {
                            categoryid = subcategory.getSelectedItemPosition() + 2923;
                        }
                        break;
                    case 3:
                        categoryid = subcategory.getSelectedItemPosition() + getResources().getInteger(R.integer.achievement_sport_start_id);
                        break;
                    case 4:
                            categoryid = subcategory.getSelectedItemPosition() + getResources().getInteger( R.integer.achievement_art_start_id);
                        break;
                    case 5:
                        if (subcategory.getSelectedItemPosition() ==1) {
                            categoryid = 2854;
                        } else {
                            categoryid = subcategory.getSelectedItemPosition() + getResources().getInteger(R.integer.achievement_social_start_id);
                        }
                        break;
                    default:
                        Toast toast = Toast.makeText(getActivity().getApplicationContext(), "Выберите направление", Toast.LENGTH_SHORT);
                        toast.show();
                        category.requestFocus();
                        return;
                }
                if (authorization.isOnline(getActivity())) {
                    send.setEnabled(false);
                    if (achid.equals("")) {
                        new sendachievement().execute(name.getText().toString(), strdate, String.valueOf(categoryid));
                    } else {
                        new sendachievement().execute(name.getText().toString(), strdate, String.valueOf(categoryid), achid);
                    }
                }
            }
        });

        if (bundle != null) {
            name.setText(bundle.getString("name"));
            String[] ardate ;
            ardate= bundle.getString("date", "").split("\\.");
            date.updateDate(Integer.parseInt(ardate[2]), Integer.parseInt(ardate[1]), Integer.parseInt(ardate[0]));
            final int categoryval = Integer.parseInt(bundle.getString("category", ""));
            int categoryv;
            if (categoryval<2917 && categoryval>2913) {
                category.setSelection(1);
                categoryv=0;
            } else if ((categoryval>2926 && categoryval<2934) || (categoryval<2521 && categoryval>2517)) {
                category.setSelection(2);
                categoryv=1;
            } else if (categoryval> 2922 && categoryval<2927) {
                category.setSelection(3);
                categoryv=2;
            } else if (categoryval>2917 && categoryval<2923) {
                category.setSelection(4);
                categoryv = 3;
            } else {
                category.setSelection(5);
                categoryv=4;
            }
            Log.d("cat", String.valueOf(categoryval));
            subcategory.setEnabled(true);
            switch (categoryv) {
                case 0:
                    new Handler().postDelayed(new Runnable() {
                        public void run() {
                            setadapter(R.array.achievement_subcategory_study, subcategory);
                            subcategory1adapter.notifyDataSetChanged();
                            subcategory.setSelection(categoryval - 2913,true);
                        }
                    }, 100);
                    break;
                case 1:
                    new Handler().postDelayed(new Runnable() {
                        public void run() {
                            setadapter(R.array.achievement_subcategory_science, subcategory);
                            subcategory1adapter.notifyDataSetChanged();
                            if (categoryval>2926 && categoryval<2934) {
                                subcategory.setSelection(categoryval - 2923, true);
                            } else {
                                subcategory.setSelection(categoryval - 2517, true);
                            }
                        }
                    }, 100);
                    break;
                case 2:
                    new Handler().postDelayed(new Runnable() {
                        public void run() {
                            setadapter(R.array.achievement_subcategory_sport, subcategory);
                            subcategory1adapter.notifyDataSetChanged();
                            subcategory.setSelection(categoryval - 2922, true);
                        }
                    }, 100);
                    break;
                case 3:
                    new Handler().postDelayed(new Runnable() {
                        public void run() {
                            setadapter(R.array.achievement_subcategory_art, subcategory);
                            subcategory1adapter.notifyDataSetChanged();
                            subcategory.setSelection(categoryval - 2917, true);
                        }
                    }, 100);
                    break;
                case 4:
                    new Handler().postDelayed(new Runnable() {
                        public void run() {
                            setadapter(R.array.achievement_subcategory_social, subcategory);
                            subcategory1adapter.notifyDataSetChanged();
                            if (categoryval == 2854) {
                                subcategory.setSelection(1, true);
                            } else {
                                subcategory.setSelection(categoryval - 2932, true);
                            }
                        }
                    }, 100);
                    break;
            }
            achid = bundle.getString("id");
        } else {
            subcategory.setEnabled(false);
        }
        return view;
    }

    private void setadapter(int arrayresource, Spinner subcategory) {
        subcategory1adapter = ArrayAdapter.createFromResource(view.getContext(), arrayresource, R.layout.spinner_multiline_fix);
        //subcategoryadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        subcategory.setPrompt("Выберите категорию достижения");
        subcategory.setAdapter(
                new NothingSelectedSpinnerAdapter(
                        subcategory1adapter,
                        R.layout.achievement_subcategory_spinner,
                        view.getContext()));
        subcategory.setEnabled(true);
    }

    /* Checks if external storage is available for read and write */
    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
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
        final List<Intent> cameraIntents = new ArrayList<>();
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
                    isCamera = action != null && action.equals(MediaStore.ACTION_IMAGE_CAPTURE);
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
            StringBuilder jsonString = new StringBuilder();
           /* if (authorization.cookie.length() != 0) {
                result = true;
            } else {*/
                String response = "";
                response = authorization.auth(getContext());
                if ((!response.equals("error")) && (!response.equals("no_login")) && (response.length() != 0)) {
                    result = true;
                } else if (response.equals("no_login")) {
                    result = false;
                }
           // }
            if (result) {
                try {
                    URL url;
                    String payload;
                    if (params.length == 3) {
                        url = new URL("http://irk.yourplus.ru/rating/achievements/add/");
                        payload = "------WebKitFormBoundarylHpOTMpTFaqnnId8\n" +
                                "Content-Disposition: form-data; name=\"action\"\n" +
                                "\n" +
                                "add\n" +
                                "------WebKitFormBoundarylHpOTMpTFaqnnId8\n" +
                                "Content-Disposition: form-data; name=\"NAME\"\n" +
                                "\n" +
                                params[0] + "\n" +
                                "------WebKitFormBoundarylHpOTMpTFaqnnId8\n" +
                                "Content-Disposition: form-data; name=\"DATE\"\n" +
                                "\n" +
                                params[1] + "\n" +
                                "------WebKitFormBoundarylHpOTMpTFaqnnId8\n" +
                                "Content-Disposition: form-data; name=\"award[0][type]\"\n" +
                                "\n" +
                                params[2] + "\n" +
                                "------WebKitFormBoundarylHpOTMpTFaqnnId8\n" +
                                "Content-Disposition: form-data; name=\"files[]\"; filename=\"\"\n" +
                                "Content-Type: application/octet-stream\n" +
                                "\n" +
                                "\n" +
                                "------WebKitFormBoundarylHpOTMpTFaqnnId8--";
                    } else {
                        url = new URL("http://irk.yourplus.ru/rating/achievements/edit/"+params[3]+"/");
                        payload = "------WebKitFormBoundarylHpOTMpTFaqnnId8\n" +
                                "Content-Disposition: form-data; name=\"ID\"\n" +
                                "\n" +
                                params[3]+"\n" +
                                "------WebKitFormBoundarylHpOTMpTFaqnnId8\n" +
                                "Content-Disposition: form-data; name=\"action\"\n" +
                                "\n" +
                                "edit\n" +
                                "------WebKitFormBoundarylHpOTMpTFaqnnId8\n" +
                                "Content-Disposition: form-data; name=\"NAME\"\n" +
                                "\n" +
                                params[0]+"\n" +
                                "------WebKitFormBoundarylHpOTMpTFaqnnId8\n" +
                                "Content-Disposition: form-data; name=\"DATE\"\n" +
                                "\n" +
                                params[1]+"\n" +
                                "------WebKitFormBoundarylHpOTMpTFaqnnId8\n" +
                                "Content-Disposition: form-data; name=\"award[19096][type]\"\n" +
                                "\n" +
                                params[2]+"\n" +
                                "------WebKitFormBoundarylHpOTMpTFaqnnId8\n" +
                                "Content-Disposition: form-data; name=\"award[19097][type]\"\n" +
                                "\n" +
                                params[2]+"\n" +
                                "------WebKitFormBoundarylHpOTMpTFaqnnId8\n" +
                                "Content-Disposition: form-data; name=\"files[]\"; filename=\"\"\n" +
                                "Content-Type: application/octet-stream\n" +
                                "\n" +
                                "\n" +
                                "------WebKitFormBoundarylHpOTMpTFaqnnId8--";
                    }
                    Log.d("req", params[2]);
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
            }
            return result;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            if (!result) {
                FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction()
                        .replace(R.id.container, new login_fragment())
                        .commit();
                Toast toast = Toast.makeText(getActivity().getApplicationContext(), "Неправильный Логин/Пароль", Toast.LENGTH_SHORT);
                toast.show();
                send.setEnabled(true);
            } else {
                send.setEnabled(true);
                if (achid.equals("")) {
                    Toast toast = Toast.makeText(getActivity().getApplicationContext(), "Достижение добавлено", Toast.LENGTH_SHORT);
                    toast.show();
                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                    fragmentManager.beginTransaction()
                            .replace(R.id.container, new achievements_fragment())
                            .commit();
                } else {
                    Toast toast = Toast.makeText(getActivity().getApplicationContext(), "Достижение отредактировано", Toast.LENGTH_SHORT);
                    toast.show();
                    ma.af = null;
                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                    fragmentManager.beginTransaction()
                            .replace(R.id.container, new achievements_fragment())
                            .commit();
                    achid="";
                }
            }
            try {
                File file = new File(outputFileUri.getPath());
                file.delete();
            } catch (Exception ignored) {
            }
        }
    }
}
