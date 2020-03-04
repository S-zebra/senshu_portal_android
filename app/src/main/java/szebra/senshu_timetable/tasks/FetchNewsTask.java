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
import java.util.HashMap;

import io.realm.Realm;
import io.realm.RealmList;
import szebra.senshu_timetable.PortalURL;
import szebra.senshu_timetable.models.News;
import szebra.senshu_timetable.models.NewsAttachment;
import szebra.senshu_timetable.tasks.callbacks.TaskCallback;
import szebra.senshu_timetable.util.PortalCommunicator;

/**
 * Created by s-zebra on 2/29/20.
 */
public class FetchNewsTask extends AsyncTask<NewsCategory, Void, Exception> {
  private Realm realm;
  private WeakReference<TaskCallback> reference;
  
  @Override
  protected Exception doInBackground(NewsCategory... categories) {
    realm = Realm.getDefaultInstance();
    realm.beginTransaction();
    try {
      for (NewsCategory category : categories) {
        fetchPage(category, false);
        fetchPage(category, true);
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
    Element selectBox = document.selectFirst("div.kensu select");
    if (selectBox == null) return -1;
    return selectBox.childrenSize();
  }
  
  private void fetchPage(NewsCategory category, boolean read) throws IOException, InvalidCredentialException {
    PortalCommunicator comm = PortalCommunicator.getInstance();
    String url = read ? PortalURL.NEWS_URL_READ : PortalURL.NEWS_URL_UNREAD;
    //最初に開く
    Document doc = comm.get(url);
    // カテゴリを設定
    HashMap<String, String> catData = new HashMap<>();
    catData.put("category_cd", String.valueOf(category.getNumVal()));
    doc = comm.post(url, catData);
    Log.d(getClass().getSimpleName(), "fetchPage(): " + doc.html());
    int maxPages = getMaxPages(doc);
    storeNewItems(doc, category, read);
    for (int curPage = 1; curPage < maxPages; curPage++) {
      doc = comm.get(url + "?page=" + curPage);
      storeNewItems(doc, category, read);
      try {
        Thread.sleep(100);
      } catch (InterruptedException e) {
        e.printStackTrace();
        Thread.currentThread().interrupt();
      }
    }
  }
  
  /**
   * Parse the doc and store news item to Realm.
   *
   * @param doc <code>org.jsoup.Document</code> to parse
   * @return Whether there was a new message.
   */
  private boolean storeNewItems(Document doc, NewsCategory category, boolean read) {
    Elements rows = doc.select("table.new_message  tr");
    News prevItem = null;
    Log.d(getClass().getSimpleName(), "storeNewItems(): row: " + rows.html());
    for (Element row : rows) {
      Elements cols = row.getElementsByClass("bb");
      if (cols.isEmpty()) {
        String bodyText = row.selectFirst("div.new_message_normal div").wholeText();
        Log.d(getClass().getSimpleName(), "storeNewItems(): before: " + prevItem.getSubject() + "\n" + bodyText);
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
        prevItem.setBody(bodyText);
  
        Elements attachmentLinks = row.select("div.message_file a");
        if (!attachmentLinks.isEmpty()) {
          prevItem.setAttachments(new RealmList<NewsAttachment>());
          for (Element aElem : attachmentLinks) {
            String fileId = aElem.attr("href").substring(40);
            String fileName = aElem.ownText();
            NewsAttachment attachment = new NewsAttachment(fileId, fileName);
            prevItem.getAttachments().add(attachment);
          }
        }
        realm.copyToRealmOrUpdate(prevItem);
        Log.d(getClass().getSimpleName(), "storeNewItems(): after: " + prevItem.getBody().substring(0, 50));
        continue;
      }
      News newsItem = new News();
      
      Element checkCol = cols.get(0);
      Element inElem = checkCol.getElementsByTag("input").first();
      int id = Integer.parseInt(inElem.attr("value"));
      if (!realm.where(News.class).equalTo("id", id).findAll().isEmpty()) continue;
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
      newsItem.setHasAttachments(!metaCol.getElementsByAttributeValueContaining("alt", "添付あり").isEmpty());
      newsItem.setReplyRequired(!metaCol.getElementsByAttributeValueContaining("alt", "要返信").isEmpty());
      newsItem.setRead(read);
      newsItem.setCategory(category);
      if (newsItem.isConfirmOpen()) {
        String aHref = metaCol.selectFirst("a").attr("href");
        int messageId = Integer.parseInt(aHref.substring(72, 78));
        newsItem.setCheckReadId(messageId);
      }
      newsItem.setSubject(metaCol.text().trim());
      
      newsItem.setSender(cols.get(2).text().trim());
      prevItem = newsItem;
      realm.copyToRealmOrUpdate(newsItem);
    }
    return true;
  }
}
