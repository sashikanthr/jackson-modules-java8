# Report for assignment 4

*This is a template for your report. You are free to modify it as needed. It is not required to use markdown for your report either, but the report has to be delivered in a standard, cross-platform format.*

## Project

* Name: jackson-modules-java8
* URL: <https://github.com/FasterXML/jackson-modules-java8>
* A set of support modules for Java 8 datatypes (Optionals, date/time) and features (parameter names), used by the Jackson JSON parser. 


## Onboarding experience

*Did it build and run as documented?*

*See the assignment for details; if everything works out of the box, there is no need to write much here. If the first project(s) you picked ended up being unsuitable, you can describe the "onboarding experience" for each project, along with reason(s) why you changed to a different one.*

The project uses `maven` as the build tool and it built correctly on all our machines from the beginning. All tests 593 tests pass and no tests are skipped.

## UML class diagram and the project description

This project (as the creators of the repo wrote) is a multi-module umbrella project for \[Jackson\](<https://github.com/FasterXML/jackson-modules-java8>) modules needed to support Java 8 features, especially with Jackson 2.x that only requires Java 7 for running (and until 2.7 only Java 6).

This project/repo consists of 3 models:

* The first module aims to add supports for JDK datatypes included in version 8 which can not be directly supported by core databind due to baseline being JDK 6.
* The third module aims to add support for accessing parameter names; a feature added in JDK 8.

Both the first and the third module only contain one class.

* The second module called datetime and it aims to make Jackson recognize Java 8 Date & Time API data types (JSR-310).

The the second module is the longest one and has many classes. it has also the most of the code written/used in this project. This module has also two main packages:

* One package has classes that are responsible for deserializing some data types used in date/time such as year, month, duration, .... etc. This package is called `deser`


* One package has class that are responsible for serializing some data types used in date/time such as year, month, duration, .... etc. This package is called `ser`

The following diagram represent the overall UML diagram for the classes in the `deser`:

![DeserImage](<PackageDeser.png>)

The following diagram represent the overall UML diagram for the classes in the `ser`:

![serImage](<PackageSer.png>)

Note that in both `deser` and `ser` packages there is another package called `key`. The `key` package in the `ser` 
has just one class. But the `key` package in the `deser` package has many classes. The java classes in the key packages 
define also API used for serializing/deserializing some date/time data types. For example in the the `key` package in 
the `deser` package many java classes there are children of the abstract class `KeyDeserializer`. And according to 
the documentation, this class defines API used for deserializing JSON content field names into Java Map keys.
While many classes in the main `deser` package are children of the abstract class `JsonDeserializer` which defines 
according to the documentation an API used by ObjectMapper (and other chained JsonDeserializers too) to deserialize 
Objects of arbitrary types from JSON, using provided JsonParser.
                               
The issue that we are trying to fix is located in a function called `serialize()` in the class `InstantSerializerBase` in the `ser` package. That is why, we will focus little more in it and give a little more deeper investigation and explanation:

The `InstantSerializer` class extends the class `InstantSerializerBase`. Thus, when a function wants to serialize an instance of `InstantSerializer`, the function need to call the function `serialize()` in the parent class i.e. in `InstantSerializerBase`. However this class also extends another class called `JSR310FormattedSerializerBase`.

The child class `InstantSerializer` is actually just a class that initialize or create an object of `InstantSerializer` which is actually an object of the `InstantSerializerBase` class. No more methods exists in `InstantSerializer` class. The following UML diagram explains in more details the relationships and the dependencies between these classes and even little other classes:

![InstantSerializer](<InstantSerializer.png>)

However, as written in the requirements below, we need to fix things in the corresponding derserilazer classes e.g. `InstantDeserializer`, `JSR310DateTimeDeserializerBase` and `JSR310DeserializerBase` if we change something in the serializer classes mentioned above. The image below has a UML diagram that explain the dependencies and relationships between these classes:

![InstantDerserializer](<InstantDeserializer.png>)

## Selected issue(s)

* Title: Allow Instant to be serialized as `epochSecond` without the fraction part
* URL: <https://github.com/FasterXML/jackson-modules-java8/issues/116>

*Summary in one or two sentences*

When serializing an `Instant` timestamp, which is known to be full seconds, a number of zeroes are appended to the end. An option to disable writing any decimal part when serializing should be added.

The structure of the issue is represented as follows.

![issue-structure-before](<issue-structure-before.png>)
![issue-structure-after](<issue-structure-after.png>)

### Requirements affected by functionality being refactored

*Optional (point 3): trace tests to requirements.*

The requirements for the solution of this issue are the following:

* Addition of a new flag should not break any existing tests, i.e. leaving the functionality as it is
* Setting the flag with a mapper serializes (timestamps?) without a fraction part
* Deserializing an earlier serialized value which had the flag set should get the same value back, given that the fraction is zero.

### Existing test cases relating to refactored code

### Test results

*Overall results with link to a copy or excerpt of the logs (before/after refactoring).*

### Patch/fix

*The fix can be copied or linked to (git diff).*

*Optional (point 4): the patch is clean.*

*Optional (point 5): considered for acceptance (passes all automated checks).*

## Effort spent

*For each team member, how much time was spent in*

Name | plenary discussions/meetings | discussions within parts of the group | reading documentation | configuration and setup | analyzing code/output | writing documentation | writing code | running code
-----|--------------------------------|-----------------------------------------|-------------------------|---------------------------|-----------------------|-------------------------|---------------|---------
Chang | - | - | - | - | - | - | - | - |
Jacob | - | - | - | - | - | - | - | - |
Johan | - | - | - | - | - | - | - | - |
Sashikanth | - | - | - | - | - | - | - | - |
Nour | 7 | 7 | 2 | 1 | 2.5 | 5 | 1 | 1 |

*For setting up tools and libraries (step 4), enumerate all dependencies you took care of and where you spent your time, if that time exceeds 30 minutes.*

Nour: I had some small problem with JAVA_HOME (it wasn't set up correctly). I had also an issue with maven, but once I installed mave 
correctly and set up JAVA_Home correctly, the project compiled and the all the tests succeeded. 

## Overall experience

*What are your main take-aways from this project? What did you learn?*

*Optional (point 6): How would you put your work in context with best software engineering practice?*

*Optional (point 7): Is there something special you want to mention here?*
