Yes. This section is the real heart of Spring Boot. The previous part introduced beans and the ApplicationContext; this part shows how Spring Boot makes those ideas automatic and configurable.

I’ll explain it from simple → internal working → practical Java/Spring examples, because once you understand the four mechanisms below, most Spring Boot configuration stops looking like magic.


---

1. The big picture

This entire section is really about four things:

Spring Boot
                    │
       ┌────────────┼────────────┐
       ↓            ↓            ↓
 Auto-configuration Starters  Configuration
       │            │            │
       └────────────┴────────────┘
                    ↓
             Spring Beans
                    ↓
          ApplicationContext
                    ↓
             Your application

And there is a fourth supporting piece:

Dependency Management
        ↓
Compatible library versions

So remember:

> Starters decide what capabilities you add.
Auto-configuration decides what Spring beans/configuration should appear.
Properties let you customize them.
Dependency management keeps the libraries compatible.




---

2. What exactly is auto-configuration?

This is the first major concept.

Suppose you add:

<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-jpa</artifactId>
</dependency>

You have basically told Spring Boot:

> "My application uses JPA/database functionality."



Spring Boot then examines the application and its classpath.

Conceptually:

spring-boot-starter-data-jpa
          ↓
JPA libraries available
          ↓
Spring Boot detects them
          ↓
Relevant auto-configuration activates
          ↓
Beans are created/configured

So you don't have to manually configure every single infrastructure object.


---

3. What does "conditional" mean?

This is the most important part of auto-configuration.

Spring Boot does not simply say:

> "I found this library, therefore I'll blindly create everything."



Instead, it asks questions.

Imagine:

Is DataSource.class available?
        │
       YES
        ↓
Should DataSource auto-configuration apply?
        │
       YES
        ↓
Is there already a user-defined DataSource bean?
        │
       NO
        ↓
Create/configure one

That is a conditional configuration policy.


---

4. @ConditionalOnClass

Your book gives the example:

@ConditionalOnClass(DataSource.class)

This means approximately:

> "Apply this configuration only when DataSource is available."



So imagine this:

Classpath
 ├── DataSource.class ✅
 ├── JDBC classes ✅
 └── other libraries

Then:

@ConditionalOnClass(DataSource.class)
             ↓
         condition true
             ↓
Auto-configuration can activate

But if DataSource.class isn't available:

DataSource.class ❌
       ↓
condition false
       ↓
that auto-configuration doesn't apply

This is how Spring Boot avoids configuring technology that your application isn't using.


---

5. What is a "policy"?

In this chapter, the word policy basically means a configuration class that contains rules like:

@Bean
...

plus conditions such as:

@ConditionalOnClass(...)
@ConditionalOnMissingBean(...)
@ConditionalOnProperty(...)

So think:

Auto-configuration class
       ↓
Rules
       ↓
"If conditions are satisfied..."
       ↓
Create these beans

It's not a mysterious AI-like decision.

It's mostly predefined rules written by Spring Boot developers.


---

6. The DataSource example

A DataSource is basically an abstraction for obtaining database connections.

Conceptually:

Your application
       ↓
DataSource
       ↓
Database connections
       ↓
Database

Suppose your project has H2.

Application
   ↓
H2 database
   ↓
DataSource

Spring Boot can detect the relevant database infrastructure and configure a suitable DataSource.

You don't have to write:

new SomeDataSource(...);

and manually configure every low-level setting.

Spring Boot handles common cases.


---

7. Embedded database vs connection pool

The paragraph about H2 and HikariCP is describing another conditional decision.

For example:

What database setup does this application have?
               ↓
       ┌───────┴─────────┐
       ↓                 ↓
 Embedded DB          Connection pool
   H2 etc.              HikariCP
       ↓                 ↓
 Appropriate DataSource configuration

So Spring Boot's job isn't merely:

> "Create a DataSource."



It's more like:

> "Given the libraries and environment present, determine which DataSource arrangement makes sense."



This is why auto-configuration can save a lot of setup work.


---

8. The most powerful feature: Backing off

This part is very important for interviews and real development.

Spring Boot can automatically create a bean.

But suppose you create the bean yourself.

Spring Boot generally says:

> "Okay, you've configured it. I'll step aside."



This is called backing off.

