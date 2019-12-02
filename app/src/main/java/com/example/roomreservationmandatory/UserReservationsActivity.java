package com.example.roomreservationmandatory;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class UserReservationsActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private String conn = "http://anbo-roomreservationv3.azurewebsites.net/api/Reservations/user/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_reservations);

        mAuth = FirebaseAuth.getInstance();

        //This is for redirecting to the login activity IF they're not logged in already.
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() == null) {
                    Intent intent = new Intent(getBaseContext(), Activity_Login.class);
                    startActivity(intent);
                }
                else {
                    ReadTask task = new ReadTask();
                    task.execute(conn + mAuth.getCurrentUser().getUid());
                }
            }
        };
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    public void BackButton(View view) {
        Intent intent = new Intent(getBaseContext(), MainActivity.class);
        startActivity(intent);
        finish();
    }

    private class ReadTask extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... strings) {
            String uri = strings[0];
            OkHttpClient client = new OkHttpClient();
            Request.Builder requestBuilder = new Request.Builder();
            requestBuilder.url(uri);
            Request request = requestBuilder.build();
            Call call = client.newCall(request);

            try {
                Response response = call.execute();
                if (response.isSuccessful()) {
                    ResponseBody responseBody = response.body();
                    String jsonString = responseBody.string();
                    return jsonString;
                } else {
                    cancel(true);
                    return uri + "\n" + response.code() + " " + response.message();
                }
            } catch (IOException ex) {
                cancel(true);
                return ex.getMessage();
            }
        }

        @Override
        protected void onPostExecute(String jsonString) {
            populateList(jsonString);
        }

        @Override
        protected void onCancelled(String message) {
            TextView MessageTextView = findViewById(R.id.MessageTextView);
            MessageTextView.setText(message);
            Log.e("BENIS", message);
        }
    }

    private void populateList (String jsonString) {
        Gson gson = new GsonBuilder().create();
        Log.d("BENIS", jsonString);
        JsonReservationModel[] User = gson.fromJson(jsonString, JsonReservationModel[].class);
        ListView UserReservationsListView = findViewById(R.id.UserReservationsListView);
        ArrayAdapter<JsonReservationModel> adapter = new ArrayAdapter<>(this,android.R.layout.simple_list_item_1, User);
        UserReservationsListView.setAdapter(adapter);
        UserReservationsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Intent intent = new Intent(getBaseContext(), OneReservationActivity.class);
                JsonReservationModel Reservation = (JsonReservationModel) adapterView.getItemAtPosition(position);
                intent.putExtra(OneReservationActivity.Companion.getRESERVATION(), Reservation);
                startActivity(intent);
            }
        });
    }
}
