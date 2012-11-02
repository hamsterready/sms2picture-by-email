package pl.quaternion.sms2picturebyemail;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.PictureCallback;
import android.os.AsyncTask;
import android.os.IBinder;
import android.util.Log;
import android.view.SurfaceView;

public class TakePictureService extends Service {

  private static final String TAG = "TakePictureService";

  private volatile boolean pictureTaken = true;

  private PictureCallback getJpegCallback() {
    return new PictureCallback() {
      public void onPictureTaken(final byte[] data, Camera camera) {
        Log.i(TAG, "Got picture: " + (data != null) + ".");
        camera.stopPreview();
        camera.release();

        final AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {

          @Override
          protected Void doInBackground(Void... params) {
            pictureTaken = true;

            final SharedPreferences preferences = getSharedPreferences(InfoActivity.PREFERENCES, 0);

            final String deliveryEmailAddress = preferences.getString(InfoActivity.PREFERENCES_EMAIL_ADDRESS, "");

            final String url = uploadImage(data);
            final Editor edit = preferences.edit();
            edit.putString(InfoActivity.PREFERENCES_LAST_IMAGE, url);
            edit.putLong(InfoActivity.PREFERENCES_LAST_IMAGE_DATE, new Date().getTime());
            edit.commit();

            try {
              sendEmailWithLink(url, deliveryEmailAddress);
            } catch (MalformedURLException e) {
              Log.e(TAG, e.getMessage(), e);
            } catch (IOException e) {
              Log.e(TAG, e.getMessage(), e);
            }
            return null;
          }
        };

        task.execute();
      }

      private void sendEmailWithLink(String url, String deliveryEmailAddress) throws MalformedURLException, IOException {

        Log.i(TAG, "Sending email...");

        final String urlEncoded = URLEncoder.encode(url);
        final URLConnection connection = new URL(
            "http://api.jangomail.com/api.asmx/SendTransactionalEmail?Username=hamsterready&Password=665071DL&FromEmail=sms2picturebyemail@quaternion.pl&FromName=&ToEmailAddress="
                + deliveryEmailAddress + "&Subject=PictureByEmail&MessagePlain=Picture+" + urlEncoded + "&MessageHTML=&Options=").openConnection();
        InputStream is = connection.getInputStream();
        byte[] buffer = new byte[1024];
        int s = is.read(buffer);

        String reply = new String(buffer, 0, s);
        Log.d(TAG, "Result: " + reply);
      }

      private String uploadImage(byte[] data) {

        Log.i(TAG, "Uploading image...");
        final String url = "http://api.imgur.com/2/upload";
        final String apiKey = "3fd8edab3e5a6893a5b7040b5771d71c";
        final String type = "base64";

        // Create a new HttpClient and Post Header
        HttpClient httpclient = new DefaultHttpClient();
        HttpPost httppost = new HttpPost(url);

        try {
          // Add your data
          List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
          nameValuePairs.add(new BasicNameValuePair("key", apiKey));
          nameValuePairs.add(new BasicNameValuePair("type", type));
          nameValuePairs.add(new BasicNameValuePair("image", Base64.encodeBytes(data)));
          httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

          // Execute HTTP Post Request
          HttpResponse response = httpclient.execute(httppost);
          InputStream is = response.getEntity().getContent();

          byte[] buffer = new byte[1024];
          int s = is.read(buffer);

          String reply = new String(buffer, 0, s);

          Log.d(TAG, "Result: " + reply + "");
          int x1 = reply.indexOf("<original>");
          int x2 = reply.indexOf("</original>");
          if (x1 != -1 && x2 != -1) {
            final String imageUrl = reply.substring(x1 + 10, x2);
            Log.i(TAG, "Image URL: " + imageUrl);
            return imageUrl;
          }
          return reply;
        } catch (ClientProtocolException e) {
          Log.e(TAG, e.getMessage(), e);
          return e.getMessage();
        } catch (IOException e) {
          Log.e(TAG, e.getMessage(), e);
          return e.getMessage();
        }
      }
    };
  }

  public IBinder onBind(Intent intent) {
    return null;
  }

  @Override
  public void onDestroy() {
    Log.i(TAG, "#onDestroy...");
  }

  @Override
  public void onStart(Intent intent, int startId) {
    try {
      takePictureNoPreview(getApplicationContext());
    } catch (Exception e) {
      Log.e(TAG, e.getMessage(), e);
    }
  }

  public void takePictureNoPreview(final Context context) throws IOException, InterruptedException, ExecutionException {

    pictureTaken = true;

    final Camera myCamera = Camera.open();

    Log.i(TAG, "Taking picture...");
    if (myCamera != null) {
      Parameters parameters = myCamera.getParameters();
      parameters.set("jpeg-quality", 90);
      parameters.setPictureFormat(PixelFormat.JPEG);
      myCamera.setParameters(parameters);

      SurfaceView dummy = new SurfaceView(context);
      myCamera.setPreviewDisplay(dummy.getHolder());
      myCamera.startPreview();
      myCamera.autoFocus(new AutoFocusCallback() {

        public void onAutoFocus(boolean success, final Camera camera) {
          Log.i(TAG, "Autofocus: " + success);
          new Thread(new Runnable() {

            public void run() {
              camera.takePicture(null, null, getJpegCallback());
              while (pictureTaken) {
                try {
                  Thread.sleep(1000);
                } catch (InterruptedException e) {
                  Log.e(TAG, e.getMessage(), e);
                }
              }
              Log.i(TAG, "Picture taken.");
            }
          }).start();
        }
      });
    } else {
      Log.e(TAG, "Cannot open camera.");
    }

  }
}
