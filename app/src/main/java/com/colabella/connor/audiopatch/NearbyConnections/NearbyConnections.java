package com.colabella.connor.audiopatch.NearbyConnections;

import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.view.ContextThemeWrapper;
import android.widget.Toast;

import com.colabella.connor.audiopatch.Controllers.SingletonController;
import com.colabella.connor.audiopatch.MainActivity;
import com.colabella.connor.audiopatch.R;
import com.google.android.gms.nearby.Nearby;
import com.google.android.gms.nearby.connection.AdvertisingOptions;
import com.google.android.gms.nearby.connection.ConnectionInfo;
import com.google.android.gms.nearby.connection.ConnectionLifecycleCallback;
import com.google.android.gms.nearby.connection.ConnectionResolution;
import com.google.android.gms.nearby.connection.ConnectionsStatusCodes;
import com.google.android.gms.nearby.connection.DiscoveredEndpointInfo;
import com.google.android.gms.nearby.connection.DiscoveryOptions;
import com.google.android.gms.nearby.connection.EndpointDiscoveryCallback;
import com.google.android.gms.nearby.connection.PayloadCallback;
import com.google.android.gms.nearby.connection.Strategy;

import static com.google.android.gms.nearby.connection.Strategy.P2P_STAR;

public class NearbyConnections {

    private final Strategy strategy = P2P_STAR;
    private boolean isDiscovering = false;

    public void startAdvertising() {
        MainActivity mainActivity = new MainActivity();
        Context context = mainActivity.getInstance();

        String nickname = "nickname"; //TODO get this value from somewhere else e.g. do not hardcode it
        String serviceId = mainActivity.getInstance().getResources().getString(R.string.package_name);
        ConnectionLifecycleCallback connectionLifeCycle = createConnectionLifeCycle();
        AdvertisingOptions advertisingOptions = new AdvertisingOptions.Builder().setStrategy(strategy).build();
        Nearby.getConnectionsClient(context)
                .startAdvertising(
                        nickname,
                        serviceId,
                        connectionLifeCycle,
                        advertisingOptions)
                .addOnSuccessListener(
                        (Void unused) -> {
                            // We're advertising!
                        })
                .addOnFailureListener(
                        (Exception e) -> {
                            // We were unable to start advertising.
                        });
    }

    public void stopAdvertising(Context context) {
        Nearby.getConnectionsClient(context).stopAdvertising();
    }

    public void startDiscovery() {
        MainActivity mainActivity = new MainActivity();
        Context context = mainActivity.getInstance();

        String serviceId = mainActivity.getInstance().getResources().getString(R.string.package_name);
        EndpointDiscoveryCallback endpointDiscoveryCallback = createEndpointDiscoveryCallback();

        DiscoveryOptions discoveryOptions = new DiscoveryOptions.Builder().setStrategy(strategy).build();
        Nearby.getConnectionsClient(context)
                .startDiscovery(
                        serviceId,
                        endpointDiscoveryCallback,
                        discoveryOptions)
                .addOnSuccessListener(
                        (Void unused) -> {
                            // we're discovering
                        })
                .addOnFailureListener(
                        (Exception e) -> {
                            // We're unable to start discovering.
                        });
    }

    public void stopDiscovery(Context context){
        Nearby.getConnectionsClient(context).stopDiscovery();
    }

    private EndpointDiscoveryCallback createEndpointDiscoveryCallback() {
        return new EndpointDiscoveryCallback() {
            @Override
            public void onEndpointFound(@NonNull String endpointId, @NonNull DiscoveredEndpointInfo discoveredEndpointInfo) {
                MainActivity mainActivity = new MainActivity();
                Context context = mainActivity.getInstance();
                String nickname = "nickname"; //TODO allow this to be set
                ConnectionLifecycleCallback connectionLifeCycle = createConnectionLifeCycle();

                Nearby.getConnectionsClient(context).requestConnection(
                        nickname,
                        endpointId,
                        connectionLifeCycle)
                        .addOnSuccessListener(
                                unusedResult -> {
                                    Toast.makeText(context, "Endpoint found! Requesting access...", Toast.LENGTH_SHORT).show();
                                    isDiscovering = true;
                                    // We successfully requested a connection. Now both sides
                                    // must accept before the connection is established.
                                })
                        .addOnFailureListener(
                                e -> {
                                    Toast.makeText(context, "No endpoints discovered.", Toast.LENGTH_SHORT).show();
                                    // Nearby Connections failed to request the connection.
                                });
            }

            @Override
            public void onEndpointLost(@NonNull String s) {

            }
        };
    }

