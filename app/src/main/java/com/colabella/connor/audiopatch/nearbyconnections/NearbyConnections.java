package com.colabella.connor.audiopatch.nearbyconnections;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.view.ContextThemeWrapper;
import android.widget.Toast;

import com.colabella.connor.audiopatch.audio.Audio;
import com.colabella.connor.audiopatch.controllers.SingletonController;
import com.colabella.connor.audiopatch.MainActivity;
import com.colabella.connor.audiopatch.R;
import com.colabella.connor.audiopatch.fragments.GuestFragment;
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

        String nickname = SingletonController.getInstance().getUsername();
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
                String nickname = SingletonController.getInstance().getUsername();
                ConnectionLifecycleCallback connectionLifeCycle = createConnectionLifeCycle();

                Nearby.getConnectionsClient(context).requestConnection(
                        nickname,
                        endpointId,
                        connectionLifeCycle)
                        .addOnSuccessListener(
                                unusedResult -> {
                                    String endpointFound = mainActivity.getInstance().getResources().getString(R.string.endpoint_found);
                                    Toast.makeText(context, endpointFound, Toast.LENGTH_SHORT).show();
                                    isDiscovering = true;
                                    // We successfully requested a connection. Now both sides
                                    // must accept before the connection is established.
                                })
                        .addOnFailureListener(
                                e -> {
                                    String noEndpointsFound = mainActivity.getInstance().getResources().getString(R.string.no_endpoints_found);
                                    Toast.makeText(context,  noEndpointsFound , Toast.LENGTH_SHORT).show();
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
                        String connectionTo = mainActivity.getInstance().getResources().getString(R.string.connection_to);
                        String wasSuccessful = mainActivity.getInstance().getResources().getString(R.string.was_successful);
                        Toast.makeText(context, connectionTo + " " + endpointId + " " + wasSuccessful, Toast.LENGTH_SHORT).show();
                        if(isDiscovering) {
                            SingletonController.getInstance().setGuest(true);
                            GuestFragment guestFragment = new GuestFragment();
                            mainActivity.getInstance().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                                    guestFragment, "GuestFragment").addToBackStack("open_guest").commit();

                            SingletonController.getInstance().getActivePlaylistAdapter().removeAllItems();
                        }
                        else { // host is sending data to guest who just connected
                            if(SingletonController.getInstance().getActivePlaylistAdapter().getItemCount() > 0) {
                                Audio selectedItem = SingletonController.getInstance().getActivePlaylistAdapter().getSelectedAudio();
                                if(selectedItem != null) {
                                    Bitmap albumCover = selectedItem.getAlbumArt();

                                    PayloadController payloadController = new PayloadController();
                                    payloadController.sendBytes(
                                            endpointId,
                                            "filename" + "|" + selectedItem.getTitle() + "|" + selectedItem.getArtist() + "|" + selectedItem.getDuration() + "|" + selectedItem.getSubmitter(),
                                            mainActivity.getInstance());
                                    payloadController.sendImage(endpointId, albumCover, mainActivity.getInstance());
                                }
                            }
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
                        String connectionRejected = mainActivity.getInstance().getResources().getString(R.string.connection_rejected);
                        Toast.makeText(context, connectionRejected, Toast.LENGTH_SHORT).show();
                        isDiscovering = false;
                        break;
                    }
                    case ConnectionsStatusCodes.STATUS_ERROR: {
                        // The connection broke before it was able to be accepted.
                        String connectionErrorOccurred = mainActivity.getInstance().getResources().getString(R.string.connection_error_occurred);
                        Toast.makeText(context, connectionErrorOccurred, Toast.LENGTH_SHORT).show();
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

                if (SingletonController.getInstance().getEndpointIdList() != null) {
                    for (int i = 0; SingletonController.getInstance().getEndpointIdList().size() > 0; i++) {
                        if(SingletonController.getInstance().getEndpointIdList().get(i).equals(endpointId)) {
                            SingletonController.getInstance().getEndpointIdList().remove(i); // remove disconnected endpoint
                        }
                    }
                }
                isDiscovering = false;

                // Handles resetting the home menu to normal state
                if(SingletonController.getInstance().isGuest()) {
                    String disconnectionSuccessful = mainActivity.getInstance().getResources().getString(R.string.disconnection_successful);
                    Toast.makeText(context, disconnectionSuccessful, Toast.LENGTH_SHORT).show();

                    SingletonController.getInstance().setGuest(false);
                    String selectedDrawerItem = SingletonController.getInstance().getMainDrawerAdapter().getSelectedItemName();
                    if (selectedDrawerItem != null) {
                        String home = mainActivity.getInstance().getString(R.string.home);
                        if (selectedDrawerItem.equals(home)) {
                            mainActivity.getInstance().getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                        }
                    }
                }
                PayloadController payloadController = new PayloadController();
                payloadController.deleteTempFiles(); // delete temp data left behind by application
            }
        };
    }
}