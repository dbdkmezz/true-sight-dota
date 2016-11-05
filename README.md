# true-sight-dota
True Sight for Dota 2

True Sight is a free software (GPL v3) Android app for Dota 2. True Sight provides information for counter picking enemy heroes, the details of their abilities, including a mode focused on which are blocked by spell immunity and how debuffs can be dispelled. Heroe can be idnetified by typing in their names or by taking a photo of the enemy team. 

Available from the Google Play Store:
https://play.google.com/store/apps/details?id=com.carver.paul.truesight

Dota 2 is a registered trademark of Valve Corporation. All game images and names are property of
Valve Corporation. This app is not affiliated with Valve Corporation. The images in the
app/src/main/res/drawable-nodpi directory are Valve's property, taken from the website
http://www.dota2.com/heroes/

True Sight is built using the OpenCV (Open Source Computer Vision Library) library. OpenCV is a
BSD-licensed project and is not covered by the GPL 3 library which covers the rest of this project.


To make tests work ensure the Build Variant Test Artifact is set to Unit Tests. (Set within "Build
Variant" on the left of Android Studio.) Also ensure that instant run is turned off, otherwise
you'll get a "Could not find a class for package: ... and class name:
com.android.tools.fd.runtime.BootstrapApplication" error.
