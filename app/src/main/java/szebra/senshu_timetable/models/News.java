package szebra.senshu_timetable.models;

import java.util.Date;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import szebra.senshu_timetable.tasks.NewsCategory;

/**
 * Created by s-zebra on 2/29/20.
 */
public class News extends RealmObject {
  @PrimaryKey
  private int id;
  private int checkReadId;
  private Date publishStartDate;
  private Date publishEndDate;
  private Date lastViewedDate;
  private String subject;
  private String body;
  private String sender;
  private boolean isNew;
  private boolean confirmOpen;
  private boolean important;
  private boolean replyRequired;
  private boolean hasAttachments;
  private boolean isRead;
  private int categoryCode;
  
  
  private RealmList<NewsAttachment> attachments;
  
  public News() {
  }
  
  public News(int id, Date publishStartDate, String subject, String sender, boolean isNew, boolean confirmOpen, boolean important) {
    this.id = id;
    this.publishStartDate = publishStartDate;
    this.subject = subject;
    this.sender = sender;
    this.isNew = isNew;
    this.confirmOpen = confirmOpen;
    this.important = important;
  }
  
  public void setId(int id) {
    this.id = id;
  }
  
  public void setPublishStartDate(Date publishStartDate) {
    this.publishStartDate = publishStartDate;
  }
  
  public int getCheckReadId() {
    return checkReadId;
  }
  
  public void setCheckReadId(int checkReadId) {
    this.checkReadId = checkReadId;
  }
  
  public boolean isReplyRequired() {
    return replyRequired;
  }
  
  public void setReplyRequired(boolean replyRequired) {
    this.replyRequired = replyRequired;
  }
  
  public boolean hasAttachments() {
    return hasAttachments;
  }
  
  public void setHasAttachments(boolean hasAttachments) {
    this.hasAttachments = hasAttachments;
  }
  
  public void setPublishEndDate(Date publishEndDate) {
    this.publishEndDate = publishEndDate;
  }
  
  public void setLastViewedDate(Date lastViewedDate) {
    this.lastViewedDate = lastViewedDate;
  }
  
  public void setSubject(String subject) {
    this.subject = subject;
  }
  
  public void setBody(String body) {
    this.body = body;
  }
  
  public void setSender(String sender) {
    this.sender = sender;
  }
  
  public void setIsNew(boolean isNew) {
    this.isNew = isNew;
  }
  
  public void setConfirmOpen(boolean confirmOpen) {
    this.confirmOpen = confirmOpen;
  }
  
  public void setImportant(boolean important) {
    this.important = important;
  }
  
  public void setAttachments(RealmList<NewsAttachment> attachments) {
    this.attachments = attachments;
  }
  
  public int getId() {
    return id;
  }
  
  public Date getPublishStartDate() {
    return publishStartDate;
  }
  
  public Date getPublishEndDate() {
    return publishEndDate;
  }
  
  public Date getLastViewedDate() {
    return lastViewedDate;
  }
  
  public String getSubject() {
    return subject;
  }
  
  public String getBody() {
    return body;
  }
  
  public String getSender() {
    return sender;
  }
  
  public boolean isNew() {
    return isNew;
  }
  
  public boolean isConfirmOpen() {
    return confirmOpen;
  }
  
  public boolean isImportant() {
    return important;
  }
  
  public NewsCategory getCategory() {
    return NewsCategory.fromCode(categoryCode);
  }
  
  public void setCategory(NewsCategory category) {
    this.categoryCode = category.getNumVal();
  }
  
  public boolean isRead() {
    return isRead;
  }
  
  public void setRead(boolean read) {
    isRead = read;
  }
  
  public RealmList<NewsAttachment> getAttachments() {
    return attachments;
  }
}
