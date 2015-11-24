package im.point.torgash.virtualbrest;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.os.Build;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.code.rome.android.repackaged.com.sun.syndication.feed.synd.SyndEnclosure;
import com.google.code.rome.android.repackaged.com.sun.syndication.feed.synd.SyndEntry;
import com.google.code.rome.android.repackaged.com.sun.syndication.feed.synd.SyndFeed;
import com.google.code.rome.android.repackaged.com.sun.syndication.io.FeedException;
import com.google.code.rome.android.repackaged.com.sun.syndication.io.SyndFeedInput;
import com.google.code.rome.android.repackaged.com.sun.syndication.io.XmlReader;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Iterator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

/**
 * Created by torgash on 04.03.15.
 */
public class MyUpdateService extends Service {
    private static final String TAG = "VIRTBREST";
    SyndFeed feed = null;
    private Timer timer;
//    SharedPreferences prefs = getSharedPreferences("prefs", MODE_PRIVATE);
    Context ctx;
    SharedPreferences prefs;

    private TimerTask updateTask = new TimerTask() {


        @Override
        public void run() {
            Log.d(TAG, "Task scheduled");
            SyndFeedInput input = new SyndFeedInput();
            if (prefs.getLong("serviceUpdateFrequency", 0L) == 0L) {
                Log.d(TAG, "Stopping service as zero update frequency detected in settings");
                stopSelf();
            }
            BufferedReader bufferedReader = null;
            try {
                SyndFeedInput in = input;
                in.setXmlHealerOn(true);
                XmlReader reader = new XmlReader(new URL("http://virtualbrest.by/rss/newspda.php"));

                bufferedReader = new BufferedReader(reader);
                StringBuilder responseBuilder = new StringBuilder();
                char[] buff = new char[1024 * 512];
                int read;
                while ((read = bufferedReader.read(buff)) != -1) {
                    responseBuilder.append(buff, 0, read);
                    Log.d("DOWNLOAD", "скачано " + responseBuilder.length());
                }
                String tempRSS = responseBuilder.toString();


                while (tempRSS.endsWith("_")) {
                    tempRSS = tempRSS.substring(0, tempRSS.length() - 2);
                }
                Log.d(TAG, "String to parse is: " + tempRSS);
                ByteArrayInputStream is = new ByteArrayInputStream(tempRSS.getBytes());
                reader = new XmlReader(is);
                feed = in.build(reader);

                //as we've got some stuff, we should handle it to find new important things

                List<SyndEntry> entries = null;
                if (null != feed) {
                    entries = feed.getEntries();
                    if (null != entries) {

                        //now let's get a Cursor through singleton database instance
                        int news_counter = 15;

                        Iterator<SyndEntry> iterator = entries.listIterator();

                        while (iterator.hasNext()) {
                            final SyndEntry ent = iterator.next();
                            String link = ent.getLink();

                            List<SyndEnclosure> sE = (List<SyndEnclosure>) ent.getEnclosures();
                            String imageURL = "";
                            if (sE.size() != 0) {
                                imageURL = sE.get(0).getUrl();
                            } else {
                                String description = ent.getDescription().getValue();

                                Log.d(TAG, "string with link is: " + description);

                                org.jsoup.nodes.Document doc = null;
                                doc = Jsoup.parse(description);
                                List<Element> lst = doc.getElementsByTag("img");
                                if (lst.size() != 0) {

                                    imageURL = lst.get(0).absUrl("src");
                                }

                            }
                            Log.d(TAG, "image url is " + imageURL);
                            final MyRssItem tempItem = new MyRssItem(ent.getTitle(), ent.getDescription().getValue().length() > 120 ? ent.getDescription().getValue().substring(0, 120) + "..." : ent.getDescription().getValue(), link, imageURL, "http://virtualbrest.by/rss/newspda.php");

                            if (MySQLiteSingleton.makeNewDBRecord(MyUpdateService.this, "vb", tempItem, System.currentTimeMillis())) {


                                if (ent.getTitle().contains("-важно!")) {
                                    Intent intent = new Intent(MyUpdateService.this, MainActivity.class);
                                    intent.putExtra("link", link);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                    PendingIntent pIntent = PendingIntent.getActivity(MyUpdateService.this, 1020+news_counter, intent, 0);
                                    Notification.Builder builder =
                                            new Notification.Builder(MyUpdateService.this);
                                    builder.setSmallIcon(R.drawable.ic_launcher)
                                            .setTicker("Важная новость")
                                            .setWhen(System.currentTimeMillis())
                                            .setContentTitle("Важная новость")
                                            .setContentText(ent.getTitle().substring(0, ent.getTitle().length() - 8))
                                            .setDefaults(Notification.DEFAULT_SOUND)
                                            .setSound(
                                                    RingtoneManager.getDefaultUri(
                                                            RingtoneManager.TYPE_NOTIFICATION))
                                            .setVibrate(new long[]{1000, 1000, 1000, 1000, 1000})
                                            .setContentIntent(pIntent)
                                            .setLights(Color.RED, 0, 1);

                                    Notification notification = null;
                                    if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                                        notification = builder.getNotification();
                                    } else {
                                        notification = builder.build();
                                    }
                                    notification.flags |= Notification.FLAG_AUTO_CANCEL;
                                    NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

                                    notificationManager.notify(news_counter++, notification);


                                }
                            }
                            TimeUnit.MILLISECONDS.sleep(1);

                        }
                    }
                } else {

                }


            } catch (IOException e) {
                e.printStackTrace();

            } catch (FeedException e) {
                e.printStackTrace();

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Log.d(TAG, "Timer task doing work ");
            //Do some work here
        }
    };

