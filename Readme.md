# SkySync

## product vision
An application that simplifies and optimizes staff interactions and planning in a hot air baloon enterprise.

## Links

* [Figma](https://www.figma.com/files/project/215563737/Team-project?fuid=1343517062055653454)
* [Discord](https://discord.com/channels/1215608534088024145/1216071295163170857)
* [Firebase](https://console.firebase.google.com/u/1/project/skysync-a1160/overview)

| |
| - |
| [Standup 1](https://docs.google.com/spreadsheets/d/1m8U5FLoCYPnLGZMNuJ6yGFX6ynVkkjzNFThc0HQJ9-g/edit#gid=0) |

## Testing

### UI Testing: Global Description

* Create the tests in the app/src/androidTest/java/ch/epfl/skysync folder
* Follow the structure files in app/src/main/java/ch/epfl/skysync (i.e if there is a kotlin file ChatScreen.kt in the folder screens, create a folder screens and a kotlin class ChatScreen)
* Everything you want to test must be associated with a test tag
* You will need to create 2 classes. The first contains a mapping between variables and the test tags. This class will be created in the same location as its counterpart (as described above). The second class will be the real test class that will be runned

### Add a test Tag

Assume I have a button that goes back on click in the class ChatScreen.kt

```kotlin
Button(modifier = Modifier.testTag("testButton"), onClick = "go Back")

```

### Create first (mapping) class

The name of this class should match its counterpart. Make sure a test label is added to the class so that the test can find the location of the components under test. For example if ChatScreen is a Scaffold containg the button add the test tag on the modifier of the Scaffold.

```kotlin
class Chat(semanticsProvider: SemanticsNodeInteractionsProvider) :
    ComposeScreen<ChatScreen>(
        semanticsProvider = semanticsProvider, viewBuilderAction = { hasTestTag("ChatScreen") }) {

  // Structural elements of the UI
  val testBut: KNode = child { hasTestTag("testButton") }
}
```

### Create second (test) class

The name of this class should be nameOfTheClassTest. Create this class where you want. For example you could create a folder Chat and put the ChatScreenTest inside.

```kotlin
class ChatScreenTest : TestCase(kaspressoBuilder = Kaspresso.Builder.withComposeSupport()) {

    //required to test UI components
  @get:Rule val composeTestRule = createComposeRule()
    /*
  This rule automatic initializes lateinit properties with @MockK, @RelaxedMockK,
  It is useful if you want to mock classes
    */

  @get:Rule val mockkRule = MockKRule(this)

  /*
  Here we create a mock of the NavigationActions. The methods defined in this class return default values

  */
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


### Backed tests

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

### Jacoco test report

* Run the gradle tasks check connectedCheck jacocoTestReport. 
In order to run connectedCheck you have to launch the emulator. While in the Gradle menu (accessed by clicking on the elephant icon), you can search for tasks by clicking on the terminal icon located directly beneath "Gradle". 
* Running the Gradle task jacocoTestReport will generate a coverage report, which will be in app/build/reports/jacoco/jacocoTestReport/html. Open index.html with a Web browser to see the report.
