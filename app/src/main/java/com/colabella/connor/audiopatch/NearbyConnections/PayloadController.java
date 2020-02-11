package com.colabella.connor.audiopatch.NearbyConnections;

import android.support.annotation.NonNull;
import android.text.format.DateFormat;

import com.google.android.gms.nearby.connection.Payload;
import com.google.android.gms.nearby.connection.PayloadCallback;
import com.google.android.gms.nearby.connection.PayloadTransferUpdate;

import java.util.Date;

class PayloadController extends NearbyConnections {

   /* public void sendBytes(String endpointId, Payload payload) {
        if (payload.getType() == Payload.Type.BYTES) {
            // No need to track progress for bytes.
            return;
        }
        connectionsClient = Nearby.getConnectionsClient(c);
        connectionsClient.sendPayload(endpointId, payload);
    }

    BroadcastReceiver onComplete = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            // findViewById(R.id.start).setEnabled(true);
            //Toast.makeText(getContext(), "Download complete!", Toast.LENGTH_SHORT).show();
        }
    };


    private ConnectionInfo connectionInfo;

    private ConnectionInfo getConnectionInfo() {
        return connectionInfo;
    }

    private void setConnectionInfo(ConnectionInfo c) {
        connectionInfo = c;
    }
    */

    PayloadCallback createPayloadCallback() {
        return new PayloadCallback() {
            @Override
            public void onPayloadReceived(@NonNull String endpointId, @NonNull Payload payload) {
                Date d = new Date();
                CharSequence s  = DateFormat.format("EEEE, MMMM d, yyyy ", d.getTime());
                System.out.println("Transfer initiated. " + s);

                if (payload.getType() == Payload.Type.BYTES) {
                    System.out.println("Payload type: Bytes");
                    // No need to track progress for bytes.
                    /*String string = new String(payload.asBytes(), UTF_8);
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
                    if (string.equals("EXAMPLE PAYLOAD!")) {

                    }

                    //return;*/
                } else if (payload.getType() == Payload.Type.FILE) {
                    System.out.println("Payload type: File");
                    // Add this to our tracking map, so that we can retrieve the payload later.
                    // incomingPayloads.put(valueOf(payload.getId()), payload);
                    //incomingPayloads.put(payload.getId(), payload);
                }
            }

            //String payloadId changed to String
            @Override
            public void onPayloadTransferUpdate(@NonNull String payloadId, @NonNull PayloadTransferUpdate update) {
                if (update.getStatus() == PayloadTransferUpdate.Status.SUCCESS) {
                    Date d = new Date();
                    CharSequence s  = DateFormat.format("EEEE, MMMM d, yyyy ", d.getTime());
                    System.out.println("Transfer completed. " + s);
                    // Ensures that only the device on the receiving end of the transfer is effected by the update.
                   /* if (isReceiver) {
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
                    }*/

                }
            }
        };
    }
}