package szebra.senshu_timetable.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.security.GeneralSecurityException;
import java.security.Key;

import szebra.senshu_timetable.R;
import szebra.senshu_timetable.util.CryptManager;

public class LoginActivity extends AppCompatActivity {
  private Button loginButton;
  private SharedPreferences pref;
  private EditText usernameBox, passwordBox;
  
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_login);
    loginButton = (Button) findViewById(R.id.loginbutton);
    usernameBox = (EditText) findViewById(R.id.studentid);
    passwordBox = (EditText) findViewById(R.id.password);
    final Context context = this;
    pref = PreferenceManager.getDefaultSharedPreferences(this);
    loginButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        Key key = CryptManager.generateKey();
        byte[] en_un, en_pw;
        
        try {
          en_un = CryptManager.encrypt(usernameBox.getText().toString().getBytes(), key);
          Log.d("Encryption(UN)", new String(en_un));
        } catch (GeneralSecurityException e) {
          Log.e("Unsupported key", e.toString());
          en_un = new byte[3];
        }
        
        try {
          en_pw = CryptManager.encrypt(passwordBox.getText().toString().getBytes(), key);
          Log.d("Encryption(PW)", new String(en_pw));
        } catch (GeneralSecurityException e) {
          Log.e("Unsupported key", e.toString());
          en_pw = new byte[3];
        }
        
        SharedPreferences.Editor editor = pref.edit();
        String encodedKey = Base64.encodeToString(key.getEncoded(), Base64.DEFAULT);
        String encodedUN = Base64.encodeToString(en_un, Base64.DEFAULT);
        String encodedPW = Base64.encodeToString(en_pw, Base64.DEFAULT);
        editor.putString("key", encodedKey);
        editor.putString("userName", encodedUN);
        editor.putString("pwd", encodedPW);
        editor.commit();
        startActivity(new Intent(context, MainActivity.class));
        finish();
      }
    });
  }
  
  private void encryptString(String string) {
    
  }
}
