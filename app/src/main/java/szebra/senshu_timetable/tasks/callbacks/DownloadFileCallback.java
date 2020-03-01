package szebra.senshu_timetable.tasks.callbacks;

/**
 * Created by s-zebra on 3/2/20.
 */
public interface DownloadFileCallback {
  void onDownloadCompleted(Throwable exception);
}
