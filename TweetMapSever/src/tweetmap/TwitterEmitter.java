package tweetmap;
import java.io.IOException;
import java.text.SimpleDateFormat;

import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import org.json.JSONException;
import org.json.JSONObject;

import tweetmap.DynamoManager.Twitter;

@ServerEndpoint("/TwitterEmitter")
public class TwitterEmitter {
	
	DynamoManager dynamoMngr = new DynamoManager();
	private String lastGetTime;
	private final long initGetTweetPeriod = 1L*24L*60L*60L*1000L;
	
	private String timeToStr(long timeStamp) {
		SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        dateFormatter.setTimeZone(TimeZone.getTimeZone("GMT-4"));
        return dateFormatter.format(timeStamp);
	}
	
	private String getCurrentTime() {
		return timeToStr((new Date()).getTime());
	}
	
	public TwitterEmitter() {
        lastGetTime = getCurrentTime();
	}
	
	/**
     * @throws JSONException 
	 * @throws IOException 
	 * @OnOpen allows us to intercept the creation of a new session.
     * The session class allows us to send data to the user.
     * In the method onOpen, we'll let the user know that the handshake was 
     * successful.
     */
	
	private void sendTweetMessages(List<Twitter> tweets, String operationName, Session session) throws JSONException, IOException {
		session.getBasicRemote().sendText("send to you");
		for (Twitter tweet : tweets) {
			JSONObject tweetMessage = new JSONObject(),
					tweetLoc = new JSONObject();
			tweetLoc.put("lon", tweet.getLon());
			tweetLoc.put("lat", tweet.getLat());
			tweetLoc.put("keyword", tweet.getKeyword());
			tweetMessage.put("operation", operationName);
			tweetMessage.put("tweet", tweetLoc);
			session.getBasicRemote().sendText(tweetMessage.toString());
		}
	} 
	
	private void doEndInit(Session session) throws JSONException, IOException {
		JSONObject endInitMessage = new JSONObject();
		endInitMessage.put("operation", "endInit");
		session.getBasicRemote().sendText(endInitMessage.toString());
	}
	
    @OnOpen
    public void onOpen(Session session) throws JSONException {
    	String startTime = timeToStr((new Date()).getTime() - initGetTweetPeriod),
    			endTime = getCurrentTime();
    	try {
    		List<Twitter> tweets =
    				DynamoManager.FindTweetsPostedWithinTimePeriod(DynamoManager.getSampleMapper(), startTime, endTime);
    		session.getBasicRemote().sendText("aaa");
    		sendTweetMessages(tweets, "initMap", session);
    		doEndInit(session);
    	} catch (Exception e) {
			System.out.println("problem when initialize tweet map");
		}
    }
 
    /**
     * When a user sends a message to the server, this method will intercept the message
     * and allow us to react to it. For now the message is read as a String.
     */
    @OnMessage
    public void onMessage(String message, Session session){
    	try {
    		String startTime = lastGetTime, endTime = getCurrentTime();
    		lastGetTime = endTime;
    		List<Twitter> tweets =
    				DynamoManager.FindTweetsPostedWithinTimePeriod(DynamoManager.getSampleMapper(), startTime, endTime);
    		sendTweetMessages(tweets, "newTweet", session);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
 
    /**
     * The user closes the connection.
     * 
     * Note: you can't send messages to the client from this method
     */
    @OnClose
    public void onClose(Session session){
        System.out.println("Session " +session.getId()+" has ended");
    }

}
