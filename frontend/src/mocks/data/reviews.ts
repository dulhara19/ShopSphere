/**
 * Mock Review Data
 */

import { Review, RatingSummary } from '@/types/review';

export const mockReviews: Review[] = [
  {
    id: 'review-1',
    productId: 'prod-1',
    userId: 'user-1',
    userName: 'John D.',
    userAvatar:
      'https://images.unsplash.com/photo-1472099645785-5658abf4ff4e?w=50',
    rating: 5,
    title: 'Best headphones I have ever owned!',
    content:
      'The noise cancellation is incredible. I use these for work calls and listening to music during my commute. Battery life is exactly as advertised - 30 hours easily. Highly recommend!',
    helpfulCount: 45,
    unhelpfulCount: 2,
    verified: true,
    createdAt: '2024-05-15T10:00:00Z',
    updatedAt: '2024-05-15T10:00:00Z',
  },
  {
    id: 'review-2',
    productId: 'prod-1',
    userId: 'user-4',
    userName: 'Sarah M.',
    rating: 4,
    title: 'Great sound, comfortable fit',
    content:
      'Really happy with these headphones. Sound quality is excellent and they are very comfortable for long listening sessions. Only minor issue is that they are a bit bulky for travel.',
    helpfulCount: 23,
    unhelpfulCount: 1,
    verified: true,
    createdAt: '2024-05-20T14:30:00Z',
    updatedAt: '2024-05-20T14:30:00Z',
  },
  {
    id: 'review-3',
    productId: 'prod-1',
    userId: 'user-5',
    userName: 'Mike R.',
    rating: 5,
    title: 'Worth every penny',
    content:
      'Was hesitant about the price but these are absolutely worth it. The ANC is top-tier and the sound profile is perfect for all genres. Build quality is solid too.',
    helpfulCount: 18,
    unhelpfulCount: 0,
    verified: true,
    createdAt: '2024-06-01T09:00:00Z',
    updatedAt: '2024-06-01T09:00:00Z',
  },
  {
    id: 'review-4',
    productId: 'prod-2',
    userId: 'user-1',
    userName: 'John D.',
    rating: 5,
    title: 'Elegant and reliable',
    content:
      'Beautiful minimalist design. The leather strap is genuine and high quality. Watch keeps perfect time. Very happy with this purchase.',
    helpfulCount: 12,
    unhelpfulCount: 0,
    verified: true,
    createdAt: '2024-04-10T11:00:00Z',
    updatedAt: '2024-04-10T11:00:00Z',
  },
  {
    id: 'review-5',
    productId: 'prod-3',
    userId: 'user-6',
    userName: 'Emily K.',
    rating: 4,
    title: 'Great for running!',
    content:
      'These shoes are perfect for my daily runs. Very comfortable and the cushioning is excellent. Only giving 4 stars because they run a bit small - order half size up.',
    helpfulCount: 34,
    unhelpfulCount: 3,
    verified: true,
    createdAt: '2024-05-28T16:00:00Z',
    updatedAt: '2024-05-28T16:00:00Z',
  },
];

export const getMockRatingSummary = (productId: string): RatingSummary => {
  const productReviews = mockReviews.filter((r) => r.productId === productId);
  const totalReviews = productReviews.length;
  const averageRating =
    totalReviews > 0
      ? productReviews.reduce((acc, r) => acc + r.rating, 0) / totalReviews
      : 0;

  return {
    productId,
    averageRating,
    totalReviews,
    distribution: {
      1: productReviews.filter((r) => r.rating === 1).length,
      2: productReviews.filter((r) => r.rating === 2).length,
      3: productReviews.filter((r) => r.rating === 3).length,
      4: productReviews.filter((r) => r.rating === 4).length,
      5: productReviews.filter((r) => r.rating === 5).length,
    },
    verifiedPurchaseCount: productReviews.filter((r) => r.verified).length,
  };
};