The classic condition is:

@ConditionalOnMissingBean

Conceptually:

Does a DataSource bean already exist?
        │
   ┌────┴────┐
   │         │
  YES       NO
   │         │
   ↓         ↓
Back off   Create one

That is brilliant because it gives you both:

Convenience

Spring Boot configures common things automatically.

Control

You can replace its defaults with your own configuration.


---

9. Example of backing off

Suppose Spring Boot would normally provide:

DataSource → H2

But you define:

@Bean
DataSource myDataSource() {
    // your custom configuration
}

Now there is already a DataSource.

Spring Boot's auto-configuration sees that and effectively says:

User already provided DataSource
              ↓
Don't create another default one

Your bean wins.

This is why Spring Boot is not a "black box" that traps you into its defaults.


---

10. Auto-configuration in one formula

A useful mental model is:

Dependency present
        +
Conditions satisfied
        +
User hasn't provided replacement
        ↓
Spring Boot creates/configures bean

That one formula explains a huge amount of Spring Boot.


---

11. Spring Boot 4's more modular auto-configuration

Your book emphasizes a change in Spring Boot 4.

Earlier versions had more broadly grouped auto-configuration.

Spring Boot 4 moves toward smaller, technology-focused modules.

Conceptually:

Older style

Large auto-configuration package
├── lots of unrelated configuration
├── many technologies
└── large artifact

Spring Boot 4 approach

Technology-focused configuration

MVC
JPA
MongoDB
Kafka
Redis
...

with more explicit boundaries.

The point isn't really:

> "369 KB is cool."



The more important architectural idea is:

> The application's dependency graph should communicate which technologies the application intentionally uses.



That makes the build easier to understand and reduces accidental behavior from unrelated transitive dependencies.


---

12. Why "explicit dependencies" matter

Imagine your project has:

Project
 ├── Web
 ├── JPA
 ├── PostgreSQL
 └── Redis

A developer looking at your pom.xml can immediately understand:

This is a web application
This uses JPA
This uses PostgreSQL
This uses Redis

Your build file starts acting as part of the application's architecture documentation.

That's a really useful idea.


---

13. The huge Spring Boot auto-configuration list

The book then gives a massive list:

Kafka
MongoDB
Redis
JPA
Flyway
Liquibase
Thymeleaf
Jackson
WebFlux
MVC
WebSocket
Mail
Quartz
...

Don't memorize this.

Seriously.

You need to understand the pattern:

Add technology dependency
        ↓
Spring Boot detects it
        ↓
Relevant auto-configuration may activate
        ↓
Required infrastructure beans appear

For example:

Kafka dependency
      ↓
Kafka-related configuration

JPA dependency
      ↓
JPA-related configuration

Redis dependency
      ↓
Redis-related configuration

WebMVC dependency
      ↓
MVC-related configuration


---

14. Starters — the second major concept

Now we move from:

> "What does Spring Boot automatically configure?"



to:

> "How do I tell Spring Boot what technologies I want?"



Answer:

Starters

A starter is a convenient dependency grouping for a particular capability.

For example:

<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-webmvc</artifactId>
</dependency>

This communicates:

> "I want the MVC web stack."




---

15. Why not add every dependency individually?

Imagine building a web application manually.

You might need a collection of things such as:

Spring MVC
Spring Web
JSON support
embedded servlet container
validation
logging
...

Managing all of them manually can become annoying.

A starter gives you a curated starting point.

Think of it like a restaurant menu.

Instead of ordering:

bread
cheese
sauce
vegetables
spices
...

you order:

Web Application Package

and receive the components normally associated with that capability.


---

16. Starter vs auto-configuration

These two are related, but they're not the same thing.

This distinction is critical.

Starter

Helps you bring the required dependencies onto the classpath.

Auto-configuration

Uses those available dependencies to configure Spring beans/infrastructure.

So:

Starter
   ↓
Dependencies appear
   ↓
Auto-configuration detects them
   ↓
Beans/configuration appear

Remember:

> Starter = what you add.
Auto-configuration = what Spring Boot does with what you added.




---

17. MVC vs WebFlux

The book gives an excellent example of architectural intent.

Spring MVC

Traditional servlet-based web architecture.

Conceptually:

Browser
   ↓
HTTP request
   ↓
