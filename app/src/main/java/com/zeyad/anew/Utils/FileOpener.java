package com.zeyad.anew.Utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import androidx.core.content.FileProvider;

import java.io.File;
import java.io.IOException;

public class FileOpener {

    public static void openFile(File file, Context context) throws IOException {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
            Uri file_uri = FileProvider.getUriForFile(context,
                    context.getApplicationContext().getOpPackageName() + ".provider", file);
            Intent openFiles = new Intent(Intent.ACTION_VIEW);

            // check for file type for set dataAndType
            if (file_uri.toString().contains(".doc")) {
                openFiles.setDataAndType(file_uri, "application/msword");
            } else if (file_uri.toString().contains(".mp3") || file_uri.toString().contains(".wav")) {
                openFiles.setDataAndType(file_uri, "audio/x-wav");
            } else if (file_uri.toString().contains(".jpeg") || file_uri.toString().contains(".jpg")
                    || file_uri.toString().contains(".png")) {
                openFiles.setDataAndType(file_uri, "image/jpeg");
            } else if (file_uri.toString().toLowerCase().contains(".pdf")) {
                openFiles.setDataAndType(file_uri, "application/pdf");
            } else if (file_uri.toString().toLowerCase().contains(".mp4")) {
                openFiles.setDataAndType(file_uri, "video/*");
            } else if (file_uri.toString().toLowerCase().contains(".zip")) {
                openFiles.setDataAndType(file_uri, "application/zip");

            } else if (file_uri.toString().toLowerCase().contains(".apk")) {
                {
                    openFiles.setDataAndType(file_uri, "application/vnd.android.package-archive");
                    openFiles.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                }
            } else {
                openFiles.setDataAndType(file_uri, "*/*");
            }

            openFiles.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            context.startActivity(openFiles);

        }
    }

}
