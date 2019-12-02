package com.example.roomreservationmandatory;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class MainActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    private String conn = "https://anbo-roomreservationv3.azurewebsites.net/api/Rooms";

    private GestureDetector gestureDetector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.mandatorytoolbar);
        setSupportActionBar(toolbar);
        setTitle("ZEALAND");

        mAuth = FirebaseAuth.getInstance();

        gestureDetector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onFling(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
                Log.d("BENIS", motionEvent.toString() + " AND " + motionEvent1.toString());

                boolean rightSwipe = motionEvent.getX() < motionEvent1.getX();
                Log.d("BENIS", "Oh, it's a " + rightSwipe);

                return true;
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.LogOut) {
            mAuth.signOut();
            Intent intent = new Intent(getBaseContext(), MainActivity.class);
            startActivity(intent);
            Toast.makeText(MainActivity.this, "Du er nu logget ud!", Toast.LENGTH_LONG).show();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.mainmenu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onStart() {
        super.onStart();
        ReadTask task = new ReadTask();
        task.execute(conn);
    }

    public void UserReservationButtonClicked(View view) {
        Intent intent = new Intent(getBaseContext(), UserReservationsActivity.class);
        startActivity(intent);
    }

   /* private void getDataUsingOkHttpEnqueue() {
        OkHttpClient client = new OkHttpClient();
        Request.Builder requestBuilder = new Request.Builder();
        requestBuilder.url(conn);
        Request request = requestBuilder.build();
        okhttp3.Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull okhttp3.Call call, @NotNull final IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        TextView MessageTextView = findViewById(R.id.MessageTextView);
                        MessageTextView.setText(e.getMessage());
                    }
                });
            }

            @Override
            public void onResponse(@NotNull okhttp3.Call call, @NotNull final okhttp3.Response response) throws IOException {
                if (response.isSuccessful()) {
                    final String jsonString = response.body().string();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            populateList(jsonString);
                        }
                    });
                }
                else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            TextView MessageTextView = findViewById(R.id.MessageTextView);
                            MessageTextView.setText(conn + "\n" + response.code() + " " + response.message());
                        }
                    });
                }
            }
        });
    } */


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
        final JsonRoomModel[] Rooms = gson.fromJson(jsonString, JsonRoomModel[].class);
        ListView JsonsListView = findViewById(R.id.JsonsListView);
        //    RoomListAdapter adapter = new RoomListAdapter(getBaseContext(), R.layout.room_list_adapter, Rooms);
        ArrayAdapter<JsonRoomModel> adapter = new ArrayAdapter<JsonRoomModel>(this, android.R.layout.simple_list_item_1, Rooms);
        JsonsListView.setAdapter(adapter);
        JsonsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Intent intent = new Intent(getBaseContext(), OneRoomActivity.class);
                JsonRoomModel Room = (JsonRoomModel) adapterView.getItemAtPosition(position);
                intent.putExtra(OneRoomActivity.ROOM, Room);
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return gestureDetector.onTouchEvent(event);
    }
}