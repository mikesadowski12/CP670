package com.example.androidassignments;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.util.Xml;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import org.xmlpull.v1.XmlPullParser;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import java.util.List;
import java.util.ArrayList;

public class WeatherForecast extends AppCompatActivity {
    protected static final String ACTIVITY_NAME = "WeatherForecast";

    ProgressBar progressBar;
    ImageView image;
    TextView max_temp;
    TextView min_temp;
    TextView curr_temp;
    String urlString = "https://api.openweathermap.org/data/2.5/weather?q=ottawa,ca&APPID=79cecf493cb6e52d25bb7b7050ff723c&mode=xml&units=metric";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather_forecast);

        List<String> cities = new ArrayList<String>();
        cities.add("Ottawa");
        cities.add("Toronto");
        cities.add("Vancouver");
        cities.add("Montreal");
        cities.add("Winnipeg");
        cities.add("Edmonton");
        cities.add("Kitchener");

        Spinner spinner = (Spinner) findViewById(R.id.spinner);
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, cities);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(dataAdapter);

        curr_temp = findViewById(R.id.currentTemp);
        min_temp = findViewById(R.id.minTemp);
        max_temp = findViewById(R.id.maxTemp);
        image = findViewById(R.id.currentWeather);

        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);

        ForecastQuery f = new ForecastQuery();
        f.execute();

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                urlString = "https://api.openweathermap.org/data/2.5/weather?q=" + spinner.getSelectedItem().toString() + ",ca&APPID=79cecf493cb6e52d25bb7b7050ff723c&mode=xml&units=metric";
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub
            }
        });
    }

    public class ForecastQuery extends AsyncTask<String, Integer, String> {
        String minTemp;
        String maxTemp;
        String currTemp;
        Bitmap picture;

        @Override
        protected String doInBackground(String... strings) {
            try {
                URL url = new URL(urlString);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(10000 /* milliseconds */);
                conn.setConnectTimeout(15000 /* milliseconds */);
                conn.setRequestMethod("GET");
                conn.setDoInput(true);
                conn.connect();
                InputStream in = conn.getInputStream();

                try {
                    XmlPullParser parser = Xml.newPullParser();
                    parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
                    parser.setInput(in, null);

                    while (parser.getEventType() != XmlPullParser.END_DOCUMENT) {
                        if (parser.getEventType() == XmlPullParser.START_TAG) {
                            if (parser.getName().equals("temperature")) {
                                currTemp = parser.getAttributeValue(null, "value");
                                publishProgress(25);
                                minTemp = parser.getAttributeValue(null, "min");
                                publishProgress(50);
                                maxTemp = parser.getAttributeValue(null, "max");
                                publishProgress(75);

                            } else if (parser.getName().equals("weather")) {
                                String icon = parser.getAttributeValue(null, "icon");
                                String iconName = icon + ".png";

                                Log.i(ACTIVITY_NAME, "Looking for file: " + iconName);

                                if (fileExistance((iconName))) {
                                    FileInputStream fis = null;

                                    try {
                                        fis = openFileInput(iconName);

                                    } catch (FileNotFoundException e) {
                                        e.printStackTrace();
                                    }
                                    Log.i(ACTIVITY_NAME, "Found the file locally");
                                    picture = BitmapFactory.decodeStream(fis);

                                } else {
                                    String iconUrl =  "https://openweathermap.org/img/w/" + iconName;
                                    picture = getImage(new URL(iconUrl));
                                    FileOutputStream outputStream = openFileOutput(iconName, Context.MODE_PRIVATE);

                                    picture.compress(Bitmap.CompressFormat.PNG, 80,outputStream);
                                    outputStream.flush();
                                    outputStream.close();
                                }

                                publishProgress(100);
                            }
                        }

                        parser.next();
                    }
                } finally {
                    in.close();
                }

            } catch (Exception ex){
                ex.printStackTrace();
            }

            return null;
        }

        public boolean fileExistance(String fname){
            File file = getBaseContext().getFileStreamPath(fname);
            return file.exists();
        }

        public Bitmap getImage(URL url) {
            HttpsURLConnection connection = null;
            try {
                connection = (HttpsURLConnection) url.openConnection();
                connection.connect();
                int responseCode = connection.getResponseCode();

                if (responseCode == 200) {
                    return BitmapFactory.decodeStream(connection.getInputStream());

                } else return null;

            } catch (Exception e) {
                return null;

            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
            }
        }

        @Override
        protected void onPostExecute(String a) {
            progressBar.setVisibility(View.INVISIBLE);
            image.setImageBitmap(picture);
            curr_temp.setText(currTemp + "C\u00b0");
            min_temp.setText(minTemp + "C\u00b0");
            max_temp.setText(maxTemp + "C\u00b0");
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            progressBar.setProgress(values[0]);
        }
    }
}
