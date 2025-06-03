import type { NextConfig } from "next";
// @ts-ignore - i18n.config.js is a CJS module
import i18nConfig from './i18n.config.js';

const nextConfig: NextConfig = {
  /* config options here */
  i18n: i18nConfig,
  reactStrictMode: true, // Default, kept for explicitness
  output: 'standalone',
};

export default nextConfig;
