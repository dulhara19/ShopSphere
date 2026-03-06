/**
 * Shipping Service API Client
 *
 * Handles shipping rates, labels, and tracking.
 * Service: shipping-service (Port 3006)
 *
 * AI AGENT INTEGRATION:
 * - Set NEXT_PUBLIC_ENABLE_SHIPPING_SERVICE=true when service is ready
 * - Ensure NEXT_PUBLIC_SHIPPING_SERVICE_URL is correct
 */

import { shippingClient } from './client';
import { ApiResponse } from '@/types/api';
import {
  ShippingRate,
  TrackingInfo,
  ShippingZone,
  AddressValidationResponse,
  CalculateRateRequest,
  CreateShippingLabelRequest,
  ShippingLabelResponse,
  FreeShippingThreshold,
} from '@/types/shipping';
import { Address } from '@/types/user';

export const shippingApi = {
  // ==========================================
  // Address Validation
  // ==========================================

  /**
   * POST /api/shipping/validate-address
   * Validate and standardize an address
   */
  validateAddress: (address: Partial<Address>) =>
    shippingClient.post<ApiResponse<AddressValidationResponse>>(
      '/api/shipping/validate-address',
      address
    ),

  // ==========================================
  // Shipping Zones
  // ==========================================

  /**
   * GET /api/shipping/zones
   * Get all shipping zones
   */
  getZones: () =>
    shippingClient.get<ApiResponse<ShippingZone[]>>('/api/shipping/zones'),

  /**
   * GET /api/shipping/zones/{country}
   * Get zone for a specific country
   */
  getZoneByCountry: (country: string) =>
    shippingClient.get<ApiResponse<ShippingZone>>(`/api/shipping/zones/${country}`),

  // ==========================================
  // Shipping Rates
  // ==========================================

  /**
   * POST /api/shipping/calculate-rate
   * Calculate shipping cost
   */
  calculateRate: (data: CalculateRateRequest) =>
    shippingClient.post<ApiResponse<ShippingRate[]>>('/api/shipping/calculate-rate', data),

  /**
   * GET /api/shipping/rates/flat
   * Get flat rate shipping options
   */
  getFlatRates: () =>
    shippingClient.get<ApiResponse<ShippingRate[]>>('/api/shipping/rates/flat'),

  /**
   * GET /api/shipping/free-shipping-threshold
   * Get free shipping threshold
   */
  getFreeShippingThreshold: () =>
    shippingClient.get<ApiResponse<FreeShippingThreshold>>(
      '/api/shipping/free-shipping-threshold'
    ),

  // ==========================================
  // Shipment Management
  // ==========================================

  /**
   * POST /api/shipping/create-label
   * Create a shipping label
   */
  createLabel: (data: CreateShippingLabelRequest) =>
    shippingClient.post<ApiResponse<ShippingLabelResponse>>(
      '/api/shipping/create-label',
      data
    ),

  /**
   * GET /api/shipping/{trackingNumber}
   * Track a shipment
   */
  trackShipment: (trackingNumber: string) =>
    shippingClient.get<ApiResponse<TrackingInfo>>(`/api/shipping/${trackingNumber}`),

  /**
   * PUT /api/shipping/{id}/status
   * Update shipment status
   */
  updateShipmentStatus: (id: string, status: string) =>
    shippingClient.put<ApiResponse<void>>(`/api/shipping/${id}/status`, { status }),
};
