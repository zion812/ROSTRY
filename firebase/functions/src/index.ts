import {onDocumentWritten} from "firebase-functions/v2/firestore";
import {onCall, HttpsError} from "firebase-functions/v2/https";
import * as admin from "firebase-admin";

// Initialize Firebase Admin
admin.initializeApp();

/**
 * Cloud Function to set custom claims when user role changes.
 * Triggered when a user document is created or updated in Firestore.
 *
 * Custom claims set:
 * - role: User's current role (GENERAL, FARMER, ENTHUSIAST)
 * - verified: Whether user is verified (VERIFIED status)
 */
export const setUserRoleClaim = onDocumentWritten("users/{userId}", async (event) => {
    const userId = event.params.userId;

    // Check if event.data exists
    if (!event.data) {
        console.log(`No data for event: ${userId}`);
        return;
    }

    // If document was deleted, remove custom claims
    if (!event.data.after.exists) {
        await admin.auth().setCustomUserClaims(userId, {});
        console.log(`Removed custom claims for deleted user: ${userId}`);
        return;
    }

    const userData = event.data.after.data();
    if (!userData) return;

    const role = userData.userType || "GENERAL";
    const verified = userData.verificationStatus === "VERIFIED";

    try {
        // Set custom claims
        await admin.auth().setCustomUserClaims(userId, {
            role: role,
            verified: verified,
        });

        console.log(
            `Set custom claims for ${userId}: role=${role}, verified=${verified}`
        );

        // Update the user document with a timestamp of when claims were set
        // This helps with debugging and can trigger client token refresh
        await event.data.after.ref.update({
            customClaimsUpdatedAt: admin.firestore.FieldValue.serverTimestamp(),
        });
    } catch (error) {
        console.error(`Error setting custom claims for ${userId}:`, error);
        throw error;
    }
});

/**
 * Callable function to manually refresh custom claims.
 * Can be called from the client to force a custom claims update.
 */
export const refreshUserClaims = onCall(async (request) => {
    // Ensure user is authenticated
    if (!request.auth) {
        throw new HttpsError(
            "unauthenticated",
            "Must be authenticated to refresh claims"
        );
    }

    const userId = request.auth.uid;

    try {
        // Fetch user document from Firestore
        const userDoc = await admin.firestore().collection("users").doc(userId).get();

        if (!userDoc.exists) {
            throw new HttpsError(
                "not-found",
                "User document not found"
            );
        }

        const userData = userDoc.data();
        const role = userData?.userType || "GENERAL";
        const verified = userData?.verificationStatus === "VERIFIED";

        // Update custom claims
        await admin.auth().setCustomUserClaims(userId, {
            role: role,
            verified: verified,
        });

        console.log(`Refreshed custom claims for ${userId}: role=${role}, verified=${verified}`);

        return {
            success: true,
            role: role,
            verified: verified,
        };
    } catch (error: any) {
        console.error(`Error refreshing claims for ${userId}:`, error);
        throw new HttpsError("internal", error.message);
    }
});
