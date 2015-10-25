<!DOCTYPE html>
<html lang="en">
  <head>
    <style>
      #map-canvas {
        height: 100%;
        margin: 10px;
        padding: 10px
      }
    </style>

    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="initial-scale=1,user-scalable=no,maximum-scale=1,width=device-width">
    <meta name="mobile-web-app-capable" content="yes">
    <meta name="apple-mobile-web-app-capable" content="yes">
    <meta name="theme-color" content="#000000">
    <meta name="description" content="">
    <meta name="author" content="">
    <title>Tweet Map</title>

    <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.5/css/bootstrap.min.css">
    <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/font-awesome/4.4.0/css/font-awesome.min.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/leaflet/0.7.5/leaflet.css">
    <link rel="stylesheet" href="https://api.tiles.mapbox.com/mapbox.js/plugins/leaflet-markercluster/v0.4.0/MarkerCluster.css">
    <link rel="stylesheet" href="https://api.tiles.mapbox.com/mapbox.js/plugins/leaflet-markercluster/v0.4.0/MarkerCluster.Default.css">
    <link rel="stylesheet" href="https://api.tiles.mapbox.com/mapbox.js/plugins/leaflet-locatecontrol/v0.43.0/L.Control.Locate.css">
    <link rel="stylesheet" href="assets/leaflet-groupedlayercontrol/leaflet.groupedlayercontrol.css">
    <link rel="stylesheet" href="assets/css/app.css">

    <link rel="apple-touch-icon" sizes="76x76" href="assets/img/favicon-76.png">
    <link rel="apple-touch-icon" sizes="120x120" href="assets/img/favicon-120.png">
    <link rel="apple-touch-icon" sizes="152x152" href="assets/img/favicon-152.png">
    <link rel="icon" sizes="196x196" href="assets/img/favicon-196.png">
    <link rel="icon" type="image/x-icon" href="assets/img/favicon.ico">
  </head>

  <body onload="initializeAll()">
    <div class="navbar navbar-inverse navbar-fixed-top" role="navigation">
      <div class="container-fluid">
        <div class="navbar-header">
          <div class="navbar-icon-container">
            <a href="#" class="navbar-icon pull-right visible-xs" id="nav-btn"><i class="fa fa-bars fa-lg white"></i></a>
            <a href="#" class="navbar-icon pull-right visible-xs" id="sidebar-toggle-btn"><i class="fa fa-search fa-lg white"></i></a>
          </div>
          <a class="navbar-brand" href="#">Tweet Map</a>
        </div>
        <div class="navbar-collapse collapse">
          <ul class="nav navbar-nav">
            <li class="dropdown">
                <a class="dropdown-toggle" id="downloadDrop" href="#" role="button" data-toggle="dropdown"><i class="fa fa-globe white"></i>&nbsp;&nbsp;Categories <b class="caret"></b></a>
                <ul class="dropdown-menu">
                  <li onclick="doFilt('defaultMap')"><a target="_blank" data-toggle="collapse" data-target=".navbar-collapse.in"><i class="fa fa-globe white"></i>&nbsp;&nbsp;all</a></li>
                  <li onclick="doFilt('sports')"><a target="_blank" data-toggle="collapse" data-target=".navbar-collapse.in"><i class="fa fa-globe white"></i>&nbsp;&nbsp;sports</a></li>
                  <li onclick="doFilt('entertainment')"><a target="_blank" data-toggle="collapse" data-target=".navbar-collapse.in"><i class="fa fa-globe white"></i>&nbsp;&nbsp;entertainment</a></li>
                  <li onclick="doFilt('technology')"><a target="_blank" data-toggle="collapse" data-target=".navbar-collapse.in"><i class="fa fa-globe white"></i>&nbsp;&nbsp;technology</a></li>
                </ul>
            </li>
            
            <li class="hidden-xs"><a href="#" data-toggle="collapse" data-target=".navbar-collapse.in" id="list-btn"><i class="fa fa-list white"></i>&nbsp;&nbsp;Show Filter</a></li>
          </ul>
        </div><!--/.navbar-collapse -->
      </div>
    </div>

    <div id="container">
      <div id="sidebar">
        <div class="sidebar-wrapper">
          <div class="panel panel-default" id="features">
            <div class="panel-heading">
              <h3 class="panel-title">Keywords Filtering
              <button type="button" class="btn btn-xs btn-default pull-right" id="sidebar-hide-btn"><i class="fa fa-chevron-left"></i></button></h3>
            </div>
            <div class="panel-body">
              <div class="row">
                <div class="col-xs-8 col-md-8">
                  <input id="messageinput" type="text" class="form-control search" placeholder="Filter" />
                </div>
                <div class="col-xs-4 col-md-4">
                  <button type="button" class="btn btn-primary pull-right sort" data-sort="feature-name" id="sort-btn" onclick="filtByInput()"><i class="fa fa-sort"></i>&nbsp;&nbsp;Filt</button>
                </div>
              </div>
            </div>
            <div class="sidebar-table">
              <table class="table table-hover" id="feature-list">
                <thead class="hidden">
                  <tr>
                    <th>Icon</th>
                  <tr>
                  <tr>
                    <th>Name</th>
                  <tr>
                  <tr>
                    <th>Chevron</th>
                  <tr>
                </thead>
                <tbody class="list"></tbody>
              </table>
            </div>
          </div>
        </div>
      </div>
      <div id="map-canvas"></div>
    </div>
    


    <script src="https://code.jquery.com/jquery-2.1.4.min.js"></script>
    <script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.5/js/bootstrap.min.js"></script>
    <script src="https://cdnjs.cloudflare.com/ajax/libs/typeahead.js/0.10.5/typeahead.bundle.min.js"></script>
    <script src="https://cdnjs.cloudflare.com/ajax/libs/handlebars.js/3.0.3/handlebars.min.js"></script>
    <script src="https://cdnjs.cloudflare.com/ajax/libs/list.js/1.1.1/list.min.js"></script>
    <script src="https://cdnjs.cloudflare.com/ajax/libs/leaflet/0.7.5/leaflet.js"></script>
    <script src="https://api.tiles.mapbox.com/mapbox.js/plugins/leaflet-markercluster/v0.4.0/leaflet.markercluster.js"></script>
    <script src="https://api.tiles.mapbox.com/mapbox.js/plugins/leaflet-locatecontrol/v0.43.0/L.Control.Locate.min.js"></script>
    <script src="assets/leaflet-groupedlayercontrol/leaflet.groupedlayercontrol.js"></script>
    <script src="assets/js/app.js"></script>
    <script src="https://maps.googleapis.com/maps/api/js?v=3.exp&signed_in=true&libraries=visualization"></script>
  <script type="text/javascript">
    var webSocket;
    var messages = document.getElementById("messages");
    //var serverURL = "ws://localhost:8080/TestTweetMap/TwitterEmitter";
    //var serverURL = "ws://ec2-52-88-88-242.us-west-2.compute.amazonaws.com:8080/TestTweetMap/TwitterEmitter";
    var serverURL = "ws://ec2-52-88-186-106.us-west-2.compute.amazonaws.com:8080/TwitterEmitter";
    function openSocket() {
      if (webSocket !== undefined
          && webSocket.readyState !== WebSocket.CLOSED) {
        writeResponse("WebSocket is already opened.");
        return;
      }
      webSocket = new WebSocket(serverURL);
      webSocket.onopen = function(event) {
        if (event.data === undefined)
          return;
        writeResponse(JSON.stringify(event));
      };

      webSocket.onmessage = function(event) {
        resolveMessage(event.data);
      };

      webSocket.onclose = function(event) {
        writeResponse(JSON.stringify(event));
        writeResponse("Connection closed");
      };
    }

    function resolveMessage(messageStr) {
      messageJson = JSON.parse(messageStr);
      operation = messageJson.operation;
      if (operation == "initMap")
        markTweet(messageJson.tweet);
      else if (operation == "endInit")
        pullTweets();
      else if (operation == "newTweet")
        markTweet(messageJson.tweet);
    }
  
    function send() {
      var text = document.getElementById("messageinput").value;
      webSocket.send(text);
    }
  
    function closeSocket() {
      nullMap.setMap(map);
      heatmap.setMap(null);
    }
  
    function writeResponse(text) {
      messages.innerHTML += "<br/>" + text;
    }
    
    var map, heatMap, liveTweets;
    
    var keywords = ["defaultMap", "sports", "entertainment", "technology"];
    
    var mapDict = new Object();
    
    function initializeMapDict() {
      for (i in keywords) {
        mapDict[keywords[i]] = new Object();
        mapDict[keywords[i]].mvcArray = new google.maps.MVCArray();
        mapDict[keywords[i]].heatMap = new google.maps.visualization.HeatmapLayer({
          data: mapDict[keywords[i]].mvcArray
        });
      }
      mapDict["defaultMap"].heatMap.setMap(map);
    }
    
    function initializeMap() {
      var myLatlng = new google.maps.LatLng(-22.363882, 111.044922);
      var mapOptions = {
        zoom : 1,
        center : myLatlng,
        mapTypeId: google.maps.MapTypeId.SATELLITE
      }
      map = new google.maps.Map(document.getElementById('map-canvas'), mapOptions);
      initializeMapDict();
    }
    
    google.maps.event.addDomListener(window, 'load', initializeMap);
    
    function clearAllheatMap() {
      for (keyword in mapDict)
        mapDict[keyword].heatMap.setMap(null);
    }
    
    function doFilt(keyword) {
      if (mapDict[keyword] === undefined)
        return;
      mapDict[keyword].heatMap.setMap(map);
      for (key in mapDict)
        if (key != keyword)
          mapDict[key].heatMap.setMap(null);
    }
    
    function filterKeyword() {
      var keyword = document.getElementById("filter").value;
      doFilt(keyword);
    }
    
    function filtByInput() {
      var keyword = document.getElementById("messageinput").value;
      if (mapDict[keyword] !== undefined)
        doFilt(keyword);
    }
    
    function initializeAll() {
      setTimeout(openSocket,2000);
    }
    
    function markTweet(tweet) {
      
      lon = String(tweet.lon);
      lat = String(tweet.lat);
      keyword = String(tweet.keyword);
      var myLatlng = new google.maps.LatLng(parseFloat(lat), parseFloat(lon));
  
      var mapOptions = {
        zoom : 4,
        center : myLatlng
      }
      mapDict["defaultMap"].mvcArray.push(myLatlng);
      mapDict[keyword].mvcArray.push(myLatlng);
    }
    
    function pullTweets() {
      send();
      setTimeout(pullTweets,1000);
    }
  </script>
  </body>
</html>
