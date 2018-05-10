<%--
  Created by IntelliJ IDEA.
  User: zsl
  Date: 2018/5/1
  Time: 19:45
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<link rel="stylesheet" href="css/bootstrap.min.css">
<link rel="stylesheet" href="css/myStyle.css">
<head>
    <meta name="viewport" content="initial-scale=1.0, user-scalable=no">
    <meta charset="utf-8">
    <title>Google Map Visualization</title>
    <style>
        /* Always set the map height explicitly to define the size of the div
         * element that contains the map. */
        #map {
            height: 100%;
        }

        /* Optional: Makes the sample page fill the window. */
        html, body {
            height: 100%;
            margin: 0;
            padding: 0;
        }
    </style>
</head>
<body>

<nav nav class="navbar navbar-expand-sm bg-primary navbar-dark">
    <ul class="navbar-nav">
        <li class="nav-item ">
            <a class="nav-link" href="index.jsp">Home</a>
        </li>
        <li class="nav-item ">
            <a class="nav-link" href="Sentiment.jsp">Twitter Analysis</a>
        </li>
        <li class="nav-item active">
            <a class="nav-link" href="Map.jsp">Google Map</a>
        </li>
    </ul>
    <div class="dropdown" style="float:right">
        <button class="btn btn-primary dropdown-toggle" type="button" data-toggle="dropdown">Scenarios
            <span class="caret"></span></button>
        <ul class="dropdown-menu">
            <a id="food_btn" class="dropdown-item">Food</a>
            <a id="sent_btn" class="dropdown-item">Sentiment</a>
            <a id="veggies_btn" class="dropdown-item">Veggies</a>
            <a id="beauty_btn" class="dropdown-item">Beauty and Makeup </a>
        </ul>
    </div>


