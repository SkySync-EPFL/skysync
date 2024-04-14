# Android Architecture

> [Official documentation](https://developer.android.com/topic/architecture/intro)

The global architecture is structured in 4 layers:

* **Data Source**:  
  This is where you fetch your data from. It could be a network API, a local database, or any other source. 

  For us, it is `FirestoreDatabase`.

* **Repository**:  
  The repository acts as a bridge between your data sources and the rest of your application. It's responsible for coordinating data operations, such as fetching data from the network or database, and deciding where to get the data from (cache, network, etc.).

  We don't have an exact equivalent to that, the closest we have are the tables (`UserTable`, ...).

* **ViewModel**:  
  ViewModels are responsible for managing UI-related data in a lifecycle-conscious way. They usually interact with repositories to get the data needed for the UI. ViewModels should not directly interact with data sources; instead, they delegate this responsibility to repositories.

* **UI Layer**:  
  This is where you define your UI using Jetpack Compose. The UI layer consumes data provided by ViewModels through state objects or state flow.