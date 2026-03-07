'use client';

import { useState } from 'react';
import { useQuery } from '@tanstack/react-query';
import {
  TrendingUp,
  TrendingDown,
  DollarSign,
  ShoppingCart,
  Users,
  Eye,
  Calendar,
} from 'lucide-react';
import { analyticsApi } from '@/lib/api/analytics';
import { Button } from '@/components/ui/button';
import {
  Card,
  CardContent,
  CardDescription,
  CardHeader,
  CardTitle,
} from '@/components/ui/card';
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from '@/components/ui/select';
import { Skeleton } from '@/components/ui/skeleton';
import { Tabs, TabsContent, TabsList, TabsTrigger } from '@/components/ui/tabs';
import { formatPriceSimple } from '@/lib/utils/format';

function getDateRange(range: string): { startDate: string; endDate: string } {
  const end = new Date();
  const start = new Date();
  const days = parseInt(range) || 30;
  start.setDate(end.getDate() - days);
  return {
    startDate: start.toISOString().split('T')[0],
    endDate: end.toISOString().split('T')[0],
  };
}

export default function AdminAnalyticsPage() {
  const [timeRange, setTimeRange] = useState('30d');
  const dateRange = getDateRange(timeRange);

  const { data: salesData, isLoading: salesLoading } = useQuery({
    queryKey: ['salesAnalytics', timeRange],
    queryFn: () => analyticsApi.getSalesAnalytics(dateRange),
  });

  const { data: productData, isLoading: productLoading } = useQuery({
    queryKey: ['productAnalytics', timeRange],
    queryFn: () => analyticsApi.getTopSellingProducts({ ...dateRange, limit: 10 }),
  });

  const { data: customerData, isLoading: customerLoading } = useQuery({
    queryKey: ['customerAnalytics', timeRange],
    queryFn: () => analyticsApi.getDashboard(dateRange),
  });

  // Backend returns arrays — aggregate into summary objects
  const salesRaw: any[] = salesData?.data?.data || salesData?.data || [];
  const salesList = Array.isArray(salesRaw) ? salesRaw : [];
  const sales = {
    totalRevenue: salesList.reduce((sum: number, d: any) => sum + (d.totalRevenue || 0), 0),
    totalOrders: salesList.reduce((sum: number, d: any) => sum + (d.totalOrders || 0), 0),
    averageOrderValue: salesList.length
      ? salesList.reduce((sum: number, d: any) => sum + (d.averageOrderValue || 0), 0) / salesList.length
      : 0,
    totalVisitors: salesList.reduce((sum: number, d: any) => sum + (d.viewCount || 0), 0),
    conversionRate: salesList.length
      ? +(
          (salesList.reduce((s: number, d: any) => s + (d.purchaseCount || 0), 0) /
            Math.max(salesList.reduce((s: number, d: any) => s + (d.viewCount || 0), 0), 1)) *
          100
        ).toFixed(1)
      : 0,
    cartAbandonmentRate: salesList.length
      ? +(
          ((salesList.reduce((s: number, d: any) => s + (d.addToCartCount || 0), 0) -
            salesList.reduce((s: number, d: any) => s + (d.purchaseCount || 0), 0)) /
            Math.max(salesList.reduce((s: number, d: any) => s + (d.addToCartCount || 0), 0), 1)) *
          100
        ).toFixed(1)
      : 0,
    abandonedCarts:
      salesList.reduce((s: number, d: any) => s + (d.addToCartCount || 0), 0) -
      salesList.reduce((s: number, d: any) => s + (d.purchaseCount || 0), 0),
    revenueChange: 0,
    dataPoints: salesList,
  };

  const productsRaw: any[] = productData?.data?.data || productData?.data || [];
  const productList = Array.isArray(productsRaw) ? productsRaw : [];
  const products = {
    totalUnitsSold: productList.reduce((sum: number, d: any) => sum + (d.unitsSold || 0), 0),
    totalViews: productList.reduce((sum: number, d: any) => sum + (d.viewCount || 0), 0),
    averageRating: productList.length
      ? productList.reduce((sum: number, d: any) => sum + (d.avgRating || 0), 0) / productList.length
      : 0,
    topProducts: productList.slice(0, 10).map((p: any) => ({
      id: p.productId,
      name: p.productName || p.productId,
      unitsSold: p.unitsSold,
      revenue: p.revenue,
      views: p.viewCount,
    })),
  };

  const customerRaw = customerData?.data?.data || customerData?.data || {};
  const customers = Array.isArray(customerRaw) ? {} : customerRaw;

  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-3xl font-bold">Analytics</h1>
          <p className="text-muted-foreground">
            Monitor your store performance
          </p>
        </div>
        <Select value={timeRange} onValueChange={setTimeRange}>
          <SelectTrigger className="w-40">
            <Calendar className="h-4 w-4 mr-2" />
            <SelectValue />
          </SelectTrigger>
          <SelectContent>
            <SelectItem value="7d">Last 7 days</SelectItem>
            <SelectItem value="30d">Last 30 days</SelectItem>
            <SelectItem value="90d">Last 90 days</SelectItem>
            <SelectItem value="365d">Last year</SelectItem>
          </SelectContent>
        </Select>
      </div>

      <Tabs defaultValue="sales" className="space-y-6">
        <TabsList>
          <TabsTrigger value="sales">Sales</TabsTrigger>
          <TabsTrigger value="products">Products</TabsTrigger>
          <TabsTrigger value="customers">Customers</TabsTrigger>
        </TabsList>

        <TabsContent value="sales" className="space-y-6">
          {/* Sales Metrics */}
          <div className="grid gap-4 md:grid-cols-2 lg:grid-cols-4">
            {salesLoading ? (
              [1, 2, 3, 4].map((i) => (
                <Card key={i}>
                  <CardHeader className="pb-2">
                    <Skeleton className="h-4 w-24" />
                  </CardHeader>
                  <CardContent>
                    <Skeleton className="h-8 w-32" />
                  </CardContent>
                </Card>
              ))
            ) : (
              <>
                <Card>
                  <CardHeader className="flex flex-row items-center justify-between pb-2">
                    <CardTitle className="text-sm font-medium text-muted-foreground">
                      Total Revenue
                    </CardTitle>
                    <DollarSign className="h-4 w-4 text-muted-foreground" />
                  </CardHeader>
                  <CardContent>
                    <div className="text-2xl font-bold">
                      {formatPriceSimple(sales?.totalRevenue || 0)}
                    </div>
                    <div className="flex items-center text-sm">
                      {(sales?.revenueChange || 0) >= 0 ? (
                        <TrendingUp className="h-4 w-4 text-green-500 mr-1" />
                      ) : (
                        <TrendingDown className="h-4 w-4 text-red-500 mr-1" />
                      )}
                      <span
                        className={
                          (sales?.revenueChange || 0) >= 0
                            ? 'text-green-500'
                            : 'text-red-500'
                        }
                      >
                        {sales?.revenueChange}%
                      </span>
                      <span className="text-muted-foreground ml-1">
                        vs previous period
                      </span>
                    </div>
                  </CardContent>
                </Card>

                <Card>
                  <CardHeader className="flex flex-row items-center justify-between pb-2">
                    <CardTitle className="text-sm font-medium text-muted-foreground">
                      Orders
                    </CardTitle>
                    <ShoppingCart className="h-4 w-4 text-muted-foreground" />
                  </CardHeader>
                  <CardContent>
                    <div className="text-2xl font-bold">
                      {sales?.totalOrders || 0}
                    </div>
                    <p className="text-sm text-muted-foreground">
                      Avg. {formatPriceSimple(sales?.averageOrderValue || 0)} per
                      order
                    </p>
                  </CardContent>
                </Card>

                <Card>
                  <CardHeader className="flex flex-row items-center justify-between pb-2">
                    <CardTitle className="text-sm font-medium text-muted-foreground">
                      Conversion Rate
                    </CardTitle>
                    <TrendingUp className="h-4 w-4 text-muted-foreground" />
                  </CardHeader>
                  <CardContent>
                    <div className="text-2xl font-bold">
                      {sales?.conversionRate || 0}%
                    </div>
                    <p className="text-sm text-muted-foreground">
                      From {sales?.totalVisitors || 0} visitors
                    </p>
                  </CardContent>
                </Card>

                <Card>
                  <CardHeader className="flex flex-row items-center justify-between pb-2">
                    <CardTitle className="text-sm font-medium text-muted-foreground">
                      Cart Abandonment
                    </CardTitle>
                    <ShoppingCart className="h-4 w-4 text-muted-foreground" />
                  </CardHeader>
                  <CardContent>
                    <div className="text-2xl font-bold">
                      {sales?.cartAbandonmentRate || 0}%
                    </div>
                    <p className="text-sm text-muted-foreground">
                      {sales?.abandonedCarts || 0} abandoned carts
                    </p>
                  </CardContent>
                </Card>
              </>
            )}
          </div>

          {/* Revenue Chart */}
          <Card>
            <CardHeader>
              <CardTitle>Revenue Over Time</CardTitle>
              <CardDescription>Daily revenue for the selected period</CardDescription>
            </CardHeader>
            <CardContent>
              <div className="h-[350px] flex items-center justify-center text-muted-foreground">
                <div className="text-center">
                  <TrendingUp className="h-12 w-12 mx-auto mb-2" />
                  <p>Revenue Chart Placeholder</p>
                  <p className="text-sm">Integrate with recharts for visualization</p>
                </div>
              </div>
            </CardContent>
          </Card>
        </TabsContent>

        <TabsContent value="products" className="space-y-6">
          {/* Product Metrics */}
          <div className="grid gap-4 md:grid-cols-3">
            {productLoading ? (
              [1, 2, 3].map((i) => (
                <Card key={i}>
                  <CardHeader className="pb-2">
                    <Skeleton className="h-4 w-24" />
                  </CardHeader>
                  <CardContent>
                    <Skeleton className="h-8 w-32" />
                  </CardContent>
                </Card>
              ))
            ) : (
              <>
                <Card>
                  <CardHeader className="pb-2">
                    <CardTitle className="text-sm font-medium text-muted-foreground">
                      Total Products Sold
                    </CardTitle>
                  </CardHeader>
                  <CardContent>
                    <div className="text-2xl font-bold">
                      {products?.totalUnitsSold || 0}
                    </div>
                  </CardContent>
                </Card>
                <Card>
                  <CardHeader className="pb-2">
                    <CardTitle className="text-sm font-medium text-muted-foreground">
                      Product Views
                    </CardTitle>
                  </CardHeader>
                  <CardContent>
                    <div className="text-2xl font-bold">
                      {products?.totalViews?.toLocaleString() || 0}
                    </div>
                  </CardContent>
                </Card>
                <Card>
                  <CardHeader className="pb-2">
                    <CardTitle className="text-sm font-medium text-muted-foreground">
                      Avg. Rating
                    </CardTitle>
                  </CardHeader>
                  <CardContent>
                    <div className="text-2xl font-bold">
                      {products?.averageRating?.toFixed(1) || 0}/5
                    </div>
                  </CardContent>
                </Card>
              </>
            )}
          </div>

          {/* Top Products Table */}
          <Card>
            <CardHeader>
              <CardTitle>Top Performing Products</CardTitle>
              <CardDescription>Products with highest revenue</CardDescription>
            </CardHeader>
            <CardContent>
              <div className="space-y-4">
                {products?.topProducts?.map((product: any, i: number) => (
                  <div key={product.id} className="flex items-center gap-4">
                    <span className="text-lg font-bold text-muted-foreground w-6">
                      {i + 1}
                    </span>
                    <div className="flex-1">
                      <p className="font-medium">{product.name}</p>
                      <p className="text-sm text-muted-foreground">
                        {product.unitsSold} units sold
                      </p>
                    </div>
                    <div className="text-right">
                      <p className="font-medium">
                        {formatPriceSimple(product.revenue)}
                      </p>
                      <p className="text-sm text-muted-foreground">
                        {product.views?.toLocaleString()} views
                      </p>
                    </div>
                  </div>
                )) || (
                  <p className="text-center text-muted-foreground py-8">
                    No product data available
                  </p>
                )}
              </div>
            </CardContent>
          </Card>
        </TabsContent>

        <TabsContent value="customers" className="space-y-6">
          {/* Customer Metrics */}
          <div className="grid gap-4 md:grid-cols-4">
            {customerLoading ? (
              [1, 2, 3, 4].map((i) => (
                <Card key={i}>
                  <CardHeader className="pb-2">
                    <Skeleton className="h-4 w-24" />
                  </CardHeader>
                  <CardContent>
                    <Skeleton className="h-8 w-32" />
                  </CardContent>
                </Card>
              ))
            ) : (
              <>
                <Card>
                  <CardHeader className="flex flex-row items-center justify-between pb-2">
                    <CardTitle className="text-sm font-medium text-muted-foreground">
                      Total Customers
                    </CardTitle>
                    <Users className="h-4 w-4 text-muted-foreground" />
                  </CardHeader>
                  <CardContent>
                    <div className="text-2xl font-bold">
                      {customers?.totalCustomers?.toLocaleString() || 0}
                    </div>
                  </CardContent>
                </Card>
                <Card>
                  <CardHeader className="pb-2">
                    <CardTitle className="text-sm font-medium text-muted-foreground">
                      New Customers
                    </CardTitle>
                  </CardHeader>
                  <CardContent>
                    <div className="text-2xl font-bold">
                      {customers?.newCustomers || 0}
                    </div>
                    <p className="text-sm text-muted-foreground">
                      This period
                    </p>
                  </CardContent>
                </Card>
                <Card>
                  <CardHeader className="pb-2">
                    <CardTitle className="text-sm font-medium text-muted-foreground">
                      Returning Customers
                    </CardTitle>
                  </CardHeader>
                  <CardContent>
                    <div className="text-2xl font-bold">
                      {customers?.returningCustomers || 0}%
                    </div>
                  </CardContent>
                </Card>
                <Card>
                  <CardHeader className="pb-2">
                    <CardTitle className="text-sm font-medium text-muted-foreground">
                      Customer Lifetime Value
                    </CardTitle>
                  </CardHeader>
                  <CardContent>
                    <div className="text-2xl font-bold">
                      {formatPriceSimple(customers?.averageLifetimeValue || 0)}
                    </div>
                  </CardContent>
                </Card>
              </>
            )}
          </div>

          {/* Customer Segments */}
          <div className="grid gap-4 md:grid-cols-2">
            <Card>
              <CardHeader>
                <CardTitle>Customer Segments</CardTitle>
                <CardDescription>Distribution by purchase frequency</CardDescription>
              </CardHeader>
              <CardContent>
                <div className="space-y-4">
                  {[
                    { segment: 'New', count: 234, percentage: 30 },
                    { segment: 'Active', count: 456, percentage: 45 },
                    { segment: 'At Risk', count: 123, percentage: 15 },
                    { segment: 'Churned', count: 87, percentage: 10 },
                  ].map((item) => (
                    <div key={item.segment} className="space-y-2">
                      <div className="flex justify-between text-sm">
                        <span>{item.segment}</span>
                        <span className="text-muted-foreground">
                          {item.count} ({item.percentage}%)
                        </span>
                      </div>
                      <div className="h-2 bg-muted rounded-full overflow-hidden">
                        <div
                          className="h-full bg-primary rounded-full"
                          style={{ width: `${item.percentage}%` }}
                        />
                      </div>
                    </div>
                  ))}
                </div>
              </CardContent>
            </Card>

            <Card>
              <CardHeader>
                <CardTitle>Top Customers</CardTitle>
                <CardDescription>By total spend</CardDescription>
              </CardHeader>
              <CardContent>
                <div className="space-y-4">
                  {customers?.topCustomers?.map((customer: any, i: number) => (
                    <div key={customer.id} className="flex items-center gap-4">
                      <span className="text-lg font-bold text-muted-foreground w-6">
                        {i + 1}
                      </span>
                      <div className="flex-1">
                        <p className="font-medium">{customer.name}</p>
                        <p className="text-sm text-muted-foreground">
                          {customer.orderCount} orders
                        </p>
                      </div>
                      <p className="font-medium">
                        {formatPriceSimple(customer.totalSpent)}
                      </p>
                    </div>
                  )) || (
                    <p className="text-center text-muted-foreground py-8">
                      No customer data available
                    </p>
                  )}
                </div>
              </CardContent>
            </Card>
          </div>
        </TabsContent>
      </Tabs>
    </div>
  );
}
