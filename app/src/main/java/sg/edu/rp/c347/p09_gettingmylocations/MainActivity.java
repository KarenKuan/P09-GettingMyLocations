package sg.edu.rp.c347.p09_gettingmylocations;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.PermissionChecker;

import android.Manifest;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;


public class MainActivity extends AppCompatActivity {

    TextView tvLat, tvLng;
    Button btnStart, btnStop, btnCheck;
    FusedLocationProviderClient cilent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // TextView
        tvLat = (TextView) findViewById(R.id.tvLat);
        tvLng = (TextView) findViewById(R.id.tvLng);

        // Button
        btnStart = (Button) findViewById(R.id.btnStart);
        btnStop = (Button) findViewById(R.id.btnStop);
        btnCheck = (Button) findViewById(R.id.btnCheck);

        cilent = LocationServices.getFusedLocationProviderClient(this);

        if (checkPermission() == true) {
            Task<Location> task = cilent.getLastLocation();
            task.addOnSuccessListener(MainActivity.this, new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    // Check last known location. Can be bull.
                    if (location != null) {
                        tvLat.setText("Latitude : " + String.valueOf(location.getLatitude()));
                        tvLng.setText("Longitude : " + String.valueOf(location.getLongitude()));
                    } else {
                        String msg = "No Last Known Location Found";
                        Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } else {
            // Permission not granted
            // Request for permission
        }

        btnCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, RecordsActivity.class);
                startActivity(i);

                String folderLocation = Environment.getExternalStorageDirectory().getAbsolutePath() + "/P09";

                File targetFile = new File(folderLocation, "data.txt");

                if (targetFile.exists()) {
                    String data = "";
                    try {
                        FileReader reader = new FileReader(targetFile);
                        BufferedReader br = new BufferedReader(reader);
                        String line = br.readLine();
                        while (line != null) {
                            data += line + "\n";
                            line = br.readLine();
                        }
                        br.close();
                        reader.close();
                    } catch (Exception e) {
                        Toast.makeText(MainActivity.this, "Failed to Read!",
                                Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
                    Toast.makeText(MainActivity.this, data, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, "Record file doesn't exits", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

        private boolean checkPermission(){
            int permissionCheck_Coarse = ContextCompat.checkSelfPermission(
                    MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION);
            int permissionCheck_Fine = ContextCompat.checkSelfPermission(
                    MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION);
            int permissionCheck_Storage = ContextCompat.checkSelfPermission(
                    MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);


            if (permissionCheck_Coarse == PermissionChecker.PERMISSION_GRANTED
                    && permissionCheck_Fine == PermissionChecker.PERMISSION_GRANTED
                    && permissionCheck_Storage == PermissionChecker.PERMISSION_GRANTED)
            {
                return true;
            } else {
                return false;
            }
        }
    }
