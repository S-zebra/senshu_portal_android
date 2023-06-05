package szebra.senshu_timetable;

import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class AboutFragment extends Fragment {

  @Nullable
  @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
    return inflater.inflate(R.layout.fragment_about, container, false);
  }

  @Override
  public void onActivityCreated(@Nullable Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    View view = getView();
    String versionString = getString(R.string.label_about_version, BuildConfig.VERSION_NAME);
    ((TextView) view.findViewById(R.id.about_version)).setText(versionString);
    
    String authorString = getString(R.string.label_about_author, getString(R.string.author_name));
    ((TextView) view.findViewById(R.id.about_author)).setText(authorString);
    
    String emailString = getString(R.string.label_about_email, getString(R.string.mail_address));
    ((TextView) view.findViewById(R.id.about_email)).setText(emailString);
    
    String ghUrlTag = getString(R.string.url_tag, "Github", getString(R.string.github_url));
    String ghText = getString(R.string.label_about_cont_gh, ghUrlTag);
    TextView ghLabel = view.findViewById(R.id.about_cont_github);
    ghLabel.setMovementMethod(LinkMovementMethod.getInstance());
    ghLabel.setText(Html.fromHtml(ghText));
  }
}
