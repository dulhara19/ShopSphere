/**
 * Shipping Service Types
 *
 * Types for shipping and tracking.
 * Based on: shared/contracts/shipping-service.yaml
 */

import { ISO8601, UUID } from './api';
import { Address } from './user';

// Shipment status
export type ShipmentStatus = 'pending' | 'in_transit' | 'out_for_delivery' | 'delivered';

// Shipping rate
export interface ShippingRate {
  carrier: string;
  service: string;
  rate: number;
  currency: string;
  estimatedDays: number;
  deliveryDate: string;
}

// Tracking event
export interface TrackingEvent {
  timestamp: ISO8601;
  location: string;
  description: string;
  status: string;
}

// Tracking info
export interface TrackingInfo {
  trackingNumber: string;
  carrier: string;
  status: ShipmentStatus;
  estimatedDelivery: string;
  events: TrackingEvent[];
}

// Shipping zone
export interface ShippingZone {
  id: UUID;
  name: string;
  countries: string[];
  rates: ZoneRate[];
}

// Zone rate
export interface ZoneRate {
  carrier: string;
  service: string;
  baseRate: number;
  perKgRate: number;
  estimatedDays: number;
}

// Address validation response
export interface AddressValidationResponse {
  valid: boolean;
  standardizedAddress?: Partial<Address>;
  suggestions?: Array<Partial<Address>>;
  errors?: string[];
}

// Calculate rate request
export interface CalculateRateRequest {
  fromAddress: Partial<Address>;
  toAddress: Partial<Address>;
  items: Array<{
    weight: number;
    dimensions?: {
      length: number;
      width: number;
      height: number;
    };
  }>;
}

// Create shipping label request
export interface CreateShippingLabelRequest {
  orderId: UUID;
  carrier: string;
  service: string;
  fromAddress: Address;
  toAddress: Address;
  weight: number;
}

// Shipping label response
export interface ShippingLabelResponse {
  trackingNumber: string;
  labelUrl: string;
  carrier: string;
  service: string;
}

// Free shipping threshold
export interface FreeShippingThreshold {
  enabled: boolean;
  threshold: number;
  currency: string;
}
