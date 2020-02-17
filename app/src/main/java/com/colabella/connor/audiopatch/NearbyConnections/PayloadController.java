package com.colabella.connor.audiopatch.NearbyConnections;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.widget.Toast;

import com.colabella.connor.audiopatch.Audio.Audio;
import com.colabella.connor.audiopatch.Controllers.AudioController;
import com.colabella.connor.audiopatch.Controllers.SingletonController;
import com.colabella.connor.audiopatch.MainActivity;
import com.google.android.gms.nearby.Nearby;
import com.google.android.gms.nearby.connection.Payload;
import com.google.android.gms.nearby.connection.PayloadCallback;
import com.google.android.gms.nearby.connection.PayloadTransferUpdate;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;

import static java.nio.charset.StandardCharsets.UTF_8;

public class PayloadController extends NearbyConnections {

    private void sendBytes(String endpointId, String string, Context context) {
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

                    MainActivity mainActivity = new MainActivity();
                    Context context = mainActivity.getInstance();
                    Toast.makeText(context, inputSplit[1], Toast.LENGTH_SHORT).show();

                    if (inputSplit[0].equals("filename")) {
                        String filename = inputSplit[1]; // get filename from the array ; format is [filename|audio.getTitle()]
                        String artist = inputSplit[2];
                        String duration = inputSplit[3];
                        String submitter = inputSplit[4];
                        String[] filenameInput = {endpointId, filename, artist, duration, submitter}; // create a new array and associate it with the given endpoint
                        if (!filenames.contains(filenameInput)) { // check to see if the item already exists in the array
                            filenames.add(filenameInput); // store the item
                        }
                    }
                } else if (payload.getType() == Payload.Type.FILE) {
                    //incomingPayloads.put(Long.toString(payload.getId()), payload); // store the payload so it can be referenced in onPayloadTransferUpdate()
                    incomingPayloads.add(payload); // store the payload so it can be referenced in onPayloadTransferUpdate()
                   /* System.out.println(payload.getId());
                    for (int i = 0; i < filenames.size(); i++) {
                        if (filenames.get(i)[0].equals(endpointId)) { // after updating the array, payload id will be used for linking the two items, not the endpointId
                            String[] item = new String[]{
                                    Long.toString(payload.getId()), // payloadId
                                    filenames.get(i)[1] // title of file
                            };
                            filenames.set(i, item); // add the item to the queue
                            break;
                        }
                    }*/
                }
            }

            @Override
            public void onPayloadTransferUpdate(@NonNull String payloadId, @NonNull PayloadTransferUpdate update) {
                if (update.getStatus() == PayloadTransferUpdate.Status.SUCCESS) {

                    if (incomingPayloads.size() > 0) {
                        System.out.println(payloadId);
                        Payload payload = incomingPayloads.remove(0);
                        File payloadFile = payload.asFile().asJavaFile();
                        String[] audioData = filenames.remove(0);
                        String filename = audioData[1];
                        String artist = audioData[2];
                        String duration = audioData[3];
                        String submitter = audioData[4];

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
                            }
                            else {
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
                }
            }
        };
    }
}