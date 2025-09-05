# DESCRIPTION
A simple app that fetches a list of randomly generated users from a free public API.  
The users can be bookmarked and/or viewed separately in the detailed screen.  
For data persistence, a local database was set up via Room.

---

# TECH STACK
- **Architecture**: MVVM, clean code
- **UI**: Jetpack Compose
- **DI**: Hilt
- **API calls**: Retrofit
- **Tests**: JUnit (unit tests)

---

# STEPS TAKEN TO CREATE THIS APP

## Files and Structure + Remote Data
1. Created a repo based on the default template
2. Created a new branch called `dev01`
3. Added a list of basic dependencies
4. Checked and built the skeleton app
5. Created the 3 main packages: `data`, `domain`, `presentation` — to separate concerns
6. Created model response and domain model
7. Created a repository interface in the domain layer and implemented it in the data layer
8. Implemented dependency injection using Hilt and created the `AppModule`
9. Checked and tested the model response and domain model in the MainActivity file

## Presentation Layer
10. Created a ViewModel in the presentation layer and injected the repository interface
11. Created a sealed class to handle the different states of the UI (loading, success, error)
12. Created a composable function to display the data in a list using `LazyColumn`
13. Created a composable function to display each item in the list
14. Handled the user data and bookmarked user data states when top app bar button is clicked
15. Handled the bookmarking toggle functionality in both ViewModels
16. Handled the pull-to-refresh and end-reached functionality in the main screen composable

## Tests
17. Created unit tests based on the ViewModel  
