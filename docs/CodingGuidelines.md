# Code Style Guidelines
## Introduction
The intention of this guide is to provide a set of conventions that encourage good code. While some suggestions are more strict than others, you should always practice good judgement.

Much of this guide was adapted from the [Google Code Style Guide for Java](http://google.github.io/styleguide/javaguide.html) and [Twitter Code Style Guide for Java](https://github.com/twitter/commons/blob/master/src/java/com/twitter/common/styleguide.md). It is highly recommended you read through those guides, as they cover the topics in this document in far greater depth.

## Formatting
### Braces
Braces are used with `if`, `else`, `for`, `do` and `while` statements, even when the body is empty or contains only a single statement.
```
// Like this.
if (x < 0) {
    negative(x);
} else {
    nonnegative(x);
}

// Not like this.
if (x < 0)
    negative(x);

// Also not like this.
if (x < 0) negative(x);
```

### Column Limit
You should follow the convention set by the body of code you are working with. We tend to use 100 columns for a balance between fewer continuation lines but still easily fitting two editor tabs side-by-side on a reasonably-high resolution display.

### Line Breaks
There are generally two reasons to insert a line break:
- Your statement exceeds the column limit.
- You want to logically separate a thought.

Writing code is like telling a story. Written language constructs like chapters, paragraphs, and punctuation (e.g. semicolons, commas, periods, hyphens) convey thought hierarchy and separation. We have similar constructs in programming languages; you should use them to your advantage to effectively tell the story to those reading the code.

### Imports
Wildcard imports make the source of an imported class less clear. They also tend to hide a high class fan-out.
```
// Bad.
//   - Where did Foo come from?
import com.twitter.baz.foo.*;
import com.twitter.*;

interface Bar extends Foo {
  ...
}

// Good.
import com.twitter.baz.foo.BazFoo;
import com.twitter.Foo;

interface Bar extends Foo {
  ...
}
```

## Naming
### Package Names
Package names are all lowercase, with consecutive words simply concatenated together (no underscores). For example, `com.example.deepspace`, not `com.example.deepSpace` or `com.example.deep_space`.

### Class Names
Class names are written in UpperCamelCase.

Test classes are named starting with the name of the class they are testing, and ending with Test. For example, HashTest or HashIntegrationTest.

## Programming Practices
### `@Override`
A method is marked with the @Override annotation whenever it is legal. This includes a class method overriding a superclass method, a class method implementing an interface method, and an interface method respecifying a superinterface method.

### `@Nullable` and `@NotNull`
To eliminate NullPointerExceptions, you must be disciplined about null references. We've been successful at this by following and enforcing a simple rule: Every parameter is non-null unless explicitly specified. @Nullable can be used to annotate a parameter that permits the null value.
```
import static com.google.common.base.Preconditions.checkNotNull;
import javax.annotation.Nullable;

public class Person {
  ...

  public Person(String firstName, String lastName, @Nullable Phone phone) {
    this.firstName = checkNotNull(firstName, "firstName");
    this.lastName = checkNotNull(lastName, "lastName");
    this.phone = phone;
  }
```

As such, `@NotNull` is not necessary, because it is implied that the parameter must not be null.

### Using `final` Keyword
Usage of the `final` keyword is recommended but not mandatory for variables in methods. It's superfluous on local variables, as it doesn't convey useful extra information.

However, all method parameters must be declared `final`. It is important to be explicit that the variable doesn't change.
```
public void doSomething(final String v1, final int v2, final Integer v3) {
    ...
}
```

### Throws Clause in Method Signature
Unchecked exceptions do not need to be explicitly thrown from a method with `throws` if the method is documented with a Javadoc comment that uses `@throws`. This is the preferred method.
```
/**
  * Does a thing.
  *
  * @throws IllegalArgumentException if the argument is invalid
  **/
public void doSomething(final String arg) {
    ...
    throw new IllegalArgumentException("reason");
}
```

If the exceptions are not documented, they should be included in the method signature.
```
public void doSomething(final String arg) throws IndexOutOfBoundsException {
    ...
    throw new IndexOutOfBoundsException("reason");
}
```

### `Log and Throw` Antipattern
It might seem logical to log the exception where it was thrown and then rethrow it to the caller who can implement a use case specific handling, but you should not do it for the following three reasons:
- You don’t have enough information about the use case the caller of your method wants to implement. The exception might be part of the expected behavior and handled by the client. In this case, there might be no need to log it. That would only add a false error message to your log file which needs to be filtered by your operations team.
- The log message doesn’t provide any information that isn’t already part of the exception itself. Its message and stack trace should provide all relevant information about the exceptional event. The message describes it, and the stack trace contains detailed information about the class, method, and line in which it occurred.
- You might log the same exception multiple times when you log it in every catch block that catches it. That messes up the statistics in your monitoring tool and makes the log file harder to read for your operations and development team.

```
//Don't do this
try {
    fail();
} catch(SpecificException e) {
    logger.error("an error occurred because reason", e);
    throw e;
}

//Don't do this
try {
    fail();
} catch(SpecificException e) {
    logger.error("an error occurred because reason", e);
    throw new AnotherException("an error occurred because reason", e);
}

//Do this instead
try {
    fail();
} catch(SpecificException e) {
    throw new AnotherException("an error occurred because reason", e);
}
```

### Exception `Cause`
When you instantiate a new exception, you should always set the caught exception as its cause. Otherwise, you lose the message and stack trace that describe the exceptional event that caused your exception. The Exception class and all its subclasses provide several constructor methods which accept the original exception as a parameter and set it as the cause.

```
//Don't do this
try {
    fail();
} catch(SpecificException e) {
    throw new AnotherException("an error occurred because reason");
}

//Do this instead
try {
    fail();
} catch(SpecificException e) {
    throw new AnotherException("an error occurred because reason", e);
}
```

### Comments
Javadoc comments are recommended but not mandatory. Take into consideration those who will need to maintain your code in the future. Javadoc comments will help them and anyone who needs to make use of your code.

Comments that aren't pertinent or useful should be avoided, and self-documenting code should take precedence over inline comments.
```
//Don't do this
public void doSomething() {
    ...
    //Extract the username from the User entity
    String name = user.getUsername();
    ...
}

//Do this instead
public void doSomething() {
    ...
    String authenticatedUsername = authenticatedUser.getUsername();
    ...
}
```

## Additional Resources
- [Google Code Style Guide for Java](http://google.github.io/styleguide/javaguide.html)
- [Twitter Code Style Guide for Java](https://github.com/twitter/commons/blob/master/src/java/com/twitter/common/styleguide.md)
- [Need some humor?](https://blog.codinghorror.com/new-programming-jargon/)