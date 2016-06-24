/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tweetmining;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.Clock;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import twitter4j.*;
import twitter4j.conf.ConfigurationBuilder;

/**
 *
 * @author nacheteam
 */
public class MiningFunctions {
  
  public final static String ConsumerKey = TwitterPersonalData.ConsumerKey;
  public final static String ConsumerSecret = TwitterPersonalData.ConsumerSecret;
  public final static String AccessToken = TwitterPersonalData.AccessToken;
  public final static String AccessTokenSecret = TwitterPersonalData.AccessTokenSecret;
  
  private final String fichero1 = TwitterPersonalData.fichero1;
  private final String fichero2 = TwitterPersonalData.fichero2;
  private final String fichero3 = TwitterPersonalData.fichero3;
  private TwitterFactory tf;
  private twitter4j.Twitter twitter;
  public static PrintWriter pw;
  public static PrintWriter pw2;
  public static PrintWriter pw3;
  private TwitterStream twitterStream;
  private StatusListener listener;
  public static long cont=0;
  
  /**
   * This method creates an instance of Miningfunctions based on your TwitterApss credentials.
   * You should as well check filepaths to put yours.
   * 
   * @throws FileNotFoundException 
   */
  
  public MiningFunctions() throws FileNotFoundException, IOException
  {
    ConfigurationBuilder configurationBuilder = new ConfigurationBuilder();
    configurationBuilder.setDebugEnabled(true)
            .setOAuthConsumerKey(ConsumerKey)
            .setOAuthConsumerSecret(ConsumerSecret)
            .setOAuthAccessToken(AccessToken)
            .setOAuthAccessTokenSecret(AccessTokenSecret);
    
    tf = new TwitterFactory(configurationBuilder.build());
    
    twitter = tf.getInstance();
    
    File f = new File(fichero1);
    File f2 = new File(fichero2);
    File f3 = new File(fichero3);
    if(!f.exists())
      f.createNewFile();
    if(!f2.exists())
      f2.createNewFile();
    if(!f3.exists())
      f3.createNewFile();
    pw = new PrintWriter(new FileOutputStream(f, true));
    pw2 = new PrintWriter(new FileOutputStream(f2, true));
    pw3 = new PrintWriter(new FileOutputStream(f3, true));
    
    listener = new StatusListener(){
        public void onStatus(Status status) {
          if(status.getGeoLocation()!=null)
          {
            cont++;
            System.out.println("Loc not null----" + cont);
            GeoLocation loc = status.getGeoLocation();
            pw.println(String.valueOf(loc.getLatitude()) + ";" + String.valueOf(loc.getLongitude()) + ";" + status.getUser().getName());
            System.out.println(loc.getLatitude() + " " + loc.getLongitude() + " " + status.getUser().getName());
          }
          pw2.println(status.getUser().getName() + "--->" + status.getText());
          try {
            CloseWriteAndOpen();
          } catch (FileNotFoundException ex) {
            java.util.logging.Logger.getLogger(MiningFunctions.class.getName()).log(Level.SEVERE, null, ex);
          }
        }
        public void onDeletionNotice(StatusDeletionNotice statusDeletionNotice) {}
        public void onTrackLimitationNotice(int numberOfLimitedStatuses) {}
        public void onException(Exception ex) {
            ex.printStackTrace();
        }

      @Override
      public void onScrubGeo(long l, long l1) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
      }

      @Override
      public void onStallWarning(StallWarning sw) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
      }
    };
    configurationBuilder = new ConfigurationBuilder();
    configurationBuilder.setDebugEnabled(true)
            .setOAuthConsumerKey(ConsumerKey)
            .setOAuthConsumerSecret(ConsumerSecret)
            .setOAuthAccessToken(AccessToken)
            .setOAuthAccessTokenSecret(AccessTokenSecret);
    twitterStream = new TwitterStreamFactory(configurationBuilder.build()).getInstance();
  }
  
  /**
   * This method mines data from a particular twitter user.
   * @param user String which has the name of a Twitter user without @ 
   */
  public void MineFromUser(String user)
  {       
    int pageno = 1;
    List<Status> statuses = new ArrayList();

    while (true) {

      try {

        int size = statuses.size(); 
        Paging page = new Paging(pageno++, 100);
        statuses.addAll(twitter.getUserTimeline(user, page));
        if (statuses.size() == size)
          break;
      }
      catch(TwitterException e) {

        e.printStackTrace();
      }
    }
    
    System.out.println(statuses.size());
    
    for(Status st : statuses)
      {
        GeoLocation loc = st.getGeoLocation();
        if(loc!=null)
        {
          System.out.println("Loc not null");
          Double lat = loc.getLatitude();
          Double lon = loc.getLongitude();
          pw.println(lat.toString() + ";" + lon.toString() + ";" + st.getUser().getName());
        }
        pw2.println(st.getUser().getName() + "-----" + st.getText());
      }
    
    pw.close();
    pw2.close();
  }  
  
  /**
   * This method mines data from youw twitter account based on the query string that you pass by
   * parameters.
   * @param query Query you want to place.
   * @throws FileNotFoundException
   * @throws TwitterException 
   */
  public void MineFromQuery(String query) throws FileNotFoundException, TwitterException
  {      
    Query q = new Query(query);
    QueryResult r;
    q.count(100);
    
    do
    {
      r = twitter.search(q);
      List<Status> statuses = r.getTweets();
      for(Status st : statuses)
      {
        GeoLocation loc = st.getGeoLocation();
        if(loc!=null)
        {
          System.out.println("Loc not null");
          Double lat = loc.getLatitude();
          Double lon = loc.getLongitude();
          pw.println(lat.toString() + ";" + lon.toString() + ";" + st.getUser().getName());
        }
      }
      q = r.nextQuery();
    }while(r.hasNext());
  }
  /**
   * Mines data from stream based on the Listener created in the constructor.
   */  
  public void MineFromStream()
  {
    FilterQuery fq = new FilterQuery();
    twitterStream.addListener(listener);
    
    twitterStream.sample();
  }
  /**
   * Private helping function in the mining process of a stream data.
   * @throws FileNotFoundException 
   */
  private void CloseWriteAndOpen() throws FileNotFoundException
  {
    pw.close();
    pw2.close();
    pw3.close();
    File f = new File(fichero1);
    File f2 = new File(fichero2);
    File f3 = new File(fichero3);
    pw = new PrintWriter(new FileOutputStream(f, true));
    pw2 = new PrintWriter(new FileOutputStream(f2, true));
    pw3 = new PrintWriter(new FileOutputStream(f3, true));
  }
  
  public ArrayList<String> CreateFriendsList(String user) throws TwitterException
  {
    ArrayList<String> friends = new ArrayList<String>();
    long cursor = -1;
    IDs ids;
    
    do
    {
      ids = twitter.getFollowersIDs(user, cursor);
      for(long id : ids.getIDs())
      {
        friends.add(twitter.showUser(id).getName());
      }
    }while ((cursor=ids.getNextCursor())!=0);
      
    return friends;
  }
  
}