Servlet container
   ↓
Spring MVC
   ↓
Controller
   ↓
Service
   ↓
Repository

Spring WebFlux

Reactive/non-blocking web programming model.

Conceptually:

Request
   ↓
Reactive pipeline
   ↓
Non-blocking processing
   ↓
Response

So when you choose:

spring-boot-starter-webmvc

you are explicitly saying:

> "My application is using the MVC servlet model."



Whereas:

spring-boot-starter-webflux

signals:

> "My application is using the reactive WebFlux model."



That makes the build itself communicate architectural choices.


---

18. What does starter-webmvc approximately give you?

The book lists things such as:

Spring MVC
Spring Web
Jackson
Embedded servlet container
Validation support
Logging/configuration
MVC auto-configuration

So instead of thinking:

starter-webmvc = one library

think:

starter-webmvc
      ↓
A curated dependency set
      ↓
Enough infrastructure to start an MVC web app


---

19. Jakarta EE

You'll see this a lot in modern Spring Boot.

Previously Java enterprise APIs used:

javax.*

Modern Jakarta EE uses:

jakarta.*

For example:

jakarta.servlet.*

rather than:

javax.servlet.*

The important thing for you is simply:

Old Java EE
    ↓
javax.*

Modern Jakarta EE
    ↓
jakarta.*

Modern Spring Boot uses the Jakarta namespace for relevant enterprise APIs.


---

20. Configuration Properties — the third major concept

Now imagine Spring Boot creates Tomcat automatically.

Tomcat needs settings such as:

Port
Threads
SSL
Context path
...

What if you want port 9000 instead of the default?

You can write:

server.port=9000

in:

src/main/resources/application.properties

Then Spring Boot reads it.

Conceptually:

application.properties
        ↓
server.port=9000
        ↓
Spring Boot configuration
        ↓
embedded server configured
        ↓
server listens on 9000


---

21. Why this is powerful

Your Java code doesn't have to contain:

server.setPort(9000);

Instead, configuration is separated from code.

Code
 ↓
Business logic

Properties
 ↓
Environment/application configuration

That's a very important engineering principle.


---

22. application.properties vs application.yml

Spring Boot supports both.

Properties:

server.port=9000
spring.application.name=books

YAML:

server:
  port: 9000

spring:
  application:
    name: books

They're two configuration formats.

The underlying goal is the same:

External configuration
        ↓
Spring Boot
        ↓
Application settings


---

23. Creating your own configuration properties

This part is especially important.

Spring Boot doesn't restrict configuration properties to its own settings.

You can create yours.

The book uses:

@Component
@ConfigurationProperties(prefix = "my.app")
public class MyCustomProperties {

    private String header;
    private String footer;

    // getters and setters
}

Then:

my.app.header=Learning Spring Boot 4
my.app.footer=Find all the source code...

Spring Boot binds those values into your object.


---

24. What does "binding" mean?

This is another word you should understand.

Suppose:

my.app.header=Hello
my.app.footer=Goodbye

Spring Boot creates your object:

MyCustomProperties

header → "Hello"
footer → "Goodbye"

So:

Properties file
      ↓
Property binding
      ↓
Java object

This is called configuration property binding.


---

25. Understanding the two annotations

@Component

@Component

means:

> "Spring, create/manage an instance of this class as a bean."



So:

MyCustomProperties
       ↓
@Component
       ↓
Spring Bean

@ConfigurationProperties

@ConfigurationProperties(prefix = "my.app")

means:

> "Bind configuration properties beginning with my.app into this object."



So:

my.app.header
my.app.footer
      ↓
MyCustomProperties

Together:

@Component
@ConfigurationProperties(prefix = "my.app")

means approximately:

> "Create this class as a Spring bean and populate it from configuration properties beginning with my.app."




---

26. Why is this better than @Value everywhere?

You will also encounter:

@Value("${my.app.header}")
private String header;

That's useful for individual values.

But for larger configuration groups, @ConfigurationProperties is often cleaner:

DatabaseSettings
SecuritySettings
PaymentSettings
MailSettings
AppSettings

Each can be represented as a typed configuration object.

For example:

app.database.url
app.database.username
app.database.timeout
app.database.pool-size

can map into:

DatabaseProperties

This scales nicely.


---

