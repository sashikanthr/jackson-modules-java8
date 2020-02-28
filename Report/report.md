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

This project (as the creators of the repo wrote) is a multi-module umbrella project for [Jackson](<https://github.com/FasterXML/jackson-modules-java8>) modules needed to support Java 8 features, especially with Jackson 2.x that only requires Java 7 for running (and until 2.7 only Java 6).

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


### Requirements affected by functionality being refactored

*Optional (point 3): trace tests to requirements.*

The requirements for the solution of this issue are the following:

#### Requirement 1

* ID: issue/#116-requirement1
* Title: Addition of a new flag to deal with fraction part
* Desciption: Addition of a new flag and set the flag with a mapper serializes without a fraction part. The flag is defined in SerializationFeture in Jackson bind, and used when building the ObjectMapper. With this feature set, the fraction part of a number will be ignored when doing serialization. 

#### Requirement 2

* ID: issue/#116-requirement2
* Title: Original test cases still passes
* Desciption: It should not break any existing tests, i.e. leaving the functionality as it is. Ensure all test cases pass after begin refactored. 

### Structure of the issue

The structure of the issue is represented as follows.

![issue-structure-before](<issue-structure-before.png>)

This is the structure of the issue after we addressed it. 

![issue-structure-after](<issue-structure-after.png>)

### Existing test cases relating to refactored code

### Test results

*Overall results with link to a copy or excerpt of the logs (before/after refactoring).*

### Patch/fix

*The fix can be copied or linked to (git diff).*

*Optional (point 4): the patch is clean.*

Both our solutions are clean in terms of code structure as well as output cleanness. 

Solution 1 uses variable `_useFraction` as a flag, pretty much like the variable `_useNanoseconds`, 
which brings high complexity into the code. It works well on all test cases, but to some extent 
it increases complexity and reduce the readability. 

Solution 2 does some modification on jackson bind package. It defines a serialization feature 
called `WRITE_TIMESTAMP_WITHOUT_FRACTION` in place of a single flag, which is likely to be 
a more formal way to address this issue because it integrates much bette within the structure of jackson. 
This solutions involves less class in the modification, making it eaiser to analyze, maintain and use. 

As for test cases, we add new test cases illustrating what the issue is, when it arises, why it arises and 
how it can be addressed. Some test cases are mainly for better understanding and may be removed in 
future patch. 

*Optional (point 5): considered for acceptance (passes all automated checks).*

## Effort spent

*For each team member, how much time was spent in*

| Name       | plenary discussions/meetings   | discussions within parts of the group | reading documentation | configuration and setup | analyzing code/output | writing documentation | writing code | running code |
| ---------- | ------------------------------ | ------------------------------------- | --------------------- | ----------------------- | --------------------- | --------------------- | ------------ | ------------ |
| Chang      | 6                              | 2                                     | 3                     | 0.5                     | 7                     | 2                     | 4            | 0.5          |
| Jacob      | 6                              | 3                                     | 2                     | 4                       | 3                     | 1                     | 3            | 1            |
| Johan      | 6                              | 3                                     | 2                     | 0.5                     | 7                     | 1                     | 4            | 0.5          |
| Sashikanth | 6                              | 4                                     | 2                     | 1                       | 6                     | 1                     | 4            | 1            |
| Nour       | 6                              | 4                                     | 2                     | 1                       | 5                     | 4.5                     | 1            | 1.5            |

*For setting up tools and libraries (step 4), enumerate all dependencies you took care of and where you spent your time, if that time exceeds 30 minutes.*

- Nour: I had some small problem with JAVA_HOME (it wasn't set up correctly). I had also an issue with maven, but once I installed mave 
correctly and set up JAVA_Home correctly, the project compiled and the all the tests succeeded. 

- Jacob: All tests but one ran correctly on my main machine, took quite some time to try and understand what caused the issue. Did not fully get to the bottom of it in order to file an issue, but it seems like an issue with the java version used. Might look further into it later but I stopped since it was working fully on my secondary computer and to work on the issue instead of setup.


### Statement of Contributions

#### Chang (@changf)

- Involved in discussions on the project.
- Analysed the code and architecture to understand the issue.
- Create test cases for the issue for understanding and testing. 
- Provide a simple solution to the issue. 

#### Jacob (@jadlers)

- Involved in discussions on the project
- Analysed the code and architecture to understand the issue.
- Looked into potential issues with deserialization caused by the new feature
- Created a build environment to locally develop the two modules
  `jackson-modules-8` and `databind`
- Added a new feature along with documentation to `databind` necessary for the
  changes made in `jackson-modules-8`

#### Johan (@ande9)
- Involved in discussions on the project.
- Analysed the code and architecture to understand the issue.
- Worked on deserializing.

#### Nour (@almajni)
- Involved in discussions on the project.
- Analysed the code and architecture to understand the issue.
- Create the UML diagrams and analyse the whole structure of the repo

#### Sashikanth (@sraa)
- Involved in discussions on the project.
- Analysed the code and architecture to understand the issue.
- Development and Unit Test for Duration Serializer class.

## Overall experience

*What are your main take-aways from this project? What did you learn?*

To understand a project and how everything works is very time consuming. Most of our time was spent trying to figure out how the code works even though we only focused on a small part of the code base. Another take-away was that it is still possible to work on an issue even if you dont have knowledge of the entire project. 

*Optional (point 6): How would you put your work in context with best software engineering practice?*

In SEMAT kernel's point of view, there are three discrete areas of concern when developing a software system: Customer, Solution and Endeavor, each with several Alphas representing the essential things to do. We'll take each one of Alphas from these three areas, combine with our work and analyze the benefits, drawbacks and limitations of our work. 

Stakeholders are the people, groups and organizations that affect or are affected by the software system. Once we decide the issue we're going to put hands on, the stakeholders are identified. For this project, we have different groups of stakeholders. The most obvious one is the issue provider, who has the willing to make changes to the software system and might be the key stakeholder as well. Other stakeholders include the users of the software system, i.e. Jackson library in this project, which of course include us. The biggest drawback during our work is that we have merely no contact with any stakeholders, and the responsibilities of the stakeholders during our development is unclear, which means we didn't receive any assistance from any stakeholders. The only stakeholder we have contact is the repository administrator, who is responsible for evaluating our work and decide whether to accept our pull request. We're around `Involved` state right now, waiting feedbacks from stakeholders who are interested. 

Requirements are what the software system must do to address. Conceived phase is finished by initiating a new issue, where the issue provider provides detailed information about the description, requirements, necessity of the issue. The purpose is agreed by setting the issue explictly a `new-feature` tag. Then it's our work to make the requirements more clear by creating demostrations and cases, and understand constraints by analyzing code. Since we only work on a little issue in the whole project, we can't provide essential characteristics of the new system. The requirements are then shared within the team to evalute its feasibility, and are testable using JUnit. We are now around `Addressed` state, waiting for acceptance. 

Team is a group of people actively engaged in the development, and in this project it's clearly us. The mission for us is to address an issue in a proper way, which is quite clear. We have workload devided to each person, and we all accept and understand what we should do and how. We are all familiar with each other, which is helpful for communication. Unfortunately, we don't have explicit leadership. I think we work as a cohesive unit in an open and honest environment. We meet each other regularly, but the limitation is that we have rather ambiguous commitments each time, and no milestone is set. We are now at the end of `Performing` state and are available for other assignments. 

*Optional (point 7): Is there something special you want to mention here?*
