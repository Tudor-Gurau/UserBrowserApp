STEPS TAKEN TO CREATE THIS APP:

FILES AND STRUCTURE + REMOTE DATA
1) created a repo based on the default template
2) created a new branch called "dev01"
3) added a list of basic dependencies
4) checked and built the app the skeleton app 
5) created the 3 main packages: data, domain, presentation - to separate concerns
6) created model response and domain model
7) created a repository interface in the domain layer and implemented it in the data layer
8) implemented dependency injection using Hilt and created the AppModule
9) checked and tested the model response and domain model in the main activity file

PRESENTATION LAYER
10) created a viewmodel in the presentation layer and injected the repository interface
11) created a sealed class to handle the different states of the UI (loading, success, error)
12) created a composable function to display the data in a list using LazyColumn
13) created a composable function to display each item in the list
14) handled the user data and bookmarked user data states when top app bar button is clicked
15) handled the bookmarking toggle functionality in both viewmodels
16) handled the pull-to-refresh and end-reached functionality in the main screen composable 