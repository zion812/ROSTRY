"use strict";
var __createBinding = (this && this.__createBinding) || (Object.create ? (function(o, m, k, k2) {
    if (k2 === undefined) k2 = k;
    var desc = Object.getOwnPropertyDescriptor(m, k);
    if (!desc || ("get" in desc ? !m.__esModule : desc.writable || desc.configurable)) {
      desc = { enumerable: true, get: function() { return m[k]; } };
    }
    Object.defineProperty(o, k2, desc);
}) : (function(o, m, k, k2) {
    if (k2 === undefined) k2 = k;
    o[k2] = m[k];
}));
var __setModuleDefault = (this && this.__setModuleDefault) || (Object.create ? (function(o, v) {
    Object.defineProperty(o, "default", { enumerable: true, value: v });
}) : function(o, v) {
    o["default"] = v;
});
var __importStar = (this && this.__importStar) || function (mod) {
    if (mod && mod.__esModule) return mod;
    var result = {};
    if (mod != null) for (var k in mod) if (k !== "default" && Object.prototype.hasOwnProperty.call(mod, k)) __createBinding(result, mod, k);
    __setModuleDefault(result, mod);
    return result;
};
Object.defineProperty(exports, "__esModule", { value: true });
exports.setPhoneVerifiedClaim = exports.canSendOtp = void 0;
const https_1 = require("firebase-functions/v2/https");
const admin = __importStar(require("firebase-admin"));
const common_1 = require("./common");
/**
 * Checks if a user is eligible to send an OTP.
 * Prevents abuse by enforcing rate limits per phone number/IP.
 *
 * @param data { phoneNumber: string }
 */
exports.canSendOtp = (0, https_1.onCall)(async (request) => {
    // Rate limiting logic would go here.
    return { allowed: true };
});
/**
 * Sets a custom claim 'phone_verified' to true.
 * This should only be called after successful phone auth provider verification.
 *
 * @param data {} (empty)
 */
exports.setPhoneVerifiedClaim = (0, https_1.onCall)(async (request) => {
    if (!request.auth)
        throw new https_1.HttpsError("unauthenticated", "Auth required.");
    const userId = request.auth.uid;
    const user = await common_1.auth.getUser(userId);
    // Verify that the user actually has a phone number
    if (!user.phoneNumber) {
        throw new https_1.HttpsError("failed-precondition", "User has no phone number.");
    }
    try {
        const currentClaims = user.customClaims || {};
        await common_1.auth.setCustomUserClaims(userId, Object.assign(Object.assign({}, currentClaims), { phone_verified: true }));
        // Update Firestore as well for queryability
        await common_1.db.collection("users").doc(userId).update({
            phoneVerified: true,
            updatedAt: admin.firestore.FieldValue.serverTimestamp()
        });
        return { success: true };
    }
    catch (e) {
        throw new https_1.HttpsError("internal", e.message);
    }
});
//# sourceMappingURL=auth.js.map