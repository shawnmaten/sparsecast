# Database

## Tables

### Image

- category
- instagram shortcode

### Saved Place

- user id
- place id
- name
- lat
- lng

### GCM

- user id
- gcm token

## Notification Flow

### Current Location
1. when app opens go to [GCM Registrar Service](#GCMRegistrarService)
2. if device is set to supported locale automatically set notification preference to true

  ### Forecast Check Task
  1. device reports location, triggering forecast check
  2. forecast check completes and sends notification if appropriate
  3. deletes any pending location tasks
  4. queues a location check task based on forecast data

  ### Location Check Task
  1. sends GCM message to device to trigger location report
  2. device should send report
  3. if successful
    - device role is done go to [Forecast Check Task](#ForecastCheckTask)
  4. else if failed
    - device takes role of retrying
    - eventually device movement, app reset, or system reset will trigger notification flow to restart if broken

  ### GCM Registrar Service
  1. if GCM token is null
    - register with backend
  2. else if not null
    - go to [Forecast Check Task](#ForecastCheckTask)

  ### GCM Token Refresh Service
  1. go to [GCM Registrar Service](#GCMRegistrarService)

  ### Location Service
  1. after GCM registrar check
  2. get location updates every hour
  3. if moved more than 5 kilometers since last report, report again and save as last report

  ### Notification Delivery
  1. may want timestamp on notification in case it is received after its applicable time frame

3. else if not supported or user opts out, device is left registered in GCM table but current location updates are not triggered

# General Notes

- make all requests server side
- consider making a forecast check that only includes minutely to increase response time

