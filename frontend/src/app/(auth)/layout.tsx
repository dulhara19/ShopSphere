import Link from 'next/link';

export default function AuthLayout({
  children,
}: {
  children: React.ReactNode;
}) {
  return (
    <div className="min-h-screen flex flex-col items-center justify-center bg-muted/50 px-4">
      <Link href="/" className="mb-8">
        <h1 className="text-2xl font-bold">ShopSphere</h1>
      </Link>
      {children}
    </div>
  );
}
