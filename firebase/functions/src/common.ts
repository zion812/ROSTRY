import * as admin from "firebase-admin";

// Ensure app is initialized only once
if (!admin.apps.length) {
    admin.initializeApp();
}

export const db = admin.firestore();
export const auth = admin.auth();
