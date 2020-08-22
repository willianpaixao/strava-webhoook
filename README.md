# strava-webhook

[![pipeline status](https://gitlab.com/willianpaixao/strava-webhook/badges/master/pipeline.svg)](https://gitlab.com/willianpaixao/strava-webhook/-/commits/master)

### Deployment
```shell script
$ gcloud functions deploy strava-webhook \
    --entry-point com.paixao.strava.StravaWebhook \
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
$ curl -X POST https://us-central1-zero-plus-x.cloudfunctions.net/strava-webhook \
    -H "Authorization: bearer $(gcloud auth print-identity-token)"
```
#### Running unit tests
```shell script
$ mvn test
```
