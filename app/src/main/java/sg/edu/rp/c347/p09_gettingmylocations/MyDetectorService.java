package sg.edu.rp.c347.p09_gettingmylocations;

import android.Manifest;
import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.core.content.PermissionChecker;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import java.io.File;
import java.io.FileWriter;

public class MyDetectorService extends Service {

    boolean started;
    private FusedLocationProviderClient cilent;
    private LocationCallback mLocationCallBack;

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        Log.d("Service", "Service created");
        super.onCreate();

        cilent = LocationServices.getFusedLocationProviderClient(this);

        createLocationCallBack();

        String folderLocation = Environment.getExternalStorageDirectory().getAbsolutePath() + "/P09";

        File folder = new File(folderLocation);
        if (folder.exists() == false) {
            boolean result = folder.mkdir();
            if (result == false) {
                Toast.makeText(MyDetectorService.this, "Folder can't be created in External memory, " + " Service exiting", Toast.LENGTH_SHORT).show();
                stopSelf();
            }
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("Service", "onStart");
        if (checkPermission() == true) {
            LocationRequest mLocationRequest = new LocationRequest();
            mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            mLocationRequest.setInterval(10000);
            mLocationRequest.setFastestInterval(50000);
            mLocationRequest.setSmallestDisplacement(100);

            cilent.requestLocationUpdates(mLocationRequest, mLocationCallBack, null);
        } else {
            // Permission not granted... quiting
            stopSelf();
        }
        return Service.START_STICKY;
    }

    private void createLocationCallBack() {
        mLocationCallBack = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult != null) {
                    Location locData = locationResult.getLastLocation();

                    String data = locData.getLatitude() + "," + locData.getLongitude();
                    Log.d("Service - Loc changed", data);

                    String folderLocation = Environment.getExternalStorageDirectory().getAbsolutePath() + "/P09";

                    File targetFile = new File(folderLocation, "data.txt");

                    try {
                        FileWriter writer = new FileWriter(targetFile, true);
                        writer.write(data + "\n");
                        writer.flush();
                        writer.close();

                    } catch (Exception e) {
                        Toast.makeText(MyDetectorService.this, "Failed to Write!",
                                Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
                }
            };
        };
    }

    private boolean checkPermission(){
        int permissionCheck_Coarse = ContextCompat.checkSelfPermission(
                MyDetectorService.this, Manifest.permission.ACCESS_COARSE_LOCATION);
        int permissionCheck_Fine = ContextCompat.checkSelfPermission(
                MyDetectorService.this, Manifest.permission.ACCESS_FINE_LOCATION);
        int permissionCheck_Storage = ContextCompat.checkSelfPermission(
                MyDetectorService.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);


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
