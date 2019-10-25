### Functionality

This application mimics a simple icon for representing how much mail is in your inbox.

There are 3 buttons

- Send yourself another mail
- Read a mail
- Real all your mail

The app starts with a mailbox of 97 messages, these are stored in memory, but accessed in a way that this could be a local storage or remote (api) endpoint of data.

The icon is empty if you have 0 mail.
The icon has a mail if you have between 1-99 mails.
The icon shows flames if you have over 99 mails.
The icon keeps count of how much mail you have.

### MVP Architecture with ViewModel, Repository & RxJava

Blog post is coming to explain this.

The example is based on the idea from the recent Android Dev Summit talk:

[Understanding Compose](https://www.youtube.com/watch?v=Q9MtlmmN4Q0)

and whilst this repo doesn't use compose, it's a baseline so we can compare another repo/compose example against it.

![](/mailbox.gif)
