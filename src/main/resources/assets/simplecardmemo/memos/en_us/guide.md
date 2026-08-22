# Simple Card Memo's Guide

> Preview texts anytime and anywhere in the Minecraft game.

Simple Card Memo is a Minecraft Java mod which adds memo cards to Minecraft. More specifically, it introduces a text preview system to the game.

> Markdown is supported! Using a Markdown Text Render System from [MineMark](https://github.com/DeDiamondPro/MineMark).

## Usage

| Item         | How to use                                                                                             |
|--------------|--------------------------------------------------------------------------------------------------------|
| New Memo     | This item allows you to view the last memo you've opened.                                              |
| Memo Editor  | Edit a new memo card in the game. Click the "export" button and a new memo card will be available.     |
| Memo Manager | Don't worry if you lost a memo, you can get it again by the Memo Manager while consuming a new viewer. |
| Memo Mail    | You can send a memo to other players by using it.                                                      |

> Please note that **Memo Editor** is still under development. It's recommended to edit your Memos with a code editor.

## Available Formats

> More formats can be viewed at [MineMark](https://github.com/DeDiamondPro/MineMark).

### Title

### H3
#### H4
##### H5

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