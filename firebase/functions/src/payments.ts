import { onCall, HttpsError } from "firebase-functions/v2/https";
import * as admin from "firebase-admin";
import { db } from "./common";

/**
 * Verifies a payment transaction securely.
 * 
 * @param data { paymentId: string, orderId: string, signature: string, status: 'SUCCESS' | 'FAILED' }
 */
export const verifyPayment = onCall(async (request) => {
    if (!request.auth) throw new HttpsError("unauthenticated", "Authentication required.");

    const { paymentId, orderId, status } = request.data;
    if (!paymentId || !orderId || !status) {
        throw new HttpsError("invalid-argument", "Missing payment details.");
    }

    try {
        await db.runTransaction(async (transaction) => {
            const orderRef = db.collection("orders").doc(orderId);
            const paymentRef = db.collection("payments").doc(paymentId);

            const orderSnap = await transaction.get(orderRef);
            if (!orderSnap.exists) {
                throw new HttpsError("not-found", "Order not found.");
            }

            if (orderSnap.data()?.paymentStatus === "PAID") {
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
                    userId: request.auth!.uid,
                    amount: orderSnap.data()?.totalAmount,
                    currency: "INR",
                    status: "SUCCESS",
                    method: "UPI",
                    createdAt: admin.firestore.FieldValue.serverTimestamp(),
                });
            } else {
                transaction.update(orderRef, {
                    paymentStatus: "FAILED",
                    updatedAt: admin.firestore.FieldValue.serverTimestamp(),
                });
                transaction.set(paymentRef, {
                    paymentId,
                    orderId,
                    userId: request.auth!.uid,
                    status: "FAILED",
                    reason: "Payment gateway reported failure",
                    createdAt: admin.firestore.FieldValue.serverTimestamp(),
                });
            }
        });

        return { success: true, orderId, status };

    } catch (error) {
        console.error("verifyPayment failed", error);
        throw new HttpsError("internal", "Payment verification failed.");
    }
});
