package geoLocalization;

import android.app.IntentService;
import android.content.Intent;
import android.location.Location;
import android.util.Log;

import com.example.com.db.DbManager;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;

import java.sql.Connection;
import java.sql.SQLException;

public class ListenForLocationRequests extends IntentService {

    /**
     * server UDP port for nearby service
     */
    private final int PORT = 6000;
    private final String SERVER_IP = /*"95.236.93.207"*/"192.168.1.7";
    private final int MINUTE = 1000*60;

    private FusedLocationProviderClient locationClient;
    private LocationCallback locationCallback;
    private LocationRequest lr;
    private LocationResultReceiver resultReceiver;

    /**
     * output data of the process.
     */
    private UserData userData;

    public ListenForLocationRequests() {

        super(null);

        Log.d("ListenForLocationReq", "service created");

        /*locationClient = LocationServices.getFusedLocationProviderClient(this);
        resultReceiver = new LocationResultReceiver(this, new Handler());
        // define the callback
        locationCallback = new LocationCallback() {

            @Override
            public void onLocationResult(LocationResult locationResult) {

                if(locationResult == null)
                    return;

                Location loc = locationResult.getLocations().get(0);

                Log.d("ListenForLocationReq", "got location: " + loc);

                // start reverse geocoding service (auto-closes)
                startRevGeocodingIntentService(loc);
            }
        };*/
    }

    public ListenForLocationRequests(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        Log.d("ListenForLocationReq", "service started");

        getRealTimeLocation();

        // connect to the webservice and store the location on the DB


        // send the data to the server
        //Log.d("ListenForLocationReq", "SERVICE OUTPUT: " + userData.toString());

        /*DatagramSocket dsock = null;

        try {
            dsock = new DatagramSocket(PORT);
        } catch (SocketException e) {
            e.printStackTrace();
        }

        byte[] recBuf = new byte[1024];

        DatagramPacket dgram = new DatagramPacket(recBuf, recBuf.length);

        boolean stop = false;

        while(!stop) {

            Log.d("ListenForLocationReq", "waiting for dgrams...");

            // === receive command ===
            try {
                // blocking call
                dsock.receive(dgram);

            } catch (IOException e) {
                e.printStackTrace();
            }

            String recString = new String(Arrays.copyOfRange(recBuf, 0, dgram.getLength()));

            Log.d("ListenForLocationReq", "received command: " + recString);

            SocketAddress clientAddress = dgram.getSocketAddress();

            Log.d("ListenForLocationReq", "client address: " + clientAddress);

            // === get <address, GPS coordinates> and send them back to the server ===
            getRealTimeLocation();

            Log.d("locationSender", "final data: " + userData);

            byte[] buffer = userData.toString().getBytes();

            dgram = new DatagramPacket(buffer, buffer.length, dgram.getAddress(), PORT);

            try {
                dsock.send(dgram);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }*/


    }

    protected void startRevGeocodingIntentService(Location location) {
        Intent intent = new Intent(this, FetchAddressIntentService.class);
        intent.putExtra(Constants.RECEIVER, resultReceiver);
        intent.putExtra(Constants.LOCATION_DATA, location);
        startService(intent);
    }

    public void getRealTimeLocation() {

        // builder pattern
        lr = LocationRequest.create().
                setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY).
                setInterval(MINUTE).
                setFastestInterval(10 * MINUTE);

        try {
            if(locationClient == null)
                Log.d("ListenForLocationReq", "OCIO DE PT.2");
            locationClient.requestLocationUpdates(this.lr, this.locationCallback, null);
        }
        catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    /**
     * gets user data back from the ResultReceiver
     */
    protected void fillUserData(UserData ud) {

        userData = ud;
    }
}