import { onCall, HttpsError } from "firebase-functions/v2/https";
import { onDocumentWritten } from "firebase-functions/v2/firestore";
import * as admin from "firebase-admin";
import { auth } from "./common";

/**
 * Cloud Function to set custom claims when user role changes.
 * Triggered when a user document is created or updated in Firestore.
 */
export const setUserRoleClaim = onDocumentWritten("users/{userId}", async (event) => {
    const userId = event.params.userId;
    const dataBefore = event.data?.before.data();
    const dataAfter = event.data?.after.data();

    // If document was deleted, remove custom claims
    if (!event.data?.after.exists) {
        await auth.setCustomUserClaims(userId, {});
        console.log(`Removed custom claims for deleted user: ${userId}`);
        return;
    }

    if (!dataAfter) return;

    const newRole = dataAfter.userType || "GENERAL";
    const newVerified = dataAfter.verificationStatus === "VERIFIED";
    
    // Idempotency check: Only update if relevant fields changed
    const oldRole = dataBefore?.userType || "GENERAL";
    const oldVerified = dataBefore?.verificationStatus === "VERIFIED";
    
    if (event.data?.before.exists && newRole === oldRole && newVerified === oldVerified && dataAfter.customClaimsUpdatedAt) {
        console.log(`Claims already up to date for ${userId}. Skipping.`);
        return;
    }

    try {
        await auth.setCustomUserClaims(userId, {
            role: newRole,
            verified: newVerified,
            admin: newRole === "ADMIN",
        });
        console.log(`Set custom claims for ${userId}: role=${newRole}, verified=${newVerified}`);

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
    if (!request.auth) {
        throw new HttpsError("unauthenticated", "Must be authenticated to refresh claims");
    }
    const userId = request.auth.uid;
    try {
        const userDoc = await admin.firestore().collection("users").doc(userId).get();
        if (!userDoc.exists) {
            throw new HttpsError("not-found", "User document not found");
        }
        const userData = userDoc.data();
        const role = userData?.userType || "GENERAL";
        const verified = userData?.verificationStatus === "VERIFIED";

        await auth.setCustomUserClaims(userId, {
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
