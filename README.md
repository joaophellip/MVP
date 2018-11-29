# Cozo Delivery - Minimum Viable Product

[![Build Status](https://travis-ci.org/joaophellip/MVP.svg?branch=master)](https://travis-ci.org/joaophellip/MVP)
[![Maintainability](https://api.codeclimate.com/v1/badges/ccb2007fb0d7620ea9d1/maintainability)](https://codeclimate.com/github/joaophellip/MVP/maintainability)

Cozo Delivery is a statup whose mission is to build an open and decentralised food delivery community. 

*Code Climate test coverage tool doesn't offer support to Kotlin yet; check [this link](https://docs.codeclimate.com/docs/configuring-test-coverage#section-supported-languages-and-formats) for more information.*

## About

This repository contains source code for an Android application. The application is written in Kotlin using the Model-View-Presenter pattern.

## How to install the app

You may clone this repository and compile the source code using [**Android Studio**](https://developer.android.com/studio/run/). You can compile it manually by running either `gradlew` or `gradle.bat` on the project root folder. Make sure you have the Java Development Kit version 8 installed to match compileSDK version 27.

## Dependencies

###### Back-end API

Beyond the development dependencies specified in the app `build.gradle` file, this application relies on a communcation with a back-end API, which is external to this repository. For now, you must implement your own back-end endpoint to communicate with the app. 

The expected HTTP calls can be found inside the package `com.cozo.cozomvp.networkapi`. Additionaly, you will need a socketIO server for real time communication with the app. The socketIO configuration and call can be found in same package `com.cozo.cozomvp.networkapi`.

As a side note, we plan to supply a simple but totally functional open-source implementation of the back-end API in order to provide a way of properly running the application.

###### Google Maps API

You will also need your own Google API key to be able to launch GoogleMaps in debug mode. Generate a valid key and place it inside `google_maps_api.xml` resource file. See [**this link**](https://developers.google.com/maps/documentation/android-sdk/start) for further details on how to create a key - please, note that you must have a project created on Google Cloud Platform Console beforehand.

###### Firebase Authentication

The current authentication logic relies on the Firebase SDK Authentication for Android. In order to use it, you will need to setup a project on Google Cloud, add this package to the project, and download the configuration file `google-services.json`. See [this link](https://firebase.google.com/docs/android/setup) for detailed instructions for this setup.

## License

This repository is licensed under the [Apache License 2.0](http://www.apache.org/licenses/). See the [LICENSE](LICENSE) for the full Apache License 2.0.
