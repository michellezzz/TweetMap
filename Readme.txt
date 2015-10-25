Tweet Map

This is a Java Application that posts tweets with geomatric location to Google Heat Map. The application is deployed on Amazon Elastic Bean for scalability concern.

-------------------
Team member:

Yuzhe Shen (ys2821)
Xingying Liu (xl2493)

--------------------
How to run:

You can see our demo here: 
http://tweetenv-zezmgp4xpp.elasticbeanstalk.com

To see what’s trending, click on "Categories" in the menu and select the category you like. Or you can just type the category name in "Keywords Filtering" to show only the relevant twitters on the map.

---------------------
Design scheme:

Back end
1) Tweet Collector
Tweet Collector implemented as a daemon process deployed on an individual instance. It continuously collect data from Twitter, analyse the Category of each tweets, and store them into Amazon DynamoDB. We set the time as the range key for future data extraction.

2) Web Server
Once the server is connected by a client, it will immediately extract all tweets from last week from the database, and send them to the client. We used web socket for communication and data transfer.

Front end
The front end send request to the server once it's launched and got the Tweets data shortly after to plot on Google heat map.
When the websocket is first connected, it will request the server for a burst of tweets from previous 24 hours to initialize the heat map. After that it will 
query the server periodically for new tweets.
Each keyword is implemented with an individual heat map which is designed for filtering.

We deployed the application on Amazon Elastic Bean Programmatically.
