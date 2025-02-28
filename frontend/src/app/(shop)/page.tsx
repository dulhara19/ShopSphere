'use client';

import Link from 'next/link';
import Image from 'next/image';
import { ArrowRight } from 'lucide-react';
import { Button } from '@/components/ui/button';
import { ProductGrid } from '@/components/products/product-grid';
import { useQuery } from '@tanstack/react-query';
import { productApi } from '@/lib/api/product';
import { recommendationApi } from '@/lib/api/recommendation';

export default function HomePage() {
  const { data: productsData, isLoading: productsLoading } = useQuery({
    queryKey: ['products', 'featured'],
    queryFn: () => productApi.getProducts({ size: 8, sort: 'rating' }),
  });

  const { data: trendingData, isLoading: trendingLoading } = useQuery({
    queryKey: ['recommendations', 'trending'],
    queryFn: () => recommendationApi.getTrending({ limit: 4 }),
  });

  const rawProducts = productsData?.data?.data || productsData?.data;
  const products = rawProducts?.content || rawProducts || [];
  const trending = trendingData?.data?.data || trendingData?.data || [];

  return (
    <div className="flex flex-col">
      {/* Hero Section */}
      <section className="relative h-[500px] md:h-[600px] bg-gradient-to-r from-primary/10 to-primary/5">
        <div className="container h-full flex items-center">
          <div className="max-w-2xl space-y-6">
            <h1 className="text-4xl md:text-6xl font-bold tracking-tight">
              Discover Your Style at ShopSphere
            </h1>
            <p className="text-lg md:text-xl text-muted-foreground">
              Explore our curated collection of premium products. From electronics
              to fashion, find everything you need in one place.
            </p>
            <div className="flex gap-4">
              <Button size="lg" asChild>
                <Link href="/products">
                  Shop Now
                  <ArrowRight className="ml-2 h-4 w-4" />
                </Link>
              </Button>
              <Button size="lg" variant="outline" asChild>
                <Link href="/products?category=cat-1">Electronics</Link>
              </Button>
            </div>
          </div>
        </div>
      </section>

      {/* Categories */}
      <section className="py-16 container">
        <h2 className="text-2xl md:text-3xl font-bold mb-8">Shop by Category</h2>
        <div className="grid grid-cols-2 md:grid-cols-4 gap-4">
          {[
            {
              name: 'Electronics',
              slug: 'cat-1',
              image:
                'https://images.unsplash.com/photo-1498049794561-7780e7231661?w=400',
            },
            {
              name: 'Clothing',
              slug: 'cat-2',
              image:
                'https://images.unsplash.com/photo-1445205170230-053b83016050?w=400',
            },
            {
              name: 'Home & Garden',
              slug: 'cat-3',
              image:
                'https://images.unsplash.com/photo-1556909114-f6e7ad7d3136?w=400',
            },
            {
              name: 'Sports',
              slug: 'cat-4',
              image:
                'https://images.unsplash.com/photo-1461896836934-gy2d3e1?w=400',
            },
          ].map((category) => (
            <Link
              key={category.slug}
              href={`/products?category=${category.slug}`}
              className="relative group aspect-square rounded-lg overflow-hidden"
            >
              <Image
                src={category.image}
                alt={category.name}
                fill
                className="object-cover transition-transform group-hover:scale-105"
              />
              <div className="absolute inset-0 bg-black/40 flex items-center justify-center">
                <h3 className="text-white text-xl font-semibold">
                  {category.name}
                </h3>
              </div>
            </Link>
          ))}
        </div>
      </section>

      {/* Trending Products */}
      <section className="py-16 bg-muted/50">
        <div className="container">
          <div className="flex justify-between items-center mb-8">
            <h2 className="text-2xl md:text-3xl font-bold">Trending Now</h2>
            <Button variant="ghost" asChild>
              <Link href="/products?sort=rating">
                View All
                <ArrowRight className="ml-2 h-4 w-4" />
              </Link>
            </Button>
          </div>
          <ProductGrid products={trending} isLoading={trendingLoading} />
        </div>
      </section>

      {/* Featured Products */}
      <section className="py-16 container">
        <div className="flex justify-between items-center mb-8">
          <h2 className="text-2xl md:text-3xl font-bold">Featured Products</h2>
          <Button variant="ghost" asChild>
            <Link href="/products">
              View All
              <ArrowRight className="ml-2 h-4 w-4" />
            </Link>
          </Button>
        </div>
        <ProductGrid products={products} isLoading={productsLoading} />
      </section>

      {/* CTA Banner */}
      <section className="py-16 bg-primary text-primary-foreground">
        <div className="container text-center space-y-6">
          <h2 className="text-3xl md:text-4xl font-bold">
            Join ShopSphere Today
          </h2>
          <p className="text-lg opacity-90 max-w-2xl mx-auto">
            Sign up now and get 10% off your first order. Plus, get access to
            exclusive deals and early access to new arrivals.
          </p>
          <Button size="lg" variant="secondary" asChild>
            <Link href="/register">Create Account</Link>
          </Button>
        </div>
      </section>
    </div>
  );
}
