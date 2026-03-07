'use client';

import { useState } from 'react';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import {
  Package,
  AlertTriangle,
  RefreshCw,
  MoreHorizontal,
  PackageX,
  Warehouse,
} from 'lucide-react';
import { inventoryApi } from '@/lib/api/inventory';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Badge } from '@/components/ui/badge';
import { Label } from '@/components/ui/label';
import {
  Card,
  CardContent,
  CardDescription,
  CardHeader,
  CardTitle,
} from '@/components/ui/card';
import {
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from '@/components/ui/table';
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuTrigger,
} from '@/components/ui/dropdown-menu';
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogFooter,
  DialogHeader,
  DialogTitle,
} from '@/components/ui/dialog';
import { Skeleton } from '@/components/ui/skeleton';
import { useToast } from '@/hooks/use-toast';
import type { LowStockAlert } from '@/types/inventory';

export default function AdminInventoryPage() {
  const { toast } = useToast();
  const queryClient = useQueryClient();

  // Dialog state
  const [updateStockItem, setUpdateStockItem] = useState<LowStockAlert | null>(null);
  const [setThresholdItem, setSetThresholdItem] = useState<LowStockAlert | null>(null);
  const [quantityInput, setQuantityInput] = useState('');
  const [thresholdInput, setThresholdInput] = useState('');

  // Fetch low stock alerts
  const {
    data: lowStockData,
    isLoading,
    isRefetching,
  } = useQuery({
    queryKey: ['adminInventoryLowStock'],
    queryFn: () => inventoryApi.getLowStockAlerts(),
  });

  // Extract alerts array from response — backend returns InventoryDTO[] with snake_case
  const rawAlerts = lowStockData?.data?.data || lowStockData?.data || [];
  const alerts: LowStockAlert[] = (Array.isArray(rawAlerts) ? rawAlerts : []).map(
    (item: any) => ({
      productId: item.product_id || item.productId || item.id || '',
      productName: item.productName || item.product_name || 'N/A',
      currentQuantity: item.quantity ?? item.currentQuantity ?? 0,
      threshold: item.low_stock_threshold ?? item.lowStockThreshold ?? 0,
      warehouse: item.warehouse || 'Default',
    })
  );

  // Compute summary stats
  const totalProducts = alerts.length;
  const outOfStockItems = alerts.filter((a) => a.currentQuantity === 0).length;
  const lowStockItems = alerts.filter((a) => a.currentQuantity > 0).length;

  // Update stock mutation
  const updateStockMutation = useMutation({
    mutationFn: ({ productId, quantity }: { productId: string; quantity: number }) =>
      inventoryApi.updateStock(productId, { quantity }),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['adminInventoryLowStock'] });
      toast({ title: 'Stock updated successfully' });
      setUpdateStockItem(null);
      setQuantityInput('');
    },
    onError: () => {
      toast({
        title: 'Error',
        description: 'Failed to update stock',
        variant: 'destructive',
      });
    },
  });

  // Set threshold mutation
  const setThresholdMutation = useMutation({
    mutationFn: ({
      productId,
      lowStockThreshold,
    }: {
      productId: string;
      lowStockThreshold: number;
    }) => inventoryApi.setThreshold(productId, { lowStockThreshold }),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['adminInventoryLowStock'] });
      toast({ title: 'Threshold updated successfully' });
      setSetThresholdItem(null);
      setThresholdInput('');
    },
    onError: () => {
      toast({
        title: 'Error',
        description: 'Failed to update threshold',
        variant: 'destructive',
      });
    },
  });

  const handleUpdateStock = () => {
    if (!updateStockItem || !quantityInput) return;
    const quantity = parseInt(quantityInput, 10);
    if (isNaN(quantity) || quantity < 0) {
      toast({
        title: 'Invalid quantity',
        description: 'Please enter a valid non-negative number',
        variant: 'destructive',
      });
      return;
    }
    updateStockMutation.mutate({
      productId: updateStockItem.productId,
      quantity,
    });
  };

  const handleSetThreshold = () => {
    if (!setThresholdItem || !thresholdInput) return;
    const threshold = parseInt(thresholdInput, 10);
    if (isNaN(threshold) || threshold < 0) {
      toast({
        title: 'Invalid threshold',
        description: 'Please enter a valid non-negative number',
        variant: 'destructive',
      });
      return;
    }
    setThresholdMutation.mutate({
      productId: setThresholdItem.productId,
      lowStockThreshold: threshold,
    });
  };

  const truncateId = (id: string) => {
    if (id.length <= 8) return id;
    return `${id.substring(0, 8)}...`;
  };

  const getStatusBadge = (alert: LowStockAlert) => {
    if (alert.currentQuantity === 0) {
      return (
        <Badge className="bg-red-100 text-red-800">Critical</Badge>
      );
    }
    if (alert.currentQuantity <= alert.threshold) {
      return (
        <Badge className="bg-yellow-100 text-yellow-800">Warning</Badge>
      );
    }
    return <Badge className="bg-green-100 text-green-800">OK</Badge>;
  };

  return (
    <div className="space-y-6">
      {/* Header */}
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-3xl font-bold">Inventory Management</h1>
          <p className="text-muted-foreground">
            Monitor stock levels and manage inventory alerts
          </p>
        </div>
        <Button
          variant="outline"
          onClick={() =>
            queryClient.invalidateQueries({ queryKey: ['adminInventoryLowStock'] })
          }
          disabled={isRefetching}
        >
          <RefreshCw
            className={`h-4 w-4 mr-2 ${isRefetching ? 'animate-spin' : ''}`}
          />
          Refresh
        </Button>
      </div>

      {/* Summary Cards */}
      <div className="grid gap-4 md:grid-cols-3">
        <Card>
          <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
            <CardTitle className="text-sm font-medium">Total Products</CardTitle>
            <Package className="h-4 w-4 text-muted-foreground" />
          </CardHeader>
          <CardContent>
            {isLoading ? (
              <Skeleton className="h-8 w-16" />
            ) : (
              <div className="text-2xl font-bold">{totalProducts}</div>
            )}
            <p className="text-xs text-muted-foreground">
              Products with stock alerts
            </p>
          </CardContent>
        </Card>

        <Card>
          <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
            <CardTitle className="text-sm font-medium">Low Stock Items</CardTitle>
            <AlertTriangle className="h-4 w-4 text-yellow-500" />
          </CardHeader>
          <CardContent>
            {isLoading ? (
              <Skeleton className="h-8 w-16" />
            ) : (
              <div className="text-2xl font-bold">{lowStockItems}</div>
            )}
            <p className="text-xs text-muted-foreground">
              Below threshold quantity
            </p>
          </CardContent>
        </Card>

        <Card>
          <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
            <CardTitle className="text-sm font-medium">Out of Stock</CardTitle>
            <PackageX className="h-4 w-4 text-red-500" />
          </CardHeader>
          <CardContent>
            {isLoading ? (
              <Skeleton className="h-8 w-16" />
            ) : (
              <div className="text-2xl font-bold">{outOfStockItems}</div>
            )}
            <p className="text-xs text-muted-foreground">
              Require immediate restocking
            </p>
          </CardContent>
        </Card>
      </div>

      {/* Low Stock Alerts Table */}
      <Card>
        <CardHeader>
          <CardTitle>Low Stock Alerts</CardTitle>
          <CardDescription>
            Products that are below their stock threshold or out of stock
          </CardDescription>
        </CardHeader>
        <CardContent>
          {isLoading ? (
            <div className="space-y-4">
              {[1, 2, 3, 4, 5].map((i) => (
                <div key={i} className="flex items-center gap-4">
                  <div className="flex-1 space-y-2">
                    <Skeleton className="h-4 w-3/4" />
                    <Skeleton className="h-3 w-1/4" />
                  </div>
                  <Skeleton className="h-6 w-16" />
                  <Skeleton className="h-4 w-20" />
                </div>
              ))}
            </div>
          ) : alerts.length === 0 ? (
            <div className="text-center py-12">
              <Warehouse className="h-12 w-12 mx-auto text-muted-foreground mb-4" />
              <h3 className="text-lg font-medium">No low stock alerts</h3>
              <p className="text-muted-foreground mt-1">
                All products have sufficient stock levels
              </p>
            </div>
          ) : (
            <Table>
              <TableHeader>
                <TableRow>
                  <TableHead>Product ID</TableHead>
                  <TableHead>Product Name</TableHead>
                  <TableHead>Current Quantity</TableHead>
                  <TableHead>Threshold</TableHead>
                  <TableHead>Status</TableHead>
                  <TableHead className="text-right">Actions</TableHead>
                </TableRow>
              </TableHeader>
              <TableBody>
                {alerts.map((alert) => (
                  <TableRow key={alert.productId}>
                    <TableCell>
                      <span
                        className="font-mono text-sm"
                        title={alert.productId}
                      >
                        {truncateId(alert.productId)}
                      </span>
                    </TableCell>
                    <TableCell>
                      <p className="font-medium">
                        {alert.productName || 'N/A'}
                      </p>
                    </TableCell>
                    <TableCell>
                      <span
                        className={
                          alert.currentQuantity === 0
                            ? 'text-red-600 font-semibold'
                            : 'text-yellow-600 font-semibold'
                        }
                      >
                        {alert.currentQuantity}
                      </span>
                    </TableCell>
                    <TableCell>{alert.threshold}</TableCell>
                    <TableCell>{getStatusBadge(alert)}</TableCell>
                    <TableCell className="text-right">
                      <DropdownMenu>
                        <DropdownMenuTrigger asChild>
                          <Button variant="ghost" size="icon">
                            <MoreHorizontal className="h-4 w-4" />
                          </Button>
                        </DropdownMenuTrigger>
                        <DropdownMenuContent align="end">
                          <DropdownMenuItem
                            onClick={() => {
                              setUpdateStockItem(alert);
                              setQuantityInput(
                                String(alert.currentQuantity)
                              );
                            }}
                          >
                            <Package className="h-4 w-4 mr-2" />
                            Update Stock
                          </DropdownMenuItem>
                          <DropdownMenuItem
                            onClick={() => {
                              setSetThresholdItem(alert);
                              setThresholdInput(String(alert.threshold));
                            }}
                          >
                            <AlertTriangle className="h-4 w-4 mr-2" />
                            Set Threshold
                          </DropdownMenuItem>
                        </DropdownMenuContent>
                      </DropdownMenu>
                    </TableCell>
                  </TableRow>
                ))}
              </TableBody>
            </Table>
          )}
        </CardContent>
      </Card>

      {/* Update Stock Dialog */}
      <Dialog
        open={!!updateStockItem}
        onOpenChange={(open) => {
          if (!open) {
            setUpdateStockItem(null);
            setQuantityInput('');
          }
        }}
      >
        <DialogContent>
          <DialogHeader>
            <DialogTitle>Update Stock</DialogTitle>
            <DialogDescription>
              Update the stock quantity for{' '}
              {updateStockItem?.productName || updateStockItem?.productId}
            </DialogDescription>
          </DialogHeader>
          <div className="space-y-4 py-4">
            <div className="space-y-2">
              <Label htmlFor="quantity">New Quantity</Label>
              <Input
                id="quantity"
                type="number"
                min="0"
                placeholder="Enter new stock quantity"
                value={quantityInput}
                onChange={(e) => setQuantityInput(e.target.value)}
              />
            </div>
          </div>
          <DialogFooter>
            <Button
              variant="outline"
              onClick={() => {
                setUpdateStockItem(null);
                setQuantityInput('');
              }}
            >
              Cancel
            </Button>
            <Button
              onClick={handleUpdateStock}
              disabled={updateStockMutation.isPending || !quantityInput}
            >
              {updateStockMutation.isPending ? 'Updating...' : 'Update Stock'}
            </Button>
          </DialogFooter>
        </DialogContent>
      </Dialog>

      {/* Set Threshold Dialog */}
      <Dialog
        open={!!setThresholdItem}
        onOpenChange={(open) => {
          if (!open) {
            setSetThresholdItem(null);
            setThresholdInput('');
          }
        }}
      >
        <DialogContent>
          <DialogHeader>
            <DialogTitle>Set Low Stock Threshold</DialogTitle>
            <DialogDescription>
              Set the low stock alert threshold for{' '}
              {setThresholdItem?.productName || setThresholdItem?.productId}
            </DialogDescription>
          </DialogHeader>
          <div className="space-y-4 py-4">
            <div className="space-y-2">
              <Label htmlFor="threshold">Threshold</Label>
              <Input
                id="threshold"
                type="number"
                min="0"
                placeholder="Enter low stock threshold"
                value={thresholdInput}
                onChange={(e) => setThresholdInput(e.target.value)}
              />
            </div>
          </div>
          <DialogFooter>
            <Button
              variant="outline"
              onClick={() => {
                setSetThresholdItem(null);
                setThresholdInput('');
              }}
            >
              Cancel
            </Button>
            <Button
              onClick={handleSetThreshold}
              disabled={setThresholdMutation.isPending || !thresholdInput}
            >
              {setThresholdMutation.isPending
                ? 'Updating...'
                : 'Set Threshold'}
            </Button>
          </DialogFooter>
        </DialogContent>
      </Dialog>
    </div>
  );
}
