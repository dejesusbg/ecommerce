// @ts-check

/**
 * @type {import('next').NextConfig['i18n']}
 */
module.exports = {
  defaultLocale: 'en',
  locales: ['en', 'es-CO'],
  localeDetection: false, // Using browser language detector, but Next.js can also do path prefixing
};
