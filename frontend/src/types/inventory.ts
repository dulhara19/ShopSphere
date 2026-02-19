/**
 * Inventory Service Types
 *
 * Types for inventory management.
 * Based on: shared/contracts/inventory-service.yaml
 */

import { ISO8601, UUID } from './api';

// Reservation status
export type ReservationStatus = 'PENDING' | 'CONFIRMED' | 'RELEASED';

// Inventory item
export interface InventoryItem {
  productId: UUID;
  quantity: number;
  reserved: number;
  available: number;
  lowStockThreshold: number;
  isLowStock: boolean;
  warehouse: string;
  updatedAt: ISO8601;
}

// Low stock alert
export interface LowStockAlert {
  productId: UUID;
  productName: string;
  currentQuantity: number;
  threshold: number;
  warehouse: string;
}

// Stock reservation
export interface StockReservation {
  reservationId: UUID;
  productId: UUID;
  quantity: number;
  expiresAt: ISO8601;
  status: ReservationStatus;
}

// Check availability request
export interface CheckAvailabilityRequest {
  items: Array<{
    productId: UUID;
    quantity: number;
  }>;
}

// Check availability response
export interface CheckAvailabilityResponse {
  items: Array<{
    productId: UUID;
    requested: number;
    available: number;
    isAvailable: boolean;
  }>;
  allAvailable: boolean;
}

// Reserve stock request
export interface ReserveStockRequest {
  items: Array<{
    productId: UUID;
    quantity: number;
  }>;
  orderId?: UUID;
}

// Reserve stock response
export interface ReserveStockResponse {
  reservationId: UUID;
  items: Array<{
    productId: UUID;
    quantity: number;
    reserved: boolean;
  }>;
  expiresAt: ISO8601;
  allReserved: boolean;
}

// Confirm reservation request
export interface ConfirmReservationRequest {
  reservationId: UUID;
}

// Release reservation request
export interface ReleaseReservationRequest {
  reservationId: UUID;
}

// Update inventory request (seller)
export interface UpdateInventoryRequest {
  quantity: number;
}

// Bulk update inventory request
export interface BulkUpdateInventoryRequest {
  items: Array<{
    productId: UUID;
    quantity: number;
  }>;
}

// Set threshold request
export interface SetThresholdRequest {
  lowStockThreshold: number;
}
