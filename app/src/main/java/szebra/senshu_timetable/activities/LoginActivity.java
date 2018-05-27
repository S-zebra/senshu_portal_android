package szebra.senshu_timetable.activities;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;

import io.realm.Realm;
import szebra.senshu_timetable.R;
import szebra.senshu_timetable.models.Credential;
import szebra.senshu_timetable.util.PortalCommunicator;

public class LoginActivity extends AppCompatActivity {
  private Button loginButton;
  private EditText usernameBox, passwordBox;
  private ProgressBar progressBar;
  private TextView waitingLabel;
  
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_login);
    loginButton = (Button) findViewById(R.id.loginbutton);
    usernameBox = (EditText) findViewById(R.id.studentid);
    passwordBox = (EditText) findViewById(R.id.password);
    progressBar = (ProgressBar) findViewById(R.id.progressBar);
    waitingLabel = (TextView) findViewById(R.id.waitingLabel);
    
    final PortalCommunicator communicator = PortalCommunicator.getInstance();
    final Realm realm = Realm.getDefaultInstance();
    
    loginButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        setControlsEnabled(false);
        setBusyItems(true);
        if (usernameBox.getText().length() == 0) {
          usernameBox.setError(getResources().getString(R.string.error_required_field));
        }
        if (passwordBox.getText().length() == 0) {
          passwordBox.setError(getResources().getString(R.string.error_required_field));
        }
        new AsyncTask<Void, Void, Boolean>() {
  
          String userName, password;
  
          @Override
          protected void onPreExecute() {
            userName = usernameBox.getText().toString();
            password = passwordBox.getText().toString();
          }
  
          @Override
          protected Boolean doInBackground(Void... params) {
            try {
              return communicator.logIn(userName, password);
            } catch (IOException e) {
              cancel(true);
              return false;
            }
          }
  
          @Override
          protected void onCancelled(Boolean aBoolean) {
            Toast.makeText(LoginActivity.this, getResources().getText(R.string.comm_failed), Toast.LENGTH_SHORT).show();
            setControlsEnabled(true);
            setBusyItems(false);
          }
          
          @Override
          protected void onPostExecute(Boolean loginSuccess) {
            if (loginSuccess) {
              realm.beginTransaction();
              realm.copyToRealmOrUpdate(new Credential(1, userName, password));
              realm.commitTransaction();
              realm.close();
              startActivity(new Intent(LoginActivity.this, MainActivity.class));
              finish();
            } else {
              Toast.makeText(LoginActivity.this, getResources().getText(R.string.wrong_id_pass), Toast.LENGTH_LONG).show();
              setControlsEnabled(true);
              setBusyItems(false);
            }
          }
        }.execute();
      }
    });
  }
  
  protected void setControlsEnabled(boolean enabled) {
    usernameBox.setEnabled(enabled);
    passwordBox.setEnabled(enabled);
    loginButton.setEnabled(enabled);
  }
  
  protected void setBusyItems(boolean visible) {
    progressBar.setVisibility(visible ? View.VISIBLE : View.INVISIBLE);
    waitingLabel.setVisibility(visible ? View.VISIBLE : View.INVISIBLE);
  }
}
