package com.example.erasmusvalencia;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

// Class that helps reading and writing files
public class FileHandler {

    private String fileName;
    private String content;
    final static private String TAG = "FileHandler";
    public static final String FILE_NAME = "my_events";
    public static Context context;

    public FileHandler(String fileName) {
        this.fileName = fileName;
        content="no content yet";
        try {
            if (!fileExists()) createFile();
        } catch (IOException e) {
            Log.e(TAG, "FileHandler: exception at creation", e);
        }
    }

    public String getContent(){ return content; }


    public void appendFile(String text) throws IOException {
        OutputStreamWriter fout = new OutputStreamWriter(context.openFileOutput(fileName, Context.MODE_APPEND));
        fout.write(text);
        fout.write(System.getProperty("line.separator"));
        fout.close();
    }

    public static String readFromRaw() {
        String s, total = "";

        try {
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(context.getResources().openRawResource(R.raw.raw_event_data)));
            while ((s = reader.readLine())!=null) {
                total += " " + s;
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.i(TAG, "readFromRaw: read string " + total) ;
        return total;
    }

    public void writeFile(String text) throws IOException {
        OutputStreamWriter fout = new OutputStreamWriter(context.openFileOutput(fileName, Context.MODE_PRIVATE));
        fout.write(text);
        fout.close();
    }

    public void clearFile() throws IOException {
        this.writeFile("");
    }

    private void createFile() throws IOException {
        File file = new File(context.getFilesDir(), fileName);
        FileOutputStream fout = new FileOutputStream(file);
        fout.close();
    }

    public void readFile() throws IOException {
        if (!fileExists()) return;
        InputStreamReader fis = new InputStreamReader(context.openFileInput(fileName));
        BufferedReader fin = new BufferedReader(fis);

        String s, total = "";
        while ((s = fin.readLine())!=null) {
            total += " " + s;
        }
        fin.close();
        content = total;
        return;
    }

    private boolean fileExists() {
        File file = new File(context.getFilesDir(), fileName);
        return file.exists();
    }
}
