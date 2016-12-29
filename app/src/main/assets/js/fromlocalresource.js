// implementation of AR-Experience (aka "World")
var World = {
	// you may request new data from server periodically, however: in this sample data is only requested once
	isRequestingData: false,

	// true once data was fetched
	initiallyLoadedData: false,

	// different POI-Marker assets
	markerDrawable_idle: null,
	markerDrawable_selected: null,
	markerDrawable_directionIndicator: null,

	// list of AR.GeoObjects that are currently shown in the scene / World
	markerList: [],

	// The last selected marker
	currentMarker: null,
	locationUpdateCounter: 0,
    	updatePlacemarkDistancesEveryXLocationUpdates: 10,

	// called to inject new POI data
	loadPoisFromJsonData: function loadPoisFromJsonDataFn(poiData) {

	// destroys all existing AR-Objects (markers & radar)
    		AR.context.destroyAll();


	// show radar & set click-listener
    		PoiRadar.show();
    		$('#radarContainer').unbind('click');
    		$("#radarContainer").click(PoiRadar.clickedRadar);


		// empty list of visible markers
		World.markerList = [];

		// start loading marker assets
		World.markerDrawable_idle = new AR.ImageResource("assets/marker_idle.png");
		World.markerDrawable_selected = new AR.ImageResource("assets/marker_selected.png");
		World.markerDrawable_directionIndicator = new AR.ImageResource("assets/indi.png");

		// loop through POI-information and create an AR.GeoObject (=Marker) per POI
		for (var currentPlaceNr = 0; currentPlaceNr < poiData.length; currentPlaceNr++) {
			var singlePoi = {
				"id": poiData[currentPlaceNr].id,
				"latitude": parseFloat(poiData[currentPlaceNr].latitude),
				"longitude": parseFloat(poiData[currentPlaceNr].longitude),
				"altitude": parseFloat(poiData[currentPlaceNr].altitude),
				"title": poiData[currentPlaceNr].name,
				"description": poiData[currentPlaceNr].description
			};

			World.markerList.push(new Marker(singlePoi));
		}

		// updates distance information of all placemarks
        		World.updateDistanceToUserValues();

		World.updateStatusMessage(currentPlaceNr + ' places loaded');

		// set distance slider to 100%
        		$("#panel-distance-range").val(100);
        		$("#panel-distance-range").slider("refresh");
	},

	// sets/updates distances of all makers so they are available way faster than calling (time-consuming) distanceToUser() method all the time
    	updateDistanceToUserValues: function updateDistanceToUserValuesFn() {
    		for (var i = 0; i < World.markerList.length; i++) {
    			World.markerList[i].distanceToUser = World.markerList[i].markerObject.locations[0].distanceToUser();
    		}
    	},

	// updates status message shown in small "i"-button aligned bottom center
	updateStatusMessage: function updateStatusMessageFn(message, isWarning) {

		var themeToUse = isWarning ? "e" : "c";
		var iconToUse = isWarning ? "alert" : "info";

		$("#status-message").html(message);
		$("#popupInfoButton").buttonMarkup({
			theme: themeToUse
		});
		$("#popupInfoButton").buttonMarkup({
			icon: iconToUse
		});
	},

	/*
    		It may make sense to display POI details in your native style.
    		In this sample a very simple native screen opens when user presses the 'More' button in HTML.
    		This demoes the interaction between JavaScript and native code.
    	*/
    	// user clicked "More" button in POI-detail panel -> fire event to open native screen
        	onPoiDetailMoreButtonClicked: function onPoiDetailMoreButtonClickedFn() {
        		var currentMarker = World.currentMarker;
        		var architectSdkUrl = "http://citycentre.com.np/";
        		/*
        			The urlListener of the native project intercepts this call and parses the arguments.
        			This is the only way to pass information from JavaSCript to your native code.
        			Ensure to properly encode and decode arguments.
        			Note: you must use 'document.location = "architectsdk://...' to pass information from JavaScript to native.
        			! This will cause an HTTP error if you didn't register a urlListener in native architectView !
        		*/
        		document.location = architectSdkUrl;
        	},



	// location updates, fired every time you call architectView.setLocation() in native environment
	locationChanged: function locationChangedFn(lat, lon, alt, acc) {

		// request data if not already present
		if (!World.initiallyLoadedData) {
			World.requestDataFromLocal(lat, lon);
			World.initiallyLoadedData = true;
		}
	},

	// fired when user pressed maker in cam
    	onMarkerSelected: function onMarkerSelectedFn(marker) {
    		World.currentMarker = marker;

    		/*
    			In this sample a POI detail panel appears when pressing a cam-marker (the blue box with title & description),
    			compare index.html in the sample's directory.
    		*/
    		// update panel values
    		$("#poi-detail-title").html(marker.poiData.title);
    		$("#poi-detail-description").html(marker.poiData.description);

    		// distance and altitude are measured in meters by the SDK. You may convert them to miles / feet if required.
    		var distanceToUserValue = (marker.distanceToUser > 9999999) ? ((marker.distanceToUser / 1000).toFixed(2) + " km") : (Math.round(marker.distanceToUser) + " m");

    		$("#poi-detail-distance").html(distanceToUserValue);

    		// show panel
    		$("#panel-poidetail").panel("open", 123);

    		$(".ui-panel-dismiss").unbind("mousedown");

    		// deselect AR-marker when user exits detail screen div.
    		$("#panel-poidetail").on("panelbeforeclose", function(event, ui) {
    			World.currentMarker.setDeselected(World.currentMarker);
    		});
    	},

	// screen was clicked but no geo-object was hit
	onScreenClick: function onScreenClickFn() {
		if (World.currentMarker) {
			World.currentMarker.setDeselected(World.currentMarker);
		}
	},



	// returns distance in meters of placemark with maxdistance * 1.1
    	getMaxDistance: function getMaxDistanceFn() {

    		// sort palces by distance so the first entry is the one with the maximum distance
    		World.markerList.sort(World.sortByDistanceSortingDescending);

    		// use distanceToUser to get max-distance
    		var maxDistanceMeters = World.markerList[0].distanceToUser;

    		// return maximum distance times some factor >1.0 so ther is some room left and small movements of user don't cause places far away to disappear
    		return maxDistanceMeters * 1.1;
    	},

    	// udpates values show in "range panel"
    	updateRangeValues: function updateRangeValuesFn() {

    		// get current slider value (0..100);
    		var slider_value = $("#panel-distance-range").val();

    		// max range relative to the maximum distance of all visible places
    		var maxRangeMeters = Math.round(World.getMaxDistance() * (slider_value / 100));

    		// range in meters including metric m/km
    		var maxRangeValue = (maxRangeMeters > 999) ? ((maxRangeMeters / 1000).toFixed(2) + " km") : (Math.round(maxRangeMeters) + " m");

    		// number of places within max-range
    		var placesInRange = World.getNumberOfVisiblePlacesInRange(maxRangeMeters);

    		// update UI labels accordingly
    		$("#panel-distance-value").html(maxRangeValue);
    		$("#panel-distance-places").html((placesInRange != 1) ? (placesInRange + " Places") : (placesInRange + " Place"));

    		// update culling distance, so only palces within given range are rendered
    		AR.context.scene.cullingDistance = Math.max(maxRangeMeters, 1);

    		// update radar's maxDistance so radius of radar is updated too
    		PoiRadar.setMaxDistance(Math.max(maxRangeMeters, 1));
    	},

    	// returns number of places with same or lower distance than given range
    	getNumberOfVisiblePlacesInRange: function getNumberOfVisiblePlacesInRangeFn(maxRangeMeters) {

    		// sort markers by distance
    		World.markerList.sort(World.sortByDistanceSorting);

    		// loop through list and stop once a placemark is out of range ( -> very basic implementation )
    		for (var i = 0; i < World.markerList.length; i++) {
    			if (World.markerList[i].distanceToUser > maxRangeMeters) {
    				return i;
    			}
    		};

    		// in case no placemark is out of range -> all are visible
    		return World.markerList.length;
    	},

    	handlePanelMovements: function handlePanelMovementsFn() {

            $("#panel-distance").on("panelclose", function(event, ui) {
                $("#radarContainer").addClass("radarContainer_left");
                $("#radarContainer").removeClass("radarContainer_right");
                PoiRadar.updatePosition();
            });

            $("#panel-distance").on("panelopen", function(event, ui) {
                $("#radarContainer").removeClass("radarContainer_left");
                $("#radarContainer").addClass("radarContainer_right");
                PoiRadar.updatePosition();
            });
        },

        // display range slider
        	showRange: function showRangeFn() {
        		if (World.markerList.length > 0) {

        			// update labels on every range movement
        			$('#panel-distance-range').change(function() {
        				World.updateRangeValues();
        			});

        			World.updateRangeValues();
        			World.handlePanelMovements();

        			// open panel
        			$("#panel-distance").trigger("updatelayout");
        			$("#panel-distance").panel("open", 1234);
        		} else {

        			// no places are visible, because the are not loaded yet
        			World.updateStatusMessage('No places available yet', true);
        		}
        	},

        	/*
            		You may need to reload POI information because of user movements or manually for various reasons.
            		In this example POIs are reloaded when user presses the refresh button.
            		The button is defined in index.html and calls World.reloadPlaces() on click.
            	*/

            	// reload places from content source
            	reloadPlaces: function reloadPlacesFn() {
                		if (!World.isRequestingData) {
                			if (!World.userLocation) {
                				World.requestDataFromLocal(World.userLocation.latitude, World.userLocation.longitude);
                			} else {
                				World.updateStatusMessage('Unknown user-location.', true);
                			}
                		} else {
                			World.updateStatusMessage('Already requesting places...', true);
                		}
                	},

	/*
		In case the data of your ARchitect World is static the content should be stored within the application.
		Create a JavaScript file (e.g. myJsonData.js) where a globally accessible variable is defined.
		Include the JavaScript in the ARchitect Worlds HTML by adding <script src="js/myJsonData.js"/> to make POI information available anywhere in your JavaScript.
	*/

	// request POI data
	requestDataFromLocal: function requestDataFromLocalFn(lat, lon) {

		var poisNearby = Helper.bringPlacesToUser(myJsonData, lat, lon);
		World.loadPoisFromJsonData(poisNearby);

		/*
		For demo purpose they are relocated randomly around the user using a 'Helper'-function.
		Comment out previous 2 lines and use the following line > instead < to use static values 1:1.
		*/

		World.loadPoisFromJsonData(myJsonData);
	}

};

var Helper = {

	/*
		For demo purpose only, this method takes poi data and a center point (latitude, longitude) to relocate the given places randomly around the user
	*/
	bringPlacesToUser: function bringPlacesToUserFn(poiData, latitude, longitude) {
		for (var i = 0; i < poiData.length; i++) {
			poiData[i].latitude = latitude + (Math.random() / 5 - 0.1);
			poiData[i].longitude = longitude + (Math.random() / 5 - 0.1);
			/*
			Note: setting altitude to '0'
			will cause places being shown below / above user,
			depending on the user 's GPS signal altitude.
				Using this contant will ignore any altitude information and always show the places on user-level altitude
			*/
			poiData[i].altitude = AR.CONST.UNKNOWN_ALTITUDE;
		}
		return poiData;
	}
}


/* forward locationChanges to custom function */
AR.context.onLocationChanged = World.locationChanged;


/* forward clicks in empty area to World */
AR.context.onScreenClick = World.onScreenClick;