package com.example.roomreservationmandatory;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DateFormat;
import java.util.Calendar;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ReserveRoomActivity extends AppCompatActivity {
    public static final String RESERVEROOM = "RESERVEROOM";
    private JsonRoomModel TheRoom;
    private String conn = "https://anbo-roomreservationv3.azurewebsites.net/api/Rooms";
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private Calendar DateStartTime = Calendar.getInstance();
    private Calendar DateFinishTime = Calendar.getInstance();
    private Button FromDateButton;
    private Button ToDateButton;
    private Button FromTimeButton;
    private Button ToTimeButton;
    private String UserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reserve_room);
        FromDateButton = findViewById(R.id.FromDateButton);
        ToDateButton = findViewById(R.id.ToDateButton);
        FromTimeButton = findViewById(R.id.FromTimeButton);
        ToTimeButton = findViewById(R.id.ToTimeButton);

        mAuth = FirebaseAuth.getInstance();

        //This is for redirecting to the login activity IF they're not logged in already.
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() == null) {
                    Intent intent = new Intent(getBaseContext(), Activity_Login.class);
                    startActivity(intent);
                }
            }
        };

        //Presentable data
        Intent intent = getIntent();
        TheRoom = (JsonRoomModel) intent.getSerializableExtra(RESERVEROOM);
        TextView ReservationRoomName = findViewById(R.id.ReservationRoomName);
        ReservationRoomName.setText("Reservation til rum " + TheRoom.getName());

    }

    @Override
    protected void onStart(){
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    public void FromDatePickButtonClicked(View view) {
        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                DateStartTime.set(Calendar.YEAR, year);
                DateStartTime.set(Calendar.MONTH, month);
                DateStartTime.set(Calendar.DAY_OF_MONTH, day);
                DateFormat dateFormat = DateFormat.getDateInstance();
                String dateString = dateFormat.format(DateStartTime.getTimeInMillis());
                FromDateButton.setText(dateString);
            }
        };
        Calendar calendar = Calendar.getInstance();
        int CurrentYear = calendar.get(Calendar.YEAR);
        int CurrentMonth = calendar.get(Calendar.MONTH);
        int CurrentDay = calendar.get(Calendar.DATE);
        DatePickerDialog dialog = new DatePickerDialog(
                this, dateSetListener, CurrentYear, CurrentMonth, CurrentDay );
        dialog.show();
    }

    public void FromTimePickButtonClicked(View view) {
        TimePickerDialog.OnTimeSetListener timeSetListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int hour, int minute) {
                DateStartTime.set(Calendar.HOUR_OF_DAY, hour);
                DateStartTime.set(Calendar.MINUTE, minute);
                DateFormat dateFormat = DateFormat.getTimeInstance();
                String timeString = dateFormat.format(DateStartTime.getTimeInMillis());
                FromTimeButton.setText(timeString);
            }
        };
        Calendar calendar = Calendar.getInstance();
        int CurrentHour = calendar.get(Calendar.HOUR_OF_DAY);
        int CurrentMinute = calendar.get(Calendar.MINUTE);
        TimePickerDialog dialog = new TimePickerDialog(
                this, timeSetListener, CurrentHour, CurrentMinute, true);
        dialog.show();
    }

    public void ToDatePickButtonClicked(View view) {
        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                DateFinishTime.set(Calendar.YEAR, year);
                DateFinishTime.set(Calendar.MONTH, month);
                DateFinishTime.set(Calendar.DAY_OF_MONTH, day);
                DateFormat dateFormat = DateFormat.getDateInstance();
                String dateString = dateFormat.format(DateFinishTime.getTimeInMillis());
                ToDateButton.setText(dateString);
            }
        };
        Calendar calendar = Calendar.getInstance();
        int CurrentYear = calendar.get(Calendar.YEAR);
        int CurrentMonth = calendar.get(Calendar.MONTH);
        int CurrentDay = calendar.get(Calendar.DATE);
        DatePickerDialog dialog = new DatePickerDialog(
                this, dateSetListener, CurrentYear, CurrentMonth, CurrentDay );
        dialog.show();
    }

    public void ToTimePickButtonClicked(View view) {
        TimePickerDialog.OnTimeSetListener timeSetListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int hour, int minute) {
                DateFinishTime.set(Calendar.HOUR_OF_DAY, hour);
                DateFinishTime.set(Calendar.MINUTE, minute);
                DateFormat dateFormat = DateFormat.getTimeInstance();
                String timeString = dateFormat.format(DateFinishTime.getTimeInMillis());
                ToTimeButton.setText(timeString);
            }
        };
        Calendar calendar = Calendar.getInstance();
        int CurrentHour = calendar.get(Calendar.HOUR_OF_DAY);
        int CurrentMinute = calendar.get(Calendar.MINUTE);
        TimePickerDialog dialog = new TimePickerDialog(
                this, timeSetListener, CurrentHour, CurrentMinute, true);
        dialog.show();
    }

    public void ReserveRoomButtonClicked(View view) {
        long fromTime = DateStartTime.getTime().getTime();
        long toTime = DateFinishTime.getTime().getTime();
        long unixFrom = fromTime/1000;
        long unixTo = toTime/1000;
        int RoomId = TheRoom.getId();
        UserId = mAuth.getCurrentUser().getUid();
        String Purpose = ((EditText) findViewById(R.id.ReservationReasonEditText)).getText().toString();

        TextView MessageTextView = findViewById(R.id.MessageTextView);
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("fromTime", unixFrom);
            jsonObject.put("toTime", unixTo);
            jsonObject.put("userId", UserId);
            jsonObject.put("purpose", Purpose);
            jsonObject.put("roomId", RoomId);

            String jsonDocument = jsonObject.toString();
            Log.d("BENIS", jsonDocument);
            PostReservationOkHttpTask task = new PostReservationOkHttpTask();

            task.execute("http://anbo-roomreservationv3.azurewebsites.net/api/Reservations", jsonDocument);
        }
        catch (JSONException ex) {
            Log.d("BENIS",ex.getMessage());
            MessageTextView.setText(ex.getMessage());
        }
    }

    private class PostReservationOkHttpTask extends AsyncTask<String, Void, String> {
        protected String doInBackground(String... strings) {
            String conn = strings[0];
            String postdata = strings[1];
            MediaType MEDIA_TYPE = MediaType.parse("application/json");
            OkHttpClient client = new OkHttpClient();
            RequestBody body = RequestBody.create(postdata, MEDIA_TYPE);
            Request request = new Request.Builder()
                    .url(conn)
                    .post(body)
                    .header("Accept", "application/json")
                    .header("Content-Type","application/json")
                    .build();
            try {
                Response response = client.newCall(request).execute();
                if (response.isSuccessful()) {
                   return response.body().string();
                }
                else {
                    String message = conn + "\n" + response.code() + " " + response.message();
                    return message;
                }
            }
            catch (IOException ex) {
                Log.e("BENIS",ex.getMessage());
                return ex.getMessage();
            }
        }

        @Override
        protected void onPostExecute(String jsonString) {
            super.onPostExecute(jsonString);
            TextView MessageTextView = findViewById(R.id.MessageTextView);
            MessageTextView.setText(jsonString);
            Log.d("BENIS", jsonString);
            Toast.makeText(ReserveRoomActivity.this, "Reservation lavet!", Toast.LENGTH_LONG).show();
            finish();
        }

        @Override
        protected void onCancelled(String message) {
            super.onCancelled(message);
            TextView MessageTextView = findViewById(R.id.MessageTextView);
            MessageTextView.setText(message);
            Log.d("BENIS", message);
        }
    }
}