27. Don't store secrets in your code

The book's GitHub credential example is teaching a very important engineering lesson.

Bad:

String githubPersonalCode =
    "my-secret-password";

That secret can accidentally end up in:

Git repository
JAR
logs
source code
backups

Instead, use external configuration/secrets management.

Conceptually:

Application code
      +
External secret
      ↓
Application

For serious production systems, dedicated secret-management systems are usually preferable to simply putting secrets in a checked-in properties file.


---

28. Externalized configuration

This is one of Spring Boot's most useful features.

Imagine your application is the same in:

Development
Testing
Production

But each environment needs different settings.

You don't want:

Different Java source code for every environment

Instead:

Same JAR
   +
Different configuration

For example:

Application
   │
   ├── dev configuration
   ├── test configuration
   └── production configuration


---

29. Profiles

Profiles are basically named environment/configuration sets.

Typical example:

dev
test
prod

You can have:

application.properties
application-dev.properties
application-test.properties

Think of it as:

application.properties
        ↓
common/default settings

application-dev.properties
        ↓
development overrides

application-test.properties
        ↓
testing overrides


---

30. Example with a database URL

Base configuration:

my.app.databaseUrl=production-db

Testing configuration:

my.app.databaseUrl=test-db

Activate test profile:

-Dspring.profiles.active=test

Then conceptually:

application.properties
        ↓
base value

application-test.properties
        ↓
override value

Final effective value
        ↓
test-db


---

31. The idea of precedence

Now we're getting into a deeper Spring Boot concept.

Suppose the same property exists in several places:

application.properties
environment variable
system property
command-line argument
test property

Which one wins?

Spring Boot has an ordering/precedence model.

Generally, later/higher-precedence property sources can override earlier/lower-precedence ones.

The important mental model is:

Many possible sources
       ↓
Property precedence
       ↓
One effective value

You don't need to memorize the complete list on day one.

But you absolutely should understand:

> The final configuration value can come from several places, and Spring Boot resolves them according to precedence rules.




---

32. Why command-line arguments are useful

Suppose you build one JAR:

myapp.jar

You could run it with different settings without changing the JAR itself.

For example:

java -jar myapp.jar --server.port=9000

Conceptually:

Same JAR
   ↓
different startup arguments
   ↓
different behavior

That's very useful in deployment.


---

33. External configuration file beside the JAR

You can also have:

myapp.jar
application.properties

instead of packaging every setting inside the JAR.

Conceptually:

Deployment folder
├── myapp.jar
└── application.properties

Now:

Application
    ↓
loads external configuration

This is one way to make a single application artifact work in multiple environments.


---

34. SPRING_CONFIG_LOCATION

This takes external configuration one step further.

You can tell Spring Boot explicitly where configuration files are located.

Conceptually:

Environment
    ↓
SPRING_CONFIG_LOCATION
    ↓
External config directory/file
    ↓
Spring Boot

This is useful when configuration is maintained separately from the application artifact.


---

35. Property-based conditional beans

Now we combine two ideas:

Configuration properties
+
Conditional bean creation

The book gives:

@Bean
@ConditionalOnProperty(prefix="my.app", name="video")
YouTubeService youTubeService() {
    return new YouTubeService();
}

This means:

> Create this bean only when the specified property condition is satisfied.



So:

my.app.video=youtube

can cause:

YouTubeService ✅

to be created.

Without the property:

YouTubeService ❌

isn't created.


---

36. Why is this useful?

Imagine your application supports:

YouTube
Vimeo

Instead of hardcoding the choice, configuration determines it.

For example:

my.app.video=youtube

then:

YouTubeService

Or:

my.app.video=vimeo

then:

VimeoService

That means one application can behave differently depending on configuration.


---

37. havingValue

Now we make the condition explicit.

@Bean
@ConditionalOnProperty(
    prefix = "my.app",
    name = "video",
    havingValue = "youtube"
)
YouTubeService youTubeService() {
    return new YouTubeService();
}

and:

@Bean
@ConditionalOnProperty(
    prefix = "my.app",
    name = "video",
    havingValue = "vimeo"
)
VimeoService vimeoService() {
    return new VimeoService();
}

Now Spring effectively does:

my.app.video
      │
 ┌────┴─────┐
 ↓          ↓
