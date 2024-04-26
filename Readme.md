# SkySync

## product vision
An application that simplifies and optimizes staff interactions and planning in a hot air baloon enterprise.

## Links

* [Figma](https://www.figma.com/files/project/215563737/Team-project?fuid=1343517062055653454)
* [Discord](https://discord.com/channels/1215608534088024145/1216071295163170857)
* [Firebase](https://console.firebase.google.com/u/1/project/skysync-a1160/overview)
* [Standup](https://docs.google.com/spreadsheets/d/1i_jQHeHb9f_Q8SR2-SSEBDQeCGQ0PqrkyyI27g9egv4/edit#gid=0)
* [Team Retrospective](https://docs.google.com/spreadsheets/d/1HttrnOvkY0A7PFclFm-wyba8CDeAdHuONOSRZka6cV8/edit#gid=2104623826)

## Roles

Each user is assigned a primary authentication role, such as crew, pilot, or admin. Additionally, users may temporarily acquire supplementary roles, such as maitre fondu during a flight, which should not expand their permissions.

These roles are stored in the database.

### Auth Crew

Crew members are responsible for preparing the flight, (includes arranging equipment and food for the journey). During the flight, they either follow the balloon in a vehicle or remain inside to cater to the passengers. After the theft, they are responsible for storing the equipment.. read have access to their personal data and to information relating to the flight to which they are assigned.

### Auth Pilot

Pilots are the only ones legally entitled to drive a hot-air balloon. They are responsible for specialized equipment if required (high-altitude flight). Pilots have read access to their personal data and to information relating to the flight to which they are assigned.

### Auth Admin

Administrators create flights and assign staff to them. They have read/write acccess to everything.

### Other roles

TBD

## Database
![SkySync database diagram](https://github.com/SkySync-EPFL/skysync/assets/93386280/b013e412-8891-4f17-81f5-499af5cfaeb7)

## Architecture Diagram

![SwEnt Architecture Diagram](https://github.com/SkySync-EPFL/skysync/assets/75271095/fa9c1de8-369b-4ebf-b376-aa8ef0ec9848)

## Data Models
<img width="1145" alt="Screenshot 2024-04-11 at 17 35 25" src="https://github.com/SkySync-EPFL/skysync/assets/37707941/f76ff34b-f7c3-4929-b7ae-b2c378c7e701">


## Testing

### Firebase emulators

We use [emulators](https://firebase.google.com/docs/emulator-suite?_gl=1*1lfc7ua*_up*MQ..*_ga*MTQwMTc0MTY3Mi4xNzEyNDAwNTk1*_ga_CW55HF8NVT*MTcxMjQwMDU5NC4xLjAuMTcxMjQwMDU5NC4wLjAuMA..) to integrate Firebase products in the tests.

To run the tests that use the emulators locally, you need to setup the emulators:

* Install the Firebase CLI ([doc](https://firebase.google.com/docs/cli?_gl=1*1fghral*_up*MQ..*_ga*NTQ0MjY1MjUxLjE3MTI0Nzk1OTQ.*_ga_CW55HF8NVT*MTcxMjQ3OTU5My4xLjAuMTcxMjQ3OTU5My4wLjAuMA..)), on linux:
  ```
  curl -sL https://firebase.tools | bash
  ```
* Login the Firebase CLI
  ```
  firebase login
  ```
* Before running the tests, on a new terminal, run:
  ```
  firebase emulators:start --only firestore
  ```
* After running the tests, you can stop the emulators with Ctrl+C

> [!NOTE]
> Tests using the Firebase emulators need to be UI tests, they need to run on the Android emulator.

### UI Testing: Global Description

Here is the official documentation: https://developer.android.com/develop/ui/compose/testing#isolation

Here is the cheatsheet: https://developer.android.com/develop/ui/compose/testing-cheatsheet

* Create the tests in the app/src/androidTest/java/ch/epfl/skysync folder
* Follow the structure files in app/src/main/java/ch/epfl/skysync (i.e if there is a kotlin file ChatScreen.kt in the folder screens, create a folder screens and a kotlin class ChatScreen)
* Everything you want to test must be uniquely identifiable on the screen. 

There are 2 methods. An easier one, which I recommend, and a more complicated one which requires creating 2 classes for more elaborate tests.


### First method

### Find the component to test

If the component has a unique text identifier, you can use it to test it.
Assume I have a button that goes back on click in the class ChatScreen.kt. 

```kotlin
Button(onClick = "go Back", Text = "testButton")

```

If not you can add a test tag on it.

```kotlin
Button(modifier = Modifier.testTag("testButton"), onClick = "go Back")

```

### Create testing class

The name of this class should be nameOfTheClassTest. Create this class where you want. For example you could create a folder Chat and put the ChatScreenTest inside.

```kotlin
class ChatScreenTest : TestCase(kaspressoBuilder = Kaspresso.Builder.withComposeSupport()) {

    //required to test UI components
  @get:Rule val composeTestRule = createComposeRule()

  @Before
  fun testSetup() {
    val vm = ToDoViewModel()
    //here ChatScreen refers to the class in app/src/main/java/ch/epfl/skysync
    composeTestRule.setContent { ChatScreen(vm, mockNavActions) }
  }

  @Test
  fun ButtonIsCorrectlyDisplayedAndhasClickAction() { 
    composeTestRule.onNodeWithText("testButton").assertIsDisplayed()
    composeTestRule.onNodeWithText("testButton").assertHasClickAction()
    composeTestRule.onNodeWithText("testButton").performClick()

    //if using the test tag
    composeTestRule.onNode(hasTestTag("testButton")).assertIsDisplayed()
    composeTestRule.onNode(hasTestTag("testButton")).assertHasClickAction()
    composeTestRule.onNode(hasTestTag("testButton")).performClick()

  }

}
```

You can find an example testing navigation here app/src/androidTest/java/ch/epfl/skysync

### Second method

First you have to add test tags to all components you want to test

### Create first (mapping) class

The name of this class should match its counterpart. Make sure a test label is added to the class so that the test can find the location of the tested components. For example if ChatScreen is a Scaffold containg the button add the test tag on the modifier of the Scaffold.

```kotlin
class ChatScreen(semanticsProvider: SemanticsNodeInteractionsProvider) :
    ComposeScreen<ChatScreen>(
      //this test tag must be set on the "top level" component (Scaffold, Surface)
        semanticsProvider = semanticsProvider, viewBuilderAction = { hasTestTag("ChatScreen") }) {

  // Structural elements of the UI
  val testBut: KNode = child { hasTestTag("testButton") }
}
```

### Create second (test) class

The name of this class should be nameOfTheClassTest. Create this class where you want. You could create a folder Chat and put the ChatScreenTest inside.

```kotlin
class ChatScreenTest : TestCase(kaspressoBuilder = Kaspresso.Builder.withComposeSupport()) {

    //required to test UI components
  @get:Rule val composeTestRule = createComposeRule()
    /*
  This rule automatic initializes lateinit properties with @MockK, @RelaxedMockK,
  It gives the mocked class methods default values
    */

  @get:Rule val mockkRule = MockKRule(this)


  @RelaxedMockK lateinit var mockNavActions: NavigationActions

  @Before
  fun testSetup() {
    val vm = ToDoViewModel()
    //here ChatScreen refers to the class in app/src/main/java/ch/epfl/skysync
    composeTestRule.setContent { ChatScreen(vm, mockNavActions) }
  }
  @Test
  fun titleAndButtonAreCorrectlyDisplayed() { run {
    //here ChatScreen refers to the class in app/src/androidTest/java/ch/epfl/skysync
    onComposeScreen<ChatScreen>(composeTestRule) {
    
    //this has to have the same name as defined in the first class
      testBut {
        //tests if the button is displayed
        assertIsDisplayed()
        //tests if the button can be clicked
        assertHasClickAction()
      }
    }
  }
  }
  @Test
  fun goBackButtonTriggersBackNavigation() = run {
    //here ChatScreen refers to the class in app/src/androidTest/java/ch/epfl/skysync
    onComposeScreen<ChatScreen>(composeTestRule) {
        //this has to have the same name as defined in the first class
      testBut {
        // arrange: verify the pre-conditions
        assertIsDisplayed()
        assertIsEnabled()

        // act: go back !
        performClick()
      }
    }

    // assert: the nav action has been called 
    //go back will return a default value
    verify { mockNavActions.goBack() }
    confirmVerified(mockNavActions)
  }
}
```

#### More advanced 

* A class is dependant on another (for example login screen) you can use the following rule
```kotlin
  @get:Rule val composeTestRule = createAndroidComposeRule<MainActivity>()

```
* Test that a function was not called (for navigation for example)

```kotlin
verify { mockNavActions wasNot Called }
```

For more advanced UI testing you can refer to the tests of the bootcamp or online.


### Backend tests

* Create the backed tests in the app/src/test/java/ch/epfl/skysync
* Follow the structure files in app/src/main/java/ch/epfl/skysync 

Here is how to create a simple unit test

```kotlin
class ExampleUnitTest {

    private val testmyClass: myClass = myClass()
  @Test
  fun addition_isCorrect() {
    val expected = 4
    assertEquals(expected, testmyClass.getFour())
  }
}
```
You can find an example in app/src/test/java/ch/epfl/skysync/database/schemas.

### Jacoco test report

* Run the gradle tasks 

```
check connectedCheck jacocoTestReport

```
In order to run connectedCheck you have to launch the emulator. While in the Gradle menu (accessed by clicking on the elephant icon), you can search for tasks by clicking on the terminal icon located directly beneath "Gradle". 
* Running the Gradle task jacocoTestReport will generate a coverage report, which will be in app/build/reports/jacoco/jacocoTestReport/html. Open index.html with a Web browser to see the report.


