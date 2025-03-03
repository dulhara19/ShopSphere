'use client';

import Link from 'next/link';
import Image from 'next/image';
import { Star, ShoppingCart } from 'lucide-react';
import { Button } from '@/components/ui/button';
import { Card, CardContent, CardFooter } from '@/components/ui/card';
import { Badge } from '@/components/ui/badge';
import { ProductSummary } from '@/types/product';
import { useCartStore } from '@/stores/cart-store';
import { formatPriceSimple } from '@/lib/utils/format';
import { cn } from '@/lib/utils';

interface ProductCardProps {
  product: ProductSummary;
  className?: string;
}

export function ProductCard({ product, className }: ProductCardProps) {
  const { addItem, isLoading } = useCartStore();

  const handleAddToCart = async (e: React.MouseEvent) => {
    e.preventDefault();
    e.stopPropagation();
    await addItem({ productId: product.id, quantity: 1 });
  };

  const discount = product.compareAtPrice
    ? Math.round(
        ((product.compareAtPrice - product.price) / product.compareAtPrice) * 100
      )
    : 0;

  return (
    <Card className={cn('group overflow-hidden', className)}>
      <Link href={`/products/${product.id}`}>
        <div className="relative aspect-square overflow-hidden bg-muted">
          <Image
            src={product.primaryImage || '/images/placeholder-product.svg'}
            alt={product.name}
            fill
            className="object-cover transition-transform group-hover:scale-105"
          />
          {discount > 0 && (
            <Badge variant="destructive" className="absolute top-2 left-2">
              -{discount}%
            </Badge>
          )}
        </div>
        <CardContent className="p-4">
          <h3 className="font-medium line-clamp-2 group-hover:text-primary transition-colors">
            {product.name}
          </h3>
          <div className="flex items-center gap-1 mt-1">
            <Star className="h-4 w-4 fill-yellow-400 text-yellow-400" />
            <span className="text-sm text-muted-foreground">
              {(product.averageRating || 0).toFixed(1)} ({product.reviewCount || 0})
            </span>
          </div>
          <div className="flex items-center gap-2 mt-2">
            <span className="text-lg font-bold">
              {formatPriceSimple(product.price)}
            </span>
            {product.compareAtPrice && (
              <span className="text-sm text-muted-foreground line-through">
                {formatPriceSimple(product.compareAtPrice)}
              </span>
            )}
          </div>
        </CardContent>
      </Link>
      <CardFooter className="p-4 pt-0">
        <Button
          className="w-full"
          onClick={handleAddToCart}
          disabled={isLoading || product.status === 'OUT_OF_STOCK'}
        >
          <ShoppingCart className="h-4 w-4 mr-2" />
          {product.status === 'OUT_OF_STOCK' ? 'Out of Stock' : 'Add to Cart'}
        </Button>
      </CardFooter>
    </Card>
  );
}
