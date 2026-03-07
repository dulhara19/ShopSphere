import { NextRequest, NextResponse } from 'next/server';

const DEFAULT_URL = 'http://localhost:3007';

function getReviewServiceUrl() {
  return process.env.REVIEW_SERVICE_URL || DEFAULT_URL;
}

export async function POST(request: NextRequest) {
  const body = await request.text();
  const serviceUrl = getReviewServiceUrl();

  const res = await fetch(`${serviceUrl}/api/reviews`, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
      ...(request.headers.get('authorization')
        ? { Authorization: request.headers.get('authorization')! }
        : {}),
    },
    body,
  });

  const data = await res.text();
  return new NextResponse(data, {
    status: res.status,
    headers: { 'Content-Type': 'application/json' },
  });
}
