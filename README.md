# playback-features

## Introduction

DiagnosticService is a generic service that can store different types of requests.
It has a timer that will make a callback to perform certain action at the end of
each timer period.

DiagnosticService implements the callback interface and performs the action
when each timer period completes.

MyDiagnosticService implements DiagnosticService. See its Javadoc for details.

MyPlaybackDiagnostics implements PlaybackDiagnostics that will log playback requests
into DiagnosticService at different times. Then it queries DiagnosticService to get
the the playback requests and calculate statistics.

## Software
1. JDK 1.7
2. JUnit 4.12
3. Gradle 2.10

## Example
Please look at the unit test to see how to run the services.

## Tests
> gradle test

## Design Decisions
1. In MyDiagnosticService, I defined two maps. One is "workingMap" which is a 
   synchronized map, the other is "requestStore" which a concurrent map.
   "workingMap" is used to store the incoming requests in current ongoing period.
   "requestStore" is used to store all requests across all completed periods.
   
2. In MyDiagnosticService.doAction(), I chose to save "workingMap" directly into 
   "requestStore" for performance reason. workingMap is a synchronized map. 
   Ideally, we should copy it into a non-synchronized map and save it into the 
   request store for fast access by multiple client threads later. But workingMap 
   could be huge, copy it into another non-synchronized map could take a long time.
   So here I chose to sacrifice the clients concurrency and save it directly into 
   the request store s.t. doAction() does not hold up the timer thread and the data
   is immediately available for access by the client threads.


## Notes
1. For "rate", if there is only one completed period, it is 0. Same applies to "average rate".
2. Unit tests 
    1. I wrote uses 5 second for the timer.
    2. I did not write unit tests that create multiple threads to call PlaybackDiagnostics.
    3. I did not write unit tests that log requests with different countries
    4. I did not write unit tests to stress the system resources e.g. millions of requests.
    5. I did not write unit tests with very short timer period e.g. milliseconds or nanoseconds.
2. I added quite some log messages using Util.printWithTime() for easy debug. 

## Time
Total about 9 hours

1. About 3 hours on design and first draft of code (on Friday March 11)
2. About 6 hours on test, debug and documentation (on Saturday March 12)


