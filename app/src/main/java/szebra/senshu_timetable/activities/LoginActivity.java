package szebra.senshu_timetable.activities;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.IOException;

import io.realm.Realm;
import szebra.senshu_timetable.R;
import szebra.senshu_timetable.models.Credential;
import szebra.senshu_timetable.util.PortalCommunicator;

public class LoginActivity extends AppCompatActivity {
  private Button loginButton;
  private EditText usernameBox, passwordBox;
  
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_login);
    loginButton = (Button) findViewById(R.id.loginbutton);
    usernameBox = (EditText) findViewById(R.id.studentid);
    passwordBox = (EditText) findViewById(R.id.password);
  
    final PortalCommunicator communicator = PortalCommunicator.getInstance();
    final Realm realm = Realm.getDefaultInstance();
    
    loginButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
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
              new Handler().post(new Runnable() {
                @Override
                public void run() {
                  Toast.makeText(LoginActivity.this, "通信に失敗しました。", Toast.LENGTH_SHORT).show();
                }
              });
              return false;
            }
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
              Toast.makeText(LoginActivity.this, "ユーザー名またはパスワードが違います。", Toast.LENGTH_LONG).show();
            }
          }
        }.execute();
      }
    });
  }
}
