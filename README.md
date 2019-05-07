# Audio Recorder

## Table of Contents
1. [Overview](#Overview)
1. [Product Spec](#Product-Spec)
1. [Wireframes](#Wireframes)
2. [Schema](#Schema)

## Overview
### Description
Audio recording and able to upload to a cloud point. Audios are accessibale and shareable for selected users.

### App Evaluation
- **Category:** Business / Utility
- **Mobile:** This app would be primarily developed for mobile but would perhaps be just as viable on a computer.
- **Story:** User can record a audio and upload to user's cloud. User can also choose to share the audio with other users
- **Market:** Any individual who need to record voice/sound/music can use this app.
- **Habit:** Can be used frequently for student, secretary, laywer, doctor. Can be used occationally for people who want to record the sound on their surrounding.
- **Scope:** Individual can use this app for personal note taking, voice recording. Can potentially be evolue as an social group app for imformation sharing/entertainment.

## Product Spec
### 1. User Stories (Required and Optional)

**Required Must-have Stories**

- [x ] Users can sign up an account
- [x ] Users can login to their accounts
- [x ] Users can record and play their audio
- [ ] Users can view, play, delete their audio files in their profile page
- [ ] Users can upload their audio files to the cloud, or download the files from the cloud


**Optional Nice-to-have Stories**

* User can choose to send, or share their audio files to other users
* Settings (Accesibility, Notification, General, etc)
* User can modify their audio files such as cut audio
* Short-cut button to instantly start record

### Video Walkthrough

**Sign up and log in**
<img src="https://github.com/YoTNT/Audio-Recorder/blob/master/login_signUp_audioRecorder.gif"><br>
<img src="https://github.com/YoTNT/Audio-Recorder/blob/master/walkthrough.gif"><br>

### 2. Screen Archetypes

* Login Screen
  * Upon reopening of the application, the user is prompted to log in to gain access to their profile information to be properly matched with another person
* Sign up Screen
* Profile Screen 
   * Allows user view list of audio files toolbar to view file information, delete, download, upload file
* Recording Screen
   * Allows user to start record a voice/sound/music
* Audio Play Screen
   * Allows user to play their desired audio file
* Settings Screen
   * Lets people change preference, notification.

### 3. Navigation

**Tab Navigation** (Tab to Screen)

* Recording Screen
* Profile
* Audio Playing Screen

## Wireframes
<img src="https://github.com/YoTNT/Audio-Recorder/blob/master/UI%20Collection.jpg" width=600><br>

### [BONUS] Digital Wireframes & Mockups
The image above is actually made as Mockups.
<img src="https://github.com/YoTNT/Audio-Recorder/blob/master/Mockups.jpg" width=600><br>


### [BONUS] Interactive Prototype
<img src="https://github.com/YoTNT/Audio-Recorder/blob/master/walkthrough_audioRecorder.gif"><br>

## Schema 

### Models
Post
Property	Type			Description
objectId	String			unique id for the user post (default field)
author		Pointer to User		audio author
Audio File	File			Audio that user posts
Audio Name	String			Name of the Audio file
Audio Length	Number			Length of the Audio file
Audio Size	Number			Size of the Audio file
Audio Tag	String			Tag of the Audio file
createdAt	DateTime		date when post is created (default field)

### Networking
Home Feed Screen
(Read/GET) Query all posts where user is author
let query = PFQuery(className:"Post")
query.whereKey("author", equalTo: currentUser)
query.order(byDescending: "createdAt")
query.findObjectsInBackground { (posts: [PFObject]?, error: Error?) in
   if let error = error { 
      print(error.localizedDescription)
   } else if let posts = posts {
      print("Successfully retrieved \(posts.count) posts.")
  // TODO: Do something with posts...
   }
}
(Create/POST) Create a new like on a post
(Delete) Delete existing like
(Create/POST) Create a new comment on a post
(Delete) Delete existing comment
Create Post Screen
(Create/POST) Create a new post object
Profile Screen
(Read/GET) Query logged in user object
(Update/PUT) Update user profile image

- [OPTIONAL: List endpoints if using existing API such as Yelp]
