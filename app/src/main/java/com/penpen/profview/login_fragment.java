package com.penpen.profview;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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
        Button loginb = (Button) view.findViewById(R.id.loginbutton);
        loginb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new auth().execute(login.getText().toString(), pass.getText().toString());
            }
        });

        Button regb = (Button) view.findViewById(R.id.regbutton);
        regb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LinearLayout loginlay = (LinearLayout) view.findViewById(R.id.loginlayout);
                loginlay.setVisibility(View.INVISIBLE);
                if (login.getText().length() != 0) {
                    EditText reglogin = (EditText) view.findViewById(R.id.reglogin);
                    reglogin.setText(login.getText().toString());
                }
                if (pass.getText().length() != 0) {
                    EditText regpass = (EditText) view.findViewById(R.id.regpass);
                    regpass.setText(pass.getText().toString());
                }
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
                Toast toast = Toast.makeText(getActivity().getApplicationContext(), message, Toast.LENGTH_SHORT);
                toast.show();
                et.requestFocus();
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(et, InputMethodManager.SHOW_IMPLICIT);
            }

            @Override
            public void onClick(View v) {
                EditText loginreg = (EditText) view.findViewById(R.id.reglogin);
                EditText passreg = (EditText) view.findViewById(R.id.regpass);
                EditText passconfirmreg = (EditText) view.findViewById(R.id.regpassconf);
                EditText emailreg = (EditText) view.findViewById(R.id.regemail);
                EditText captcha = (EditText) view.findViewById(R.id.regtextcaptcha);
                if (passreg.getText().toString().equals(passconfirmreg.getText().toString())) {
                    if (loginreg.getText().toString().length() != 0) {
                        if (emailreg.getText().toString().length() !=0) {
                            if (captcha.getText().toString().length() != 0) {
                                new sendregfirst().execute(loginreg.getText().toString(), passreg.getText().toString(), emailreg.getText().toString(), captcha.getText().toString(), CAPTCHAsid);
                                new auth().execute(loginreg.getText().toString(), passreg.getText().toString());
                                loginreg.setText("");
                                passreg.setText("");
                                passconfirmreg.setText("");
                                emailreg.setText("");
                                captcha.setText("");
                            } else {
                                focusedit(captcha, "Введите каптчу");
                            }
                        } else {
                            focusedit(emailreg, "Введите email");
                        }
                    } else {
                        focusedit(loginreg, "Введите логин");
                    }
                } else {
                    focusedit(passreg, "Пароли не совпадают");
                }
                EditText lastnamereg = (EditText) view.findViewById(R.id.regsecondname);
                EditText firstnamereg = (EditText) view.findViewById(R.id.regfirstname);
                EditText thirdnamereg = (EditText) view.findViewById(R.id.regthirdname);
                String[] city = new String[3];
                city[0] = "19094";
                city[1] = "19095";
                city[2] = "45685";
                String[] university_irkutsk = new String[13];
                university_irkutsk[0] = "";
                Log.d("cookie", authorization.cookie);
                String sessid = authorization.cookie.substring(authorization.cookie.indexOf("PHPSESSID=") + 10, authorization.cookie.indexOf(";", authorization.cookie.indexOf("PHPSESSID=") + 10));
                Log.d("sessid", sessid);
                String userid = "";
                //new sendregsecond().execute(sessid, userid, lastnamereg.getText().toString(), firstnamereg.getText().toString(), thirdnamereg.getText().toString(), loginreg.getText().toString(), emailreg.getText().toString(),);
                //Log.d("b", spinner.getSelectedItem().toString());
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
                new getCAPTCHA().execute();
            }
        });

        pass.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
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
                Log.d("b", CAPTCHAstr);
                CAPTCHAsid = CAPTCHAstr.substring(CAPTCHAstr.indexOf("captcha_sid=")+12, CAPTCHAstr.indexOf("\"", CAPTCHAstr.indexOf("captcha_sid=")+12));
                //Log.d("csid", CAPTCHAsid);
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
                ImageView captchaIMG = (ImageView) getActivity().findViewById(R.id.regcaptcha);
                captchaIMG.setImageBitmap(result);
            } else {
                Toast toast = Toast.makeText(getActivity().getApplicationContext(), "Невозможно загрузить каптчу, попробуйте позже", Toast.LENGTH_SHORT);
                toast.show();
            }
        }
    }

    class auth extends AsyncTask<String, Void, Boolean> {

        @Override
        protected Boolean doInBackground(String... params) {
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
            } else if (response.equals("no_login") == true) {
                result = false;
            }
            return result;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            if (result == false) {
                /*//Todo: test this
                FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction()
                        .replace(R.id.container, new login_fragment())
                        .commit();*/
                Toast toast = Toast.makeText(getActivity().getApplicationContext(), "Неправильный Логин/Пароль", Toast.LENGTH_SHORT);
                toast.show();
            } else {
                SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getContext());
                EditText login = (EditText) getView().findViewById(R.id.login);
                EditText pass = (EditText) getView().findViewById(R.id.pass);
                SharedPreferences.Editor editor = settings.edit();
                editor.putString("login_preference", login.getText().toString());
                editor.putString("pass_preference", pass.getText().toString());
                editor.commit();
                Toast toast = Toast.makeText(getActivity().getApplicationContext(), "Вход выполнен", Toast.LENGTH_SHORT);
                toast.show();
            }
        }
    }

    class sendregsecond extends AsyncTask<String, Void, Boolean> {

        @Override
        protected Boolean doInBackground(String... params) {
            Boolean result = false;
            String line;
            StringBuffer jsonString = new StringBuffer();
            if (params.length == 5) {
                try {
                    URL url = new URL("http://irk.yourplus.ru/registration/");
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
                            "45685\n" +
                            "------WebKitFormBoundaryyRG7LAYrKtBYJ6ON\n" +
                            "Content-Disposition: form-data; name=\"UF_LINK_SUBJECT\"\n" +
                            "\n" +
                            "45830\n" +
                            "------WebKitFormBoundaryyRG7LAYrKtBYJ6ON\n" +
                            "Content-Disposition: form-data; name=\"UF_LINK_FACULTY\"\n" +
                            "\n" +
                            "45873\n" +
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
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setDoInput(true);
                    connection.setDoOutput(true);
                    connection.setRequestMethod("POST");
                    connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=----WebKitFormBoundaryyRG7LAYrKtBYJ6ON");
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
                //Log.d("out", jsonString.toString());
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
                //Log.d("out", jsonString.toString());
            }
            return result;
        }

        @Override
        protected void onPostExecute(Boolean result) {

        }
    }
}
