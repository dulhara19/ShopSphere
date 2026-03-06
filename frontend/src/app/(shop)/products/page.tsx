'use client';

import { useSearchParams } from 'next/navigation';
import { useQuery } from '@tanstack/react-query';
import { productApi } from '@/lib/api/product';
import { ProductGrid } from '@/components/products/product-grid';
import { Button } from '@/components/ui/button';
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from '@/components/ui/select';
import { Input } from '@/components/ui/input';
import { Checkbox } from '@/components/ui/checkbox';
import { Label } from '@/components/ui/label';
import { Separator } from '@/components/ui/separator';
import { useState, useEffect } from 'react';
import { PRODUCT_SORT_OPTIONS, PRICE_RANGES } from '@/config/constants';

export default function ProductsPage() {
  const searchParams = useSearchParams();

  const initialCategory = searchParams.get('category') || undefined;
  const initialSearch = searchParams.get('search') || undefined;
  const initialSort = searchParams.get('sort') || 'newest';

  const [category, setCategory] = useState(initialCategory);
  const [search, setSearch] = useState(initialSearch || '');
  const [sort, setSort] = useState(initialSort);
  const [minPrice, setMinPrice] = useState<number | undefined>();
  const [maxPrice, setMaxPrice] = useState<number | undefined>();
  const [page, setPage] = useState(1);

  const { data, isLoading, refetch } = useQuery({
    queryKey: ['products', { category, search, sort, minPrice, maxPrice, page }],
    queryFn: () =>
      productApi.getProducts({
        category,
        search: search || undefined,
        sort: sort as any,
        minPrice,
        maxPrice,
        page,
        size: 12,
      }),
  });

  const { data: categoriesData } = useQuery({
    queryKey: ['categories'],
    queryFn: () => productApi.getCategories(),
  });

  const products = data?.data.data || [];
  const meta = data?.data.meta;
  const categories = categoriesData?.data.data || [];

  const handlePriceRangeChange = (min: number, max: number | null) => {
    setMinPrice(min);
    setMaxPrice(max || undefined);
    setPage(1);
  };

  const handleClearFilters = () => {
    setCategory(undefined);
    setSearch('');
    setMinPrice(undefined);
    setMaxPrice(undefined);
    setSort('newest');
    setPage(1);
  };

  return (
    <div className="container py-8">
      <div className="flex flex-col md:flex-row gap-8">
        {/* Filters Sidebar */}
        <aside className="w-full md:w-64 space-y-6">
          <div>
            <h2 className="text-lg font-semibold mb-4">Filters</h2>
            <Button variant="outline" size="sm" onClick={handleClearFilters}>
              Clear All
            </Button>
          </div>

          <Separator />

          {/* Categories */}
          <div>
            <h3 className="font-medium mb-3">Category</h3>
            <div className="space-y-2">
              <div className="flex items-center space-x-2">
                <Checkbox
                  id="cat-all"
                  checked={!category}
                  onCheckedChange={() => {
                    setCategory(undefined);
                    setPage(1);
                  }}
                />
                <Label htmlFor="cat-all" className="cursor-pointer">
                  All Categories
                </Label>
              </div>
              {categories.map((cat) => (
                <div key={cat.id} className="flex items-center space-x-2">
                  <Checkbox
                    id={cat.id}
                    checked={category === cat.id}
                    onCheckedChange={() => {
                      setCategory(cat.id);
                      setPage(1);
                    }}
                  />
                  <Label htmlFor={cat.id} className="cursor-pointer">
                    {cat.name} ({cat.productCount})
                  </Label>
                </div>
              ))}
            </div>
          </div>

          <Separator />

          {/* Price Range */}
          <div>
            <h3 className="font-medium mb-3">Price</h3>
            <div className="space-y-2">
              {PRICE_RANGES.map((range, i) => (
                <div key={i} className="flex items-center space-x-2">
                  <Checkbox
                    id={`price-${i}`}
                    checked={minPrice === range.min && maxPrice === (range.max || undefined)}
                    onCheckedChange={() =>
                      handlePriceRangeChange(range.min, range.max)
                    }
                  />
                  <Label htmlFor={`price-${i}`} className="cursor-pointer">
                    {range.label}
                  </Label>
                </div>
              ))}
            </div>
          </div>
        </aside>

        {/* Products */}
        <div className="flex-1">
          {/* Header */}
          <div className="flex flex-col sm:flex-row justify-between items-start sm:items-center gap-4 mb-6">
            <div>
              <h1 className="text-2xl font-bold">Products</h1>
              {meta && (
                <p className="text-muted-foreground">
                  {meta.totalElements} products found
                </p>
              )}
            </div>
            <div className="flex items-center gap-4">
              <Input
                placeholder="Search..."
                value={search}
                onChange={(e) => setSearch(e.target.value)}
                className="w-[200px]"
              />
              <Select value={sort} onValueChange={(v) => setSort(v)}>
                <SelectTrigger className="w-[180px]">
                  <SelectValue placeholder="Sort by" />
                </SelectTrigger>
                <SelectContent>
                  {PRODUCT_SORT_OPTIONS.map((option) => (
                    <SelectItem key={option.value} value={option.value}>
                      {option.label}
                    </SelectItem>
                  ))}
                </SelectContent>
              </Select>
            </div>
          </div>

          {/* Product Grid */}
          <ProductGrid products={products} isLoading={isLoading} />

          {/* Pagination */}
          {meta && meta.totalPages > 1 && (
            <div className="flex justify-center gap-2 mt-8">
              <Button
                variant="outline"
                disabled={page <= 1}
                onClick={() => setPage((p) => p - 1)}
              >
                Previous
              </Button>
              <span className="flex items-center px-4">
                Page {page} of {meta.totalPages}
              </span>
              <Button
                variant="outline"
                disabled={page >= meta.totalPages}
                onClick={() => setPage((p) => p + 1)}
              >
                Next
              </Button>
            </div>
          )}
        </div>
      </div>
    </div>
  );
}
