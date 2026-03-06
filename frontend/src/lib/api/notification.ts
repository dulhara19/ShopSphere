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
  NotificationListParams,
  UnreadCountResponse,
} from '@/types/notification';

export const notificationApi = {
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
   * Get unread count for a user
   */
  getUnreadCount: (userId: string) =>
    notificationClient.get<ApiResponse<UnreadCountResponse>>(
      '/api/notifications/unread-count',
      { params: { userId } }
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
   * Mark all as read for a user
   */
  markAllAsRead: (userId: string) =>
    notificationClient.put<ApiResponse<void>>('/api/notifications/read-all', null, {
      params: { userId },
    }),

  /**
   * DELETE /api/notifications/{id}
   * Delete notification
   */
  deleteNotification: (id: string) =>
    notificationClient.delete<ApiResponse<void>>(`/api/notifications/${id}`),

  /**
   * DELETE /api/notifications/clear-all
   * Clear all notifications for a user
   */
  clearAll: (userId: string) =>
    notificationClient.delete<ApiResponse<void>>('/api/notifications/clear-all', {
      params: { userId },
    }),
};
