# Guide

Welcome to use Simple Card Memo! You can use it to create a simple text which binds with an item.

> Markdown rendering is supported, however, it is not comprehensive. 
> 
> The available formats which are allowed here will be provided below.

## Title

### H3
#### H4
#### H5

## Text

This is a **Bold** text.

This is an *Italic* text.

This is a ~~Deleted~~ text.

This is an <u>Underlined</u> text.

> Rendering such simple text style is not amazing, for they are also available in the `Component` object. 

## Quote

> **Note** that you can use *Markdown syntax* within a blockquote.

## Sheet

| A | B | C | D | E | F |
|---|---|---|---|---|---|
| 1 | 2 | 3 | 4 | 5 | 6 |

## Code

Code is also supported, but unfortunately, you may not be able to read them with your favorite `Consolas` font style.

This is an `inline code`.

Below is a Code block:

```java
public class HelloWorld {
    public static void main(String[] args) {
        System.out.println("Hello World");
    }
}
```