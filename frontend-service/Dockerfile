# 1. Install dependencies only when needed
FROM node:18-alpine AS deps
# Install libc6-compat for Next.js on Alpine (often needed for sharp or other native modules)
RUN apk add --no-cache libc6-compat
WORKDIR /usr/src/app
# Copy package.json and package-lock.json (or yarn.lock, pnpm-lock.yaml)
COPY package.json package-lock.json* ./
# Use 'npm ci' for cleaner installs from lock file, falls back to 'npm install' if no lock file
RUN npm ci

# 2. Rebuild the source code only when needed
FROM node:18-alpine AS builder
WORKDIR /usr/src/app
# Copy node_modules from the 'deps' stage
COPY --from=deps /usr/src/app/node_modules ./node_modules
# Copy the rest of the application source code
COPY . .
# Optional: Disable Next.js telemetry during build
ENV NEXT_TELEMETRY_DISABLED 1
# Build the Next.js application
RUN npm run build

# 3. Production image, copy all the files and run next
FROM node:18-alpine AS runner
WORKDIR /usr/src/app

ENV NODE_ENV production
# Optional: Disable Next.js telemetry during runtime
ENV NEXT_TELEMETRY_DISABLED 1

# Create a non-root user and group for security
RUN addgroup --system --gid 1001 nodejs
RUN adduser --system --uid 1001 nextjs

# Copy necessary files from the 'builder' stage
# These paths are specific to Next.js with `output: 'standalone'`
COPY --from=builder /usr/src/app/public ./public
# The standalone output mode creates a .next/standalone directory
# which includes a minimal server.js and copies necessary node_modules.
COPY --from=builder --chown=nextjs:nodejs /usr/src/app/.next/standalone ./
# Static assets are also needed
COPY --from=builder --chown=nextjs:nodejs /usr/src/app/.next/static ./.next/static

# Set the user to the non-root user
USER nextjs

EXPOSE 3000
# Set PORT environment variable, Next.js standalone server will use this
ENV PORT 3000

# Command to run the Next.js standalone server
# The server.js file is part of the standalone output
CMD ["node", "server.js"]
