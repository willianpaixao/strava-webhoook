# strava-webhook

[![pipeline status](https://gitlab.com/willianpaixao/strava-webhook/badges/master/pipeline.svg)](https://gitlab.com/willianpaixao/strava-webhook/-/commits/master)
[![coverage report](https://gitlab.com/willianpaixao/strava-webhook/badges/master/coverage.svg)](https://gitlab.com/willianpaixao/strava-webhook/-/commits/master)

### Deployment
```shell script
$ gcloud functions deploy strava-webhook \
    --entry-point com.paixao.rundapp.StravaWebhook \
    --env-vars-file=.dev.env \
    --memory=256MB \
    --runtime java11 \
    --trigger-http \
    --allow-unauthenticated
```

### Testing
#### Running cloud function locally
> NOTE: It requires [Google Cloud SDK](https://cloud.google.com/sdk) installed and credentials set.
```shell script
$ mvn function:run
```
#### Invoking cloud function
> NOTE: It requires [Google Cloud SDK](https://cloud.google.com/sdk) installed and credentials set.
```shell script
$ curl --request POST --data '{"aspect_type":"create","event_time":1549560669,"object_id":1,"object_type":"activity","owner_id":9999999,"subscription_id":999999}' localhost:8080
```
#### Running unit tests
```shell script
$ mvn test
```
