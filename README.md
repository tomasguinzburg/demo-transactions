# demo-transactions
Java microservice for stack and architecture demonstration purposes.
This project uses a lot of automated code generation, I recommend installing the [IntelliJ Idea lombok plugin](https://plugins.jetbrains.com/plugin/6317-lombok) or your favourite IDEs equivalent for a nicer experience.

## Execution
By default it runs on port 4567 or whatever is set in the environment variable $PORT, make sure no other processes are using it.
##### OSX / UNIX
```bash
git clone git@github.com:tomasguinzburg/demo-transactions.git
cd demo-transactions
./gradlew build
./gradlew run
```
##### Windows
```shell
git clone git@github.com:tomasguinzburg/demo-transactions.git
cd demo-transactions
gradlew.bat build
gradlew.bat run
```

## Testing
Executing the build command runs all tests and testCoverage verifications.

You should find the test coverage output after gradle build in: `build/jacoco/index.html`. 
If not present, run 
```bash
./gradlew test jacocoTestReport jacocoTestCoverageVerification
```
to force the tests and coverage tasks. 

For API testing you can import the Postman collection `demo-transactions.postman_collection.json`.
You can also test the application online in [heroku](https://warm-shelf-67032.herokuapp.com/transactions) changing the urls in the postman collections.

It's important to know that only two accounts are created in the system, with IBANs `ES9820385778983000760237` and `ES9820385778983000760236`
These accounts have balances of 100 and 10000000 respectively. 
At the moment there's no way to create new accounts other than locally modifying the [InMemoryAccountRepositoryImpl](https://github.com/tomasguinzburg/demo-transactions/blob/a722ece6e4bea23ff8e38619b0bb06d72cefb5bc/src/main/java/com/tomasguinzburg/demo/impl/repositories/InMemoryAccountRepositoryImpl.java)
and rebooting the server.

Another important note is that account balances will update with incoming transactions, so debits might stop working if they run out of money.
To fix this, just send a credit with a very big amount to each of them. It may be necessary to do this at the start if running an automated test suite.

## Architecture
The service architecture is loosely influenced by Clean Architecture by Robert C. Martin, and several microservices I have worked on
from authors who follow his design advices. It is in no way a rigorous application of his philosophy, but focuses on some core concepts:
- Business code is implementation agnostic. The only dependencies of use-case implementations (ServiceImpl) are the repository APIs.
- Tools know about business code, business code knows nothing about tools and frameworks. A usecase is the same regardless of it being called from HTTP or the CLI,
and a repository is just a place that stores entities, whether it is a DB, message queue or an HTTPClient to another kind of implementation (an in-memory mock, in this case)
- Complex functions should be separated in several simpler, chained functions

Given the time constraints, some of these predicates have been applied somewhat loosely.
The [core](https://github.com/tomasguinzburg/demo-transactions/blob/a722ece6e4bea23ff8e38619b0bb06d72cefb5bc/src/main/java/com/tomasguinzburg/demo/core) package
contains all the business specific code; while the [impl](https://github.com/tomasguinzburg/demo-transactions/blob/a722ece6e4bea23ff8e38619b0bb06d72cefb5bc/src/main/java/com/tomasguinzburg/demo/impl) package
contains the necessary tools and implementations for this application to run as a stand alone microservice.

The advantage of this architecture is that you can literally copy the core package, paste it in some other application, write the missing repository implementations and endpoints and voila,
you just switched frameworks. I may showcase this by switching to spring-boot and an H2 database in another branch, but probably not before the deadline's due.

## Tools
For this size of an application, I find it better to work with a small, well-chosen toolkit rather than a big framework. This makes the service pretty light too, with the fatJar weighting only 6MB even though it contains all libraries.
It also prevents some code smells that come from automatically defaulting to the same framework without assesing the real needs of the project. I do use a few tools though:

- [Gradle](https://gradle.org/) for dependency management and task-control. I chose it over maven because it makes it a lot easier to configure CI runners in Github. The con is that is not so well known and the Kotlin DSL can have a steep learning curve.
- [Dagger2](https://dagger.dev/) for dependency injection. Pros: All the injection is done through code generation, so literally 0 runtime and startup overhead. Cons: Not very well known in the backend environment, although Android Developers might know it too well. I like that it's lightweight and only has dependency injection functionalities, nothing else. 
- [lombok](https://projectlombok.org/) nobody likes writing boilerplate in their free time. Lombok auto-generates it for me. It also makes so that the pull requests are smaller and easier to review. 
- [sparkjava](https://sparkjava.com/) a fast server microframework to sketch things up. It behaves well under pressure too! I like the javascript/golang like syntax, although it breaks JAX-RS Api.
- [jUnit4](https://junit.org/junit4/) guess I should have learnt jUnit5 at this point. 😔
- [mockito](https://site.mockito.org/) for mocking dependencies in unit tests.
- [gson](https://github.com/google/gson) fast JSON mapping with minimal configuration.
- [jacoco](https://www.eclemma.org/jacoco/) for code coverage reports and verification. Set to 60% of all non-generated lines.
- [guava](https://github.com/google/guava) used internally by Gradle, class MoreCollectors.onlyElement() came in very handy.

#### Bonus points?
This project comes with a CI workflow already set up. Try pushing to feature/, release/, develop, or creating any type of pull request to see it do the thing. You can also check past executions in the [Actions](https://github.com/tomasguinzburg/demo-transactions/actions) tab. 

And that's pretty much it.
Have fun breaking it apart :)
