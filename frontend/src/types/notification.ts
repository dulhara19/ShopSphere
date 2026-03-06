/**
 * Notification Service Types
 *
 * Types for notifications and preferences.
 * Based on: shared/contracts/notification-service.yaml
 */

import { ISO8601, UUID } from './api';

// Notification type
export type NotificationType = 'order' | 'promotion' | 'system' | 'review' | 'shipping';

// Notification channel
export type NotificationChannel = 'EMAIL' | 'SMS' | 'PUSH' | 'IN_APP';

// Notification entity
export interface Notification {
  id: UUID;
  type: NotificationType;
  title: string;
  message: string;
  data?: Record<string, unknown>;
  isRead: boolean;
  createdAt: ISO8601;
}

// Email preferences
export interface EmailPreferences {
  orderUpdates: boolean;
  promotions: boolean;
  reviews: boolean;
  newsletter: boolean;
}

// SMS preferences
export interface SmsPreferences {
  orderUpdates: boolean;
  promotions: boolean;
}

// Push preferences
export interface PushPreferences {
  orderUpdates: boolean;
  promotions: boolean;
  reviews: boolean;
}

// Full notification preferences
export interface NotificationPreferences {
  email: EmailPreferences;
  sms: SmsPreferences;
  push: PushPreferences;
}

// Device registration
export interface Device {
  id: UUID;
  type: 'ios' | 'android' | 'web';
  token: string;
  name?: string;
  createdAt: ISO8601;
}

// Notification list params
export interface NotificationListParams {
  page?: number;
  size?: number;
  type?: NotificationType;
  unreadOnly?: boolean;
}

// Update preferences request
export interface UpdatePreferencesRequest {
  email?: Partial<EmailPreferences>;
  sms?: Partial<SmsPreferences>;
  push?: Partial<PushPreferences>;
}

// Register device request
export interface RegisterDeviceRequest {
  type: 'ios' | 'android' | 'web';
  token: string;
  name?: string;
}

// Unread count response
export interface UnreadCountResponse {
  count: number;
}

// Notification template (admin)
export interface NotificationTemplate {
  id: UUID;
  type: NotificationType;
  channel: NotificationChannel;
  subject?: string;
  body: string;
  variables: string[];
  active: boolean;
}
