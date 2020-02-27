package com.colabella.connor.audiopatch.nearbyconnections;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.widget.Toast;
import com.colabella.connor.audiopatch.audio.Audio;
import com.colabella.connor.audiopatch.controllers.AudioController;
import com.colabella.connor.audiopatch.controllers.SingletonController;
import com.colabella.connor.audiopatch.MainActivity;
import com.colabella.connor.audiopatch.fragments.GuestFragment;
import com.google.android.gms.nearby.Nearby;
import com.google.android.gms.nearby.connection.Payload;
import com.google.android.gms.nearby.connection.PayloadCallback;
import com.google.android.gms.nearby.connection.PayloadTransferUpdate;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import static java.nio.charset.StandardCharsets.UTF_8;

public class PayloadController extends NearbyConnections {

    void sendImage(String endpointId, Bitmap albumCover, Context context) {
        if (albumCover != null) {
            File albumCoverFile = new File(context.getCacheDir(), "file");         // create a file to write bitmap data
            try {
                albumCoverFile.createNewFile();
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

                albumCover.compress(Bitmap.CompressFormat.PNG, 0 /*ignored for PNG*/, byteArrayOutputStream);
                byte[] bitmapData = byteArrayOutputStream.toByteArray();

                FileOutputStream fileOutputStream;
                fileOutputStream = new FileOutputStream(albumCoverFile);
                fileOutputStream.write(bitmapData);
                fileOutputStream.flush();
                fileOutputStream.close();

                if (albumCoverFile.exists()) {
                    Payload imagePayload = Payload.fromFile(albumCoverFile);
                    Nearby.getConnectionsClient(context).sendPayload(endpointId, imagePayload); // send the payload
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    void sendBytes(String endpointId, String string, Context context) {
        byte[] bytes = string.getBytes(UTF_8); // convert string to bytes
        Payload bytesPayload = Payload.fromBytes(bytes); // convert bytes to payload object
        Nearby.getConnectionsClient(context).sendPayload(endpointId, bytesPayload); // send the payload
    }

    public void sendAudio(String endpointId, Audio audio, Context context) {
        Payload filePayload = null;
        Uri outgoingUri = Uri.parse(audio.getData()); // convert audio data to uri
        File file = new File(String.valueOf(outgoingUri)); // create file object from uri
        try {
            filePayload = Payload.fromFile(file); // create payload object from file object
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        if (filePayload != null) {
            // if(filePayload != null) { // null check to make certain we have data to send
            String payloadFilename = "filename" + "|" + audio.getTitle() + "|" + audio.getArtist() + "|" + audio.getDuration() + "|" + audio.getSubmitter();
            //String payloadFilename = "filename" + "|" + audio.getTitle();
            sendBytes(endpointId, payloadFilename, context); // send the payload; the goal is to attach it to the sent audio file and rebuild the object on the receiving device

            Nearby.getConnectionsClient(context).sendPayload(endpointId, filePayload); // send the payload
            //}
        }
    }

    // sends update data to connected guest devices
    public void sendUpdate() {
        int endpointListSize = SingletonController.getInstance().getEndpointIdList().size();
        if(endpointListSize > 0) {
            if (!SingletonController.getInstance().isGuest()) {
                Audio selectedItem = SingletonController.getInstance().getActivePlaylistAdapter().getSelectedAudio();
                if(selectedItem != null) {
                    Bitmap albumCover = selectedItem.getAlbumArt();
                    PayloadController payloadController = new PayloadController();
                    MainActivity mainActivity = new MainActivity();
                    String token;
                    if(selectedItem.getAlbumArt() != null) {
                        token = "filename";
                    }
                    else {
                        token = "albumcovernull";
                    }
                    for (int i = 0; i < endpointListSize; i++) {
                        String endpointId = SingletonController.getInstance().getEndpointIdList().get(i);
                        payloadController.sendBytes(
                                endpointId,
                                token
                                        + "|" + selectedItem.getTitle()
                                        + "|" + selectedItem.getArtist()
                                        + "|" + selectedItem.getDuration()
                                        + "|" + selectedItem.getSubmitter(),
                                mainActivity.getInstance());
                        payloadController.sendImage(endpointId, albumCover, mainActivity.getInstance());
                    }
                }
            }
        }
    }

    PayloadCallback createPayloadCallback() {
        ArrayList<Payload> incomingPayloads = new ArrayList<>(); // format is [endpointId|audio.getTitle()]
        ArrayList<String[]> filenames = new ArrayList<>(); // format is [endpointId|audio.getTitle()]

        return new PayloadCallback() {
            @Override
            public void onPayloadReceived(@NonNull String endpointId, @NonNull Payload payload) {
                if (payload.getType() == Payload.Type.BYTES) {
                    byte[] receivedBytes = payload.asBytes();
                    String input = new String(receivedBytes, StandardCharsets.UTF_8);
                    String[] inputSplit = input.split("\\|");

                    if (inputSplit[0].equals("filename")) {
                        MainActivity mainActivity = new MainActivity();
                        Context context = mainActivity.getInstance();
                        Toast.makeText(context, inputSplit[1], Toast.LENGTH_SHORT).show();

                        String filename = inputSplit[1]; // get filename from the array ; format is [filename|audio.getTitle()]
                        String artist = inputSplit[2];
                        String duration = inputSplit[3];
                        String submitter = inputSplit[4];
                        String[] filenameInput = {endpointId, filename, artist, duration, submitter}; // create a new array and associate it with the given endpoint
                        if (!filenames.contains(filenameInput)) { // check to see if the item already exists in the array
                            filenames.add(filenameInput); // store the item
                        }
                    }
                    else if (inputSplit[0].equals("albumcovernull")) {
                        String filename = inputSplit[1]; // get filename from the array ; format is [filename|audio.getTitle()]
                        String artist = inputSplit[2];
                        String duration = inputSplit[3];
                        String submitter = inputSplit[4];
                        GuestFragment guestFragment = new GuestFragment();
                        guestFragment.updateGuestData(null, filename, artist, duration, submitter);
                    }
                } else if (payload.getType() == Payload.Type.FILE) {
                    incomingPayloads.add(payload); // store the payload so it can be referenced in onPayloadTransferUpdate()
                }
            }

            @Override
            public void onPayloadTransferUpdate(@NonNull String payloadId, @NonNull PayloadTransferUpdate update) {
                if (update.getStatus() == PayloadTransferUpdate.Status.SUCCESS) {

                    if (incomingPayloads.size() > 0) {
                        String filename = "";
                        String artist = "";
                        String duration = "";
                        String submitter = "";
                        if(filenames.size() > 0) {
                            String[] audioData = filenames.remove(0);
                            filename = audioData[1];
                            artist = audioData[2];
                            duration = audioData[3];
                            submitter = audioData[4];
                        }

                        // If user is the host
                        if (!SingletonController.getInstance().isGuest()) {
                            Payload payload = incomingPayloads.remove(0);
                            File payloadFile = payload.asFile().asJavaFile();
                            boolean success;
                            if (payloadFile != null) {
                                success = payloadFile.renameTo(new File(Environment.getExternalStorageDirectory().toString() + File.separator + "AudioPatch", filename + ".mp3"));
                                Uri uri;
                                String data;
                                Bitmap bitmap;
                                if (success) {
                                    AudioController audioController = new AudioController();
                                    uri = Uri.parse(Environment.getExternalStorageDirectory().toString() + File.separator + "AudioPatch" + File.separator + filename + ".mp3");
                                    data = uri.toString();
                                    bitmap = audioController.getAlbumCover(data);
                                } else {
                                    AudioController audioController = new AudioController();
                                    uri = Uri.parse(payloadFile.getAbsolutePath());
                                    data = uri.toString();
                                    bitmap = audioController.getAlbumCover(data);
                                }
                                Audio audio = new Audio(data, filename, bitmap, artist, duration, submitter); // Audio object rebuilt and can be used at leisure
                                SingletonController.getInstance().getActivePlaylistAdapter().addItem(audio); // Add the item to the playlist
                                SingletonController.getInstance().getActivePlaylistAdapter().notifyDataSetChanged();
                            }
                        }
                        else { // user is the guest
                            Payload payload = incomingPayloads.remove(0);
                            File payloadFile = payload.asFile().asJavaFile();

                            if (payloadFile != null) {
                                int size = (int) payloadFile.length();

                                byte[] bytes = new byte[size];
                                try {
                                    BufferedInputStream buf = new BufferedInputStream(new FileInputStream(payloadFile));
                                    buf.read(bytes, 0, bytes.length);
                                    buf.close();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                Bitmap albumArt = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                                GuestFragment guestFragment = new GuestFragment();
                                guestFragment.updateGuestData(albumArt, filename, artist, duration, submitter);
                            }
                        }
                    }
                }
            }
        };
    }
}