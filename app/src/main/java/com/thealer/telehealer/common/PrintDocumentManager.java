package com.thealer.telehealer.common;

import android.content.Context;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.os.ParcelFileDescriptor;
import android.print.PageRange;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintDocumentInfo;
import android.print.PrintManager;
import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import android.webkit.WebView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by Aswin on 07,December,2018
 */
public class PrintDocumentManager {
    private static FragmentActivity activity;
    private static File file;
    private static String fileName;
    private static PrintDocumentManager printDocumentManager;
    private static PrintManager printManager;

    public static PrintDocumentManager with(FragmentActivity fragmentActivity) {
        if (printDocumentManager == null) {
            printDocumentManager = new PrintDocumentManager();
        }
        activity = fragmentActivity;
        printManager = (PrintManager) activity.getSystemService(Context.PRINT_SERVICE);
        return printDocumentManager;
    }

    public PrintDocumentManager print(@NonNull File inputFile, @NonNull String inputFileName) {
        file = inputFile;
        fileName = inputFileName;

        PrintAdapter printAdapter = new PrintAdapter(file, fileName);
        printManager.print(fileName, printAdapter, null);

        return printDocumentManager;
    }

    public PrintDocumentManager print(@NonNull WebView webView, @NonNull String title) {
        PrintDocumentAdapter printDocumentAdapter = webView.createPrintDocumentAdapter(title);
        printManager.print(title, printDocumentAdapter, new PrintAttributes.Builder().build());

        return printDocumentManager;
    }
}

class PrintAdapter extends PrintDocumentAdapter {
    private File file;
    private String fileName;

    public PrintAdapter(File file, String fileName) {
        this.file = file;
        this.fileName = fileName;
    }

    @Override
    public void onLayout(PrintAttributes oldAttributes, PrintAttributes newAttributes, CancellationSignal cancellationSignal, LayoutResultCallback layoutResultCallback, Bundle extras) {
        if (cancellationSignal.isCanceled()) {
            layoutResultCallback.onLayoutCancelled();
        } else {
            PrintDocumentInfo.Builder builder =
                    new PrintDocumentInfo.Builder(fileName);
            builder.setContentType(PrintDocumentInfo.CONTENT_TYPE_DOCUMENT)
                    .setPageCount(PrintDocumentInfo.PAGE_COUNT_UNKNOWN)
                    .build();
            layoutResultCallback.onLayoutFinished(builder.build(),
                    !newAttributes.equals(oldAttributes));
        }
    }

    @Override
    public void onWrite(PageRange[] pages, ParcelFileDescriptor destination, CancellationSignal cancellationSignal, WriteResultCallback callback) {

        InputStream in = null;
        OutputStream out = null;
        try {
            in = new FileInputStream(file);
            out = new FileOutputStream(destination.getFileDescriptor());

            byte[] buf = new byte[16384];
            int size;

            while ((size = in.read(buf)) >= 0
                    && !cancellationSignal.isCanceled()) {
                out.write(buf, 0, size);
            }

            if (cancellationSignal.isCanceled()) {
                callback.onWriteCancelled();
            } else {
                callback.onWriteFinished(new PageRange[]{PageRange.ALL_PAGES});
            }
        } catch (Exception e) {
            callback.onWriteFailed(e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
