package com.example.roomreservationmandatory;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
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

public class OneRoomActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    public static final String ROOM = "ROOM";
    private JsonRoomModel OneRoom;

    private String conn = "https://anbo-roomreservationv3.azurewebsites.net/api/Reservations/room/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_one_room);

        Intent intent = getIntent();
        OneRoom = (JsonRoomModel) intent.getSerializableExtra(ROOM);

        TextView MessageTextView = findViewById(R.id.MessageTextView);
        MessageTextView.setText("Liste over reservationer for " + OneRoom.getName());

        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    protected void onStart() {
        super.onStart();
        ReadTask task = new ReadTask();
        task.execute(conn + OneRoom.getId());
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

    private void populateList(String jsonString) {
        Gson gson = new GsonBuilder().create();
        Log.d("BENIS", jsonString);
        JsonReservationModel[] ARoom = gson.fromJson(jsonString, JsonReservationModel[].class);
        ListView ReservationsListView = findViewById(R.id.ReservationsListView);
        ArrayAdapter<JsonReservationModel> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, ARoom);
        ReservationsListView.setAdapter(adapter);
    }

    //Buttons
    public void ReserveRoomButton(View view) {
        Intent intent = new Intent(getBaseContext(), ReserveRoomActivity.class);
        intent.putExtra(ReserveRoomActivity.RESERVEROOM, OneRoom);
        startActivity(intent);
    }

    public void BackButton(View view) {
        finish();
    }
}
