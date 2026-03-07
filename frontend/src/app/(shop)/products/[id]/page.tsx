'use client';

import { useParams } from 'next/navigation';
import Image from 'next/image';
import Link from 'next/link';
import { useQuery } from '@tanstack/react-query';
import { Star, Minus, Plus, ShoppingCart, Heart, Share2 } from 'lucide-react';
import { productApi } from '@/lib/api/product';
import { reviewApi } from '@/lib/api/review';
import { recommendationApi } from '@/lib/api/recommendation';
import { Button } from '@/components/ui/button';
import { Badge } from '@/components/ui/badge';
import { Separator } from '@/components/ui/separator';
import { Tabs, TabsContent, TabsList, TabsTrigger } from '@/components/ui/tabs';
import { Avatar, AvatarFallback, AvatarImage } from '@/components/ui/avatar';
import { ProductGrid } from '@/components/products/product-grid';
import { useCartStore } from '@/stores/cart-store';
import { formatPriceSimple, formatRelativeTime, formatRating } from '@/lib/utils/format';
import { useState } from 'react';
import { Skeleton } from '@/components/ui/skeleton';

export default function ProductDetailPage() {
  const params = useParams();
  const productId = params.id as string;
  const [quantity, setQuantity] = useState(1);
  const [selectedImage, setSelectedImage] = useState(0);
  const { addItem, isLoading: cartLoading } = useCartStore();

  const { data: productData, isLoading: productLoading } = useQuery({
    queryKey: ['product', productId],
    queryFn: () => productApi.getProduct(productId),
  });

  const { data: reviewsData } = useQuery({
    queryKey: ['reviews', productId],
    queryFn: () => reviewApi.getProductReviews(productId, { size: 5 }),
  });

  const { data: ratingSummaryData } = useQuery({
    queryKey: ['ratingSummary', productId],
    queryFn: () => reviewApi.getRatingSummary(productId),
  });

  const { data: similarData } = useQuery({
    queryKey: ['similar', productId],
    queryFn: () => recommendationApi.getSimilarProducts(productId, { limit: 4 }),
  });

  const product = productData?.data?.data || productData?.data;
  const reviewsRaw = reviewsData?.data?.data || reviewsData?.data;
  const rawReviews: any[] = reviewsRaw?.content || reviewsRaw || [];
  // Map backend ReviewResponse fields to frontend Review shape
  const reviews = rawReviews.map((r: any) => ({
    ...r,
    userName: r.userName || r.userId || 'Anonymous',
    content: r.content || r.body || '',
    verified: r.verified ?? false,
    userAvatar: r.userAvatar || '',
  }));
  const rawSummary = ratingSummaryData?.data?.data || ratingSummaryData?.data;
  // Normalize distribution keys from string ("1") to number (1)
  const ratingSummary = rawSummary
    ? {
        ...rawSummary,
        distribution: rawSummary.distribution
          ? Object.fromEntries(
              Object.entries(rawSummary.distribution).map(([k, v]) => [Number(k), v])
            )
          : { 1: 0, 2: 0, 3: 0, 4: 0, 5: 0 },
      }
    : null;
  const similarProducts = similarData?.data?.data || similarData?.data || [];

  const handleAddToCart = async () => {
    if (product) {
      await addItem({ productId: product.id, quantity });
    }
  };

  if (productLoading) {
    return (
      <div className="container py-8">
        <div className="grid md:grid-cols-2 gap-8">
          <Skeleton className="aspect-square" />
          <div className="space-y-4">
            <Skeleton className="h-8 w-3/4" />
            <Skeleton className="h-4 w-1/4" />
            <Skeleton className="h-6 w-1/3" />
            <Skeleton className="h-24 w-full" />
          </div>
        </div>
      </div>
    );
  }

  if (!product) {
    return (
      <div className="container py-16 text-center">
        <h1 className="text-2xl font-bold">Product not found</h1>
        <Button asChild className="mt-4">
          <Link href="/products">Back to Products</Link>
        </Button>
      </div>
    );
  }

  // Handle images - backend returns string[] or ProductImage[]
  const productImages: string[] = (product.images || []).map((img: any) =>
    typeof img === 'string' ? img : img.url
  ).filter(Boolean);
  if (!productImages.length && product.primaryImage) {
    productImages.push(product.primaryImage);
  }

  const discount = product.compareAtPrice
    ? Math.round(
        ((product.compareAtPrice - product.price) / product.compareAtPrice) * 100
      )
    : 0;

  return (
    <div className="container py-8">
      <div className="grid md:grid-cols-2 gap-8">
        {/* Images */}
        <div className="space-y-4">
          <div className="relative aspect-square rounded-lg overflow-hidden bg-muted">
            {productImages.length > 0 ? (
              <Image
                src={productImages[selectedImage]}
                alt={product.name}
                fill
                className="object-cover"
              />
            ) : (
              <div className="w-full h-full flex items-center justify-center text-muted-foreground">
                <ShoppingCart className="h-16 w-16" />
              </div>
            )}
            {discount > 0 && (
              <Badge variant="destructive" className="absolute top-4 left-4">
                -{discount}%
              </Badge>
            )}
          </div>
          {productImages.length > 1 && (
            <div className="flex gap-2 overflow-x-auto">
              {productImages.map((imgUrl, i) => (
                <button
                  key={i}
                  onClick={() => setSelectedImage(i)}
                  className={`relative h-20 w-20 rounded-md overflow-hidden border-2 ${
                    selectedImage === i ? 'border-primary' : 'border-transparent'
                  }`}
                >
                  <Image
                    src={imgUrl}
                    alt={`${product.name} ${i + 1}`}
                    fill
                    className="object-cover"
                  />
                </button>
              ))}
            </div>
          )}
        </div>

        {/* Product Info */}
        <div className="space-y-6">
          <div>
            <p className="text-sm text-muted-foreground">{product.categoryName || product.category?.name || ''}</p>
            <h1 className="text-3xl font-bold mt-1">{product.name}</h1>
            {product.brand && (
              <p className="text-muted-foreground">by {product.brand}</p>
            )}
          </div>

          <div className="flex items-center gap-2">
            <div className="flex items-center">
              <Star className="h-5 w-5 fill-yellow-400 text-yellow-400" />
              <span className="ml-1 font-medium">
                {formatRating(product.averageRating || 0)}
              </span>
            </div>
            <span className="text-muted-foreground">
              ({product.reviewCount || 0} reviews)
            </span>
          </div>

          <div className="flex items-baseline gap-3">
            <span className="text-3xl font-bold">
              {formatPriceSimple(product.price)}
            </span>
            {product.compareAtPrice && (
              <span className="text-xl text-muted-foreground line-through">
                {formatPriceSimple(product.compareAtPrice)}
              </span>
            )}
          </div>

          <Separator />

          <p className="text-muted-foreground">{product.description}</p>

          <div className="flex items-center gap-4">
            <div className="flex items-center border rounded-md">
              <Button
                variant="ghost"
                size="icon"
                onClick={() => setQuantity((q) => Math.max(1, q - 1))}
                disabled={quantity <= 1}
              >
                <Minus className="h-4 w-4" />
              </Button>
              <span className="w-12 text-center">{quantity}</span>
              <Button
                variant="ghost"
                size="icon"
                onClick={() => setQuantity((q) => Math.min(99, q + 1))}
                disabled={quantity >= 99}
              >
                <Plus className="h-4 w-4" />
              </Button>
            </div>
            <Button
              size="lg"
              className="flex-1"
              onClick={handleAddToCart}
              disabled={cartLoading || product.status === 'OUT_OF_STOCK'}
            >
              <ShoppingCart className="h-4 w-4 mr-2" />
              {product.status === 'OUT_OF_STOCK' ? 'Out of Stock' : 'Add to Cart'}
            </Button>
            <Button variant="outline" size="icon">
              <Heart className="h-4 w-4" />
            </Button>
          </div>

          <div className="flex gap-4 text-sm text-muted-foreground">
            <span>SKU: {product.sku}</span>
            {product.tags?.length > 0 && (
              <span>Tags: {product.tags.join(', ')}</span>
            )}
          </div>
        </div>
      </div>

      {/* Tabs */}
      <Tabs defaultValue="reviews" className="mt-12">
        <TabsList>
          <TabsTrigger value="reviews">
            Reviews ({product.reviewCount})
          </TabsTrigger>
          <TabsTrigger value="details">Details</TabsTrigger>
        </TabsList>

        <TabsContent value="reviews" className="mt-6">
          {ratingSummary && (
            <div className="flex gap-8 mb-8">
              <div className="text-center">
                <div className="text-5xl font-bold">
                  {formatRating(ratingSummary.averageRating)}
                </div>
                <div className="flex justify-center mt-2">
                  {Array.from({ length: 5 }).map((_, i) => (
                    <Star
                      key={i}
                      className={`h-5 w-5 ${
                        i < Math.round(ratingSummary.averageRating)
                          ? 'fill-yellow-400 text-yellow-400'
                          : 'text-muted'
                      }`}
                    />
                  ))}
                </div>
                <p className="text-muted-foreground mt-1">
                  {ratingSummary.totalReviews} reviews
                </p>
              </div>
              <div className="flex-1 space-y-2">
                {[5, 4, 3, 2, 1].map((rating) => (
                  <div key={rating} className="flex items-center gap-2">
                    <span className="w-8">{rating}</span>
                    <Star className="h-4 w-4 fill-yellow-400 text-yellow-400" />
                    <div className="flex-1 bg-muted rounded-full h-2">
                      <div
                        className="bg-yellow-400 h-2 rounded-full"
                        style={{
                          width: `${
                            (ratingSummary.distribution[rating as 1 | 2 | 3 | 4 | 5] /
                              ratingSummary.totalReviews) *
                            100
                          }%`,
                        }}
                      />
                    </div>
                    <span className="w-8 text-muted-foreground text-sm">
                      {ratingSummary.distribution[rating as 1 | 2 | 3 | 4 | 5]}
                    </span>
                  </div>
                ))}
              </div>
            </div>
          )}

          <div className="space-y-6">
            {reviews.map((review) => (
              <div key={review.id} className="border-b pb-6">
                <div className="flex items-start gap-4">
                  <Avatar>
                    <AvatarImage src={review.userAvatar} />
                    <AvatarFallback>{review.userName[0]}</AvatarFallback>
                  </Avatar>
                  <div className="flex-1">
                    <div className="flex items-center gap-2">
                      <span className="font-medium">{review.userName}</span>
                      {review.verified && (
                        <Badge variant="secondary">Verified Purchase</Badge>
                      )}
                    </div>
                    <div className="flex items-center gap-2 mt-1">
                      <div className="flex">
                        {Array.from({ length: 5 }).map((_, i) => (
                          <Star
                            key={i}
                            className={`h-4 w-4 ${
                              i < review.rating
                                ? 'fill-yellow-400 text-yellow-400'
                                : 'text-muted'
                            }`}
                          />
                        ))}
                      </div>
                      <span className="text-sm text-muted-foreground">
                        {formatRelativeTime(review.createdAt)}
                      </span>
                    </div>
                    <h4 className="font-medium mt-2">{review.title}</h4>
                    <p className="text-muted-foreground mt-1">{review.content}</p>
                    <div className="flex items-center gap-4 mt-3 text-sm text-muted-foreground">
                      <button className="hover:text-primary">
                        Helpful ({review.helpfulCount})
                      </button>
                    </div>
                  </div>
                </div>
              </div>
            ))}
          </div>
        </TabsContent>

        <TabsContent value="details" className="mt-6">
          <div className="prose max-w-none">
            <p>{product.description}</p>
          </div>
        </TabsContent>
      </Tabs>

      {/* Similar Products */}
      {similarProducts.length > 0 && (
        <section className="mt-16">
          <h2 className="text-2xl font-bold mb-6">Similar Products</h2>
          <ProductGrid products={similarProducts} />
        </section>
      )}
    </div>
  );
}
