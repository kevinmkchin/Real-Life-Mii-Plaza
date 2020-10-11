# Elevator pitch
first hello is a social networking app which uses facial recognition instead of a username or handle. It helps users connect with new friends by giving them preliminary information (name, interests, pronouns, etc) before meeting new people!

# Inspiration
Imagine a social networking app that uses your face as your unique handle/identifier rather than an username or @.

# What it does
Find out someone's name, interests, and pronouns by simply scanning their face! Users first register themselves on our database providing a picture of themselves, their name, and a few of their interests. Using the search functionality, users can scan the area for anyone that has registered in the app. If a person's face is recognized in the search, their information will be displayed on screen!

Let's meet Marco!
![gallery](https://user-images.githubusercontent.com/45786074/72234144-77c97980-3580-11ea-9bb9-c29b49b96804.jpg)

Home Screen:
![homescreen](https://user-images.githubusercontent.com/45786074/72234156-89ab1c80-3580-11ea-92b5-13d9f6bc827c.jpg)

# How we built it
The entire app was built with Android Studio, XML, and Java. The user registration functionality relies on Google Cloud Firebase, and the user search functionality uses Microsoft Azure Face API.

# Challenges we ran into
Because Firebase returns data asynchronously, it was challenging to work with calls to Firebase and threads.

# Accomplishments that we are proud of
- Getting data asynchronously from Firebase
- Consistent facial verification between database photos and Microsoft Azure

# What we learned
- How to work with APIs from both Google and Microsoft
- Building Android applications

# What's next for first hello
Larger scaling/better performance, display information in AR

