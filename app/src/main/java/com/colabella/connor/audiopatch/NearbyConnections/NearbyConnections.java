/*package com.colabella.connor.audiopatch.NearbyConnections;

import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.util.SimpleArrayMap;
import android.widget.Toast;
import com.google.android.gms.nearby.Nearby;
import com.google.android.gms.nearby.connection.AdvertisingOptions;
import com.google.android.gms.nearby.connection.ConnectionInfo;
import com.google.android.gms.nearby.connection.ConnectionLifecycleCallback;
import com.google.android.gms.nearby.connection.ConnectionResolution;
import com.google.android.gms.nearby.connection.ConnectionsClient;
import com.google.android.gms.nearby.connection.ConnectionsStatusCodes;
import com.google.android.gms.nearby.connection.DiscoveredEndpointInfo;
import com.google.android.gms.nearby.connection.DiscoveryOptions;
import com.google.android.gms.nearby.connection.EndpointDiscoveryCallback;
import com.google.android.gms.nearby.connection.Payload;
import com.google.android.gms.nearby.connection.PayloadCallback;
import com.google.android.gms.nearby.connection.PayloadTransferUpdate;
import com.google.android.gms.nearby.connection.Strategy;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.colabella.connor.audiopatch.Audio.Audio;
import com.colabella.connor.audiopatch.MainActivity;
import java.io.File;
import static com.google.android.gms.nearby.connection.Strategy.P2P_STAR;
import static java.lang.String.valueOf;
import static java.nio.charset.StandardCharsets.UTF_8;

public class NearbyConnections /*extends Activity*/ /*{

    public static final Strategy strategy = P2P_STAR;
    private static boolean isConnected = false;
    private Context context;
    private String nickname;
    private final SimpleArrayMap<Long, Payload> incomingPayloads = new SimpleArrayMap<>();
    private final SimpleArrayMap<Long, String> filePayloadFilenames = new SimpleArrayMap<>();
    private final SimpleArrayMap<Long, String> filePayloadNicknames = new SimpleArrayMap<>();
    private long key;
    private boolean isReceiver = false;
    private DownloadManager mgr = null;

    NearbyConnections(){ }

    public NearbyConnections(Context context, String nickname){
        this.context = context;
        this.nickname = nickname;
    }

    public String getUserNickname(){
        return nickname;
    }

    public void setUserNickname(String userNickname){
        nickname = userNickname;
    }

    public void startAdvertising(Context c){
        Nearby.getConnectionsClient(c)
                .startAdvertising(
                         nickname, // endpointName
                         "com.redowul.audiopatch",  // serviceId
                        myConnectionLifeCycle,
                        //new AdvertisingOptions.Builder().build());
                        //new AdvertisingOptions(s));
                        new AdvertisingOptions(strategy));

    }

    public void stopAdvertising() {
        Nearby.getConnectionsClient(context).stopAdvertising();
    }

    //private ConnectionsClient connectionsClient = Nearby.getConnectionsClient(context);
    private ConnectionsClient connectionsClient;

    public void sendBytes(String endpointId, Payload payload) {
        if (payload.getType() == Payload.Type.BYTES) {
            // No need to track progress for bytes.
            return;
        }
        connectionsClient = Nearby.getConnectionsClient(c);
        connectionsClient.sendPayload(endpointId, payload);
    }

    private final PayloadCallback mPayloadCallback = new PayloadCallback() {

        @Override
        public void onPayloadReceived(String endpointId, Payload payload) {
            isReceiver = true;
            incomingPayloads.put(payload.getId(), payload);
            key = payload.getId();

            if(incomingPayloads.size() > 0){
                Toast.makeText(getContext(), "Please work + " + valueOf(payload.getId() + " (1)"), Toast.LENGTH_SHORT).show();
            }

            if (payload.getType() == Payload.Type.BYTES) {
                // No need to track progress for bytes.
                String string = new String(payload.asBytes(), UTF_8);
                String payloadFilenameMessage = new String(payload.asBytes(), UTF_8);

                int colonIndex = payloadFilenameMessage.indexOf(":");
                // The id is going to be from the start of the string to the colon. After that would be the title i.e. "12345:audioTitle" the id in this case is the numbers.
                long payloadId = Long.parseLong(payloadFilenameMessage.substring(0, colonIndex));
                String filename = payloadFilenameMessage.substring(colonIndex + 1, payloadFilenameMessage.lastIndexOf(":"));
                String nickname = payloadFilenameMessage.substring(payloadFilenameMessage.lastIndexOf(":") + 1);
                //String filename = payloadFilenameMessage.substring(colonIndex + 1);
                filePayloadFilenames.put(payloadId, filename);
                filePayloadNicknames.put(payloadId, nickname);

                //isReceiver = true;

                // Using a string.equals() function, Payloads of type Bytes can be used to push updates
                // to connecting devices, making the host device into a sort of rudimentary "server."
                if(string.equals("EXAMPLE PAYLOAD!")){
                    Toast.makeText(getContext(), "Function 2 works." + string, Toast.LENGTH_SHORT).show();
                }
                //return;
            }
            else if (payload.getType() == Payload.Type.FILE) {
                // Add this to our tracking map, so that we can retrieve the payload later.
                // incomingPayloads.put(valueOf(payload.getId()), payload);
                incomingPayloads.put(payload.getId(), payload);
            }
        }

        //String payloadId changed to String
        @Override
        public void onPayloadTransferUpdate(String payloadId, PayloadTransferUpdate update) {
            if (update.getStatus() == PayloadTransferUpdate.Status.SUCCESS) {
                // Ensures that only the device on the receiving end of the transfer is effected by the update.
                if(isReceiver){
                    Payload payload = incomingPayloads.remove(key);
                    Toast.makeText(getContext(), "payload reads: " + payload.asBytes() + "; key is " + key, Toast.LENGTH_SHORT).show();
                    if (payload.getType() == Payload.Type.FILE) {
                        // Retrieve the filename that was received in a bytes payload.
                        //TODO The files save as mp3 based on whether the extension is part of the string or not. So, don't remove that from the name until AFTER sending. Another use for isGuest.

                        String newFilename2 = filePayloadFilenames.remove(key);
                        String newFilename = newFilename2 + ".mp3";

                        //This was null
                        File payloadFile = payload.asFile().asJavaFile();

                        payloadFile.renameTo(new File(Environment.getExternalStorageDirectory().toString() + File.separator + "AudioPatch", newFilename));

                        Uri uri = Uri.parse(Environment.getExternalStorageDirectory().toString() + File.separator + "AudioPatch" + File.separator + newFilename);

                        //TODO add global Mainactivity variable
                        MainActivity m = new MainActivity();

                        //TEMPORARILY COMMENTING THIS
                        //TODO After fixing the titles of the songs, make sure they can be passed via the addAudio() method
                        AudioData a = new AudioData();

                        String senderNickname = filePayloadNicknames.remove(key);

                        Audio audio = m.createAudio(uri, newFilename2, a.getFileData(uri, context, 2),
                                a.getFileData(uri, context, 3), senderNickname);
                        m.playSong2.createRecyclerView(audio);

                        Toast.makeText(getContext(), "Filename is " + newFilename + ", parent directory is " +
                                Environment.getExternalStorageDirectory().toString() + File.separator + "AudioPatch", Toast.LENGTH_SHORT).show();
                    }
                    isReceiver = false;
                }
            }
        }
    };

    BroadcastReceiver onComplete = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            // findViewById(R.id.start).setEnabled(true);
            Toast.makeText(getContext(), "Download complete!", Toast.LENGTH_SHORT).show();
        }
    };

    private ConnectionInfo connectionInfo;

    private ConnectionInfo getConnectionInfo(){
        return connectionInfo;
    }

    private void setConnectionInfo(ConnectionInfo c){
        connectionInfo = c;
    }

    String regendpointId;

    public String getEndpointId(){
        return regendpointId;
    }

    boolean isGuest = false;

    public boolean getIsGuest(){
        return isGuest;
    }

    ConnectionLifecycleCallback myConnectionLifeCycle = new ConnectionLifecycleCallback() {
        @Override
        public void onConnectionInitiated(@NonNull String endpointId, @NonNull final ConnectionInfo connectionInfo) {

            regendpointId = endpointId;

            new AlertDialog.Builder(context)
                    .setTitle("Accept connection to " + connectionInfo.getEndpointName())
                    .setMessage("Confirm the code " + connectionInfo.getAuthenticationToken() + " is also displayed on the other device")
                    .setPositiveButton("Accept", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // The user confirmed, so we can accept the connection.

                            // String endpointId = connectionInfo.getEndpointName();
                            String endpointId = regendpointId;
                            //Context c = context;

                            Nearby.getConnectionsClient(context).acceptConnection(endpointId, mPayloadCallback);
                            if(ConnectionsStatusCodes.STATUS_OK == 0) {
                                //Toast.makeText(context, "Connecting please?", Toast.LENGTH_SHORT).show();
                                setConnectionInfo(connectionInfo);
                            }
                        }
                    })
                    .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {

                            String endpointId = regendpointId;

                            Context c = context;
                            Nearby.getConnectionsClient(c).rejectConnection(endpointId);
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        }

        @Override
        public void onConnectionResult(String endpointId, ConnectionResolution result) {
            switch (result.getStatus().getStatusCode()) {
                case ConnectionsStatusCodes.STATUS_OK:
                    // We're connected! Can now start sending and receiving data.
                    Toast.makeText(context, "Connected on both ends!", Toast.LENGTH_SHORT).show();
                    setIsConnected(true);
                    //TextView tv = findViewById(R.id.connectionStatus);
                    //tv.setText("Connected");

                    // only fired by device which doesn't find "getUserNickname" on the other end. (Meaning the ones searching for the host)
                    //TODO When users input their own usernames, a new String field will need to be applied to define them as guests, rather than as the host.
                    //(This is because getUserNickname() could be any number of results and would thus only match ONE user. If every guest is denoted as the same thing then
                    // it'll fix that problem.)
                    //if(!getConnectionInfo().getEndpointName().equals(getUserNickname())){
                    MainActivity m = new MainActivity();
                    if(m.checkIsAdvertising() == false){

                        Toast.makeText(context, "TEST 123", Toast.LENGTH_SHORT).show();
                        stopDiscovery();
                        m.setIsDiscovering(false);
                        // Sets the user to a guest, which enables guest functionality.
                        // MainActivity m = new MainActivity();
                        isGuest = true;
                        //Toast.makeText(context, "isGuest is " + m.getIsGuest(), Toast.LENGTH_SHORT).show();
                        // MainActivity m = new MainActivity();
                        //View v = findViewById(R.id.button6);
                        // m.payloadVisible(v);
                    }
                    else{

                    }

                    break;
                case ConnectionsStatusCodes.STATUS_CONNECTION_REJECTED:
                    // The connection was rejected by one or both sides.

                    Toast.makeText(context, "Connection rejected.", Toast.LENGTH_SHORT).show();

                    break;
                case ConnectionsStatusCodes.STATUS_ERROR:
                    // The connection broke before it was able to be accepted.

                    Toast.makeText(context, "Connection error occurred.", Toast.LENGTH_SHORT).show();

                    break;
            }
        }

        @Override
        public void onDisconnected(String endpointId) {
            // We've been disconnected from this endpoint. No more data can be
            // sent or received.
            isGuest = false;
            setIsConnected(false);
        }
    };*/