youtube    vimeo
 ↓          ↓
YouTube    Vimeo
Service    Service

That's a very clean configuration-driven design.


---

38. Important distinction: Profiles vs @ConditionalOnProperty

These solve related but different problems.

Profiles

Usually represent environment/configuration groups:

dev
test
prod

@ConditionalOnProperty

Usually represents feature/configuration choices:

video=youtube
video=vimeo
feature-x=true

So think:

Profile
→ Which environment/configuration am I running in?

Property condition
→ Which component/feature should exist?

They can be used together.


---

39. Dependency management — the fourth major concept

This is less flashy than auto-configuration, but extremely important in real projects.

Suppose you use:

Spring Framework
Spring Data
Spring Security
Spring Integration
Jackson
Hibernate
...

Each has versions.

And those projects may themselves depend on other libraries.

Now imagine manually choosing:

Spring Framework 7.x
Spring Data X
Spring Security Y
Hibernate Z
Jackson A
...

You can easily create incompatible combinations.


---

40. What Spring Boot does

Spring Boot provides a curated set of compatible versions.

Conceptually:

Spring Boot 4.x
       ↓
Version-aligned ecosystem
       ├── Spring Framework
       ├── Spring Data
       ├── Spring Security
       ├── Jackson
       ├── Hibernate
       └── other libraries

This removes a huge amount of version micromanagement.


---

41. What is a BOM?

BOM means:

Bill of Materials

Don't think of it as some scary Maven thing.

Think of it as:

> A centrally maintained map of compatible dependency versions.



For example:

Dependency                         Version
------------------------------------------------
Spring Framework                   X
Spring Data                        Y
Jackson                            Z
Hibernate                          A
...

Your build system uses that information.


---

42. Why this is so useful

Without dependency management:

Upgrade library A
      ↓
Library B breaks
      ↓
Upgrade B
      ↓
Library C breaks
      ↓
Upgrade C
      ↓
Something else breaks

With a version-aligned Spring Boot ecosystem:

Upgrade Spring Boot
        ↓
Aligned dependency versions
        ↓
Many ecosystem components update together

It doesn't guarantee that every upgrade is painless, but it dramatically reduces manual version coordination.


---

43. Maven and Gradle

Spring Boot supports both major Java build systems:

Maven
Gradle

You'll encounter files such as:

Maven

pom.xml

Gradle

build.gradle

or Kotlin DSL:

build.gradle.kts

The dependency-management concept works with either.


---

44. Don't manually specify versions unnecessarily

A common beginner mistake is:

<dependency>
    ...
    <version>random-version</version>
</dependency>

for every Spring dependency.

When Spring Boot's dependency management already provides the compatible version, you generally let Boot manage it.

This is one of the reasons starters and the BOM work so well together.


---

45. Putting everything together

Now let's combine the whole chapter.

Suppose you're building a web application with PostgreSQL.

You add a web starter and JPA/database-related dependencies.

Conceptually:

Your build file
                       │
             ┌─────────┴─────────┐
             ↓                   ↓
      Web starter         JPA/database deps
             │                   │
             └─────────┬─────────┘
                       ↓
                  Classpath
                       ↓
               Spring Boot starts
                       ↓
             Auto-configuration
                       ↓
        ┌──────────────┼──────────────┐
        ↓              ↓              ↓
      MVC            JPA          DataSource
        │              │              │
        └──────────────┼──────────────┘
                       ↓
                ApplicationContext
                       ↓
                  Your beans
                       ↓
               Dependency Injection

Then configuration enters:

application.properties
        ↓
server.port=9000
        ↓
Spring Boot configuration
        ↓
Server runs on 9000

And profiles:

application.properties
application-dev.properties
application-test.properties

allow environment-specific settings.

And dependency management:

Spring Boot version
        ↓
compatible ecosystem versions

keeps the build manageable.


---

46. One complete mini example

Let's imagine:

# application.properties

server.port=9000

my.app.video=youtube
my.app.header=My Video App

Then:

@Component
@ConfigurationProperties(prefix = "my.app")
public class MyCustomProperties {

    private String header;
    private String video;

    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    public String getVideo() {
        return video;
    }

    public void setVideo(String video) {
        this.video = video;
    }
}

Then:

