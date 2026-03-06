/**
 * Mock User Data
 */

import { User, Address, AuthResponse } from '@/types/user';

export const mockUsers: User[] = [
  {
    id: 'user-1',
    email: 'customer@example.com',
    firstName: 'John',
    lastName: 'Doe',
    phone: '+1234567890',
    avatarUrl: 'https://images.unsplash.com/photo-1472099645785-5658abf4ff4e?w=100',
    role: 'CUSTOMER',
    status: 'ACTIVE',
    emailVerified: true,
    createdAt: '2024-01-15T10:00:00Z',
    updatedAt: '2024-06-01T14:30:00Z',
  },
  {
    id: 'user-2',
    email: 'seller@example.com',
    firstName: 'Jane',
    lastName: 'Smith',
    phone: '+1234567891',
    avatarUrl: 'https://images.unsplash.com/photo-1494790108377-be9c29b29330?w=100',
    role: 'SELLER',
    status: 'ACTIVE',
    emailVerified: true,
    createdAt: '2024-02-01T08:00:00Z',
    updatedAt: '2024-05-20T11:00:00Z',
  },
  {
    id: 'user-3',
    email: 'admin@example.com',
    firstName: 'Admin',
    lastName: 'User',
    role: 'ADMIN',
    status: 'ACTIVE',
    emailVerified: true,
    createdAt: '2024-01-01T00:00:00Z',
    updatedAt: '2024-06-10T09:00:00Z',
  },
];

export const mockAddresses: Address[] = [
  {
    id: 'addr-1',
    userId: 'user-1',
    label: 'Home',
    fullName: 'John Doe',
    phone: '+1234567890',
    line1: '123 Main Street',
    line2: 'Apt 4B',
    city: 'New York',
    state: 'NY',
    postalCode: '10001',
    country: 'US',
    isDefault: true,
    createdAt: '2024-01-20T10:00:00Z',
    updatedAt: '2024-01-20T10:00:00Z',
  },
  {
    id: 'addr-2',
    userId: 'user-1',
    label: 'Office',
    fullName: 'John Doe',
    phone: '+1234567890',
    line1: '456 Business Ave',
    city: 'New York',
    state: 'NY',
    postalCode: '10002',
    country: 'US',
    isDefault: false,
    createdAt: '2024-02-15T14:00:00Z',
    updatedAt: '2024-02-15T14:00:00Z',
  },
];

export const createAuthResponse = (user: User): AuthResponse => ({
  accessToken: `mock-access-token-${user.id}`,
  refreshToken: `mock-refresh-token-${user.id}`,
  tokenType: 'Bearer',
  expiresIn: 900,
  user,
});

// Test credentials
export const testCredentials = {
  customer: { email: 'customer@example.com', password: 'password123' },
  seller: { email: 'seller@example.com', password: 'password123' },
  admin: { email: 'admin@example.com', password: 'password123' },
};
