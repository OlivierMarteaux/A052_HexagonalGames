// functions/index.js

const {onDocumentCreated} = require("firebase-functions/v2/firestore");
const {initializeApp} = require("firebase-admin/app");
const {getMessaging} = require("firebase-admin/messaging");

// Initialize Firebase Admin SDK
initializeApp();

/**
 * Sends a notification to all users when a new post is created.
 * Uses a topic called "allUsers" — devices must subscribe to this topic.
 */
exports.sendNotificationOnPostCreated = onDocumentCreated(
    "posts/{postId}",
    async (event) => {
      try {
        const post = event.data.data();
        if (!post) {
          console.log("No post data found, skipping notification.");
          return;
        }

        // Build the notification payload
        const payload = {
          notification: {
            title: post.title || "New Post Created!",
            body: post.content || "Check out the latest post!",
          },
          topic: "allUsers", // Send to topic
        };

        // Send notification
        await getMessaging().send(payload);
        console.log(`Notification sent for post: ${post.title || post.id}`);
      } catch (error) {
        console.error("Error sending notification:", error);
      }
    },
);
