package com.example.weatherapp;

import android.os.AsyncTask;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {
    private EditText user_field;
    private Button main_btn;
    private TextView result_info;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        user_field = findViewById(R.id.user_field);
        main_btn = findViewById(R.id.main_btn);
        result_info = findViewById(R.id.result_info);

        main_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (user_field.getText().toString().trim().equals(""))
                    Toast.makeText(MainActivity.this, R.string.no_use_input, Toast.LENGTH_LONG).show();
                else {
                    String city = user_field.getText().toString();
                    String key = "25f7f8bc19281c7581774ce45e4c9552";
                    String url = "https://api.openweathermap.org/data/2.5/weather?q=" + city + "&appid=" + key + "&units=metric&lang=ru";

                    new GetURLdata().execute(url);
                }
            }
        });
    }

    private class GetURLdata extends AsyncTask<String, String, String> {

        protected void onPreExecute() {
            super.onPreExecute();
            result_info.setText("Ожидайте...");
        }

        @Override
        protected String doInBackground(String... strings) {
            HttpURLConnection connection = null;
            BufferedReader reader = null;

            try {
                URL url = new URL(strings[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                InputStream stream = connection.getInputStream();
                reader = new BufferedReader(new InputStreamReader(stream));

                StringBuffer buffer = new StringBuffer();
                String line = "";

                while ((line = reader.readLine()) != null)
                    buffer.append(line).append("\n");

                return buffer.toString();

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (connection != null)
                    connection.disconnect();
                try {
                    if (reader != null)
                        reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            try {
                JSONObject jsonObject = new JSONObject(result);
                String weather = jsonObject.getJSONArray("weather").getJSONObject(0).getString("main");
                switch (weather) {
                    case "Clouds":
                        weather = "Облачно";
                        break;
                    case "Clear":
                        weather = "Ясно";
                        break;
                    case "Rain":
                        weather = "Дождь";
                        break;
                    case "Snow":
                        weather = "Снег";
                        break;
                    case "Fog":
                        weather = "Туман";
                        break;
                }

                String temp = "Температура: " + jsonObject.getJSONObject("main").getDouble("temp") + "°C" + "\n";
                String wind = "Ветер " + jsonObject.getJSONObject("wind").getDouble("speed") + " м/с" + "\n";
                String humidity = "Влажность " + jsonObject.getJSONObject("main").getDouble("humidity") + " %\n";
                String pressure = "Атмосферное давление " + (int) (jsonObject.getJSONObject("main").getDouble("pressure") / 1.33) + " мм.рт.ст." + "\n";

                String tw = weather + "\n" + temp + wind + humidity + pressure;
                result_info.setText(tw);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
