/**
 * Notification Service API Client
 *
 * Handles notifications and user preferences.
 * Service: notification-service (Port 3009)
 *
 * AI AGENT INTEGRATION:
 * - Set NEXT_PUBLIC_ENABLE_NOTIFICATION_SERVICE=true when service is ready
 * - Ensure NEXT_PUBLIC_NOTIFICATION_SERVICE_URL is correct
 */

import { notificationClient } from './client';
import { ApiResponse, PaginatedResponse } from '@/types/api';
import {
  Notification,
  NotificationPreferences,
  Device,
  NotificationListParams,
  UpdatePreferencesRequest,
  RegisterDeviceRequest,
  UnreadCountResponse,
  NotificationTemplate,
} from '@/types/notification';

export const notificationApi = {
  // ==========================================
  // Preferences
  // ==========================================

  /**
   * GET /api/notifications/preferences
   * Get notification preferences
   */
  getPreferences: () =>
    notificationClient.get<ApiResponse<NotificationPreferences>>(
      '/api/notifications/preferences'
    ),

  /**
   * PUT /api/notifications/preferences
   * Update all notification preferences
   */
  updatePreferences: (data: UpdatePreferencesRequest) =>
    notificationClient.put<ApiResponse<NotificationPreferences>>(
      '/api/notifications/preferences',
      data
    ),

  /**
   * PUT /api/notifications/preferences/email
   * Update email preferences only
   */
  updateEmailPreferences: (data: UpdatePreferencesRequest['email']) =>
    notificationClient.put<ApiResponse<NotificationPreferences>>(
      '/api/notifications/preferences/email',
      data
    ),

  /**
   * PUT /api/notifications/preferences/sms
   * Update SMS preferences only
   */
  updateSmsPreferences: (data: UpdatePreferencesRequest['sms']) =>
    notificationClient.put<ApiResponse<NotificationPreferences>>(
      '/api/notifications/preferences/sms',
      data
    ),

  /**
   * PUT /api/notifications/preferences/push
   * Update push preferences only
   */
  updatePushPreferences: (data: UpdatePreferencesRequest['push']) =>
    notificationClient.put<ApiResponse<NotificationPreferences>>(
      '/api/notifications/preferences/push',
      data
    ),

  // ==========================================
  // Notifications
  // ==========================================

  /**
   * GET /api/notifications
   * List notifications
   */
  getNotifications: (params: NotificationListParams) =>
    notificationClient.get<PaginatedResponse<Notification>>('/api/notifications', {
      params,
    }),

  /**
   * GET /api/notifications/unread-count
   * Get unread count
   */
  getUnreadCount: () =>
    notificationClient.get<ApiResponse<UnreadCountResponse>>(
      '/api/notifications/unread-count'
    ),

  /**
   * GET /api/notifications/{id}
   * Get single notification
   */
  getNotification: (id: string) =>
    notificationClient.get<ApiResponse<Notification>>(`/api/notifications/${id}`),

  /**
   * PUT /api/notifications/{id}/read
   * Mark notification as read
   */
  markAsRead: (id: string) =>
    notificationClient.put<ApiResponse<Notification>>(`/api/notifications/${id}/read`),

  /**
   * PUT /api/notifications/read-all
   * Mark all as read
   */
  markAllAsRead: () =>
    notificationClient.put<ApiResponse<void>>('/api/notifications/read-all'),

  /**
   * DELETE /api/notifications/{id}
   * Delete notification
   */
  deleteNotification: (id: string) =>
    notificationClient.delete<ApiResponse<void>>(`/api/notifications/${id}`),

  // ==========================================
  // Device Management
  // ==========================================

  /**
   * POST /api/notifications/devices
   * Register device for push notifications
   */
  registerDevice: (data: RegisterDeviceRequest) =>
    notificationClient.post<ApiResponse<Device>>('/api/notifications/devices', data),

  /**
   * DELETE /api/notifications/devices/{deviceId}
   * Remove device
   */
  removeDevice: (deviceId: string) =>
    notificationClient.delete<ApiResponse<void>>(
      `/api/notifications/devices/${deviceId}`
    ),

  // ==========================================
  // Templates (Admin)
  // ==========================================

  /**
   * GET /api/admin/notification-templates
   * List notification templates
   */
  getTemplates: () =>
    notificationClient.get<ApiResponse<NotificationTemplate[]>>(
      '/api/admin/notification-templates'
    ),

  /**
   * POST /api/admin/notification-templates
   * Create template
   */
  createTemplate: (data: Partial<NotificationTemplate>) =>
    notificationClient.post<ApiResponse<NotificationTemplate>>(
      '/api/admin/notification-templates',
      data
    ),

  /**
   * PUT /api/admin/notification-templates/{id}
   * Update template
   */
  updateTemplate: (id: string, data: Partial<NotificationTemplate>) =>
    notificationClient.put<ApiResponse<NotificationTemplate>>(
      `/api/admin/notification-templates/${id}`,
      data
    ),
};