    private ConnectionLifecycleCallback createConnectionLifeCycle() {
        return new ConnectionLifecycleCallback() {
            @Override
            public void onConnectionInitiated(@NonNull String endpointId, @NonNull final ConnectionInfo connectionInfo) {
                MainActivity mainActivity = new MainActivity();
                Context context = mainActivity.getInstance();

                new AlertDialog.Builder(new ContextThemeWrapper(context, R.style.NearbyConnectionsAlertDialog))
                        .setTitle("Accept connection to " + connectionInfo.getEndpointName())
                        .setMessage("Confirm the code " + connectionInfo.getAuthenticationToken() + " is also displayed on the other device")
                        .setPositiveButton("Accept", new DialogInterface.OnClickListener() {
                            // Accept connection
                            public void onClick(DialogInterface dialog, int which) {
                                stopDiscovery(context);

                                PayloadController payloadController = new PayloadController();
                                PayloadCallback payloadCallback = payloadController.createPayloadCallback();

                                Nearby.getConnectionsClient(context).acceptConnection(endpointId, payloadCallback);
                                // if (ConnectionsStatusCodes.STATUS_OK == 0) {
                                // }
                            }
                        })
                        // Reject connection
                        .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                Nearby.getConnectionsClient(context).rejectConnection(endpointId);
                                isDiscovering = false;
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
            }

            @Override
            public void onConnectionResult(@NonNull String endpointId, @NonNull ConnectionResolution result) {
                MainActivity mainActivity = new MainActivity();
                Context context = mainActivity.getInstance();

                switch (result.getStatus().getStatusCode()) {
                    case ConnectionsStatusCodes.STATUS_OK: {
                        // We're connected! Can now start sending and receiving data.
                        Toast.makeText(context, "Connection to " + endpointId + " was successful.", Toast.LENGTH_SHORT).show();
                        if(isDiscovering) {
                            SingletonController.getInstance().setGuest(true);
                        }
                        if(SingletonController.getInstance().getEndpointIdList() != null) {
                            if(!SingletonController.getInstance().getEndpointIdList().contains(endpointId)) {
                                SingletonController.getInstance().getEndpointIdList().add(endpointId);
                            }
                        }
                        break;
                    }
                    case ConnectionsStatusCodes.STATUS_CONNECTION_REJECTED: {
                        // The connection was rejected by one or both sides.
                        Toast.makeText(context, "Connection rejected.", Toast.LENGTH_SHORT).show();
                        isDiscovering = false;
                        break;
                    }
                    case ConnectionsStatusCodes.STATUS_ERROR: {
                        // The connection broke before it was able to be accepted.
                        Toast.makeText(context, "Connection error occurred.", Toast.LENGTH_SHORT).show();
                        isDiscovering = false;
                        break;
                    }
                }
            }

            @Override
            public void onDisconnected(@NonNull String endpointId) {
                // We've been disconnected from this endpoint. No more data can be
                // sent or received.

                MainActivity mainActivity = new MainActivity();
                Context context = mainActivity.getInstance();

                Toast.makeText(context, "Disconnected successfully.", Toast.LENGTH_SHORT).show();

                if (SingletonController.getInstance().getEndpointIdList() != null) {
                    for (int i = 0; SingletonController.getInstance().getEndpointIdList().size() > 0; i++) { // remove all endpoints
                        SingletonController.getInstance().getEndpointIdList().remove(i);
                    }
                }
                SingletonController.getInstance().setGuest(false);
                isDiscovering = false;
            }
        };
    }
}