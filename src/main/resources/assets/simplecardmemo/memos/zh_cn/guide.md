# 简易备忘录教程

> 随时随地预览和发送文本。

简易备忘录为 Minecraft 添加了备忘录卡片。更具体地说，它为游戏引入了文本预览系统。

> 支持 Markdown！本模组使用来自 [MineMark](https://github.com/DeDiamondPro/MineMark) 的 Markdown 文本渲染系统 .

## 用法

| 物品         | 使用                                                             |
|--------------|------------------------------------------------------------------|
| 新的备忘录   | 打开指定的备忘录。                                               |
| 备忘录编辑器 | 在游戏中编辑新的备忘卡。点击“导出”按钮，将生成新的备忘录卡片。   |
| 备忘录管理器 | 如果您丢失了备忘录，请不要担心，您可以通过备忘录管理器重新获取。 |
| 信箱         | 发送备忘录给其他玩家。                                           |

> 请注意，**备忘录编辑器**仍在开发中。建议使用代码编辑器编辑您的备忘录。

## 可用格式

> 更多格式请参阅 [MineMark](https://github.com/DeDiamondPro/MineMark).

### 标题

### H3
#### H4
##### H5

### 文本

这是**加粗**文本.

这是*斜体*文本.

这是~~删除~~文本.

这是<u>下划线</u>文本.

> 渲染如此简单的文本样式并不令人惊讶，因为它们也可以在 `Component` 对象中直接调用。

### 引用

> **Note** that you can use *Markdown syntax* within a blockquote.

### 表格

| A | B | C | D | E | F |
|---|---|---|---|---|---|
| 1 | 2 | 3 | 4 | 5 | 6 |

### 代码

代码和代码块也受支持，但不幸的是，您可能无法使用您最喜欢的“Consolas”字体样式阅读它们。

这是行内代码：`inline code`。

这是行间代码：

```java
public class HelloWorld {
    public static void main(String[] args) {
        System.out.println("Hello World");
    }
}
```