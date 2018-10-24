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

### Method Names
### Constants Names
### Variables Names

## Programming Practices
### `@Override`
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
### Exception Handling
### Comments

## Additional Resources
[Google Code Style Guide for Java](http://google.github.io/styleguide/javaguide.html)
[Twitter Code Style Guide for Java](https://github.com/twitter/commons/blob/master/src/java/com/twitter/common/styleguide.md)
[Need some humor?](https://blog.codinghorror.com/new-programming-jargon/)