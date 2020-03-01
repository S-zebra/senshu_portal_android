package szebra.senshu_timetable.tasks;

import android.os.AsyncTask;
import android.util.Log;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import io.realm.Realm;
import szebra.senshu_timetable.PortalURL;
import szebra.senshu_timetable.models.News;
import szebra.senshu_timetable.tasks.callbacks.TaskCallback;
import szebra.senshu_timetable.util.PortalCommunicator;

/**
 * Created by s-zebra on 2/29/20.
 */
public class FetchNewsTask extends AsyncTask<NewsType, Void, Exception> {
  private Realm realm;
  private WeakReference<TaskCallback> reference;
  
  @Override
  protected Exception doInBackground(NewsType... types) {
    realm = Realm.getDefaultInstance();
    realm.beginTransaction();
    realm.delete(News.class);
    try {
      for (NewsType type : types) {
        fetchPage(type);
      }
    } catch (Exception e) {
      e.printStackTrace();
      return e;
    } finally {
      realm.commitTransaction();
      realm.close();
    }
    return null;
  }
  
  @Override
  protected void onPostExecute(Exception e) {
    if (reference.get() == null) return;
    reference.get().onTaskCompleted(e);
  }
  
  public void setReference(TaskCallback reference) {
    this.reference = new WeakReference<>(reference);
  }
  
  private int getMaxPages(Document document) {
    Elements links = document.select(".p_link a");
    int maxNumber = -1;
    for (Element e : links) {
      String href = e.attr("href");
      int curPageNum = -1;
      try {
        curPageNum = Integer.parseInt(href.substring(href.length() - 2, href.length() - 1));
      } catch (NumberFormatException nfe) {
        continue;
      }
      if (curPageNum > maxNumber) {
        maxNumber = curPageNum;
      }
    }
    return maxNumber;
  }
  
  private void fetchPage(NewsType type) throws IOException, InvalidCredentialException {
    PortalCommunicator comm = PortalCommunicator.getInstance();
    comm.refreshSession();
    Document doc = comm.moveTo(PortalCommunicator.MoveMode.GET, PortalURL.NEWS_URL_UNREAD, null);
    int maxPages = getMaxPages(doc);
    if (!storeNewItems(doc)) {
      return;
    }
    for (int curPage = 1; curPage <= maxPages; curPage++) {
      doc = comm.moveTo(PortalCommunicator.MoveMode.GET, PortalURL.NEWS_URL_UNREAD + "?page=" + curPage, null);
      if (!storeNewItems(doc)) {
        break;
      }
    }
  }
  
  /**
   * Parse the doc and store news item to Realm.
   *
   * @param doc <code>org.jsoup.Document</code> to parse
   * @return Whether there was a new message.
   */
  private boolean storeNewItems(Document doc) {
    Elements rows = doc.select("table.new_message  tr");
    News prevItem = null;
    for (Element row : rows) {
      Elements cols = row.getElementsByClass("bb");
      if (cols.isEmpty()) {
        String bodyText = row.wholeText();
        int kikanPos = bodyText.indexOf("公開期間");
        Calendar cal = Calendar.getInstance();
        cal.clear();
        cal.set(Integer.parseInt(bodyText.substring(kikanPos + 24, kikanPos + 28)),
          Integer.parseInt(bodyText.substring(kikanPos + 29, kikanPos + 31)) - 1,
          Integer.parseInt(bodyText.substring(kikanPos + 32, kikanPos + 34)),
          Integer.parseInt(bodyText.substring(kikanPos + 35, kikanPos + 37)),
          Integer.parseInt(bodyText.substring(kikanPos + 38, kikanPos + 40)));
        prevItem.setPublishEndDate(cal.getTime());
        bodyText = bodyText.substring(kikanPos + 41).trim();
        Log.d(getClass().getSimpleName(), "storeNewItems(): new: " + bodyText);
        prevItem.setBody(bodyText);
        realm.copyToRealmOrUpdate(prevItem);
        continue;
      }
      News newsItem = new News();
      
      Element checkCol = cols.get(0);
      Element inElem = checkCol.getElementsByTag("input").first();
      int id = Integer.parseInt(inElem.attr("value"));
      if (!realm.where(News.class).equalTo("id", id).findAll().isEmpty()) return false;
      newsItem.setId(id);
      DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
      try {
        newsItem.setPublishStartDate(df.parse(checkCol.text().trim()));
      } catch (ParseException pe) {
        pe.printStackTrace();
      }
      
      Element metaCol = cols.get(1);
      newsItem.setIsNew(!metaCol.getElementsByAttributeValueContaining("alt", "NEW").isEmpty());
      newsItem.setImportant(!metaCol.getElementsByAttributeValueContaining("alt", "緊急").isEmpty());
      newsItem.setConfirmOpen(!metaCol.getElementsByAttributeValueContaining("alt", "開封確認").isEmpty());
      newsItem.setSubject(metaCol.text().trim());
      
      newsItem.setSender(cols.get(2).text().trim());
      prevItem = newsItem;
      realm.copyToRealmOrUpdate(newsItem);
    }
    return true;
  }
}
