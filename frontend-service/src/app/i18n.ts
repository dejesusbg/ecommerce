import i18n from 'i18next';
import { initReactI18next } from 'react-i18next';
import HttpBackend from 'i18next-http-backend';
import LanguageDetector from 'i18next-browser-languagedetector';
// Assuming i18n.config.js is at the root of frontend-service
// Adjust path if necessary, though it's not directly used by i18next instance here
// but rather by next.config.js for Next.js level i18n routing.
// import i18nNextConfig from '../../i18n.config.js'; // Not needed for this file

const createI18nInstance = () => {
  const instance = i18n
    .use(HttpBackend) // Loads translations from backend (e.g., public/locales)
    .use(LanguageDetector) // Detects user language
    .use(initReactI18next); // Passes i18n instance to react-i18next

  instance.init({
    //lng: i18nNextConfig.defaultLocale, // Default language, if not detected
    fallbackLng: 'en', // Fallback language if detection or current lang files fail
    // supportedLngs: i18nNextConfig.locales, // Supported languages
    ns: ['common'], // Default namespace
    defaultNS: 'common',
    backend: {
      loadPath: '/locales/{{lng}}/{{ns}}.json', // Path to translation files
    },
    detection: {
      order: ['querystring', 'cookie', 'localStorage', 'navigator', 'htmlTag', 'path', 'subdomain'],
      caches: ['cookie'], // How detected language is stored
    },
    interpolation: {
      escapeValue: false, // React already safes from xss
    },
    // debug: process.env.NODE_ENV === 'development', // Enable debug logs in dev
  });
  return instance;
};

// Export a function to create a new instance on each request if needed (SSR)
// or a shared instance for CSR. For App Router, client components will use this.
// Creating it once should be fine for client-side.
const i18nInstance = createI18nInstance();

export default i18nInstance;

// It's also possible to export a function that returns a new i18n instance
// export const getI18nInstance = () => {
//   const instance = i18n.createInstance(); // Use createInstance for isolation if needed
//   instance
//     .use(HttpBackend)
//     .use(LanguageDetector)
//     .use(initReactI18next)
//     .init({
//       fallbackLng: 'en',
//       ns: ['common'],
//       defaultNS: 'common',
//       backend: {
//         loadPath: '/locales/{{lng}}/{{ns}}.json',
//       },
//       detection: {
//         order: ['querystring', 'cookie', 'localStorage', 'navigator', 'htmlTag'],
//         caches: ['cookie'],
//       },
//       interpolation: {
//         escapeValue: false,
//       },
//     });
//   return instance;
// };
