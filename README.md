# Simple Card Memo

> Preview texts anytime and anywhere in the Minecraft game.

Simple Card Memo is a Minecraft Java mod which adds memo cards to Minecraft. More specifically, it introduces a text preview system to the game.

> Markdown is supported! Using a Markdown Text Render System from [MineMark](https://github.com/DeDiamondPro/MineMark).

## Text File Manage

All the files have detailed information which is stored in `simplecardmemo-memo.json`.

```json
{
  "memos": [
    {
      "name": "temp",
      "path": "temp.md",
      "author": "Default",
      "isExternal": true,
      "lastModified": 1785244599788
    },
    {
      "name": "test1",
      "path": "test1.md",
      "author": "RunicDolphin806",
      "isExternal": true,
      "lastModified": 1785595410237
    },
    {
      "name": "test2.md",
      "path": "test2.md",
      "author": "Default",
      "isExternal": true,
      "lastModified": 1785595410238
    }
  ]
}
```

Obviously, There are two text sources: External and Resource.

### External

For External text, please put all of them in the folder `./data/simple_card_memo/`.

```yml
{version folder}
  ├── config
  │   └── simplecardmemo-common.toml     # Client config    
  │
  ├── data/simple_card_memo              # Store memo files
  │   ├── test1.md
  │   ├── test2.md
  │   ├── temp.md
  │   └── ...
  │
  └── ...
```

You can also preview texts which come from the Internet. However, it may take more time to load them into the game depending on your network connection status.

### Resource

Resource files are built-in ones which are stored in mods or resource packs.

## Usage

| Item         | How to use                                                                                             |
|--------------|--------------------------------------------------------------------------------------------------------|
| New Memo     | This item allows you to view the last memo you've opened.                                              |
| Memo Editor  | Edit a new memo card in the game. Click the "export" button and a new memo card will be available.     |
| Memo Manager | Don't worry if you lost a memo, you can get it again by the Memo Manager while consuming a new viewer. |
| Memo Mail    | You can send a memo to other players by using it.                                                      |

> Please note that **Memo Editor** is still under development. It's recommended to edit your Memos with a code editor.

| Commands                             | Function                   |
|--------------------------------------|----------------------------|
| `/simplecardmemo mail info`          | Show server's mails        |
| `/simplecardmemo mail clear`         | Clear server's mails       |
| `/simplecardmemo client cache info`  | Show local content cache   |
| `/simplecardmemo client cache clear` | Clear local content cache  |
| `/simplecardmemo server cache info`  | Show server content cache  |
| `/simplecardmemo server cache clear` | Clear server content cache |