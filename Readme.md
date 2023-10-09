# EventAPIClient SDK Usage Guide

## Introduction

The `EventApiClient` SDK is a Kotlin package designed for Android developers to easily interact with a specific API. It abstracts the complexity of making requests and handling responses. This document provides a detailed guide on how to integrate and use this SDK in your Android projects.

## Installation

The `EventApiClient` uses

- moshi https://github.com/square/moshi for hadling json parsing
- okhttp3 for http request https://square.github.io/okhttp
  both libary have to setup in the project

## Usage

### 1. Initialization

Initialize the `APIClient` with your `channelId` and `apiSecret`.

```kotlin
val apiClient = APIClient("yourChannelId", "yourApiSecret")
```

### 2. Login API Call

Before making any other API requests, you need to ensure the user is logged in. The SDK handles this internally.

### 3. Making an Event API Call

To send an event, create a list of `EventBody` and call the `eventAPICall` method.

```kotlin
val event = listOf(APIClient.EventBody(
    name = "prompt",
    text = inputText,
    payload = APIClient.EventPayload(#
        eventCategory = "userMesssgage",
        eventAction = "Send",
        source = "AndroidApp"
    )
))

apiClient.eventAPICall(events) { bricks, error ->
    if (error != null) {
        println("Error: $error")
    } else if (bricks != null) {
        bricks.forEach { println("Brick: $it") }
    }
}
```

### 4. Handling the Response

The `eventAPICall` method returns a list of `Brick` objects or an `Exception` in case of an error. You can handle the response inside the callback.

## Data Classes

The SDK contains several data classes like `EventBody`, `EventPayload`, and `Brick` to make it easier to construct requests and handle responses.

### EventBody

Create instances of this class to represent individual events.

```kotlin
val event = APIClient.EventBody(name = "eventName", text = "eventText", payload = APIClient.EventPayload(eventCategory = "category", eventAction = "action", source = "source"))
```

### EventPayload

Include additional data with your events.

```kotlin
val payload = APIClient.EventPayload(eventCategory = "category", eventAction = "action", source = "source")
```

## Error Handling

Errors are returned in the callback, and you can handle them accordingly.

```kotlin
apiClient.eventAPICall(events) { bricks, error ->
    if (error != null) {
        println("Error: $error")
    }
    // Handle bricks if there's no error
}
```

## Conclusion

The `APIClient` SDK simplifies the process of making API requests in your Android applications. Ensure to replace `"yourChannelId"` and `"yourApiSecret"` with your actual `channelId` and `apiSecret`. Always handle the errors to provide feedback to the users or for debugging purposes. Adjust your implementation as per your requirements.

For any further queries or issues, feel free to contact us.
