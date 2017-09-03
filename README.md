# location-tracker
## Strategy
Typically when I start a new Android project I begin with a template such as 
[**Android Core Kotlin**](https://github.com/neorix11/android-core-kotlin) or [**Andriod Core Java**](https://github.com/neorix11/android-core-java) which are updated versions of some other core templates I've employed in the past.
However, given the nature of this exercise I decided to avoid the setup of an MVP architecture and just keep all the code in the Main Activity for easy reference.

Given the requirements, I envisioned a button at the top of the screen that could toggle the location tracking state. Below it a countdown indicating the time remaining until the next update is requested. Below the countdown, a
log report that displays the app's tracking state as well as the location when it's received. While the additional UI elements weren't a requirement, it was easy enough to implement and besides, who doesn't like feedback?

In `onCreate()` I initialize the `locationCallback`, the `countDownTimer` and set the `onClickListener` for the tracking button.
When the user clicks the button, `toggleLocationTracking()` will evaluate if the countdown is visible. If the countdown is not visible the tracking will commence. Otherwise is will stop.
Upon receipt of location, (*every 15 seconds or so*) `postLocation(location)` will be invoked and a POST request will be made to [https://demo0280857.mockable.io/locationdata](https://demo0280857.mockable.io/locationdata)

##A Quick Dependency Rundown
 * Google Play Service - Location
 * OkHttp3 - Rest Client
 * Permission Dispatcher - Makes requesting and checkign runtime permission a sinch.
 * Anko - Makes calling layout elements much more concise. 
 * Timber - Just a logging lib
 * JodaTime - Timey-Wimey stuff. 