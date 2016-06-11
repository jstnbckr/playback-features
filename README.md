# playback-features

We maintain last three periods in memory:

requestsInThirdFromLastPeriod  <=  requestsInSecondLastPeriod  <=  requestsInLastPeriod

The statistics calculation is based on requestsInThirdFromLastPeriod and requestsInSecondLastPeriod.
