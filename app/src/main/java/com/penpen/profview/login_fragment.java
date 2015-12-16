package com.penpen.profview;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.preference.PreferenceManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;

import java.net.URL;

import adapter.NothingSelectedSpinnerAdapter;
import app.authorization;

/**
 * Created by penpen on 02.11.15.
 */
public class login_fragment extends Fragment {
    private String CAPTCHAsid="";
    private View viev;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.login_fragment, container, false);
        final SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getContext());
        final EditText login = (EditText) view.findViewById(R.id.login);
        final EditText pass = (EditText) view.findViewById(R.id.pass);
        login.setText(settings.getString("login_preference", ""));
        pass.setText(settings.getString("pass_preference", ""));
        final Spinner spinner = (Spinner) view.findViewById(R.id.Faculty_reg);
        final Spinner URspinner = (Spinner) view.findViewById(R.id.University_reg);
        final Spinner CRspinner = (Spinner) view.findViewById(R.id.City_reg);
        final LinearLayout loginlay = (LinearLayout) view.findViewById(R.id.loginlayout);
        final ScrollView reglay = (ScrollView) view.findViewById(R.id.scrollreglayout);
        Button loginb = (Button) view.findViewById(R.id.loginbutton);
        loginb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (authorization.isOnline(getActivity()) == false) {return;}
                new auth().execute(login.getText().toString(), pass.getText().toString());
            }
        });

        Button regb = (Button) view.findViewById(R.id.regbutton);
        regb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (authorization.isOnline(getActivity()) == false) {return;}
                LinearLayout loginlay = (LinearLayout) view.findViewById(R.id.loginlayout);
                loginlay.setVisibility(View.INVISIBLE);
                AccountManager manager = (AccountManager) getActivity().getSystemService(getActivity().ACCOUNT_SERVICE);
                Account[] list = manager.getAccounts();
                String gmail = "";
                for(Account account: list)
                {
                    if(account.type.equalsIgnoreCase("com.google"))
                    {
                        gmail = account.name;
                        break;
                    }
                }
                EditText regmail = (EditText) view.findViewById(R.id.regemail);
                regmail.setText(gmail);
                new getCAPTCHA().execute();
                ScrollView reglay = (ScrollView) view.findViewById(R.id.scrollreglayout);
                reglay.setVisibility(View.VISIBLE);
            }
        });

        Button sendregb = (Button) view.findViewById(R.id.regbuttonsend);
        sendregb.setOnClickListener(new View.OnClickListener() {
            private void focusedit(EditText et, String message) {
                if (authorization.isOnline(getActivity()) == false) {
                    return;
                }
                Toast toast = Toast.makeText(getActivity().getApplicationContext(), message, Toast.LENGTH_SHORT);
                toast.show();
                et.requestFocus();
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(et, InputMethodManager.SHOW_IMPLICIT);
            }

            @Override
            public void onClick(View v) {
                if (authorization.isOnline(getActivity()) == false) {
                    return;
                }
                EditText loginreg = (EditText) view.findViewById(R.id.reglogin);
                EditText passreg = (EditText) view.findViewById(R.id.regpass);
                EditText passconfirmreg = (EditText) view.findViewById(R.id.regpassconf);
                EditText emailreg = (EditText) view.findViewById(R.id.regemail);
                EditText captcha = (EditText) view.findViewById(R.id.regtextcaptcha);
                EditText lastnamereg = (EditText) getView().findViewById(R.id.regsecondname);
                EditText firstnamereg = (EditText) getView().findViewById(R.id.regfirstname);
                EditText thirdnamereg = (EditText) getView().findViewById(R.id.regthirdname);
                Spinner facultyspinner = (Spinner) getView().findViewById(R.id.Faculty_reg);
                if (passreg.getText().toString().equals(passconfirmreg.getText().toString())) {
                    if (loginreg.getText().toString().length() != 0) {
                    if (passreg.getText().toString().length() !=0) {
                        if (passconfirmreg.getText().toString().length() != 0) {

                                if (emailreg.getText().toString().length() != 0) {
                                    if (captcha.getText().toString().length() != 0) {
                                        if (lastnamereg.getText().toString().length() != 0) {
                                            if (firstnamereg.getText().toString().length() != 0) {
                                                if (thirdnamereg.getText().toString().length() != 0) {
                                                    if (facultyspinner.getSelectedItemPosition() != 0) {
                                                        new sendregfirst().execute(loginreg.getText().toString(), passreg.getText().toString(), emailreg.getText().toString(), captcha.getText().toString(), CAPTCHAsid);
                                                        new auth().execute(loginreg.getText().toString(), passreg.getText().toString(), "1", emailreg.getText().toString(), lastnamereg.getText().toString(), firstnamereg.getText().toString(), thirdnamereg.getText().toString());
                                                        loginreg.setText("");
                                                        passreg.setText("");
                                                        passconfirmreg.setText("");
                                                        emailreg.setText("");
                                                        captcha.setText("");
                                                        lastnamereg.setText("");
                                                        firstnamereg.setText("");
                                                        thirdnamereg.setText("");
                                                    } else {
                                                        if (authorization.isOnline(getActivity()) == false) {
                                                            return;
                                                        }
                                                        Toast toast = Toast.makeText(getActivity().getApplicationContext(), "Выберите факультет", Toast.LENGTH_SHORT);
                                                        toast.show();
                                                        facultyspinner.requestFocus();
                                                        return;
                                                    }
                                                } else {
                                                    focusedit(thirdnamereg, "Введите отчество");
                                                    return;
                                                }
                                            } else {
                                                focusedit(firstnamereg, "Введите имя");
                                                return;
                                            }
                                        } else {
                                            focusedit(lastnamereg, "Введите фамилию");
                                            return;
                                        }
                                    } else {
                                        focusedit(captcha, "Введите каптчу");
                                        return;
                                    }
                                } else {
                                    focusedit(emailreg, "Введите email");
                                    return;
                                }

                        } else {
                            focusedit(passconfirmreg, "Введите подтверждение");
                            return;
                        }
                    } else {
                        focusedit(passreg, "Введите пароль");
                        return;
                    }
                    } else {
                        focusedit(loginreg, "Введите логин");
                        return;
                    }
                } else {
                    focusedit(passreg, "Пароли не совпадают");
                    return;
                }
                LinearLayout loginlay = (LinearLayout) view.findViewById(R.id.loginlayout);
                loginlay.setVisibility(View.VISIBLE);
                ScrollView reglay = (ScrollView) view.findViewById(R.id.scrollreglayout);
                reglay.setVisibility(View.INVISIBLE);
            }
        });

        Button refreshcaptcha = (Button) view.findViewById(R.id.refreshcaptcha);
        refreshcaptcha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (authorization.isOnline(getActivity()) == false) {
                    return;
                }
                new getCAPTCHA().execute();
            }
        });

        pass.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (authorization.isOnline(getActivity()) == false) {
                    return false;
                }
                if (event.getAction() != KeyEvent.ACTION_DOWN)
                    return false;
                if (keyCode == KeyEvent.KEYCODE_ENTER) {
                    new auth().execute(login.getText().toString(), pass.getText().toString());
                    return true;
                }
                return false;
            }
        });

        ArrayAdapter<CharSequence> CRadapter = ArrayAdapter.createFromResource(view.getContext(), R.array.city, android.R.layout.simple_spinner_item);
        CRadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        CRspinner.setPrompt("Выберите город");
        CRspinner.setAdapter(
                new NothingSelectedSpinnerAdapter(
                        CRadapter,
                        R.layout.reg_city_spinner,
                        view.getContext()));
        CRspinner.setEnabled(false);

        ArrayAdapter<CharSequence> URadapter = ArrayAdapter.createFromResource(view.getContext(), R.array.university_Irkutsk, android.R.layout.simple_spinner_item);
        URadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        URspinner.setPrompt("Выберите ВУЗ");
        URspinner.setAdapter(
                new NothingSelectedSpinnerAdapter(
                        URadapter,
                        R.layout.reg_university_spinner,
                        view.getContext()));
        URspinner.setEnabled(false);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(view.getContext(), R.array.faculty_ISU, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setPrompt("Выберите факультет");
        spinner.setAdapter(
                new NothingSelectedSpinnerAdapter(
                        adapter,
                        R.layout.reg_faculty_spinner,
                        view.getContext()));
        viev = view;
        return view;
    }

    class getCAPTCHA extends AsyncTask<Void, Void, Bitmap> {

        @Override
        protected Bitmap doInBackground(Void... params) {
            Document doc = null;
            URL url = null;
            try {
                url = new URL("http://irk.yourplus.ru/registration/");
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            try {
                doc = Jsoup.parse(url, 6000);
            } catch (IOException e) {
                e.printStackTrace();
            }
            Bitmap CAPTCHA = null;
            if (doc!=null) {
                Elements images =doc.body().getElementsByTag("img");
                String CAPTCHAstr = images.attr("alt", "CAPTCHA").get(images.attr("alt", "CAPTCHA").size() - 1).toString();
                CAPTCHAsid = CAPTCHAstr.substring(CAPTCHAstr.indexOf("captcha_sid=")+12, CAPTCHAstr.indexOf("\"", CAPTCHAstr.indexOf("captcha_sid=")+12));
                CAPTCHAstr = "http://irk.yourplus.ru" + CAPTCHAstr.substring(CAPTCHAstr.indexOf("src=")+5, CAPTCHAstr.indexOf("\"", CAPTCHAstr.indexOf("src=")+5));
                try {
                    InputStream in = new java.net.URL(CAPTCHAstr).openStream();
                    CAPTCHA = BitmapFactory.decodeStream(in);
                } catch (Exception e) {
                    Log.e("Error", e.getMessage());
                    e.printStackTrace();
                }
            }
            return CAPTCHA;
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            super.onPostExecute(result);
            if (result != null) {
                ImageView captchaIMG = null;
                try {
                    captchaIMG = (ImageView) getActivity().findViewById(R.id.regcaptcha);
                } catch (NullPointerException e) {

                }
                if (captchaIMG != null) {
                    captchaIMG.setImageBitmap(result);
                }
            } else {
                Toast toast = Toast.makeText(getActivity().getApplicationContext(), "Невозможно загрузить каптчу, попробуйте позже", Toast.LENGTH_SHORT);
                toast.show();
            }
        }
    }

    class auth extends AsyncTask<String, Void, Boolean> {
        private Boolean isreg=false;
        private String logi;
        private String pas;
        private String email;
        private String ln;
        private String fn;
        private String tn;

        @Override
        protected Boolean doInBackground(String... params) {
            logi=params[0];
            pas = params[1];
            if (params.length == 6) {
                email = params[3];
                ln = params[4];
                fn = params[5];
                tn = params[6];
            }
            String response = "";
            Boolean result = false;
            if (params.length>0) {
                if (params[0].length() != 0 && params[1].length() != 0) {
                    response = authorization.auth(getContext(), params[0], params[1]);
                    Log.d("df", response);
                }
            }
            if ((response.equals("error") == false) && (response.equals("no_login") == false) && (response.length() != 0)) {
                result = true;
                if ((params.length == 6) && (params[2]=="1")) {
                    isreg=true;
                }
            } else if (response.equals("no_login") == true) {
                result = false;
            }
            return result;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            if (result == false) {
                Toast toast = Toast.makeText(getActivity().getApplicationContext(), "Неправильный Логин/Пароль", Toast.LENGTH_SHORT);
                toast.show();
            } else {
                SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(viev.getContext());
                EditText login = (EditText) viev.findViewById(R.id.login);
                EditText pass = (EditText) viev.findViewById(R.id.pass);
                SharedPreferences.Editor editor = settings.edit();
                editor.putString("login_preference", logi);
                editor.putString("pass_preference", pas);
                editor.commit();
                MainActivity.isLogin = true;
                MainActivity.items[0]="Выйти";
                if (MainActivity.menuadapter != null) {
                    MainActivity.menuadapter.notifyDataSetChanged();
                }
                login.setText(settings.getString("login_preference", ""));
                pass.setText(settings.getString("pass_preference", ""));
                if (isreg) {
                    Spinner cityspinner = (Spinner) getView().findViewById(R.id.City_reg);
                    Spinner facultyspinner = (Spinner) getView().findViewById(R.id.Faculty_reg);
                    String sessid = Jsoup.parse(authorization.responseHTML).body().getElementsByTag("input").get(0).attr("value");
                    String userid = Jsoup.parse(authorization.responseHTML).body().getElementsByTag("input").get(1).attr("value");
                    new sendregsecond().execute(
                            sessid,
                            userid,
                            ln,
                            fn,
                            tn,
                            logi,
                            email,
                            getResources().getStringArray(R.array.irkutskfacultyid)[facultyspinner.getSelectedItemPosition()],
                            getResources().getStringArray(R.array.cityid)[cityspinner.getSelectedItemPosition()]);
                    MainActivity.fragmentnumber=4;
                    FragmentManager fragmentManager = getFragmentManager();
                    fragmentManager.beginTransaction().replace(R.id.container, new achievements_fragment()).commit();
                } else {
                    Toast toast = Toast.makeText(viev.getContext().getApplicationContext(), "Вход выполнен", Toast.LENGTH_SHORT);
                    toast.show();
                    try {
                        MainActivity.fragmentnumber=4;
                        FragmentManager fragmentManager = getFragmentManager();
                        fragmentManager.beginTransaction().replace(R.id.container, new achievements_fragment()).commit();
                    } catch (Exception e) {

                    }
                }
            }
        }
    }

    class sendregsecond extends AsyncTask<String, Void, Boolean> {
        @Override
        protected Boolean doInBackground(String... params) {
            Boolean result = false;
            String line;
            StringBuffer jsonString = new StringBuffer();
            if (params.length == 9) {
                try {
                    URL url = new URL("http://irk.yourplus.ru/personal/");
                    String payload = "------WebKitFormBoundaryyRG7LAYrKtBYJ6ON\n" +
                            "Content-Disposition: form-data; name=\"sessid\"\n" +
                            "\n" +
                            params[0]+"\n" +
                            "------WebKitFormBoundaryyRG7LAYrKtBYJ6ON\n" +
                            "Content-Disposition: form-data; name=\"ID\"\n" +
                            "\n" +
                            params[1]+"\n" +
                            "------WebKitFormBoundaryyRG7LAYrKtBYJ6ON\n" +
                            "Content-Disposition: form-data; name=\"LAST_NAME\"\n" +
                            "\n" +
                            params[2]+"\n" +
                            "------WebKitFormBoundaryyRG7LAYrKtBYJ6ON\n" +
                            "Content-Disposition: form-data; name=\"NAME\"\n" +
                            "\n" +
                            params[3]+"\n" +
                            "------WebKitFormBoundaryyRG7LAYrKtBYJ6ON\n" +
                            "Content-Disposition: form-data; name=\"SECOND_NAME\"\n" +
                            "\n" +
                            params[4]+"\n" +
                            "------WebKitFormBoundaryyRG7LAYrKtBYJ6ON\n" +
                            "Content-Disposition: form-data; name=\"UF_LINK_CITY\"\n" +
                            "\n" +
                            params[8]+"\n" +
                            "------WebKitFormBoundaryyRG7LAYrKtBYJ6ON\n" +
                            "Content-Disposition: form-data; name=\"UF_LINK_SUBJECT\"\n" +
                            "\n" +
                            "19\n" +
                            "------WebKitFormBoundaryyRG7LAYrKtBYJ6ON\n" +
                            "Content-Disposition: form-data; name=\"UF_LINK_FACULTY\"\n" +
                            "\n" +
                            params[7]+"\n" +
                            "------WebKitFormBoundaryyRG7LAYrKtBYJ6ON\n" +
                            "Content-Disposition: form-data; name=\"LOGIN\"\n" +
                            "\n" +
                            params[5]+"\n" +
                            "------WebKitFormBoundaryyRG7LAYrKtBYJ6ON\n" +
                            "Content-Disposition: form-data; name=\"NEW_PASSWORD\"\n" +
                            "\n" +
                            "\n" +
                            "------WebKitFormBoundaryyRG7LAYrKtBYJ6ON\n" +
                            "Content-Disposition: form-data; name=\"EMAIL\"\n" +
                            "\n" +
                            params[6]+"\n" +
                            "------WebKitFormBoundaryyRG7LAYrKtBYJ6ON\n" +
                            "Content-Disposition: form-data; name=\"NEW_PASSWORD_CONFIRM\"\n" +
                            "\n" +
                            "\n" +
                            "------WebKitFormBoundaryyRG7LAYrKtBYJ6ON\n" +
                            "Content-Disposition: form-data; name=\"save\"\n" +
                            "\n" +
                            "Сохранить\n" +
                            "------WebKitFormBoundaryyRG7LAYrKtBYJ6ON--";
                    Log.d("req", payload);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setDoInput(true);
                    connection.setDoOutput(true);
                    connection.setRequestMethod("POST");
                    connection.addRequestProperty("Content-Type", "multipart/form-data; boundary=----WebKitFormBoundaryyRG7LAYrKtBYJ6ON");
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

        }
    }

    class sendregfirst extends AsyncTask<String, Void, Boolean> {

        @Override
        protected Boolean doInBackground(String... params) {
            Boolean result = false;
            String line;
            StringBuffer jsonString = new StringBuffer();
            if (params.length == 5) {
                try {
                    URL url = new URL("http://irk.yourplus.ru/registration/");
                    String payload = "------WebKitFormBoundaryQVjC7O88s1u2UcIR\n" +
                            "Content-Disposition: form-data; name=\"REGISTER[LOGIN]\"\n" +
                            "\n" +
                            params[0]+"\n" +
                            "------WebKitFormBoundaryQVjC7O88s1u2UcIR\n" +
                            "Content-Disposition: form-data; name=\"REGISTER[EMAIL]\"\n" +
                            "\n" +
                            params[2]+"\n" +
                            "------WebKitFormBoundaryQVjC7O88s1u2UcIR\n" +
                            "Content-Disposition: form-data; name=\"REGISTER[PASSWORD]\"\n" +
                            "\n" +
                            params[1]+"\n" +
                            "------WebKitFormBoundaryQVjC7O88s1u2UcIR\n" +
                            "Content-Disposition: form-data; name=\"REGISTER[CONFIRM_PASSWORD]\"\n" +
                            "\n" +
                            params[1]+"\n" +
                            "------WebKitFormBoundaryQVjC7O88s1u2UcIR\n" +
                            "Content-Disposition: form-data; name=\"captcha_word\"\n" +
                            "\n" +
                            params[3]+"\n" +
                            "------WebKitFormBoundaryQVjC7O88s1u2UcIR\n" +
                            "Content-Disposition: form-data; name=\"captcha_sid\"\n" +
                            "\n" +
                            params[4]+"\n" +
                            "------WebKitFormBoundaryQVjC7O88s1u2UcIR\n" +
                            "Content-Disposition: form-data; name=\"register_submit_button\"\n" +
                            "\n" +
                            "Регистрация\n" +
                            "------WebKitFormBoundaryQVjC7O88s1u2UcIR--";
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setDoInput(true);
                    connection.setDoOutput(true);
                    connection.setRequestMethod("POST");
                    connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=----WebKitFormBoundaryQVjC7O88s1u2UcIR");
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

        }

    }

}
