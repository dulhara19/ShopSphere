'use client';

import { useQuery } from '@tanstack/react-query';
import {
  DollarSign,
  ShoppingCart,
  Users,
  Package,
  TrendingUp,
  TrendingDown,
  ArrowUpRight,
  ArrowDownRight,
} from 'lucide-react';
import { analyticsApi } from '@/lib/api/analytics';
import {
  Card,
  CardContent,
  CardDescription,
  CardHeader,
  CardTitle,
} from '@/components/ui/card';
import { Skeleton } from '@/components/ui/skeleton';
import { formatPriceSimple } from '@/lib/utils/format';

export default function AdminDashboardPage() {
  const { data: dashboardData, isLoading } = useQuery({
    queryKey: ['adminDashboard'],
    queryFn: () => analyticsApi.getDashboard(),
  });

  const metrics = dashboardData?.data.data;

  const statCards = [
    {
      title: 'Total Revenue',
      value: metrics?.totalRevenue ?? 0,
      change: metrics?.revenueChange ?? 0,
      format: 'currency',
      icon: DollarSign,
      color: 'text-green-500',
    },
    {
      title: 'Total Orders',
      value: metrics?.totalOrders ?? 0,
      change: metrics?.ordersChange ?? 0,
      format: 'number',
      icon: ShoppingCart,
      color: 'text-blue-500',
    },
    {
      title: 'Total Customers',
      value: metrics?.totalCustomers ?? 0,
      change: metrics?.customersChange ?? 0,
      format: 'number',
      icon: Users,
      color: 'text-purple-500',
    },
    {
      title: 'Total Products',
      value: metrics?.totalProducts ?? 0,
      change: metrics?.productsChange ?? 0,
      format: 'number',
      icon: Package,
      color: 'text-orange-500',
    },
  ];

  if (isLoading) {
    return (
      <div className="space-y-6">
        <div>
          <h1 className="text-3xl font-bold">Dashboard</h1>
          <p className="text-muted-foreground">
            Welcome back! Here&apos;s what&apos;s happening.
          </p>
        </div>
        <div className="grid gap-4 md:grid-cols-2 lg:grid-cols-4">
          {[1, 2, 3, 4].map((i) => (
            <Card key={i}>
              <CardHeader className="flex flex-row items-center justify-between pb-2">
                <Skeleton className="h-4 w-24" />
                <Skeleton className="h-8 w-8 rounded" />
              </CardHeader>
              <CardContent>
                <Skeleton className="h-8 w-32 mb-2" />
                <Skeleton className="h-4 w-20" />
              </CardContent>
            </Card>
          ))}
        </div>
      </div>
    );
  }

  return (
    <div className="space-y-6">
      <div>
        <h1 className="text-3xl font-bold">Dashboard</h1>
        <p className="text-muted-foreground">
          Welcome back! Here&apos;s what&apos;s happening with your store.
        </p>
      </div>

      {/* Stats Grid */}
      <div className="grid gap-4 md:grid-cols-2 lg:grid-cols-4">
        {statCards.map((stat) => (
          <Card key={stat.title}>
            <CardHeader className="flex flex-row items-center justify-between pb-2">
              <CardTitle className="text-sm font-medium text-muted-foreground">
                {stat.title}
              </CardTitle>
              <div className={`p-2 rounded-lg bg-muted ${stat.color}`}>
                <stat.icon className="h-4 w-4" />
              </div>
            </CardHeader>
            <CardContent>
              <div className="text-2xl font-bold">
                {stat.format === 'currency'
                  ? formatPriceSimple(stat.value)
                  : stat.value.toLocaleString()}
              </div>
              <div className="flex items-center text-sm">
                {stat.change >= 0 ? (
                  <>
                    <ArrowUpRight className="h-4 w-4 text-green-500 mr-1" />
                    <span className="text-green-500">+{stat.change}%</span>
                  </>
                ) : (
                  <>
                    <ArrowDownRight className="h-4 w-4 text-red-500 mr-1" />
                    <span className="text-red-500">{stat.change}%</span>
                  </>
                )}
                <span className="text-muted-foreground ml-1">from last month</span>
              </div>
            </CardContent>
          </Card>
        ))}
      </div>

      {/* Charts Row */}
      <div className="grid gap-4 md:grid-cols-2 lg:grid-cols-7">
        <Card className="lg:col-span-4">
          <CardHeader>
            <CardTitle>Revenue Overview</CardTitle>
            <CardDescription>Monthly revenue for the past year</CardDescription>
          </CardHeader>
          <CardContent>
            <div className="h-[300px] flex items-center justify-center text-muted-foreground">
              {/* Chart placeholder - would use recharts or similar */}
              <div className="text-center">
                <TrendingUp className="h-12 w-12 mx-auto mb-2" />
                <p>Revenue Chart</p>
                <p className="text-sm">Integrate with recharts or chart.js</p>
              </div>
            </div>
          </CardContent>
        </Card>

        <Card className="lg:col-span-3">
          <CardHeader>
            <CardTitle>Recent Orders</CardTitle>
            <CardDescription>Latest orders from your store</CardDescription>
          </CardHeader>
          <CardContent>
            <div className="space-y-4">
              {metrics?.recentOrders?.slice(0, 5).map((order) => (
                <div
                  key={order.id}
                  className="flex items-center justify-between"
                >
                  <div>
                    <p className="font-medium">#{order.orderNumber}</p>
                    <p className="text-sm text-muted-foreground">
                      {order.customerName}
                    </p>
                  </div>
                  <div className="text-right">
                    <p className="font-medium">
                      {formatPriceSimple(order.total)}
                    </p>
                    <p className="text-sm text-muted-foreground">
                      {order.status}
                    </p>
                  </div>
                </div>
              )) || (
                <p className="text-muted-foreground text-center py-8">
                  No recent orders
                </p>
              )}
            </div>
          </CardContent>
        </Card>
      </div>

      {/* Bottom Row */}
      <div className="grid gap-4 md:grid-cols-2 lg:grid-cols-3">
        <Card>
          <CardHeader>
            <CardTitle>Top Products</CardTitle>
            <CardDescription>Best selling products this month</CardDescription>
          </CardHeader>
          <CardContent>
            <div className="space-y-4">
              {metrics?.topProducts?.slice(0, 5).map((product, i) => (
                <div key={product.id} className="flex items-center gap-4">
                  <span className="text-sm font-medium text-muted-foreground w-4">
                    {i + 1}
                  </span>
                  <div className="flex-1 min-w-0">
                    <p className="font-medium truncate">{product.name}</p>
                    <p className="text-sm text-muted-foreground">
                      {product.unitsSold} sold
                    </p>
                  </div>
                  <p className="font-medium">
                    {formatPriceSimple(product.revenue)}
                  </p>
                </div>
              )) || (
                <p className="text-muted-foreground text-center py-8">
                  No data available
                </p>
              )}
            </div>
          </CardContent>
        </Card>

        <Card>
          <CardHeader>
            <CardTitle>Traffic Sources</CardTitle>
            <CardDescription>Where your visitors come from</CardDescription>
          </CardHeader>
          <CardContent>
            <div className="space-y-4">
              {[
                { source: 'Direct', value: 45, color: 'bg-blue-500' },
                { source: 'Organic Search', value: 30, color: 'bg-green-500' },
                { source: 'Social Media', value: 15, color: 'bg-purple-500' },
                { source: 'Referral', value: 10, color: 'bg-orange-500' },
              ].map((item) => (
                <div key={item.source} className="space-y-1">
                  <div className="flex justify-between text-sm">
                    <span>{item.source}</span>
                    <span className="text-muted-foreground">{item.value}%</span>
                  </div>
                  <div className="h-2 bg-muted rounded-full overflow-hidden">
                    <div
                      className={`h-full ${item.color} rounded-full`}
                      style={{ width: `${item.value}%` }}
                    />
                  </div>
                </div>
              ))}
            </div>
          </CardContent>
        </Card>

        <Card>
          <CardHeader>
            <CardTitle>Low Stock Alert</CardTitle>
            <CardDescription>Products running low on inventory</CardDescription>
          </CardHeader>
          <CardContent>
            <div className="space-y-4">
              {metrics?.lowStockProducts?.slice(0, 5).map((product) => (
                <div key={product.id} className="flex items-center gap-4">
                  <div className="flex-1 min-w-0">
                    <p className="font-medium truncate">{product.name}</p>
                    <p className="text-sm text-muted-foreground">
                      SKU: {product.sku}
                    </p>
                  </div>
                  <span
                    className={`text-sm font-medium px-2 py-1 rounded ${
                      product.quantity <= 5
                        ? 'bg-red-100 text-red-700'
                        : 'bg-yellow-100 text-yellow-700'
                    }`}
                  >
                    {product.quantity} left
                  </span>
                </div>
              )) || (
                <p className="text-muted-foreground text-center py-8">
                  All products are well stocked
                </p>
              )}
            </div>
          </CardContent>
        </Card>
      </div>
    </div>
  );
}
