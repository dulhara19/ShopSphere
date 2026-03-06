FROM node:18-alpine AS deps
WORKDIR /app
COPY package.json package-lock.json* ./
RUN npm ci

FROM node:18-alpine AS builder
WORKDIR /app
COPY --from=deps /app/node_modules ./node_modules
COPY . .

ENV NEXT_TELEMETRY_DISABLED=1
ENV NEXT_PUBLIC_ENABLE_MSW=false
ENV NEXT_PUBLIC_APP_NAME=ShopSphere
ENV NEXT_PUBLIC_ENABLE_USER_SERVICE=true
ENV NEXT_PUBLIC_ENABLE_PRODUCT_SERVICE=true
ENV NEXT_PUBLIC_ENABLE_INVENTORY_SERVICE=true
ENV NEXT_PUBLIC_ENABLE_ORDER_SERVICE=true
ENV NEXT_PUBLIC_ENABLE_PAYMENT_SERVICE=true
ENV NEXT_PUBLIC_ENABLE_SHIPPING_SERVICE=true
ENV NEXT_PUBLIC_ENABLE_REVIEW_SERVICE=true
ENV NEXT_PUBLIC_ENABLE_RECOMMENDATION_SERVICE=true
ENV NEXT_PUBLIC_ENABLE_NOTIFICATION_SERVICE=true
ENV NEXT_PUBLIC_ENABLE_ANALYTICS_SERVICE=true

RUN npm run build

FROM node:18-alpine AS runner
WORKDIR /app

ENV NODE_ENV=production
ENV NEXT_TELEMETRY_DISABLED=1

RUN addgroup --system --gid 1001 nodejs
RUN adduser --system --uid 1001 nextjs

COPY --from=builder /app/public ./public
COPY --from=builder --chown=nextjs:nodejs /app/.next/standalone ./
COPY --from=builder --chown=nextjs:nodejs /app/.next/static ./.next/static

USER nextjs

EXPOSE 3000

ENV PORT=3000
ENV HOSTNAME="0.0.0.0"

CMD ["node", "server.js"]
