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
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeoutException;
import twitter4j.GeoLocation;
import twitter4j.Paging;
import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.Status;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;

/**
 *
 * @author nacheteam
 */
public class TweetMining {

  /**
   * @param args the command line arguments
   */
  public static void main(String[] args) throws FileNotFoundException, TimeoutException, InterruptedException, IOException, TwitterException {
    // TODO code application logic here
    
    MiningFunctions miningFunctions = new MiningFunctions();
    ArrayList<String> friends = miningFunctions.CreateFriendsList(TwitterPersonalData.UserToMineFriends);
    System.out.println(friends.size());
    for(String name : friends)
    {
      System.out.println(name);
    }
    
  }
}
