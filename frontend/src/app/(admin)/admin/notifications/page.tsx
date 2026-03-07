'use client';

import { useState } from 'react';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import {
  Bell,
  Send,
  Trash2,
  CheckCheck,
  Package,
  CreditCard,
  Truck,
  Megaphone,
  Settings,
  Check,
  RefreshCw,
} from 'lucide-react';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Textarea } from '@/components/ui/textarea';
import { Label } from '@/components/ui/label';
import { Badge } from '@/components/ui/badge';
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card';
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from '@/components/ui/select';
import {
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from '@/components/ui/table';
import { Tabs, TabsContent, TabsList, TabsTrigger } from '@/components/ui/tabs';
import { notificationApi } from '@/lib/api/notification';
import { useAuthStore } from '@/stores/auth-store';
import { formatRelativeTime } from '@/lib/utils/format';
import axios from 'axios';

const typeIcons: Record<string, typeof Package> = {
  ORDER: Package,
  SYSTEM: Settings,
  PAYMENT: CreditCard,
  SHIPPING: Truck,
  PROMO: Megaphone,
  REVIEW: Check,
};

export default function AdminNotificationsPage() {
  const queryClient = useQueryClient();
  const user = useAuthStore((s) => s.user);
  const [targetUserId, setTargetUserId] = useState('');
  const [browseUserId, setBrowseUserId] = useState('');
  const [title, setTitle] = useState('');
  const [message, setMessage] = useState('');
  const [type, setType] = useState('SYSTEM');
  const [sendStatus, setSendStatus] = useState<{ ok: boolean; text: string } | null>(null);

  // Browse notifications for a user
  const {
    data: notifData,
    isLoading,
    refetch,
  } = useQuery({
    queryKey: ['adminNotifications', browseUserId],
    queryFn: () =>
      notificationApi.getNotifications({
        userId: browseUserId,
        page: 0,
        size: 50,
      }),
    enabled: !!browseUserId,
  });

  const rawData = notifData?.data;
  const notifications =
    rawData?.content || rawData?.data?.content || rawData?.data || rawData || [];
  const notifList = Array.isArray(notifications) ? notifications : [];

  // Unread count
  const { data: countData } = useQuery({
    queryKey: ['adminUnreadCount', browseUserId],
    queryFn: () => notificationApi.getUnreadCount(browseUserId),
    enabled: !!browseUserId,
  });
  const unreadCount =
    countData?.data?.count ?? countData?.data?.data?.count ?? 0;

  // Mark all read
  const markAllMutation = useMutation({
    mutationFn: () => notificationApi.markAllAsRead(browseUserId),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['adminNotifications'] });
      queryClient.invalidateQueries({ queryKey: ['adminUnreadCount'] });
    },
  });

  // Delete notification
  const deleteMutation = useMutation({
    mutationFn: (id: string) => notificationApi.deleteNotification(id),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['adminNotifications'] });
      queryClient.invalidateQueries({ queryKey: ['adminUnreadCount'] });
    },
  });

  // Send notification via internal endpoint
  const handleSend = async () => {
    const userId = targetUserId || user?.id;
    if (!userId || !title || !message) return;

    setSendStatus(null);
    try {
      await axios.post('/internal/notifications/send', {
        userId,
        channels: ['IN_APP'],
        title,
        message,
        data: { type, sentBy: 'admin-ui' },
      });
      setSendStatus({ ok: true, text: `Sent to ${userId}` });
      setTitle('');
      setMessage('');
      // Refresh browse if same user
      if (userId === browseUserId) {
        queryClient.invalidateQueries({ queryKey: ['adminNotifications'] });
        queryClient.invalidateQueries({ queryKey: ['adminUnreadCount'] });
      }
    } catch (err: any) {
      setSendStatus({
        ok: false,
        text: err?.response?.data?.message || err?.message || 'Failed to send',
      });
    }
  };

  // Quick-fill current user
  const fillMyId = () => {
    if (user?.id) {
      setTargetUserId(user.id);
    }
  };

  const fillBrowseMyId = () => {
    if (user?.id) {
      setBrowseUserId(user.id);
    }
  };

  return (
    <div className="space-y-6">
      <div>
        <h1 className="text-3xl font-bold tracking-tight">Notifications</h1>
        <p className="text-muted-foreground">
          Send and browse notifications for testing
        </p>
      </div>

      <Tabs defaultValue="send" className="space-y-4">
        <TabsList>
          <TabsTrigger value="send">
            <Send className="h-4 w-4 mr-2" />
            Send Notification
          </TabsTrigger>
          <TabsTrigger value="browse">
            <Bell className="h-4 w-4 mr-2" />
            Browse Notifications
          </TabsTrigger>
        </TabsList>

        {/* ── Send Tab ── */}
        <TabsContent value="send">
          <Card>
            <CardHeader>
              <CardTitle>Send a Notification</CardTitle>
              <CardDescription>
                Send an in-app notification to any user. Use your own ID to test the bell icon.
              </CardDescription>
            </CardHeader>
            <CardContent className="space-y-4">
              <div className="space-y-2">
                <Label htmlFor="targetUserId">Target User ID</Label>
                <div className="flex gap-2">
                  <Input
                    id="targetUserId"
                    placeholder="Enter user ID or click 'Me'"
                    value={targetUserId}
                    onChange={(e) => setTargetUserId(e.target.value)}
                  />
                  <Button variant="outline" onClick={fillMyId} type="button">
                    Me ({user?.id ? user.id.slice(0, 8) + '...' : 'N/A'})
                  </Button>
                </div>
              </div>

              <div className="space-y-2">
                <Label htmlFor="type">Notification Type</Label>
                <Select value={type} onValueChange={setType}>
                  <SelectTrigger>
                    <SelectValue />
                  </SelectTrigger>
                  <SelectContent>
                    <SelectItem value="SYSTEM">System</SelectItem>
                    <SelectItem value="ORDER">Order</SelectItem>
                    <SelectItem value="PAYMENT">Payment</SelectItem>
                    <SelectItem value="SHIPPING">Shipping</SelectItem>
                    <SelectItem value="PROMO">Promotion</SelectItem>
                    <SelectItem value="REVIEW">Review</SelectItem>
                  </SelectContent>
                </Select>
              </div>

              <div className="space-y-2">
                <Label htmlFor="title">Title</Label>
                <Input
                  id="title"
                  placeholder="e.g. Order Shipped!"
                  value={title}
                  onChange={(e) => setTitle(e.target.value)}
                />
              </div>

              <div className="space-y-2">
                <Label htmlFor="message">Message</Label>
                <Textarea
                  id="message"
                  placeholder="e.g. Your order #12345 has been shipped."
                  value={message}
                  onChange={(e) => setMessage(e.target.value)}
                  rows={3}
                />
              </div>

              {sendStatus && (
                <div
                  className={`p-3 rounded-md text-sm ${
                    sendStatus.ok
                      ? 'bg-green-50 text-green-700 border border-green-200'
                      : 'bg-red-50 text-red-700 border border-red-200'
                  }`}
                >
                  {sendStatus.ok ? 'Sent successfully' : 'Error'}: {sendStatus.text}
                </div>
              )}

              <Button
                onClick={handleSend}
                disabled={!title || !message || (!targetUserId && !user?.id)}
              >
                <Send className="h-4 w-4 mr-2" />
                Send Notification
              </Button>
            </CardContent>
          </Card>
        </TabsContent>

        {/* ── Browse Tab ── */}
        <TabsContent value="browse">
          <Card>
            <CardHeader>
              <CardTitle>Browse Notifications</CardTitle>
              <CardDescription>
                View notifications for a specific user
              </CardDescription>
            </CardHeader>
            <CardContent className="space-y-4">
              <div className="flex gap-2">
                <Input
                  placeholder="Enter user ID to browse"
                  value={browseUserId}
                  onChange={(e) => setBrowseUserId(e.target.value)}
                />
                <Button variant="outline" onClick={fillBrowseMyId} type="button">
                  Me
                </Button>
                <Button
                  variant="outline"
                  size="icon"
                  onClick={() => refetch()}
                  disabled={!browseUserId}
                >
                  <RefreshCw className="h-4 w-4" />
                </Button>
              </div>

              {browseUserId && (
                <div className="flex items-center gap-4">
                  <Badge variant={unreadCount > 0 ? 'destructive' : 'secondary'}>
                    {unreadCount} unread
                  </Badge>
                  <span className="text-sm text-muted-foreground">
                    {notifList.length} total notifications
                  </span>
                  {unreadCount > 0 && (
                    <Button
                      variant="outline"
                      size="sm"
                      onClick={() => markAllMutation.mutate()}
                    >
                      <CheckCheck className="h-4 w-4 mr-1" />
                      Mark all read
                    </Button>
                  )}
                </div>
              )}

              {isLoading ? (
                <div className="py-8 text-center text-muted-foreground">Loading...</div>
              ) : notifList.length === 0 && browseUserId ? (
                <div className="py-8 text-center text-muted-foreground">
                  <Bell className="h-8 w-8 mx-auto mb-2 opacity-50" />
                  No notifications for this user
                </div>
              ) : notifList.length > 0 ? (
                <div className="rounded-md border">
                  <Table>
                    <TableHeader>
                      <TableRow>
                        <TableHead className="w-10">Status</TableHead>
                        <TableHead className="w-10">Type</TableHead>
                        <TableHead>Title</TableHead>
                        <TableHead>Message</TableHead>
                        <TableHead className="w-32">Time</TableHead>
                        <TableHead className="w-10">Actions</TableHead>
                      </TableRow>
                    </TableHeader>
                    <TableBody>
                      {notifList.map((n: any) => {
                        const isRead = n.isRead ?? n.read ?? false;
                        const Icon = typeIcons[n.type] || Settings;
                        return (
                          <TableRow
                            key={n.id}
                            className={!isRead ? 'bg-accent/30' : ''}
                          >
                            <TableCell>
                              <div
                                className={`h-2.5 w-2.5 rounded-full ${
                                  isRead ? 'bg-muted-foreground/30' : 'bg-blue-500'
                                }`}
                              />
                            </TableCell>
                            <TableCell>
                              <Icon className="h-4 w-4 text-muted-foreground" />
                            </TableCell>
                            <TableCell className="font-medium">{n.title}</TableCell>
                            <TableCell className="text-muted-foreground max-w-[300px] truncate">
                              {n.message}
                            </TableCell>
                            <TableCell className="text-sm text-muted-foreground">
                              {n.createdAt ? formatRelativeTime(n.createdAt) : '-'}
                            </TableCell>
                            <TableCell>
                              <Button
                                variant="ghost"
                                size="icon"
                                onClick={() => deleteMutation.mutate(n.id)}
                              >
                                <Trash2 className="h-4 w-4 text-destructive" />
                              </Button>
                            </TableCell>
                          </TableRow>
                        );
                      })}
                    </TableBody>
                  </Table>
                </div>
              ) : null}
            </CardContent>
          </Card>
        </TabsContent>
      </Tabs>
    </div>
  );
}
