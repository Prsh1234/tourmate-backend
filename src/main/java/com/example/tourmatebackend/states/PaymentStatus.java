package com.example.tourmatebackend.states;

public enum PaymentStatus {
    PENDING,   // Payment not done yet
    PAID,      // Payment completed successfully
    FAILED,    // Payment failed
    CANCELLED  // Payment cancelled
}
