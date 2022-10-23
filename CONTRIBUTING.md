# Contributing to Ukulele
Contributions are welcome! Below are some technical details on how to work on the project.

## Setting up your workspace
To get started you only need JDK 11 or up. Gradle will automatically install itself.

Clone this git repository. You can then import this Gradle project into your favourite IDE. Intellij IDEA is recommended.

You can build the project with `./gradlew clean build`. If you're a Windows user, you can use `gradlew.bat` instead.
A self-contained .jar file will be built in `build/libs/ukulele.jar`.

## Working with Kotlin
Kotlin is a very flexible language. It is particularly easy to learn if you already know Java.

Notable differences to Java:
* Improved type system (null-safety and better generics)
* More oriented towards functional programming
* More concise (shorter) code
* Coroutines for async functions

If you're new to Kotlin, I recommend the following reading:
* [Basic syntax](https://kotlinlang.org/docs/reference/basic-syntax.html)
* [Kotlin Koans](https://kotlinlang.org/docs/tutorials/koans.html) (Interactive)

## Working with Spring Boot
Ukulele uses Spring Boot as a framework. The basic concept is that we can declare components (beans, services, etc.) to
be depended on by other beans. This is called inversion of control. It allows us to reduce coupling between components.

All `Command`s are beans. Another bean is the `CommandManager`, which depends on all Command beans. Beans are automatically
created by Spring Boot.

Documentation: https://spring.io/projects/spring-boot#learn

## Developing commands
As mentioned above, Commands are Spring beans (they're annotated with `@Component`). Simply defining your command is
enough to register it in the `CommandManager`. 

Example:
```kotlin
@Component
class SayCommand : Command("say") {
    override suspend fun CommandContext.invoke() {
        reply(argumentText)
    }
}
```

The above is a simple `Command` that simply echoes back whatever the user said. The command name is provided as a
constructor argument to the parent type. 

The `invoke()` is a little special because it receives `CommandContext` as its receiver type. The function acts as if
it was run within the scope of the given `CommandContext`. `reply(String)` is a function of `CommandContext`.

`CommandContext` tells you a lot about where and how the command is getting invoked. It also contains several convenience
functions. `argumentText` is a convenience property that contains the message following the command trigger.

### Constructor arguments
As commands are Spring beans, you can depend on other Spring beans with them. `PlayerRegistry` and `AudioPlayerManager`
are beans that we have declared elsewhere. You can depend on them by simply adding them to the constructor.

Below is also an example of setting an alias (i.e. `p` for `play`).

```kotlin
@Component
class PlayCommand(
        val players: PlayerRegistry,
        val apm: AudioPlayerManager
) : Command("play", "p") {
    // ...
}
```
