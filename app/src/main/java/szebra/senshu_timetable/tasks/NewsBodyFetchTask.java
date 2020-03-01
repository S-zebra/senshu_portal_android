package szebra.senshu_timetable.tasks;

import android.os.AsyncTask;
import android.util.Log;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.lang.ref.WeakReference;

import io.realm.Realm;
import szebra.senshu_timetable.PortalURL;
import szebra.senshu_timetable.models.News;
import szebra.senshu_timetable.models.NewsAttachment;
import szebra.senshu_timetable.tasks.callbacks.TaskCallback;
import szebra.senshu_timetable.util.PortalCommunicator;

/**
 * Created by s-zebra on 3/1/20.
 */
public class NewsBodyFetchTask extends AsyncTask<Integer, Void, Exception> {
  private WeakReference<TaskCallback> reference;
  private int retryCount = 0;
  private static final int MAX_RETRY_COUNT = 1;
  
  public void setCallback(TaskCallback reference) {
    this.reference = new WeakReference<>(reference);
  }
  
  @Override
  protected Exception doInBackground(Integer... integers) {
    PortalCommunicator communicator = PortalCommunicator.getInstance();
    try {
      String url = PortalURL.NEWS_INDIVIDUAL + integers[0];
      Log.d(getClass().getSimpleName(), "doInBackground(): URL: " + url);
      Document doc = communicator.get(url);
      if (doc.text().contains("参照権限がありません") && retryCount <= MAX_RETRY_COUNT) {
        communicator.logIn();
        retryCount++;
        return doInBackground(integers);
      }
      Log.d(getClass().getSimpleName(), "doInBackground(): " + doc.html());
      String bodyText = doc.selectFirst("div.message_check div").wholeText();
      bodyText = bodyText.substring(bodyText.indexOf("公開期間") + 41).trim();
      
      Realm realm = Realm.getDefaultInstance();
      News targetNews = realm.where(News.class).equalTo("checkReadId", integers[0]).findFirst();
      realm.beginTransaction();
      targetNews.setBody(bodyText);
  
      Elements attachmentLinks = doc.select("div.message_file a");
      if (!attachmentLinks.isEmpty()) {
        for (Element aElem : attachmentLinks) {
          String fileId = aElem.attr("href").substring(40);
          String fileName = aElem.ownText();
          NewsAttachment attachment = new NewsAttachment(fileId, fileName);
          targetNews.getAttachments().add(attachment);
        }
      }
      realm.copyToRealmOrUpdate(targetNews);
      realm.commitTransaction();
      realm.close();
    } catch (Exception e) {
      e.printStackTrace();
      return e;
    }
    return null;
  }
  
  @Override
  protected void onPostExecute(Exception e) {
    if (reference.get() == null) return;
    reference.get().onTaskCompleted(e);
  }
}
