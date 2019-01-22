package com.colabella.connor.audiopatch.Audio;

import android.content.Context;
import android.database.Cursor;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.provider.OpenableColumns;

class AudioDataParser {

    String getFileData(Uri uri, Context context, int requestCode) {
        String result = null;
        MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
        Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);
            try {
                switch(requestCode) {
                    case 1: { //Retrieve filename from uri
                        if (uri.getScheme().equals("content")) {
                            cursor = context.getContentResolver().query(uri, null, null, null, null);
                            if (cursor != null && cursor.moveToFirst()) {
                                result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                            }
                            //Removes file extension from the end of filenames.
                            if (result != null) {
                                if (result.contains(".")) {
                                    int count = 0;
                                    for (int i = 0; i < result.length(); i++) {
                                        char c = result.charAt(i);
                                        if (c == '.') {
                                            count = i;
                                        }
                                    }
                                    result = result.substring(0, count);
                                }
                            }
                        }
                        break;
                    }
                    case 2: { // Retrieves audio file's artist metadata
                        mediaMetadataRetriever.setDataSource(context, uri);
                        result = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
                        if(result == null){
                            result = "Unknown artist";
                        }

                        break;
                    }
                    case 3: { // Retrieves audio file's duration in milliseconds
                        mediaMetadataRetriever.setDataSource(context, uri);
                        result = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
                        long timeForConversion = Long.parseLong(result);
                        AudioController audioController = new AudioController();
                        result = audioController.milliSecondsToTimer(timeForConversion);

                        if(result == null){
                            result = "null";
                        }
                        break;
                    }
                    //return nickname of submitter
                    case 4: {
                        //result = m.getNickName();
                        break;
                    }
                }
            }
            finally {
                if(cursor != null) {
                    cursor.close();
                }
            }
        /*if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }*/
        return result;
    }



}