/*
    public void startDiscovery(Context c){
        Nearby.getConnectionsClient(c).startDiscovery(
                "com.redowul.audiopatch",  // serviceId=

                myEndpointDiscoveryCallback,
                //new DiscoveryOptions.Builder().build());
                new DiscoveryOptions(strategy));
        // DiscoveryOptions.setStrategy();
    }

    public void stopDiscovery(){
        Nearby.getConnectionsClient(context).stopDiscovery();
    }

    // Context c = MainActivity.getContext();

    EndpointDiscoveryCallback myEndpointDiscoveryCallback = new EndpointDiscoveryCallback() {
        @Override
        public void onEndpointFound(@NonNull String endpointId, @NonNull DiscoveredEndpointInfo discoveredEndpointInfo) {
            Context c = getContext();

            Nearby.getConnectionsClient(c).requestConnection(getUserNickname(), endpointId, myConnectionLifeCycle)
                    .addOnSuccessListener(
                            new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unusedResult) {
                                    Context c = getContext();
                                    Toast.makeText(c, "Endpoint found! Requesting access...", Toast.LENGTH_SHORT).show();
                                    // We successfully requested a connection. Now both sides
                                    // must accept before the connection is established.
                                }
                            })
                    .addOnFailureListener(
                            new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Context c = getContext();
                                    Toast.makeText(c, "No endpoints discovered.", Toast.LENGTH_SHORT).show();
                                    // Nearby Connections failed to request the connection.
                                }
                            });
        }

        @Override
        public void onEndpointLost(@NonNull String s) {

        }
    };

    public void setIsConnected(boolean b){
        isConnected = b;
    }

    public boolean getIsConnected(){
        return isConnected;
    }
}
*/