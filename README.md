# MyTracker
This android app is tracking GPS position.
- After 1 day it is trying to send a email with the current GPS-Position.
- If this is not possible it waits for another day if a 
  network connection is available and then send a eMail with GPS-Position.
- If this is also not possible after the second day it will send 
  a SMS with the GPS-Position.
- Furthermore it will send send SMS when the Smartphone is shutdown.

#todo
  - if the battery level is low it should send SMS     
  - if i send a SMS with a predefined Message for example "LOCATION"
    it should send a SMS back with the current location.
