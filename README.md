# strava-webhook

[![pipeline status](https://gitlab.com/willianpaixao/strava-webhook/badges/master/pipeline.svg)](https://gitlab.com/willianpaixao/strava-webhook/-/commits/master)

### Deployment
```shell script
$ gcloud functions deploy strava-webhook \
    --entry-point com.zeroplusx.strava.StravaWebhook \
    --runtime java11 \
    --trigger-http \
    --allow-unauthenticated
```
