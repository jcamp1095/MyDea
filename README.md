# MyDea - An app for sharing your ideas and getting others involved
# Note: This is a work in progress. To see most updated version, go to
## By Joe Campbell and Tommy Tang

## Why we need this app
MyDea is a platform for anyone to post an idea for a business or project and collaborate with other users on that idea.  The creator posts details about the idea, and the resources he need. The person who posts the idea does so to get feedback and to seek out others who are interested in joining that team. Other users can vote on the idea, leave comments, and request to join the project easily. In addition to the public feedback section, users can also send private messages easily.

## Architecture
The app has one main activity: Navi Activity, and several fragments under it, including discover fragment for viewing posted ideas, input fragment for posting an idea etc. The reason behind this design choice is to keep a Hamburger menu visible for all the different screens. The user logins via Login activity, which implements the Facebook API for login. Then, the user can freely use all the different fragments. Discover fragments implements a RecyclerView to list all the ideas.

The backend for the app is a Node.js server hosted on Heroku. It handles POST requests for logging user information and ideas. Storage is handled by a PostgreSQL database.
