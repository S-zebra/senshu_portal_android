package szebra.senshu_timetable.activities;

import android.content.Intent;
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
import szebra.senshu_timetable.tasks.InvalidCredentialException;
import szebra.senshu_timetable.tasks.LoginTask;
import szebra.senshu_timetable.tasks.callbacks.TaskCallback;

public class LoginActivity extends AppCompatActivity implements TaskCallback {
  private Button loginButton;
  private EditText usernameBox, passwordBox;
  private ProgressBar progressBar;
  private TextView waitingLabel;
  private Credential credential;
  
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_login);
    loginButton = findViewById(R.id.loginbutton);
    usernameBox = findViewById(R.id.studentid);
    passwordBox = findViewById(R.id.password);
    progressBar = findViewById(R.id.chgLoadingCircle);
    waitingLabel = findViewById(R.id.waitingLabel);
    loginButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        if (!isInputValid()) return;
        setControlsEnabled(false);
        setBusyItems(true);
        LoginTask loginTask = new LoginTask();
        loginTask.setCallback(LoginActivity.this);
        String userName = usernameBox.getText().toString();
        String password = passwordBox.getText().toString();
        credential = new Credential(userName, password);
        loginTask.execute(credential);
      }
    });
  }
  
  private boolean isInputValid() {
    boolean isValid = true;
    if (usernameBox.getText().length() == 0) {
      usernameBox.setError(getResources().getString(R.string.error_required_field));
      isValid = false;
    }
    if (passwordBox.getText().length() == 0) {
      passwordBox.setError(getResources().getString(R.string.error_required_field));
      isValid = false;
    }
    return isValid;
  }
  
  @Override
  public void onTaskCompleted(Throwable exception) {
    if (exception != null) {
      if (exception instanceof InvalidCredentialException) {
        Toast.makeText(this, getString(R.string.wrong_id_pass), Toast.LENGTH_SHORT).show();
      }
      if (exception instanceof IOException) {
        Toast.makeText(this, getString(R.string.comm_failed), Toast.LENGTH_SHORT).show();
      }
      setControlsEnabled(true);
      setBusyItems(false);
      return;
    }
    Realm realm = Realm.getDefaultInstance();
    realm.beginTransaction();
    realm.copyToRealmOrUpdate(credential);
    realm.commitTransaction();
    realm.close();
    startActivity(new Intent(this, MainActivity.class));
    finish();
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
