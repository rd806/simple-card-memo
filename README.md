# Simple Card Memo

> Preview texts anytime and anywhere in the Minecraft game.

Simple Card Memo is a Minecraft Java mod which adds memo cards to Minecraft. More specifically, it introduces a text preview system to the game.

Markdown is supported! Using a Markdown Text Render System from [MineMark](https://github.com/DeDiamondPro/MineMark).

> [!Warning]
> If you update from earlier version to 1.2.0, you need to refresh all the memos as the NBT tags has been changed!  

## Text File Manage

All the memos have detailed information which is stored in `simplecardmemo-memo.json`.

```json
{
  "memos": [
    {
      "name": "test1",
      "path": "test1.md",
      "author": "RunicDolphin806",
      "isExternal": true,
      "lastModified": 1785595410237
    },
    {
      "name": "test2",
      "path": "https://example.com/test2.md",
      "author": "Steve",
      "isExternal": true,
      "lastModified": 1785595410238
    }
  ]
}
```

Obviously, There are two text sources: External and Resource.

### External

For External text, whose `isExternal` is `true`, please put all of them in the folder `./data/simple_card_memo/`.

```yml
{version folder}
  ├── config
  │   ├── simplecardmemo-client.toml     # Client config
  │   └── simplecardmemo-common.toml     # Common config
  │   └── simplecardmemo-memo.json       # Memo config
  │
  ├── data/simple_card_memo              # Store memo files
  │   ├── test1.md
  │   ├── test2.md
  │   ├── temp.md
  │   └── ...
  │
  └── ...
```

You can also preview texts which come from the Internet. However, it may take more time to load them into the game depending on your network connection status. In this case, you can set `PreloadFiles` to true to make it runs faster.

> [!Note]
> Currently, you have better to name all your external files with characters `[a-zA-Z0-9\-_.]`.

### Resource

Resource files are built-in ones which are stored in mods or resource packs.

> Of course, their `isExternal` is `false`.

## Usage

### General

| Item         | How to use                                                                                             |
|--------------|--------------------------------------------------------------------------------------------------------|
| New Memo     | This item allows you to view the last memo you've opened.                                              |
| Memo Editor  | Edit a new memo card in the game. Click the "export" button and a new memo card will be available.     |
| Memo Manager | Don't worry if you lost a memo, you can get it again by the Memo Manager while consuming a new viewer. |
| Memo Mail    | You can send a memo to other players by using it.                                                      |

> [!Note]
> **Memo Editor** is still under development. It's recommended to edit your Memos with a code editor.

### Server & Client Side

Since 1.2.0, there are three memo sources available: client, server and built-in, which can be selected in Memo Manager.

That means you can get memos stored in the server's `simplecardmemo-memo.json` through the *Memo Manger*. Obviously, you are not allowed to edit or delete them through a Memo Manager.

## Commands

| Commands                             | Function                   |
|--------------------------------------|----------------------------|
| `/simplecardmemo mail info`          | Show server's mails        |
| `/simplecardmemo mail clear`         | Clear server's mails       |
| `/simplecardmemo client cache info`  | Show local content cache   |
| `/simplecardmemo client cache clear` | Clear local content cache  |
| `/simplecardmemo server cache info`  | Show server content cache  |
| `/simplecardmemo server cache clear` | Clear server content cache |
| `/simplecardmemo server reload`      | Reload memos on the sever  |