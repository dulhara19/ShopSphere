/**
 * Inventory Service API Client
 *
 * Handles stock management and reservations.
 * Service: inventory-service (Port 3003)
 *
 * AI AGENT INTEGRATION:
 * - Set NEXT_PUBLIC_ENABLE_INVENTORY_SERVICE=true when service is ready
 * - Ensure NEXT_PUBLIC_INVENTORY_SERVICE_URL is correct
 */

import { inventoryClient } from './client';
import { ApiResponse } from '@/types/api';
import {
  InventoryItem,
  LowStockAlert,
  CheckAvailabilityRequest,
  CheckAvailabilityResponse,
  ReserveStockRequest,
  ReserveStockResponse,
  ConfirmReservationRequest,
  ReleaseReservationRequest,
  UpdateInventoryRequest,
  BulkUpdateInventoryRequest,
  SetThresholdRequest,
} from '@/types/api/inventory';

export const inventoryApi = {
  // ==========================================
  // Inventory Management
  // ==========================================

  /**
   * GET /api/inventory/{productId}
   * Get stock level for a product
   */
  getStock: (productId: string) =>
    inventoryClient.get<ApiResponse<InventoryItem>>(`/api/inventory/${productId}`),

  /**
   * POST /api/inventory
   * Create inventory record for a new product
   */
  createInventory: (data: { productId: string; quantity: number; warehouse?: string }) =>
    inventoryClient.post<ApiResponse<InventoryItem>>('/api/inventory', data),

  /**
   * PUT /api/inventory/{productId}
   * Update stock quantity
   */
  updateStock: (productId: string, data: UpdateInventoryRequest) =>
    inventoryClient.put<ApiResponse<InventoryItem>>(`/api/inventory/${productId}`, data),

  /**
   * DELETE /api/inventory/{productId}
   * Delete inventory record
   */
  deleteInventory: (productId: string) =>
    inventoryClient.delete<ApiResponse<void>>(`/api/inventory/${productId}`),

  /**
   * POST /api/inventory/bulk-update
   * Update multiple products' stock
   */
  bulkUpdateStock: (data: BulkUpdateInventoryRequest) =>
    inventoryClient.post<ApiResponse<InventoryItem[]>>('/api/inventory/bulk-update', data),

  /**
   * PUT /api/inventory/{productId}/threshold
   * Set low stock threshold
   */
  setThreshold: (productId: string, data: SetThresholdRequest) =>
    inventoryClient.put<ApiResponse<InventoryItem>>(
      `/api/inventory/${productId}/threshold`,
      data
    ),

  // ==========================================
  // Stock Availability
  // ==========================================

  /**
   * POST /api/inventory/check-availability
   * Check if quantities are available
   */
  checkAvailability: (data: CheckAvailabilityRequest) =>
    inventoryClient.post<ApiResponse<CheckAvailabilityResponse>>(
      '/api/inventory/check-availability',
      data
    ),

  // ==========================================
  // Reservations
  // ==========================================

  /**
   * POST /api/inventory/reserve
   * Reserve stock for checkout (15-minute hold)
   */
  reserveStock: (data: ReserveStockRequest) =>
    inventoryClient.post<ApiResponse<ReserveStockResponse>>('/api/inventory/reserve', data),

  /**
   * POST /api/inventory/confirm
   * Confirm reservation after payment
   */
  confirmReservation: (data: ConfirmReservationRequest) =>
    inventoryClient.post<ApiResponse<void>>('/api/inventory/confirm', data),

  /**
   * POST /api/inventory/release
   * Release reserved stock
   */
  releaseReservation: (data: ReleaseReservationRequest) =>
    inventoryClient.post<ApiResponse<void>>('/api/inventory/release', data),

  // ==========================================
  // Alerts
  // ==========================================

  /**
   * GET /api/inventory/low-stock
   * Get low stock alerts
   */
  getLowStockAlerts: () =>
    inventoryClient.get<ApiResponse<LowStockAlert[]>>('/api/inventory/low-stock'),
};