</nav>
<div class="div-color" style="width: 100%;height:90%">
    <div id="map"></div>
    <div>

        <!-- Optional JavaScript -->
        <!-- jQuery first, then Popper.js, then Bootstrap JS -->
        <script src="js/jquery-3.3.1.min.js"></script>
        <script src="js/popper.min.js"></script>
        <script src="js/bootstrap.min.js"></script>
        <script src="js/palette.js"></script>
        <script type="text/javascript"
                src="https://maps.googleapis.com/maps/api/js?libraries=visualization"></script>

        <script>
            //create a global variable that will point to the tooltip in the DOM
            var tipObj = null;

            //offset along x and y in px
            var offset = {
                x: 20,
                y: 20
            };

            var map;

            function initMap() {
                map = new google.maps.Map(document.getElementById('map'), {
                    zoom: 12.5,
                    center: {lat: -37.8, lng: 144.96587902}
                });

                // NOTE: This uses cross-domain XHR, and may not work on older browsers.
                //map.data.loadGeoJson(

                map.data.loadGeoJson('/data/food.json');
                setStyle()
                addListener()
            }
            var infoWindow=new google.maps.InfoWindow;
            function showText(event){
                var titleText = event.feature.getProperty('num');
                infoWindow.setContent(toString(titleText));
                console.log(event.latLng)
                infoWindow.setPosition(event.latLng);
                infoWindow.open(map);
            }
            function mouseOverDataItem(mouseEvent) {
/*                var titleText = mouseEvent.feature.getProperty('tweets');
                console.log(mouseEvent.feature)
                if (titleText) {
                    map.getDiv().setAttribute('title', titleText);
                }*/
                var titleText = mouseEvent.feature.getProperty('tweets');
                injectTooltip(mouseEvent,titleText);
            }

            function mouseOutOfDataItem(mouseEvent) {
                /*map.getDiv().removeAttribute('title');*/
                deleteTooltip(mouseEvent)
            }
            function addListener(){
                map.data.addListener('mouseover', mouseOverDataItem);
                map.data.addListener('mousemove', function(e){
                    moveTooltip(e);
                });
                map.data.addListener('mouseout', mouseOutOfDataItem);
            }

            function setStyle(){
                map.data.setStyle(function (feature) {
                    colors=palette('tol', 10).map(function (hex) {
                        return '#' + hex;
                    })
                    var num = feature.getProperty('num');
                    var color=colors[num%10]
                    return {
                        fillColor: color,
                        strokeWeight: 1,
                        strokeColor:'red'
                    };
                });
            }
            function refreshMap(){
                map.data.forEach(function (feature) {
                    map.data.remove(feature);
                });
            }
            $("#food_btn").click(function () {
                refreshMap()
                map.data.loadGeoJson('/data/food.json');
                setStyle()
            })
            $("#sent_btn").click(function () {
                refreshMap()
                map.data.loadGeoJson('/data/sentiment.json');
                setStyle()
            })
            $("#veggies_btn").click(function () {
                refreshMap()
                map.data.loadGeoJson('/data/veggies.json');
                setStyle()
            })
            $("#beauty_btn").click(function () {
                refreshMap()
                map.data.loadGeoJson('/data/beauty.json');
                setStyle()
            })




            var coordPropName = null;

            function injectTooltip(event,data){
                if(!tipObj && event){
                    //create the tooltip object
                    tipObj = document.createElement("div");
                    tipObj.style.width = 'auto';
                    tipObj.style.height = 'auto';
                    tipObj.style.background = "#1d55ad";
                    tipObj.style.color = "#FFFFFF";
                    tipObj.style.paddingTop = "5px";
                    tipObj.style.paddingBottom = "5px";
                    tipObj.style.paddingLeft="10px"
                    tipObj.style.paddingRight="10px"
                    tipObj.style.borderRadius="6px"
                    tipObj.innerHTML = data;

                    //fix for the version issue
                    eventPropNames = Object.keys(event);
                    if(!coordPropName){
                        //discover the name of the prop with MouseEvent
                        for(var i in eventPropNames){
                            var name = eventPropNames[i];
                            if(event[name] instanceof MouseEvent){
                                coordPropName = name;
                                console.log("--> mouse event in", coordPropName)
                                break;
                            }
                        }
                    }

                    if(coordPropName){
                        //position it
                        tipObj.style.position = "fixed";
                        tipObj.style.top = event[coordPropName].clientY + window.scrollY + offset.y + "px";
                        tipObj.style.left = event[coordPropName].clientX + window.scrollX + offset.x + "px";

                        //add it to the body
                        document.body.appendChild(tipObj);
                    }
                }
            }

            /********************************************************************
             * moveTooltip(e)
             * update the position of the tooltip based on the event data
             ********************************************************************/
            function moveTooltip(event){
                if(tipObj && event && coordPropName){
                    //position it
                    tipObj.style.top = event[coordPropName].clientY + window.scrollY + offset.y + "px";
                    tipObj.style.left = event[coordPropName].clientX + window.scrollX + offset.x + "px";
                }
            }

            /********************************************************************
             * deleteTooltip(e)
             * delete the tooltip if it exists in the DOM
             ********************************************************************/
            function deleteTooltip(event){
                if(tipObj){
                    //delete the tooltip if it exists in the DOM
                    document.body.removeChild(tipObj);
                    tipObj = null;
                }
            }

        </script>

       <%-- <script>
            var map;
            var results;
            function initMap() {
                map = new google.maps.Map(document.getElementById('map'), {
                    zoom: 12,
                    center: {lat: -37.805427, lng: 145},
                    mapTypeId: 'terrain'
                });

                $.ajax({
                    async: false,
                    type: "get",
                    url: "/data/melbourne.json",
                    dataType: "json",
                    success: function (data) {
                        results = data
                        console.log("map data load success")
                    },
                    error: function () {

                    }
                });
                console.log(results.features[0].geometry.coordinates[0][0].length)
                var heatmapData = [];
                for (var i = 0; i < results.features[0].geometry.coordinates[0][0].length; i++) {
                    var coords = results.features[0].geometry.coordinates[0][0][i];
                    var latLng = new google.maps.LatLng(coords[1], coords[0]);
                    heatmapData.push(latLng);
                }
                var heatmap = new google.maps.visualization.HeatmapLayer({
                    data: heatmapData,
                    dissipating: false,
                    map: map
                });
            }


            function eqfeed_callback(results) {

            }
        </script>--%>

        <script async defer
                src="https://maps.googleapis.com/maps/api/js?key=AIzaSyA4vd2Y6Q4XvxmddSV0pPngh32pBzPH2s8&libraries=visualization&callback=initMap">
        </script>

</body>
</html>
