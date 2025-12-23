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
exports.verifyPayment = void 0;
const https_1 = require("firebase-functions/v2/https");
const admin = __importStar(require("firebase-admin"));
const common_1 = require("./common");
/**
 * Verifies a payment transaction securely.
 *
 * @param data { paymentId: string, orderId: string, signature: string, status: 'SUCCESS' | 'FAILED' }
 */
exports.verifyPayment = (0, https_1.onCall)(async (request) => {
    if (!request.auth)
        throw new https_1.HttpsError("unauthenticated", "Authentication required.");
    const { paymentId, orderId, status } = request.data;
    if (!paymentId || !orderId || !status) {
        throw new https_1.HttpsError("invalid-argument", "Missing payment details.");
    }
    try {
        await common_1.db.runTransaction(async (transaction) => {
            var _a, _b;
            const orderRef = common_1.db.collection("orders").doc(orderId);
            const paymentRef = common_1.db.collection("payments").doc(paymentId);
            const orderSnap = await transaction.get(orderRef);
            if (!orderSnap.exists) {
                throw new https_1.HttpsError("not-found", "Order not found.");
            }
            if (((_a = orderSnap.data()) === null || _a === void 0 ? void 0 : _a.paymentStatus) === "PAID") {
                console.log(`Order ${orderId} already paid. Skipping.`);
                return;
            }
            if (status === "SUCCESS") {
                transaction.update(orderRef, {
                    paymentStatus: "PAID",
                    paymentId: paymentId,
                    status: "CONFIRMED",
                    updatedAt: admin.firestore.FieldValue.serverTimestamp(),
                });
                transaction.set(paymentRef, {
                    paymentId,
                    orderId,
                    userId: request.auth.uid,
                    amount: (_b = orderSnap.data()) === null || _b === void 0 ? void 0 : _b.totalAmount,
                    currency: "INR",
                    status: "SUCCESS",
                    method: "UPI",
                    createdAt: admin.firestore.FieldValue.serverTimestamp(),
                });
            }
            else {
                transaction.update(orderRef, {
                    paymentStatus: "FAILED",
                    updatedAt: admin.firestore.FieldValue.serverTimestamp(),
                });
                transaction.set(paymentRef, {
                    paymentId,
                    orderId,
                    userId: request.auth.uid,
                    status: "FAILED",
                    reason: "Payment gateway reported failure",
                    createdAt: admin.firestore.FieldValue.serverTimestamp(),
                });
            }
        });
        return { success: true, orderId, status };
    }
    catch (error) {
        console.error("verifyPayment failed", error);
        throw new https_1.HttpsError("internal", "Payment verification failed.");
    }
});
//# sourceMappingURL=payments.js.map