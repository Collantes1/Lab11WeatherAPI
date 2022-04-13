package com.example.lab11weatherapi;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONObject;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    String key = "6dff85074d434c048f784637221204";
    EditText txt1;
    EditText txt;
    TextView lab;
    ImageView img;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        txt1 = findViewById(R.id.text_api);
        txt = findViewById(R.id.text_city);
        lab = findViewById(R.id.label_temp);
        img = findViewById(R.id.img_Icon);
        txt1.setText(key);
    }

    public void onQueryClick(View v)
    {
        String key = txt1.getText().toString();
        String city = txt.getText().toString(); // Получение значения текстового поля с именем города
        // Поток
        Thread t = new Thread(() -> {
            try {
                URL url = new URL("https://api.weatherapi.com/v1/current.json?key=" + key + "&q=" + city + "&aqi=no"); // Сформированный объект URL
                HttpURLConnection con = (HttpURLConnection) url.openConnection(); // Подключение HTTP к серверу и передача текста запроса
                InputStream is = con.getInputStream(); // Получение входящих потоков данных с сервера
                byte [] buf = new byte[2048];
                String res = "";
                while (true) // Цикл, считающий небольшими пропорциями входящие данные собирая из них текстовую строку
                {
                    int len = is.read(buf, 0, buf.length);
                    if (len < 0) break;
                    res = res + new String(buf, 0, len);
                }
                con.disconnect(); // Отключение HTTP соединения
                Log.d("json", res); // Вывод полученного результата в log

                JSONObject doc = new JSONObject(res); // Экземпляр класса из полученного текста запроса
                //Вывод из текста строку температуры и т.д.
                JSONObject curr = doc.getJSONObject("current"); // Извлечение поля current в виде ещё одного объекта
                float temp = (float) curr.getDouble("temp_c"); // Считывание значение поля temp_c
                float temp0 = (float) curr.getDouble("feelslike_c");
                float temp1 = (float) curr.getDouble("wind_kph");
                float temp2 = (float) curr.getDouble("precip_mm");
                float temp3 = (float) curr.getDouble("humidity");
                float temp4 = (float) curr.getDouble("cloud");
                float temp5 = (float) curr.getDouble("vis_km");
                float temp6 = (float) curr.getDouble("uv");
                float temp7 = (float) curr.getDouble("gust_kph");

                // Работа с выводом изображения
                JSONObject cond = curr.getJSONObject("condition");
                String icon =  cond.getString("icon");

                URL url1 = new URL("https:" + icon);
                HttpURLConnection con1 = (HttpURLConnection) url1.openConnection();
                InputStream is1 = con1.getInputStream();
                Bitmap bmp = BitmapFactory.decodeStream(is1);
                con1.disconnect();

                runOnUiThread(() -> { // Вывод значений на экран
                    lab.setText("Температура " + String.valueOf(temp) + "°C\n"
                            + "Ощущается как " + String.valueOf(temp0) + "°C\n"
                            + "Скорость ветра " + String.valueOf(temp1) + " км/ч\n"
                            + "Порывы ветра до " + String.valueOf(temp7) + " км/ч\n"
                            + "Количество осадков " + String.valueOf(temp2) + " mm\n"
                            + "Влажность воздуха " + String.valueOf(temp3) + " %\n"
                            + "Облочность " + String.valueOf(temp4) + " %\n"
                            + "Видимость " + String.valueOf(temp5) + " km\n"
                            + "УФ-индекс " + String.valueOf(temp6) + "\n");
                    img.setImageBitmap(bmp);

                });
            }
            catch (Exception ex) {
                ex.printStackTrace();
            }
        });
        t.start();
    }
}