package plugin.apkInstaller;

import org.apache.cordova.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.File;
import android.os.Environment;
import android.content.Intent;
import android.net.Uri;
import android.content.Context;
import android.support.v4.content.FileProvider;
import java.text.SimpleDateFormat;

public class ApkInstaller extends CordovaPlugin {

    private static final String ACTION_INSTALL = "install";

    private static final String MIME_TYPE_APK = "application/vnd.android.package-archive";

    @Override
    public boolean execute(String action, JSONArray data, CallbackContext callbackContext) throws JSONException {
        if (action.equals(ACTION_INSTALL)) {
            String path = data.getString(0);
            String fileName = data.getString(1);
            Context context = this.cordova.getActivity().getApplicationContext();
            // TODO: use baseDir as default when user doesn't specify a path
            String baseDir = Environment.getExternalStorageDirectory().getAbsolutePath();
            File file = new File(path + fileName);

            try {
                
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N){
                    install(file, context);
                } else {
                    oldInstall(file, context);
                }

                callbackContext.success();
                
            } catch (Exception ex) {
                callbackContext.error(ex.toString());
            }
            return true;
        } else {
            return false;
        }
    }
    
    /*
     * Api up to 23 uses file:// based URIs to create the intent
     */
    private void oldInstall (File file, Context context){
                Uri uri = Uri.fromFile(file);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(uri, MIME_TYPE_APK);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    /*
    * Api 24 onwards uses context:// based URIs to create the intent
    */
    private void install (File file, Context context){
        Uri uri = FileProvider.getUriForFile(context, context.getPackageName() + ".fileprovider", file);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(uri, MIME_TYPE_APK);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(
            Intent.FLAG_GRANT_WRITE_URI_PERMISSION |
            Intent.FLAG_GRANT_READ_URI_PERMISSION
        );
        context.startActivity(intent);
    }
}
