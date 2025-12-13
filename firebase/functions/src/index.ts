import * as functions from "firebase-functions";
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
export const setUserRoleClaim = functions.firestore
    .document("users/{userId}")
    .onWrite(async (change, context) => {
        const userId = context.params.userId;

        // If document was deleted, remove custom claims
        if (!change.after.exists) {
            await admin.auth().setCustomUserClaims(userId, {});
            console.log(`Removed custom claims for deleted user: ${userId}`);
            return;
        }

        const userData = change.after.data();
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
            await change.after.ref.update({
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
export const refreshUserClaims = functions.https.onCall(async (data, context) => {
    // Ensure user is authenticated
    if (!context.auth) {
        throw new functions.https.HttpsError(
            "unauthenticated",
            "Must be authenticated to refresh claims"
        );
    }

    const userId = context.auth.uid;

    try {
        // Fetch user document from Firestore
        const userDoc = await admin.firestore().collection("users").doc(userId).get();

        if (!userDoc.exists) {
            throw new functions.https.HttpsError(
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
        throw new functions.https.HttpsError("internal", error.message);
    }
});
