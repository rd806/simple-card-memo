# Simple Card Memo's Guide

Welcome to use Simple Card Memo! You can use it to create a simple text which binds with an item.

## Usage

* Memo Editor: Edit a new memo card in the game, please use an English path to avoid errors. Once you click the "export" button, a new memo card will be sent to you while consuming the editor.
* Memo Manager: Don't worry if you lost a memo, you can get it again by the Memo Manager while consuming a new viewer.
* New Memo: This item allows you to view the last memo you've opened. For the first time it's linked to the *Guide* file.

```yml
{version folder}
  ├── config
  │   └── simplecardmemo-common.toml     # Client config
  │
  ├── data/simple_card_memo              # Store memo files
  │   ├── guide.md
  │   ├── temp.md
  │   └── ...
  │
  └── ...
```

You can add, edit or delete your Memo with any code editor.

> Markdown rendering is supported, however, it is not comprehensive. 
> 
> The available formats which are allowed here will be provided below.

## Available Formats

> More formats can be viewed at [MineMark](https://github.com/DeDiamondPro/MineMark).

### Title

### H3
#### H4
#### H5

### Text

This is a **Bold** text.

This is an *Italic* text.

This is a ~~Deleted~~ text.

This is an <u>Underlined</u> text.

> Rendering such simple text style is not amazing, for they are also available in the `Component` object. 

### Quote

> **Note** that you can use *Markdown syntax* within a blockquote.

### Sheet

| A | B | C | D | E | F |
|---|---|---|---|---|---|
| 1 | 2 | 3 | 4 | 5 | 6 |

### Code

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