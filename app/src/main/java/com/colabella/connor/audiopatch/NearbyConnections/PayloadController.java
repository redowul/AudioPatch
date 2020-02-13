package com.colabella.connor.audiopatch.NearbyConnections;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.support.annotation.NonNull;
import android.support.v4.util.SimpleArrayMap;
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
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Objects;

import static java.nio.charset.StandardCharsets.UTF_8;

public class PayloadController extends NearbyConnections {

    private void sendBytes(String endpointId, String string, Context context) {
        byte[] bytes = string.getBytes(UTF_8); // convert string to bytes
        Payload bytesPayload = Payload.fromBytes(bytes); // convert bytes to payload object
        Nearby.getConnectionsClient(context).sendPayload(endpointId, bytesPayload); // send the payload
    }

    public void sendAudio(String endpointId, Audio audio, Context context) {
        //Payload filePayload;
        Uri outgoingUri = Uri.parse(audio.getData()); // convert audio data to uri
        /*File file = new File(String.valueOf(outgoingUri)); // create file object from uri
        try {
            filePayload = Payload.fromFile(file); // create payload object from file object
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }*/

        MainActivity mainActivity = new MainActivity();
        ParcelFileDescriptor pfd = null;
        try {
            pfd = mainActivity.getInstance().getContentResolver().openFileDescriptor(outgoingUri, "r");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        if (pfd != null) {
            Payload filePayload = Payload.fromFile(pfd);

            // if(filePayload != null) { // null check to make certain we have data to send
            String payloadFilename = "filename" + "|" + audio.getTitle(); //TODO include nicknames. make '|' an illegal character
            sendBytes(endpointId, payloadFilename, context); // send the payload; the goal is to attach it to the sent audio file and rebuild the object on the receiving device

            Nearby.getConnectionsClient(context).sendPayload(endpointId, filePayload); // send the payload
            //}
        }
    }


    PayloadCallback createPayloadCallback() {
        //SimpleArrayMap<String, Payload> incomingPayloads = new SimpleArrayMap<>();  // key is payloadId
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
                        String[] filenameInput = {endpointId, filename}; // create a new array and associate it with the given endpoint
                        if (!filenames.contains(filenameInput)) { // check to see if the item already exists in the array
                            filenames.add(filenameInput); // store the item
                        }
                    }
                } else if (payload.getType() == Payload.Type.FILE) {
                    //incomingPayloads.put(Long.toString(payload.getId()), payload); // store the payload so it can be referenced in onPayloadTransferUpdate()
                    incomingPayloads.add(payload); // store the payload so it can be referenced in onPayloadTransferUpdate()
                    for (int i = 0; i < filenames.size(); i++) {
                        if (filenames.get(i)[0].equals(endpointId)) { // after updating the array, payload id will be used for linking the two items, not the endpointId
                            String[] item = new String[]{
                                    Long.toString(payload.getId()), // payloadId
                                    filenames.get(i)[1] // title of file
                            };
                            filenames.set(i, item); // add the item to the queue
                            break;
                        }
                    }
                }
            }

            @Override
            public void onPayloadTransferUpdate(@NonNull String payloadId, @NonNull PayloadTransferUpdate update) {
                if (update.getStatus() == PayloadTransferUpdate.Status.SUCCESS) {

                    if (incomingPayloads.size() > 0) {
                        Payload payload = incomingPayloads.get(0);
                        File payloadFile = payload.asFile().asJavaFile();
                        System.out.println("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
                        String filename = filenames.get(0)[1] + ".mp3";
                        payloadFile.renameTo(new File(Environment.getExternalStorageDirectory().toString() + File.separator + "AudioPatch", filename));
                        System.out.println(payloadFile.getAbsolutePath());

                        AudioController audioController = new AudioController();
                       // Uri uri = Uri.parse(Environment.getExternalStorageDirectory().toString() + File.separator + "AudioPatch" + File.separator + filename + ".mp3");
                        Uri uri = Uri.parse(payloadFile.getAbsolutePath());
                        String data = uri.toString();
                        Bitmap bitmap = audioController.getAlbumCover(data);
                        Audio audio = new Audio(data, filename, bitmap); // Audio object rebuilt and can be used at leisure

                        SingletonController.getInstance().getActivePlaylistAdapter().addItem(audio); // Add the item to the playlist
                        SingletonController.getInstance().getActivePlaylistAdapter().notifyDataSetChanged();
                    }

                    /*System.out.println(payloadId);
                    System.out.println(filenames.get(0)[0] + " TEST");
                    String filename = filenames.get(0)[1];

                    File directory = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString() + File.separator + "Nearby");
                    File directory2 = new File(Environment.getExternalStorageDirectory().toString() + File.separator + "AudioPatch");
                    File from = new File(directory, filenames.get(0)[0]);
                   // System.out.println(from.getAbsolutePath());
                    File to = new File(directory2, filename + ".mp3");
                    System.out.println(to.getAbsolutePath());

                    /*


                    //System.out.println(file.getName());
                   // System.out.println(file.getAbsolutePath());

                    //String filename = filenames.get(0)[1];
                   // file.renameTo(new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString() + File.separator + "Nearby",filename + ".mp3"));
                   // file.renameTo(new File(Environment.getExternalStorageDirectory().toString() + File.separator + "AudioPatch",filename + ".mp3"));

                    //Payload payload = incomingPayloads.get(payloadId);



                    //if(payload != null) {
                        /*if (payload.getType() == Payload.Type.FILE) {
                            if(filenames.size() > 0) {
                                for (int i = 0; i < filenames.size(); i++) {
                                    if (filenames.get(i)[0].equals(payloadId)) {
                                        String[] fileData = filenames.remove(i);

                                        File payloadFile = Objects.requireNonNull(payload.asFile()).asJavaFile();
                                        if (payloadFile != null) {
                                            String fileName = fileData[1];
                                            payloadFile.renameTo(new File(Environment.getExternalStorageDirectory().toString() + File.separator + "AudioPatch", fileName + ".mp3"));

                                            AudioController audioController = new AudioController();
                                            Uri uri = Uri.parse(Environment.getExternalStorageDirectory().toString() + File.separator + "AudioPatch" + File.separator + fileName + ".mp3");
                                            String data = uri.toString();
                                            Bitmap bitmap = audioController.getAlbumCover(data);
                                            Audio audio = new Audio(data, fileName, bitmap); // Audio object rebuilt and can be used at leisure

                                            SingletonController.getInstance().getActivePlaylistAdapter().addItem(audio); // Add the item to the playlist
                                            SingletonController.getInstance().getActivePlaylistAdapter().notifyDataSetChanged();
                                        }
                                        break;
                                    }
                                }
                            }
                        }
                        incomingPayloads.remove(payloadId);
                         */
                    // }
                }
            }
        };
    }
}