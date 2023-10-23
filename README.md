# Cube Academy - Android Task

## Task Description

This application was completed as my assessment to join the Cube Academy. I was required to complete an android application with the given UI design and API document. The app initially consisted of a basic skeleton already implemented, but there were parts of the code that needed implementation.

### The app includes the following features:

-	View the current month's nomination
-	Create a new nomination.

#### Useful links
- Designs: [Figma](https://www.figma.com/file/BAOzJacpI4IemeawyFlw5j/Mobile-Mini-Task-flow?type=design&node-id=2818-8902&mode=design&t=31N76gmtRrCVHnj9-4)
- API documentation: [OpenAPI docs](https://cube-academy-api.cubeapis.com/docs)

### API requests
The API documentation [OpenAPI docs](https://cube-academy-api.cubeapis.com/docs) was used for this purpose.

An Authorization header is required for the API calls as the API uses a Bearer token for Authorization. The Bearer token was obtained after successfully registering and logging.

** Postman was used to call the register/login endpoint. The returned Auth Token was stored in the gradle.properties. **

### My contribution
- On the first screen:
  - I populated the data of the nominations recycler view from the data taken from the api.
- On the second screen:
  - I added the input controllers to the screen's xml file and made sure that the implementation matched the given figma designs. 
  - I added logic to the controllers in the activity class.
  - I used the api to send the user's input and create a new nomination.
- On the third screen:
  - I added action logic to the two action buttons at the bottom of the screen. One should start another "Create nomination" form and the second should exit to the first screen. 
- The basic DI modules and the retrofit instance and endpoints were already defined, but were not being used. 
  - I updated the [Repository] class to work with the API.

