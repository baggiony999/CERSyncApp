package util;

import android.app.Activity;
import android.os.AsyncTask;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import util.su.SUHelper;

/**
 * Created by bgm on 3/19/2016 AD.
 */
public class CSService {
    public static String hiroScriptUrl = "http://203.209.87.52:8080/cer/file.jsp?sort=1&file=%2Fopt%2Fcer%2Fhiro%2FCER.txt";
    public static String hiroDownloadedFileStr = "/sdcard/HiroDownload/CER.txt";

    //public static String hiroScriptFileStr = "/data/CER.txt";
    public static String hiroScriptFileStr = "/sdcard/HiroMacro/Documents/CER.txt";
    //public static String hiroBackupPathStr = "/sdcard/HiroScriptBAK/";
    public static String hiroBackupPathStr = "/sdcard/HiroMacro/Documents/";


    public static String process(final Activity activity) throws IOException {
        new AsyncTask<String, String, String>() {
            @Override
            protected String doInBackground(String... params) {
                String r = "";
                try {
                    U.d("Download...");
                    publishProgress("Download... " + hiroScriptUrl);
                    downloadHiroScript();

                    U.d("Replace Hiro...");
                    publishProgress("Replace... " + hiroScriptFileStr);
                    String bakFile=replaceHiroScript();

                    String hiroContent = FileUtils.readFileToString(new File(hiroScriptFileStr));
                    r="Backup: "+bakFile+"\nHiro script updated:\n"+hiroContent;

                } catch (Exception e) {
                    U.e(e);
                    r = e.toString();
                }
                return r;
            }
            @Override
            protected void onProgressUpdate(String... progress) {
               U.setTextViewTextWithTS(activity, progress[0]);
            }
            @Override
            protected void onPostExecute(String r) {
                U.setTextViewTextWithTS(activity, r);
                U.d("Done...");
            }
        }.execute();
        return "Waiting Backgorund Process...";
    }



    private static String replaceHiroScript() throws IOException {
       // String bakFileStr = hiroBackupPathStr + hiroScriptFileStr.substring(hiroScriptFileStr.lastIndexOf("/") + 1) + "-" + U.getCurDateStr();
        String bakFileStr = hiroBackupPathStr + hiroScriptFileStr.substring(hiroScriptFileStr.lastIndexOf("/") + 1) + ".bak";

        File f= new File(bakFileStr);
        if(f.exists()) {
            U.d("backup " + hiroScriptFileStr + " to " + bakFileStr);
            SUHelper.sudoForResult("mkdir -p " + hiroBackupPathStr);
            SUHelper.sudoForResult("cp " + hiroScriptFileStr + " " + bakFileStr);
        }
        U.d("replace " + hiroScriptFileStr + " with " + hiroDownloadedFileStr);
        SUHelper.sudoForResult("cp " + hiroDownloadedFileStr + " " + hiroScriptFileStr);
        return bakFileStr;
    }

    public static void downloadHiroScript() throws IOException {
        U.d("req to " + hiroScriptUrl);
        FileUtils.copyURLToFile(new URL(hiroScriptUrl), new File(hiroDownloadedFileStr));
        U.d("saved to " + hiroDownloadedFileStr);
       }

}