    /**
     * Called by the system when the service is first created.  Do not call this method directly.
     */
    @Override
    public void onCreate() {
        super.onCreate();
        ctx = getApplicationContext();
        prefs = PreferenceManager.getDefaultSharedPreferences(ctx);

    }

    /**
     * Called by the system every time a client explicitly starts the service by calling
     * {@link android.content.Context#startService}, providing the arguments it supplied and a
     * unique integer token representing the start request.  Do not call this method directly.
     * <p/>
     * <p>For backwards compatibility, the default implementation calls
     * {@link #onStart} and returns either {@link #START_STICKY}
     * or {@link #START_STICKY_COMPATIBILITY}.
     * <p/>
     * <p>If you need your application to run on platform versions prior to API
     * level 5, you can use the following model to handle the older {@link #onStart}
     * callback in that case.  The <code>handleCommand</code> method is implemented by
     * you as appropriate:
     * <p/>
     * {@sample development/samples/ApiDemos/src/com/example/android/apis/app/ForegroundService.java
     * start_compatibility}
     * <p/>
     * <p class="caution">Note that the system calls this on your
     * service's main thread.  A service's main thread is the same
     * thread where UI operations take place for Activities running in the
     * same process.  You should always avoid stalling the main
     * thread's event loop.  When doing long-running operations,
     * network calls, or heavy disk I/O, you should kick off a new
     * thread, or use {@link android.os.AsyncTask}.</p>
     *
     * @param intent  The Intent supplied to {@link android.content.Context#startService},
     *                as given.  This may be null if the service is being restarted after
     *                its process has gone away, and it had previously returned anything
     *                except {@link #START_STICKY_COMPATIBILITY}.
     * @param flags   Additional data about this start request.  Currently either
     *                0, {@link #START_FLAG_REDELIVERY}, or {@link #START_FLAG_RETRY}.
     * @param startId A unique integer representing this specific request to
     *                start.  Use with {@link #stopSelfResult(int)}.
     * @return The return value indicates what semantics the system should
     * use for the service's current started state.  It may be one of the
     * constants associated with the {@link #START_CONTINUATION_MASK} bits.
     * @see #stopSelfResult(int)
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        timer = new Timer("UpdateTimer");
        if(prefs.getLong("serviceUpdateFrequency", 0L) == 0l) {
            Log.d(TAG, "Service stopped due to zero update frequency set");
            stopSelf();
        }
        else {
            try {

                timer.scheduleAtFixedRate(updateTask, 60000L, prefs.getLong("serviceUpdateFrequency", 0L));
                Log.d(TAG, "Service started");
                return Service.START_STICKY;
            } catch (IllegalStateException e) {
                e.printStackTrace();

            }
        }

        return Service.START_STICKY;
    }

    /**
     * Return the communication channel to the service.  May return null if
     * clients can not bind to the service.  The returned
     * {@link android.os.IBinder} is usually for a complex interface
     * that has been <a href="{@docRoot}guide/components/aidl.html">described using
     * aidl</a>.
     * <p/>
     * <p><em>Note that unlike other application components, calls on to the
     * IBinder interface returned here may not happen on the main thread
     * of the process</em>.  More information about the main thread can be found in
     * <a href="{@docRoot}guide/topics/fundamentals/processes-and-threads.html">Processes and
     * Threads</a>.</p>
     *
     * @param intent The Intent that was used to bind to this service,
     *               as given to {@link android.content.Context#bindService
     *               Context.bindService}.  Note that any extras that were included with
     *               the Intent at that point will <em>not</em> be seen here.
     * @return Return an IBinder through which clients can call on to the
     * service.
     */
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

}
