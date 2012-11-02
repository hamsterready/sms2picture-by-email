package pl.quaternion.sms2picturebyemail;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class InfoActivity extends Activity {

  public static final String PREFERENCES = "sms2picturebyemail.preferences";
  public static final String PREFERENCES_EMAIL_ADDRESS = "email_address";
  public static final String PREFERENCES_MOBILE_NUMBER = "mobile_number";
  protected static final String PREFERENCES_LAST_IMAGE = "last_image";
  protected static final String PREFERENCES_LAST_IMAGE_DATE = "last_image_date";

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_info);

    final SharedPreferences preferences = getSharedPreferences(PREFERENCES, 0);

    final EditText textEmailAddress = (EditText) findViewById(R.id.email_address);
    textEmailAddress.setText(preferences.getString(PREFERENCES_EMAIL_ADDRESS, ""));
    final EditText textMobileNumber = (EditText) findViewById(R.id.mobile_number);
    textMobileNumber.setText(preferences.getString(PREFERENCES_MOBILE_NUMBER, ""));
    final Button buttonSave = (Button) findViewById(R.id.save);
    buttonSave.setOnClickListener(new OnClickListener() {

      public void onClick(View v) {
        final Editor edit = preferences.edit();
        edit.putString(PREFERENCES_EMAIL_ADDRESS, textEmailAddress.getText().toString());
        edit.putString(PREFERENCES_MOBILE_NUMBER, textMobileNumber.getText().toString());
        edit.commit();

        Toast.makeText(InfoActivity.this, "Preferences saved.", Toast.LENGTH_SHORT).show();
      }
    });

    final TextView textLink = (TextView) findViewById(R.id.link);
    textLink.setText(Html.fromHtml("Fork me on <a href='#'>Github</a>"));
    textLink.setOnClickListener(new OnClickListener() {

      public void onClick(View v) {
        final Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://github.com/hamsterready/sms2picture-by-email"));
        startActivity(browserIntent);
      }
    });

    final TextView textLastUpload = (TextView) findViewById(R.id.last_upload);
    final long lastImageDate = preferences.getLong(PREFERENCES_LAST_IMAGE_DATE, 0);
    if (lastImageDate != 0) {

      textLastUpload
          .setText(Html.fromHtml("Last upload: <a href='#'>" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(lastImageDate)) + "</a>."));
      textLastUpload.setOnClickListener(new OnClickListener() {

        public void onClick(View v) {
          final String lastImage = preferences.getString(PREFERENCES_LAST_IMAGE, null);
          if (lastImage != null) {
            final Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(lastImage));
            startActivity(browserIntent);
          }

        }
      });
    }

    final Button buttonTest = (Button) findViewById(R.id.test);
    buttonTest.setOnClickListener(new OnClickListener() {

      public void onClick(View v) {
        startService(new Intent(InfoActivity.this, TakePictureService.class));
      }
    });

  }
}
