# Georgian Bay Turtle Hospital (GBTH) Volunteer Tracking

## Description 
 
 We are trying to produce an Android app that helps Georgian Bay Turtle Hospital to record and track the locations of volunteers in the field. This solves two problems. Firstly, real time location tracking allows senior staff to know where their volunteers are in case there is an emergency. It also allows staff to assign the nearest team to a turtle sighting; turtle sightings are reported by community members to the Hospital's hotline. Secondly, keeping records of volunteer movements provides the GBTH with valuable insight on turtle wearabouts. Furthermore, GBTH can better visualize which areas have or have not been canvased so they can better distribute their manpower. 

## Key Features

 Current features for Deliverable 2:
 
  When the user opens the app, they can see their current location on the map. They can click on the marker representing their location and see a window open that displays their name and current status. Users can also see the locations of other people currently using their app. Clicking on their markers also displays their names and statuses. Furthermore, the markers are colored to correspond with the users' statuses for easy identification. The user can double tap the screen to zoom in, pinch to zoom out, and pan around. These features allow supervisors to know where their volunteers are while they work in case of emergency. The map allows dispatchers to view the closest team/volunteer to a reported turtle sighting. The marker coloring allows dispatchers to quickly scan the map for green markers to see which teams are available to respond.
  
  In the top right corner of the action bar, there are three menu options. The first option is Set Status. Here, the user is given a list of radio buttons with different status options that they can choose from. When they select and save their status, a toast pops up to confirm their action and their marker be updated to display their new status. The second option is Export. Here, the user can choose to export a data file containing their location history. This is a csv file with coordinates and timestamps that are recorded every 10 seconds while the app is in use. These are stored on the device's external SD card. The user can choose where to export the file, such as a Google Drive account. The third menu option is Settings. Here, the user can edit what name they want displayed on the map for others to see. They can also choose to allow the app to collection location data while the app is running in the background. Constantly collecting location data and allowing the user to access this data gives organizations detailed information about where volunteers have canvased and where turtle species live.
  
  Whenever the user loses internet connection, an alert dialog pops up informing the user. Detecting internet connection is necessary for uploading location data from the device's SD card to a database for long-term storage. The dialog feature is purely for UX.
  
 
## Instructions
#### How to run Android app
1. Prepare Android device (either physical device or emulator).
2. Download `app-debug.apk` on the Android device from https://github.com/csc301-winter-2020/team-project-17-georgian-bay-turtle-hospital/blob/master/deliverables/app-debug.apk.
3. Install `app-debug.apk` file.
4. Launch `GBTHVolunteerTracking` application.
 
 The first time you open the app, you will likely see a prompt asking permission to view your location. You must allow this permission for the app to function as intended. You will also see a prompt asking for permission to access your storage. You must allow this permission so that the app can write to external storage.
 
 Once you open the mobile app, you will see the main page is a map that shows your current location. No sign-in is required. In the top right corner of the action bar, there are three vertical dots. You can click it to view a drop down menu with three options. 
 
"Set Status" allows the user to change their current status by selecting the appropriate radio button and then clicking the Set Status button. A toast message pops up to confirm your status change. You will also notice that your map marker has changed color to correspond with your status. Each status has an assigned color for easy identification.
 
"Export" allows users to export their location data to a csv file. Clicking the grey "Export" button in the center of the screen will show a prompt with exporting options. You can choose how you would like your data exported.  

The "Setting" menu options allows users to change settings of the app. There are currently two settings. The first setting is display name. If you click on this panel, you will get a dialog prompt with text editing. Use your device's keyboard to type in your display name. Now, if you return to the main page by clicking the back arrow at the top left of your screen, and then select your marker on the map, you will see that your display name has changed. The second option under settings allows the app to collect location data in the background (this feature is not yet implemented).  

The app can also detect your internet connection status. If you turn off your Wifi, you will see a dialog alerting you of the lost connection. Note, if your device automatically switches to mobile data when there is no Wifi, you will need to turn off your data to see this feature. You can make this alert disappear by either clicking "OK" in the bottom right corner of the dialog prompt.  

The location data is saved to external storage in a file called ```location_history.txt``` (this file is not accessible to the user for privacy reasons). This file contains a history of the user's latitude, longitude and timestamp (in milliseconds since the epoch) that has been collected while the app is in use. The data points are taken every 20 seconds as indicated by the timestamps.

Lastly, the Android app receives data from https://gbthtracking.herokuapp.com/. REST APIs are listed in `restapi.md`.
 
 ## Development requirements
### Mobile Application ###
Requirements:
 * Android Studio
 * JDK 8 or Higher
 * An Android phone or an Android Virtual Device on the computer with Android API 29 or above (Android Studio provides an emulator).

1. Clone the repository.
2. Open Android Studio, select "Open Existing Project"
3. Select `src/android` from this repository.
4. Select `src/android/app` for the module.
5. Mark `src/android/app/java` as source root.
6. Generate your own Google Maps API key (https://developers.google.com/maps/documentation/javascript/get-api-key). Navigate to **res > values > google_maps_api.xml** and replace the Google Maps key string with your own API key.
7. If using your own device, connect phone to computer with USB cord. If you are using an emulator, create a new device using Android Studio's AVD Manager. In both cases, turn on Developer Mode (https://developer.android.com/studio/debug/dev-options).
8. If you do not have a physical SDcard, you may simulate external memory by installing Android's SDK `platform-tools` package (https://developer.android.com/studio/command-line/adb). While your device is plugged in, type `adb shell` in the command line followed by `sm set-virtual-disk true`. This will set up virtual external storage (Reference: https://www.xda-developers.com/virtual-sd-card-android-oreo/).
9. Run `MainActivity.java` and select the device you want to run on.

### Server ###
Requirements:
 * Docker v19.03 or higher

1. Clone the repository. 
2. Enter the directory at src/backend.
3. Run `docker-compose up --build -d`.
4. Server is run by default on `http://localhost:3000/`.
5. Once development is complete, run `docker-compose down` to stop and remove Docker container.
6. For more information on running Docker, run `docker-compose --help`.
7. Check `restapi.md` for REST APIs.
 
 ## Deployment and Github Workflow
For each feature being implmented, the person working on that feature created their own Github branch from the master branch. Once they finished coding and tested their code, they created a pull request from their own branch to the master branch with a description of changes. Another team member(s) would review the changes to the code and if they approve, merge the pull request. For each pull request or push to master, we used Github Actions to build the Android app and Node.js server to make sure new changes would not break the existing code. (Github Actions yml files are located in `.github/workflows/`) The use of branches and CI preserves the integrity of master branch.  

For deployment, our partner will provide Compute Canada account but has not yet gotten approval from the site. For demo purpose, we deployed back-end server on Heroku https://gbthtracking.herokuapp.com/. We used Github Actions to automate deployment. We made a release_backend branch and whenever push is made to this branch, `.github/workflows/deploy-backend.yml` is triggered to deploy server on Heroku. We thoroughly tested master branch, and then pushed master branch to release_backend branch. By automating deployment using Github Actions, we could monitor deployment process through Github and any member could redeploy server without knowing technical details of deployment.

 ## Licenses 
Our partner has not chosen a license yet so we have temporarily provided the standard MIT license. 
