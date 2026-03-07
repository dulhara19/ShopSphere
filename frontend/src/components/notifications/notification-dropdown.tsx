'use client';

import { useEffect, useState } from 'react';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { Bell, Check, CheckCheck, Trash2, Package, CreditCard, Truck, Megaphone, Settings } from 'lucide-react';
import { Button } from '@/components/ui/button';
import { Badge } from '@/components/ui/badge';
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuLabel,
  DropdownMenuSeparator,
  DropdownMenuTrigger,
} from '@/components/ui/dropdown-menu';
import { notificationApi } from '@/lib/api/notification';
import { useAuthStore } from '@/stores/auth-store';
import { useUIStore } from '@/stores/ui-store';
import { formatRelativeTime } from '@/lib/utils/format';
import type { Notification as NotificationType } from '@/types/notification';

const typeIcons: Record<string, typeof Package> = {
  ORDER: Package,
  order: Package,
  PAYMENT: CreditCard,
  payment: CreditCard,
  SHIPPING: Truck,
  shipping: Truck,
  PROMO: Megaphone,
  promotion: Megaphone,
  SYSTEM: Settings,
  system: Settings,
  REVIEW: Check,
  review: Check,
};

function mapNotification(raw: any): NotificationType {
  return {
    id: raw.id,
    type: (raw.type || 'system').toLowerCase(),
    title: raw.title || 'Notification',
    message: raw.message || '',
    data: raw.data,
    isRead: raw.isRead ?? raw.read ?? false,
    createdAt: raw.createdAt,
  };
}

export function NotificationDropdown() {
  const queryClient = useQueryClient();
  const user = useAuthStore((s) => s.user);
  const userId = user?.id;
  const { setUnreadNotificationCount } = useUIStore();
  const unreadCount = useUIStore((s) => s.unreadNotificationCount);
  const [isOpen, setIsOpen] = useState(false);

  // Fetch unread count — runs on mount and every 30s
  const { data: countData } = useQuery({
    queryKey: ['notificationUnreadCount', userId],
    queryFn: () => notificationApi.getUnreadCount(userId!),
    enabled: !!userId,
    refetchInterval: 30000,
    retry: 1,
  });

  // Sync unread count to UI store
  useEffect(() => {
    const raw = countData?.data;
    const count = raw?.count ?? raw?.data?.count ?? 0;
    setUnreadNotificationCount(count);
  }, [countData, setUnreadNotificationCount]);

  // Fetch recent notifications — only when dropdown is open
  const { data: notifData, isLoading, refetch } = useQuery({
    queryKey: ['notificationsDropdown', userId],
    queryFn: () =>
      notificationApi.getNotifications({
        userId: userId!,
        page: 0,
        size: 10,
      }),
    enabled: !!userId && isOpen,
    retry: 1,
  });

  // Refetch when dropdown opens
  useEffect(() => {
    if (isOpen && userId) {
      refetch();
    }
  }, [isOpen, userId, refetch]);

  // Extract notifications — backend returns Spring Page with `content` array
  const rawData = notifData?.data;
  const rawNotifications =
    rawData?.content || rawData?.data?.content || rawData?.data || rawData || [];
  const notifications: NotificationType[] = (
    Array.isArray(rawNotifications) ? rawNotifications : []
  ).map(mapNotification);

  // Mark as read mutation
  const markReadMutation = useMutation({
    mutationFn: (id: string) => notificationApi.markAsRead(id),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['notificationsDropdown'] });
      queryClient.invalidateQueries({ queryKey: ['notificationUnreadCount'] });
    },
  });

  // Mark all as read
  const markAllReadMutation = useMutation({
    mutationFn: () => notificationApi.markAllAsRead(userId!),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['notificationsDropdown'] });
      queryClient.invalidateQueries({ queryKey: ['notificationUnreadCount'] });
    },
  });

  // Delete notification
  const deleteMutation = useMutation({
    mutationFn: (id: string) => notificationApi.deleteNotification(id),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['notificationsDropdown'] });
      queryClient.invalidateQueries({ queryKey: ['notificationUnreadCount'] });
    },
  });

  return (
    <DropdownMenu open={isOpen} onOpenChange={setIsOpen}>
      <DropdownMenuTrigger asChild>
        <Button variant="ghost" size="icon" className="relative">
          <Bell className="h-5 w-5" />
          {unreadCount > 0 && (
            <Badge
              variant="destructive"
              className="absolute -top-1 -right-1 h-5 w-5 flex items-center justify-center p-0 text-xs"
            >
              {unreadCount > 9 ? '9+' : unreadCount}
            </Badge>
          )}
        </Button>
      </DropdownMenuTrigger>
      <DropdownMenuContent align="end" className="w-80">
        <DropdownMenuLabel className="flex items-center justify-between">
          <span>Notifications</span>
          {unreadCount > 0 && (
            <Button
              variant="ghost"
              size="sm"
              className="h-auto py-0 px-2 text-xs text-muted-foreground"
              onClick={(e) => {
                e.preventDefault();
                e.stopPropagation();
                markAllReadMutation.mutate();
              }}
            >
              <CheckCheck className="h-3 w-3 mr-1" />
              Mark all read
            </Button>
          )}
        </DropdownMenuLabel>
        <DropdownMenuSeparator />
        {isLoading ? (
          <div className="py-8 text-center text-sm text-muted-foreground">
            Loading...
          </div>
        ) : notifications.length === 0 ? (
          <div className="py-8 text-center text-sm text-muted-foreground">
            <Bell className="h-8 w-8 mx-auto mb-2 opacity-50" />
            No notifications yet
          </div>
        ) : (
          <div className="max-h-[400px] overflow-y-auto">
            {notifications.map((notif) => {
              const Icon = typeIcons[notif.type] || Settings;
              return (
                <DropdownMenuItem
                  key={notif.id}
                  className={`flex items-start gap-3 p-3 cursor-pointer ${
                    !notif.isRead ? 'bg-accent/50' : ''
                  }`}
                  onSelect={(e) => {
                    e.preventDefault();
                    if (!notif.isRead) {
                      markReadMutation.mutate(notif.id);
                    }
                  }}
                >
                  <div
                    className={`mt-0.5 p-1.5 rounded-full shrink-0 ${
                      !notif.isRead
                        ? 'bg-primary/10 text-primary'
                        : 'bg-muted text-muted-foreground'
                    }`}
                  >
                    <Icon className="h-3.5 w-3.5" />
                  </div>
                  <div className="flex-1 min-w-0">
                    <p
                      className={`text-sm leading-tight ${
                        !notif.isRead ? 'font-semibold' : 'font-normal'
                      }`}
                    >
                      {notif.title}
                    </p>
                    <p className="text-xs text-muted-foreground mt-0.5 line-clamp-2">
                      {notif.message}
                    </p>
                    <p className="text-xs text-muted-foreground mt-1">
                      {notif.createdAt
                        ? formatRelativeTime(notif.createdAt)
                        : ''}
                    </p>
                  </div>
                </DropdownMenuItem>
              );
            })}
          </div>
        )}
      </DropdownMenuContent>
    </DropdownMenu>
  );
}
