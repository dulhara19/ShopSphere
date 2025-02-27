'use client';

import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import { ReactQueryDevtools } from '@tanstack/react-query-devtools';
import { useState, useEffect } from 'react';
import { Toaster } from '@/components/ui/toaster';

// MSW initialization for development
async function initMocks() {
  if (typeof window === 'undefined') return;

  if (process.env.NEXT_PUBLIC_ENABLE_MSW === 'true') {
    const { worker } = await import('@/mocks/browser');
    await worker.start({
      onUnhandledRequest: 'bypass',
    });
  }
}

// Initialize MSW
if (typeof window !== 'undefined') {
  initMocks();
}

export function Providers({ children }: { children: React.ReactNode }) {
  const [queryClient] = useState(
    () =>
      new QueryClient({
        defaultOptions: {
          queries: {
            staleTime: 60 * 1000, // 1 minute
            refetchOnWindowFocus: false,
            retry: 1,
          },
        },
      })
  );

  const [mounted, setMounted] = useState(false);

  useEffect(() => {
    setMounted(true);
  }, []);

  if (!mounted) {
    return null;
  }

  return (
    <QueryClientProvider client={queryClient}>
      {children}
      <Toaster />
      <ReactQueryDevtools initialIsOpen={false} />
    </QueryClientProvider>
  );
}
