package com.scott.httppostreqtesting;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    private EditText idEditText;
    private EditText urlEditText;
    private Button sendButton;
    private String sentId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        idEditText = findViewById(R.id.id_editText);
        urlEditText = findViewById(R.id.url_editText);
        sendButton = findViewById(R.id.send_button);

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String id = idEditText.getText().toString();
                String urlString = urlEditText.getText().toString();
                if (!id.isEmpty() && !urlString.isEmpty()) {
                    new SendIdTask().execute(id, urlString);
                }
            }
        });
    }

    private class SendIdTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            sentId = params[0];
            String urlString = params[1];
            StringBuilder response = new StringBuilder();

            try {
                URL url = new URL(urlString);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setDoOutput(true);
                connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

                String postData = "id=" + sentId;
                byte[] postDataBytes = postData.getBytes("UTF-8");

                OutputStream outputStream = connection.getOutputStream();
                outputStream.write(postDataBytes);
                outputStream.flush();
                outputStream.close();

                int responseCode = connection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    reader.close();
                }
                connection.disconnect();
            } catch (Exception e) {
                e.printStackTrace();
            }

            return response.toString();
        }

        @Override
        protected void onPostExecute(String response) {
            super.onPostExecute(response);
            Log.d("Response", response);

            if (response.contains("\"id\": \"" + sentId + "\"")) {
                Toast.makeText(MainActivity.this, "POST request sent successfully, ID sent: " + sentId, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(MainActivity.this, "POST request failed", Toast.LENGTH_SHORT).show();
            }
        }
    }

}