@Configuration
public class VideoConfiguration {

    @Bean
    @ConditionalOnProperty(
        prefix = "my.app",
        name = "video",
        havingValue = "youtube"
    )
    YouTubeService youTubeService() {
        return new YouTubeService();
    }

    @Bean
    @ConditionalOnProperty(
        prefix = "my.app",
        name = "video",
        havingValue = "vimeo"
    )
    VimeoService vimeoService() {
        return new VimeoService();
    }
}

Now the flow becomes:

application.properties
        │
        ├── server.port=9000
        │         ↓
        │     server → 9000
        │
        ├── my.app.header=My Video App
        │         ↓
        │   MyCustomProperties.header
        │
        └── my.app.video=youtube
                  ↓
        @ConditionalOnProperty
                  ↓
          YouTubeService ✅
          VimeoService   ❌

That's Spring Boot in action.


---

47. The most important distinction in the entire chapter

This is worth putting in your notes:

STARTER
                ↓
      Adds dependencies
                ↓
           CLASSPATH
                ↓
       AUTO-CONFIGURATION
                ↓
      Creates/configures beans
                ↓
       APPLICATION CONTEXT
                ↓
    DEPENDENCY INJECTION
                ↓
          APPLICATION

And:

CONFIGURATION PROPERTIES
          ↓
    customize behavior

And:

PROFILES
   ↓
different environments/configurations

And:

CONDITIONAL BEANS
   ↓
create components only when conditions match

And:

BOM / DEPENDENCY MANAGEMENT
   ↓
compatible library versions


---

48. What you should actually memorize

Don't memorize the giant technology list.

Memorize these eight ideas:

1. Starter

I want this capability.

2. Auto-configuration

Spring Boot sees the capability and configures appropriate infrastructure.

3. Conditional configuration

Configuration only happens when certain conditions are true.

4. Backing off

If I provide my own bean, Spring Boot can step aside.

5. Configuration properties

I can customize Spring Boot/application behavior without hardcoding it.

6. Profiles

I can have different configurations for dev/test/prod.

7. Conditional beans

Properties can decide which beans exist.

8. Dependency management

Spring Boot keeps its ecosystem versions aligned.


---

49. A real-world analogy

Imagine Spring Boot is a professional workshop.

You say:

"I want to build a car."

Starter

You order the car-building kit.

Starter
↓
Relevant parts arrive

Auto-configuration

The workshop automatically sets up the common machinery.

Dependencies
↓
Auto-configuration
↓
Infrastructure ready

Configuration properties

You tell it:

Color = black
Engine mode = X
Port = 9000

Profiles

You have different setups:

Development car
Testing car
Production car

Backing off

You say:

"I brought my own engine."

Workshop:

"Fine. We'll use yours."

Dependency management

The workshop ensures the parts are compatible.

That is basically the philosophy of Spring Boot.


---

50. The deeper philosophy

The chapter is really teaching this principle:

> Spring Boot gives you sensible defaults, but never takes away your ability to customize the application.



That is why these two ideas appear together:

AUTO-CONFIGURATION
       +
BACKING OFF

They work as a pair.

Without auto-configuration:

Too much manual work.

Without backing off:

Too little control.

Together:

Automatic by default
+
Customizable when needed
=
Spring Boot


---

Final mental model

Keep this diagram in your notes:

SPRING BOOT
                              │
         ┌────────────────────┼────────────────────┐
         │                    │                    │
         ↓                    ↓                    ↓
      STARTERS         AUTO-CONFIGURATION     CONFIGURATION
         │                    │                    │
         ↓                    ↓                    ↓
    Dependencies        Conditional rules       Properties
         │                    │                    │
         └────────────────────┼────────────────────┘
                              ↓
                     APPLICATION CONTEXT
                              │
                      ┌───────┴────────┐
                      ↓                ↓
                    Beans         Dependency Injection
                      │                │
                      └───────┬────────┘
                              ↓
                        YOUR APP

And underneath everything:

DEPENDENCY MANAGEMENT / BOM
                           ↓
                Compatible versions

Once this model is clear, Spring Boot annotations stop feeling like magic. @Bean, @Component, @ConfigurationProperties, @ConditionalOnProperty, auto-configuration, starters, profiles, and dependency management all become different pieces of the same system.